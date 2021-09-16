package com.truescend.gofit.pagers.friends.search;


import com.sn.app.net.data.app.bean.FriendInfoBean;
import com.sn.app.net.data.app.bean.SearchFriendsResultBean;

/**
 * 作者:东芝(2018/08/08).
 * 功能:添加好友
 */
public class IFriendsSearchContract {

    interface IView {
        void onLoading();
        void onLoadingDone();
        void onShowTips(String str);

        void onSearchResults(SearchFriendsResultBean.DataBean data);
        void onSearchNothing();

        void onRequestAddFriendsSuccess();
        void onRequestAddFriendsFailed();

        void onFriendsInfoResults(FriendInfoBean.DataBean data);
        void onFriendsThumbAction(int type);
    }

    interface IPresenter
    {
        void searchFriends(String searchContent);
        void requestAddFriends();

        void getFriendsInfoResults(int user_id);

        /**
         * 点赞和鼓励
         * @param type 1：点赞，2：鼓励
         */
        void setFriendsThumbAction(int type);
    }
}
