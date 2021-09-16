package com.sn.app.net.data.app.bean;

/**
 * 作者:东芝(2017/7/31).
 * 功能:注册和登录共用的bean
 */

public class SignBean {

    /**
     * ret : 0
     * message :
     * timestamp : 1501456487
     * data : {"id":1,"email":"364087825@qq.com","is_first":0,"access_token":"l8dkuRgcPZTgFA9S13Aj-Ep2TGXtrFY-"}
     */

    private int ret;
    private String message;
    private DataBean data;
    private int timestamp;

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

    public DataBean getData() {
        return data;
    }


    public void setData(DataBean data) {
        this.data = data;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public static class DataBean {
        /**
         * id : 1
         * email : 364087825@qq.com
         * is_first : 0
         * access_token : l8dkuRgcPZTgFA9S13Aj-Ep2TGXtrFY-
         */

        private int id;
        private String email;
        private int is_first;//	 是否首次登陆，1：是，0：否
        private String access_token;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public int getIs_first() {
            return is_first;
        }

        /**
         * 是否首次登陆，1：是，0：否
         * @return
         */
        public boolean isFirst() {
            return is_first==1;
        }
        public void setIs_first(int is_first) {
            this.is_first = is_first;
        }

        public String getAccess_token() {
            return access_token;
        }

        public void setAccess_token(String access_token) {
            this.access_token = access_token;
        }
    }
}
