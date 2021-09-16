package com.truescend.gofit.pagers.friends.info;


import com.sn.app.net.data.app.bean.FriendInfoBean;

/**
 * 作者:东芝(2018/08/15).
 * 功能:好友信息
 */
public class IFriendsInfoContract {

    public interface IView {
        void onLoading();
        void onLoadingDone();
        void onShowTips(String str);
        void onFriendsInfoResults(FriendInfoBean.DataBean data);
        void onFriendsThumbAction(int type);

        void onRequestAddFriendsSuccess();
        void onRequestAddFriendsFailed();
    }

    public interface IPresenter
    {

        void requestAddFriends(int user_id);
        void getFriendsInfoResults(int user_id);

        /**
         * 点赞和鼓励
         * @param user_id
         * @param type 1：点赞，2：鼓励
         */
        void setFriendsThumbAction(int user_id,int type);
    }
}
