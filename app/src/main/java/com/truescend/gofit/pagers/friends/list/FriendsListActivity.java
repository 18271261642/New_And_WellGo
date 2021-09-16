package com.truescend.gofit.pagers.friends.list;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.android.material.tabs.TabLayout;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.sn.app.net.data.app.bean.FriendInvitesBean;
import com.sn.app.net.data.app.bean.FriendListBean;
import com.sn.app.net.data.app.bean.SystemMessageBean;
import com.sn.utils.SNToast;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BaseActivity;
import com.truescend.gofit.pagers.common.dialog.InputTextDialog;
import com.truescend.gofit.pagers.common.dialog.LoadingDialog;
import com.truescend.gofit.pagers.friends.list.adapter.FriendsListAdapter;
import com.truescend.gofit.pagers.friends.list.adapter.FriendsRequestAdapter;
import com.truescend.gofit.pagers.friends.list.adapter.MessageListAdapter;
import com.truescend.gofit.utils.PageJumpUtil;
import com.truescend.gofit.utils.UnitConversion;
import com.truescend.gofit.utils.UnreadMessages;
import com.truescend.gofit.views.BadgeHelper;
import com.truescend.gofit.views.TitleLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 作者:东芝(2018/08/08).
 * 功能:好友列表
 */
public class FriendsListActivity extends BaseActivity<FriendsListPresenterImpl, IFriendsListContract.IView> implements IFriendsListContract.IView, TabLayout.OnTabSelectedListener, AdapterView.OnItemClickListener, SwipeMenuListView.OnMenuItemClickListener, OnRefreshListener, FriendsRequestAdapter.OnButtonClickListener, FriendsListAdapter.OnEditRemarkClickListener {

    private static final int ITEM_TYPE_SYS_MSG_LIST = 0;
    private static final int ITEM_TYPE_FRIENDS_REQ_LIST = 1;
    private static final int ITEM_TYPE_FRIENDS_LIST = 2;
    @BindView(R.id.tlTopTabs)
    TabLayout tlTopTabs;
    @BindView(R.id.lvList)
    SwipeMenuListView lvList;
    @BindView(R.id.rlRefresh)
    SmartRefreshLayout rlRefresh;

    private FriendsRequestAdapter requestAddFriendsAdapter;
    private FriendsListAdapter friendsListAdapter;
    private MessageListAdapter messageListAdapter;
    private BadgeHelper badgeHelperSysMsg;
    private BadgeHelper badgeHelperFriendReq;


    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, FriendsListActivity.class));
    }

    @Override
    protected FriendsListPresenterImpl initPresenter() {
        return new FriendsListPresenterImpl(this);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_friends_list;
    }

    @Override
    protected void onCreateTitle(TitleLayout titleLayout) {
        super.onCreateTitle(titleLayout);
        //右侧添加按钮
        titleLayout.addRightItem(TitleLayout.ItemBuilder.Builder().setIcon(R.mipmap.icon_add_friends).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PageJumpUtil.startFriendsSearchActivity(FriendsListActivity.this);
            }
        }));
    }

    @Override
    public void onLoading() {
        LoadingDialog.loading(this);
    }

    @Override
    public void onLoadingDone() {
        LoadingDialog.dismiss();
    }

    @Override
    public void onShowTips(String str) {
        SNToast.toast(str);
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        tlTopTabs.addOnTabSelectedListener(this);
        friendsListAdapter = new FriendsListAdapter(this);
        requestAddFriendsAdapter = new FriendsRequestAdapter(this);
        messageListAdapter = new MessageListAdapter(this);
        requestAddFriendsAdapter.setOnButtonClickListener(this);

        lvList.setOnMenuItemClickListener(this);
        lvList.setOnItemClickListener(this);
        rlRefresh.setOnRefreshListener(this);
        friendsListAdapter.setOnEditRemarkClickListener(this);

        selectPager(ITEM_TYPE_SYS_MSG_LIST);

        badgeHelperSysMsg = new BadgeHelper(this);
        badgeHelperSysMsg.setBadgeType(BadgeHelper.Type.TYPE_POINT)
                .setBadgeOverlap(false)
                .bindToTargetView(tlTopTabs, 0);

        badgeHelperFriendReq = new BadgeHelper(this);
        badgeHelperFriendReq.setBadgeType(BadgeHelper.Type.TYPE_POINT)
                .setBadgeOverlap(false)
                .bindToTargetView(tlTopTabs, 1);



        getPresenter().loadBadgeStatus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        onRefreshBadgeStatus();
    }

    @Override
    public void onRefreshBadgeStatus() {
        badgeHelperSysMsg.setBadgeEnable(UnreadMessages.getNewSysMessageUnreadNumber()>0);
        badgeHelperFriendReq.setBadgeEnable(UnreadMessages.getNewFriendRequestUnreadNumber()>0);
    }

    private SwipeMenuCreator creator = new SwipeMenuCreator() {
        @Override
        public void create(SwipeMenu menu) {
            SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
            deleteItem.setBackground(R.color.black);
            deleteItem.setWidth(UnitConversion.dipToPx(FriendsListActivity.this, 90));
            deleteItem.setTitle(getString(R.string.delete));
            deleteItem.setTitleSize(15);
            deleteItem.setTitleColor(Color.WHITE);
            menu.addMenuItem(deleteItem);
        }
    };


    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        selectPager(getTabItemType());
    }

    private void selectPager(int position) {
        switch (position) {
            case ITEM_TYPE_SYS_MSG_LIST:
                lvList.setMenuCreator(null);
                lvList.setAdapter(messageListAdapter);
                getPresenter().loadMessageList();
                break;
            case ITEM_TYPE_FRIENDS_REQ_LIST:
                lvList.setMenuCreator(creator);
                lvList.setAdapter(requestAddFriendsAdapter);
                getPresenter().loadFriendRequest();
                break;
            case ITEM_TYPE_FRIENDS_LIST:
                lvList.setMenuCreator(creator);
                lvList.setAdapter(friendsListAdapter);
                getPresenter().loadFriendList();
                break;
        }
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        selectPager(getTabItemType());
    }

    private int getTabItemType() {
        return tlTopTabs.getSelectedTabPosition();
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (getTabItemType()) {
            case ITEM_TYPE_SYS_MSG_LIST: {
                SystemMessageBean.DataBean.ItemsBean item = messageListAdapter.getItem(position);
                PageJumpUtil.startFriendsInfoActivity(this, item.getSend_user_id());
            }
            break;
            case ITEM_TYPE_FRIENDS_REQ_LIST: {
                FriendInvitesBean.DataBean item = requestAddFriendsAdapter.getItem(position);
                PageJumpUtil.startFriendsInfoActivity(this, item.getInviter_id());
            }
            break;
            case ITEM_TYPE_FRIENDS_LIST: {
                FriendListBean.DataBean item = friendsListAdapter.getItem(position);
                PageJumpUtil.startFriendsDataActivity(this, item.getFriend_user_id());
            }
            break;
        }

    }

    @Override
    public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
        switch (getTabItemType()) {
            case ITEM_TYPE_SYS_MSG_LIST: {

            }
            break;
            case ITEM_TYPE_FRIENDS_REQ_LIST: {
                FriendInvitesBean.DataBean item = requestAddFriendsAdapter.getItem(position);
                getPresenter().friendIgnore(position, item.getId());
            }
            break;
            case ITEM_TYPE_FRIENDS_LIST: {
                FriendListBean.DataBean item = friendsListAdapter.getItem(position);
                getPresenter().friendDelete(position, item.getFriend_user_id());
            }
            break;
        }


        return false;
    }


    @Override
    public void onLoadMessageList(SystemMessageBean.DataBean.MetaBean meta, List<SystemMessageBean.DataBean.ItemsBean> data) {
//        meta.getCurrentPage()
        messageListAdapter.setList(data);

    }

    @Override
    public void onLoadFriendRequest(List<FriendInvitesBean.DataBean> data) {
        requestAddFriendsAdapter.setList(data);

    }

    @Override
    public void onLoadFriendList(List<FriendListBean.DataBean> data) {
        friendsListAdapter.setList(data);
    }

    @Override
    public void onFriendAgreed(int position) {
        requestAddFriendsAdapter.getItem(position).setStatus(2);
        requestAddFriendsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFriendIgnore(int position) {
        requestAddFriendsAdapter.removeItem(position);
    }

    @Override
    public void onFriendDelete(int position) {
        friendsListAdapter.removeItem(position);
    }

    @Override
    public void onFriendRefused(int position) {
        requestAddFriendsAdapter.getItem(position).setStatus(3);
        requestAddFriendsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFriendRemark(int position, String newRemark) {
        friendsListAdapter.getItem(position).setRemark(newRemark);
        friendsListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFinishRefresh() {
        if (rlRefresh != null && rlRefresh.isRefreshing()) {
            rlRefresh.finishRefresh();
        }
    }


    @Override
    public void onClickAgreed(int position) {
        FriendInvitesBean.DataBean item = requestAddFriendsAdapter.getItem(position);
        getPresenter().friendAgreed(position, item.getId());
    }

    @Override
    public void onClickRefused(int position) {
        FriendInvitesBean.DataBean item = requestAddFriendsAdapter.getItem(position);
        getPresenter().friendRefused(position, item.getId());
    }

    @Override
    public void onEditRemarkClick(View v, final int position) {
        final FriendListBean.DataBean item = friendsListAdapter.getItem(position);
        if (item == null) {
            return;
        }
        String remark = item.getRemark();
        if (TextUtils.isEmpty(remark)) {
            remark = "";
        }
        InputTextDialog.create(FriendsListActivity.this, getString(R.string.content_set_remark), remark, getString(R.string.content_cancel), getString(R.string.content_confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }, new InputTextDialog.OnInputTextCommitListener() {
            @Override
            public void onTextCommit(String text) {
                getPresenter().friendRemark(position, item.getFriend_user_id(), text);
            }
        }).show();
    }


    @OnClick({R.id.tvInvitationFriends})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvInvitationFriends:
                PageJumpUtil.startMyQRCActivity(this);
                break;
        }
    }
}
