package com.truescend.gofit.utils;

import com.sn.app.net.AppNetReq;
import com.sn.app.net.callback.OnResponseListener;
import com.sn.app.net.data.app.bean.FriendInvitesBean;
import com.sn.app.net.data.app.bean.UnreadNumberBean;
import com.sn.app.storage.UserStorage;

/**
 * 作者:东芝(2018/8/27).
 * 功能:未读消息,分为通知未读消息 和 好友请求未读消息
 */

public class UnreadMessages {

    /**
     * 设置全部通知已读
     */
    public static void setReadAllNewSysMessage( ) {
        setReadAllNewSysMessage(null);
    }
    /**
     * 设置全部通知已读
     */
    public static void setReadAllNewSysMessage(final OnUnreadMessagesRefreshListener listener) {
        AppNetReq.getApi().setReadAllNewSysMessage().enqueue(new OnResponseListener<String>() {
            @Override
            public void onResponse(String body) throws Throwable {
                UserStorage.setNewSysMessageUnreadNumber(0);
                if (listener != null) {
                    listener.onDone();
                }
            }

            @Override
            public void onFailure(int ret, String msg) {
                if (listener != null) {
                    listener.onFail();
                }
            }
        });

    }

    /**
     * 设置全部好友请求已读
     */
    public static void setReadAllNewFriendsRequestMessage() {
        setReadAllNewFriendsRequestMessage(null);
    }
        /**
         * 设置全部好友请求已读
         */
    public static void setReadAllNewFriendsRequestMessage(final OnUnreadMessagesRefreshListener listener) {
        UserStorage.setNewFriendRequestUnreadNumber(0);
        if (listener != null) {
            listener.onDone();
        }
    }


    /**
     * 有消息未读
     *
     * @return
     */
    public static boolean hasUnreadMessage() {
        return getNewFriendRequestUnreadNumber() + getNewSysMessageUnreadNumber() > 0;
    }


    /**
     * 使用上一次调用邀请列表时的第一条的invite_time，首次传0
     *
     * @param body
     */
    public static void setLastInviteTime(FriendInvitesBean body) {
        try {
            UserStorage.setLastInviteTime(body.getData().get(0).getInvite_time());
        } catch (Exception ignored) {
        }
    }

    /**
     * 好友请求未读数
     *
     * @return
     */
    public static int getNewFriendRequestUnreadNumber() {
        return UserStorage.getNewFriendRequestUnreadNumber();
    }

    /**
     * 系统消息未读数
     *
     * @return
     */
    public static int getNewSysMessageUnreadNumber() {
        return UserStorage.getNewSysMessageUnreadNumber();
    }

    /**
     * 刷新未读数
     */
    public static void refreshUnreadNumber() {
        refreshUnreadNumber(null);
    }

    public static void refreshUnreadNumber(final OnUnreadMessagesRefreshListener listener) {
        AppNetReq.getApi().hasNewFriendRequestUnreadNumber(UserStorage.getLastInviteTime()).enqueue(new OnResponseListener<UnreadNumberBean>() {
            @Override
            public void onResponse(UnreadNumberBean body) throws Throwable {
                try {
                    UserStorage.setNewFriendRequestUnreadNumber(body.getData().getIs_new_invite());
                } catch (Exception ignored) {
                }
                AppNetReq.getApi().hasNewSysMessageUnreadNumber().enqueue(new OnResponseListener<UnreadNumberBean>() {
                    @Override
                    public void onResponse(UnreadNumberBean body) throws Throwable {
                        try {
                            UserStorage.setNewSysMessageUnreadNumber(body.getData().getUnread_count());
                        } catch (Exception ignored) {
                        }
                        if (listener != null) {
                            listener.onDone();
                        }
                    }

                    @Override
                    public void onFailure(int ret, String msg) {
                        if (listener != null) {
                            listener.onFail();
                        }
                    }
                });
            }

            @Override
            public void onFailure(int ret, String msg) {
                if (listener != null) {
                    listener.onDone();
                }
            }
        });
    }

    public interface OnUnreadMessagesRefreshListener {
        void onDone();

        void onFail();
    }
}
