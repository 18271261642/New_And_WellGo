package com.sn.app.net.data.app.bean;

import java.util.List;

/**
 * 作者:东芝(2018/8/9).
 * 功能:朋友请求加我
 */

public class FriendInvitesBean {

    /**
     * ret : 0
     * message :
     * timestamp : 1533780345
     * data : [{"id":1,"inviter_id":113267,"invitee_id":698,"invite_time":1533803967,"content":"","status":1,"inviter":{"id":113267,"country_code":"","phone":"","email":"","nickname":"vfdx","address":"","sign":"","birthday":"2018-07-24","job":"","height":160,"weight":50,"gender":1,"portrait":"","wallpaper":"","target_sleep":0,"target_step":10000,"max_step":0,"max_reach_times":0,"max_reach_date":0,"create_time":"2018-07-24 03:18:58","phone_uuid":"3d7e831def6d6ae826b25298","last_device":null,"last_weight_time":"2018-07-24"}}]
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
         * id : 1
         * inviter_id : 113267
         * invitee_id : 698
         * invite_time : 1533803967
         * content :
         * status : 1
         * inviter : {"id":113267,"country_code":"","phone":"","email":"","nickname":"vfdx","address":"","sign":"","birthday":"2018-07-24","job":"","height":160,"weight":50,"gender":1,"portrait":"","wallpaper":"","target_sleep":0,"target_step":10000,"max_step":0,"max_reach_times":0,"max_reach_date":0,"create_time":"2018-07-24 03:18:58","phone_uuid":"3d7e831def6d6ae826b25298","last_device":null,"last_weight_time":"2018-07-24"}
         */

        private int id;
        private int inviter_id;
        private int invitee_id;
        private int invite_time;
        private String content;
        private int status;
        private UserMessageBean.DataBean inviter;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getInviter_id() {
            return inviter_id;
        }

        public void setInviter_id(int inviter_id) {
            this.inviter_id = inviter_id;
        }

        public int getInvitee_id() {
            return invitee_id;
        }

        public void setInvitee_id(int invitee_id) {
            this.invitee_id = invitee_id;
        }

        public int getInvite_time() {
            return invite_time;
        }

        public void setInvite_time(int invite_time) {
            this.invite_time = invite_time;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public UserMessageBean.DataBean getInviter() {
            return inviter;
        }

        public void setInviter(UserMessageBean.DataBean inviter) {
            this.inviter = inviter;
        }


    }
}
