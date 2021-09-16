package com.truescend.gofit.pagers.friends.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatEditText;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewAnimator;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sn.app.net.data.app.bean.FriendInfoBean;
import com.sn.app.net.data.app.bean.SearchFriendsResultBean;
import com.sn.utils.SNToast;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BaseActivity;
import com.truescend.gofit.pagers.common.dialog.LoadingDialog;
import com.truescend.gofit.pagers.friends.info.FriendsInfoPresenterImpl;
import com.truescend.gofit.utils.ResUtil;
import com.truescend.gofit.views.TitleLayout;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 作者:东芝(2018/08/08).
 * 功能:添加好友
 */
public class FriendsSearchActivity extends BaseActivity<FriendsSearchPresenterImpl, IFriendsSearchContract.IView> implements IFriendsSearchContract.IView {

    private static final int DISPLAYED_DEF = 0;
    private static final int DISPLAYED_NOTHING = 1;
    private static final int DISPLAYED_RESULT = 2;
    @BindView(R.id.vaResultSwitcher)
    ViewAnimator vaResultSwitcher;
    @BindView(R.id.civHead)
    CircleImageView civHead;
    @BindView(R.id.ivZan)
    ImageView ivZan;
    @BindView(R.id.ivEncourage)
    ImageView ivEncourage;
    @BindView(R.id.tvRequestAddFriends)
    TextView tvRequestAddFriends;
    @BindView(R.id.tvNickname)
    TextView tvNickname;
    @BindView(R.id.ivGender)
    ImageView ivGender;
    @BindView(R.id.tvIdName)
    TextView tvIdName;


    private AppCompatEditText mSearchInputText;

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, FriendsSearchActivity.class));
    }

    @Override
    protected FriendsSearchPresenterImpl initPresenter() {
        return new FriendsSearchPresenterImpl(this);
    }

    @Override
    protected void onCreateTitle(TitleLayout titleLayout) {
        titleLayout.setLeftIconFinishActivity(this);//默认左边按钮是finish
        //创建搜索框
        mSearchInputText = new AppCompatEditText(this);
        mSearchInputText.setEms(10);
        mSearchInputText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        mSearchInputText.setHint(R.string.content_search_friends_hint);
        mSearchInputText.setHintTextColor(0x55888888);
        mSearchInputText.setSingleLine();
        //设置到标题中间
        titleLayout.addCenterItem(mSearchInputText);

        //搜索框监听输入法的搜索按键按下 (优化体验,可选)
        mSearchInputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    getPresenter().searchFriends(mSearchInputText.getText().toString());
                    return true;
                }
                return false;
            }
        });

        //右侧添加搜索按钮
        titleLayout.addRightItem(TitleLayout.ItemBuilder.Builder().setIcon(R.mipmap.icon_search_friends).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPresenter().searchFriends(mSearchInputText.getText().toString());
            }
        }));
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_friends_search;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        vaResultSwitcher.setDisplayedChild(DISPLAYED_DEF);

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
    public void onSearchResults(SearchFriendsResultBean.DataBean data) {
        Glide.with(civHead)
                .load(data.getPortrait())
                .apply(RequestOptions.placeholderOf(R.mipmap.img_test_picture))
                .into(civHead);
        tvIdName.setText(ResUtil.format("ID:%d", data.getUser_id()));
        tvNickname.setText(data.getNickname());
        boolean isFriend = data.getIs_friend() == 1;
        tvRequestAddFriends.setText(isFriend ? R.string.content_already_friends : R.string.content_add_friends);
        tvRequestAddFriends.setClickable(!isFriend);//已经是朋友则不可点击
        tvRequestAddFriends.setEnabled(!isFriend);//已经是朋友则不可点击
        ivGender.setSelected(data.getGender() == 1);//1：男，2：女
        vaResultSwitcher.setDisplayedChild(DISPLAYED_RESULT);
    }

    @Override
    public void onSearchNothing() {
        vaResultSwitcher.setDisplayedChild(DISPLAYED_NOTHING);
    }

    @Override
    public void onRequestAddFriendsSuccess() {
//        finish();
    }

    @Override
    public void onRequestAddFriendsFailed() {

    }

    @Override
    public void onFriendsInfoResults(FriendInfoBean.DataBean data) {
        ivZan.setSelected(data.getIs_thumb() == 1);
        ivEncourage.setSelected(data.getIs_encourage() == 1);
    }


    @Override
    public void onFriendsThumbAction(int type) {
        switch (type) {
            case FriendsSearchPresenterImpl.THUMBACTION_TYPE_ZAN:
                ivZan.setSelected(true);
                break;
            case FriendsSearchPresenterImpl.THUMBACTION_TYPE_ENCOURAGE:
                ivEncourage.setSelected(true);
                break;
        }
    }


    @OnClick({R.id.tvRequestAddFriends, R.id.ivZan, R.id.ivEncourage})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvRequestAddFriends:
                getPresenter().requestAddFriends();
                break;
            case R.id.ivZan:
                getPresenter().setFriendsThumbAction(FriendsInfoPresenterImpl.THUMBACTION_TYPE_ZAN);
                break;
            case R.id.ivEncourage:
                getPresenter().setFriendsThumbAction(FriendsInfoPresenterImpl.THUMBACTION_TYPE_ENCOURAGE);
                break;
        }
    }


}
