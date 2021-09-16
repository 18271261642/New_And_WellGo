package com.truescend.gofit.pagers.friends.list;


import com.sn.app.net.data.app.bean.FriendInvitesBean;
import com.sn.app.net.data.app.bean.FriendListBean;
import com.sn.app.net.data.app.bean.SystemMessageBean;

import java.util.List;

/**
 * 作者:东芝(2018/08/08).
 * 功能:好友列表
 */
public class IFriendsListContract {

    interface IView {
        void onLoading();

        void onLoadingDone();

        void onShowTips(String str);

        void onLoadMessageList(SystemMessageBean.DataBean.MetaBean meta, List<SystemMessageBean.DataBean.ItemsBean> data);

        void onLoadFriendRequest(List<FriendInvitesBean.DataBean> data);

        void onLoadFriendList(List<FriendListBean.DataBean> data);

        void onFriendAgreed(int position);

        void onFriendIgnore(int position);

        void onFriendDelete(int position);

        void onFriendRefused(int position);

        void onFriendRemark(int position,String newRemark);

        void onFinishRefresh();


        void onRefreshBadgeStatus();
    }

    interface IPresenter {

        void loadBadgeStatus();

        void loadMessageList();

        void loadFriendRequest();

        void loadFriendList();

        /**
         * 成为朋友
         *
         * @param position
         * @param invite_id 邀请的ID
         */
        void friendAgreed(int position, int invite_id);

        /**
         * 忽略朋友请求
         *
         * @param position
         * @param invite_id 邀请的ID
         */
        void friendIgnore(int position, int invite_id);

        /**
         * 删除朋友
         *
         * @param position
         * @param friend_user_id
         */
        void friendDelete(int position, int friend_user_id);

        /**
         * 拒绝成为朋友
         *
         * @param position
         * @param invite_id 邀请的ID
         */
        void friendRefused(int position, int invite_id);

        /**
         * 删除朋友
         *
         * @param position
         * @param friend_user_id 朋友id
         * @param newRemark 新备注
         */
        void friendRemark(int position, int friend_user_id, String newRemark);

    }
}
