package com.sn.app.net.data.app.bean;

/**
 * 作者:东芝(2018/8/27).
 * 功能:
 */

public class UnreadNumberBean {

    /**
     * ret : 0
     * message :
     * timestamp : 1535306616
     * data : {"is_new_invite":0,"unread_count":30}
     */

    private int ret;
    private String message;
    private int timestamp;
    private DataBean data;

    public void setRet(int ret) {
        this.ret = ret;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public int getRet() {
        return ret;
    }

    public String getMessage() {
        return message;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public DataBean getData() {
        return data;
    }

    public static class DataBean {
        /**
         * is_new_invite : 0
         * unread_count : 30
         */

        private int is_new_invite;
        private int unread_count;

        public void setIs_new_invite(int is_new_invite) {
            this.is_new_invite = is_new_invite;
        }

        public void setUnread_count(int unread_count) {
            this.unread_count = unread_count;
        }

        public int getIs_new_invite() {
            return is_new_invite;
        }

        public int getUnread_count() {
            return unread_count;
        }
    }
}
