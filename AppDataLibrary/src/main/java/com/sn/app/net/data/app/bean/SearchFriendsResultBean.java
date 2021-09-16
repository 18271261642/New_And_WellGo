package com.sn.app.net.data.app.bean;

import java.util.List;

/**
 * 作者:东芝(2018/8/9).
 * 功能:搜索朋友结果
 */

public class SearchFriendsResultBean {

    /**
     * ret : 0
     * message :
     * timestamp : 1533372909
     * data : [{"user_id":"1","nickname":"EH","portrait":"","gender":"1","is_friend":1,"friendship":{"user_id":93913,"friend_user_id":1,"remark":"小可爱","create_time":"2018-08-04 13:33:07"}}]
     */

    private int ret;
    private String message;
    private int timestamp;
    private List<DataBean> data;

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * user_id : 1
         * nickname : EH
         * portrait :
         * gender : 1
         * is_friend : 1
         * friendship : {"user_id":93913,"friend_user_id":1,"remark":"小可爱","create_time":"2018-08-04 13:33:07"}
         */

        private int user_id;
        private String nickname;
        private String portrait;
        private int    gender;
        private int is_friend;
        private FriendshipBean friendship;

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getPortrait() {
            return portrait;
        }

        public void setPortrait(String portrait) {
            this.portrait = portrait;
        }

        public int getGender() {
            return gender;
        }

        public void setGender(int gender) {
            this.gender = gender;
        }

        public int getIs_friend() {
            return is_friend;
        }

        public void setIs_friend(int is_friend) {
            this.is_friend = is_friend;
        }

        public FriendshipBean getFriendship() {
            return friendship;
        }

        public void setFriendship(FriendshipBean friendship) {
            this.friendship = friendship;
        }


    }
}
