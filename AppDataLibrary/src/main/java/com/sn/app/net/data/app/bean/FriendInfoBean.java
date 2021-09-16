package com.sn.app.net.data.app.bean;

/**
 * 作者:东芝(2018/8/15).
 * 功能:朋友信息
 */

public class FriendInfoBean {

    /**
     * ret : 0
     * message :
     * timestamp : 1534271626
     * data : {"id":31103,"nickname":"黄建华","gender":1,"portrait":"http://api.core.iwear88.com/uploads/SH_lFQ4nVYfREP5Y.jpg","is_self":0,"is_friend":1,"friendship":{"user_id":698,"friend_user_id":31103,"remark":"哈哈123","create_time":"2018-08-14 10:06:40"},"is_thumb":0,"is_encourage":0,"stat":{"user_id":31103,"bp_history":"","finish_days":"","heart_history":"","max_step":"","max_month":"","max_week":"","ox_history":"","sleep_history":"","sport_history":"","today_avg":"","today_calory":"","today_dbp_avg":"","today_deeps":"","today_distance":"","today_duration":"","today_lights":"","today_highest":"","today_lowest":"","today_ox_avg":"","today_ox_max":"","today_ox_min":"","today_sbp_avg":"","today_sleeps":"","today_step":"","today_wakes":"","sync_time":null}}
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
         * id : 31103
         * nickname : 黄建华
         * gender : 1
         * portrait : http://api.core.iwear88.com/uploads/SH_lFQ4nVYfREP5Y.jpg
         * is_self : 0
         * is_friend : 1
         * friendship : {"user_id":698,"friend_user_id":31103,"remark":"哈哈123","create_time":"2018-08-14 10:06:40"}
         * is_thumb : 0
         * is_encourage : 0
         * stat : {"user_id":31103,"bp_history":"","finish_days":"","heart_history":"","max_step":"","max_month":"","max_week":"","ox_history":"","sleep_history":"","sport_history":"","today_avg":"","today_calory":"","today_dbp_avg":"","today_deeps":"","today_distance":"","today_duration":"","today_lights":"","today_highest":"","today_lowest":"","today_ox_avg":"","today_ox_max":"","today_ox_min":"","today_sbp_avg":"","today_sleeps":"","today_step":"","today_wakes":"","sync_time":null}
         */

        private int id;
        private String nickname;
        private int gender;
        private String portrait;
        private int is_self;
        private int is_friend;
        private FriendshipBean friendship;
        private int is_thumb;
        private int is_encourage;
        private StatBean stat;

        public void setId(int id) {
            this.id = id;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public void setGender(int gender) {
            this.gender = gender;
        }

        public void setPortrait(String portrait) {
            this.portrait = portrait;
        }

        public void setIs_self(int is_self) {
            this.is_self = is_self;
        }

        public void setIs_friend(int is_friend) {
            this.is_friend = is_friend;
        }

        public void setFriendship(FriendshipBean friendship) {
            this.friendship = friendship;
        }

        public void setIs_thumb(int is_thumb) {
            this.is_thumb = is_thumb;
        }

        public void setIs_encourage(int is_encourage) {
            this.is_encourage = is_encourage;
        }

        public void setStat(StatBean stat) {
            this.stat = stat;
        }

        public int getId() {
            return id;
        }

        public String getNickname() {
            return nickname;
        }

        public int getGender() {
            return gender;
        }

        public String getPortrait() {
            return portrait;
        }

        public int getIs_self() {
            return is_self;
        }

        public int getIs_friend() {
            return is_friend;
        }

        public FriendshipBean getFriendship() {
            return friendship;
        }

        public int getIs_thumb() {
            return is_thumb;
        }

        public int getIs_encourage() {
            return is_encourage;
        }

        public StatBean getStat() {
            return stat;
        }

    }
}
