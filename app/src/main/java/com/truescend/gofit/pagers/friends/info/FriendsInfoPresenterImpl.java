package com.truescend.gofit.pagers.friends.info;

import com.sn.app.net.AppNetReq;
import com.sn.app.net.callback.OnResponseListener;
import com.sn.app.net.data.app.bean.FriendInfoBean;
import com.sn.app.net.data.base.DefResponseBean;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BasePresenter;
import com.truescend.gofit.utils.ResUtil;

/**
 * 作者:东芝(2018/08/08).
 * 功能:添加好友
 */
public class FriendsInfoPresenterImpl extends BasePresenter<IFriendsInfoContract.IView> implements IFriendsInfoContract.IPresenter {
    private IFriendsInfoContract.IView view;
    public static final int THUMBACTION_TYPE_ZAN = 1;//点赞
    public static final int THUMBACTION_TYPE_ENCOURAGE = 2;//鼓励
    public FriendsInfoPresenterImpl(IFriendsInfoContract.IView view) {
        this.view = view;
    }

    @Override
    public void getFriendsInfoResults(int user_id) {
        AppNetReq.getApi().friendHomepage(user_id).enqueue(new OnResponseListener<FriendInfoBean>() {
            @Override
            public void onResponse(FriendInfoBean body) throws Throwable {
                view.onFriendsInfoResults( body.getData());

            }

            @Override
            public void onFailure(int ret, String msg) {
                view.onShowTips(msg);
            }
        });

    }

    @Override
    public void setFriendsThumbAction(int user_id, final int type) {
        AppNetReq.getApi().friendThumbAction(user_id,type).enqueue(new OnResponseListener<String>() {
            @Override
            public void onResponse(String body) throws Throwable {
                view.onFriendsThumbAction(type);
            }

            @Override
            public void onFailure(int ret, String msg) {
                view.onShowTips(msg);
            }
        });
    }

    @Override
    public void requestAddFriends(int user_id) {
        if (user_id<=0) {
            return;
        }
        view.onLoading();
        AppNetReq.getApi().friendInvite(user_id).enqueue(new OnResponseListener<DefResponseBean>() {
            @Override
            public void onResponse(DefResponseBean body) throws Throwable {
                view.onLoadingDone();
                view.onRequestAddFriendsSuccess();
                view.onShowTips(ResUtil.getString(R.string.toast_tips_req_add_friends));
            }

            @Override
            public void onFailure(int ret, String msg) {
                view.onLoadingDone();
                view.onRequestAddFriendsFailed();
                view.onShowTips(msg);
            }
        });
    }
}
