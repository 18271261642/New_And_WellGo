package com.truescend.gofit.pagers.friends.list;

import com.sn.app.net.AppNetReq;
import com.sn.app.net.callback.OnResponseListener;
import com.sn.app.net.data.app.bean.FriendInvitesBean;
import com.sn.app.net.data.app.bean.FriendListBean;
import com.sn.app.net.data.app.bean.SystemMessageBean;
import com.sn.app.net.data.base.DefResponseBean;
import com.sn.utils.DateUtil;
import com.truescend.gofit.pagers.base.BasePresenter;
import com.truescend.gofit.utils.UnreadMessages;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者:东芝(2018/08/08).
 * 功能:好友列表
 */
public class FriendsListPresenterImpl extends BasePresenter<IFriendsListContract.IView> implements IFriendsListContract.IPresenter {
    private IFriendsListContract.IView view;
    private boolean isLoadFriendList;
    private boolean isLoadFriendRequest;
    private boolean isLoadMessageList;


    public FriendsListPresenterImpl(IFriendsListContract.IView view) {
        this.view = view;
    }


    @Override
    public void loadBadgeStatus() {
        UnreadMessages.refreshUnreadNumber(new UnreadMessages.OnUnreadMessagesRefreshListener() {
            @Override
            public void onDone() {
                if (!isUIEnable()) {
                    return;
                }
                onRefreshBadgeStatus();
            }

            @Override
            public void onFail() {

            }
        });
    }

    private void onRefreshBadgeStatus() {
        view.onRefreshBadgeStatus();
    }

    @Override
    public void loadMessageList() {
        if(!isLoadMessageList) {
            view.onLoading();
        }
        AppNetReq.getApi().friendMsglist().enqueue(new OnResponseListener<SystemMessageBean>() {
            @Override
            public void onResponse(SystemMessageBean body) throws Throwable {
                isLoadMessageList = true;
                if (!isUIEnable()) {
                    return;
                }
                view.onLoadMessageList(body.getData().get_meta(),body.getData().getItems());
                view.onFinishRefresh();
                view.onLoadingDone();
                UnreadMessages.setReadAllNewSysMessage(new UnreadMessages.OnUnreadMessagesRefreshListener() {
                    @Override
                    public void onDone() {
                        if (!isUIEnable()) {
                            return;
                        }
                        onRefreshBadgeStatus();
                    }

                    @Override
                    public void onFail() {

                    }
                });
            }

            @Override
            public void onFailure(int ret, String msg) {
                view.onLoadingDone();
            }
        });
    }


    @Override
    public void loadFriendRequest() {
        if(!isLoadFriendRequest) {
            view.onLoading();
        }
        AppNetReq.getApi().friendRequest().enqueue(new OnResponseListener<FriendInvitesBean>() {
            @Override
            public void onResponse(FriendInvitesBean body) throws Throwable {
                isLoadFriendRequest = true;
                if (!isUIEnable()) {
                    return;
                }
                UnreadMessages.setLastInviteTime(body);
                view.onLoadFriendRequest(body.getData());
                view.onFinishRefresh();

                UnreadMessages.setReadAllNewFriendsRequestMessage();
                onRefreshBadgeStatus();
                view.onLoadingDone();
            }

            @Override
            public void onFailure(int ret, String msg) {
                view.onLoadingDone();
            }
        });
    }

    @Override
    public void loadFriendList() {
        if(!isLoadFriendList) {
            view.onLoading();
        }
        AppNetReq.getApi().friendlist().enqueue(new OnResponseListener<FriendListBean>() {
            @Override
            public void onResponse(FriendListBean body) throws Throwable {
                isLoadFriendList = true;
                if (!isUIEnable()) {
                    return;
                }
                List<FriendListBean.DataBean> data = body.getData();
                List<FriendListBean.DataBean> validData = new ArrayList<>();
                for (FriendListBean.DataBean datum : data) {
                    FriendListBean.DataBean.FriendBean.SportBean sport = datum.getFriend().getSport();
                    if (DateUtil.equalsToday(sport.getToday_date())) {
                        validData.add(datum);
                    }
                }
                view.onLoadFriendList(validData);
                view.onFinishRefresh();
                view.onLoadingDone();
            }

            @Override
            public void onFailure(int ret, String msg) {
                view.onLoadingDone();
            }
        });



    }

    @Override
    public void friendAgreed(final int position, int invite_id) {
        view.onLoading();
        AppNetReq.getApi().friendAgreed(invite_id).enqueue(new OnResponseListener<DefResponseBean>() {
            @Override
            public void onResponse(DefResponseBean body) throws Throwable {
                if (!isUIEnable()) {
                    return;
                }
                view.onLoadingDone();
                view.onFriendAgreed(position);
            }

            @Override
            public void onFailure(int ret, String msg) {
                if (!isUIEnable()) {
                    return;
                }
                view.onLoadingDone();
                view.onShowTips(msg);
            }
        });
    }

    @Override
    public void friendIgnore(final int position, int invite_id) {
        view.onLoading();
        AppNetReq.getApi().friendIgnore(invite_id).enqueue(new OnResponseListener<DefResponseBean>() {
            @Override
            public void onResponse(DefResponseBean body) throws Throwable {
                if (!isUIEnable()) {
                    return;
                }
                view.onLoadingDone();
                view.onFriendIgnore(position);
            }

            @Override
            public void onFailure(int ret, String msg) {
                if (!isUIEnable()) {
                    return;
                }
                view.onLoadingDone();
                view.onShowTips(msg);
            }
        });
    }

    @Override
    public void friendDelete(final int position, int friend_user_id) {
        view.onLoading();
        AppNetReq.getApi().friendDelete(friend_user_id).enqueue(new OnResponseListener<DefResponseBean>() {
            @Override
            public void onResponse(DefResponseBean body) throws Throwable {
                if (!isUIEnable()) {
                    return;
                }
                view.onLoadingDone();
                view.onFriendDelete(position);
            }

            @Override
            public void onFailure(int ret, String msg) {
                if (!isUIEnable()) {
                    return;
                }
                view.onLoadingDone();
                view.onShowTips(msg);
            }
        });
    }

    @Override
    public void friendRefused(final int position, int invite_id) {
        view.onLoading();
        AppNetReq.getApi().friendRefused(invite_id).enqueue(new OnResponseListener<DefResponseBean>() {
            @Override
            public void onResponse(DefResponseBean body) throws Throwable {
                if (!isUIEnable()) {
                    return;
                }
                view.onLoadingDone();
                view.onFriendRefused(position);
            }

            @Override
            public void onFailure(int ret, String msg) {
                if (!isUIEnable()) {
                    return;
                }
                view.onLoadingDone();
                view.onShowTips(msg);
            }
        });
    }

    @Override
    public void friendRemark(final int position, int friend_user_id, final String newRemark) {
        view.onLoading();
        AppNetReq.getApi().friendRemark(friend_user_id,newRemark).enqueue(new OnResponseListener<DefResponseBean>() {
            @Override
            public void onResponse(DefResponseBean body) throws Throwable {
                if (!isUIEnable()) {
                    return;
                }
                view.onLoadingDone();
                view.onFriendRemark(position,newRemark);
            }

            @Override
            public void onFailure(int ret, String msg) {
                if (!isUIEnable()) {
                    return;
                }
                view.onLoadingDone();
                view.onShowTips(msg);
            }
        });
    }

}
