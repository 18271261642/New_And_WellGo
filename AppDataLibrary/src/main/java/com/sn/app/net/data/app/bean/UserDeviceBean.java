package com.sn.app.net.data.app.bean;

/**
 * 作者:东芝(2018/4/16).
 * 功能:
 */

public class UserDeviceBean {

    /**
     * ret : 0
     * message :
     * timestamp : 1523822360
     * data : {"user_id":18404,"update_time":"2018-04-16 03:59:20","mac":"DE:1A:0D:98:60:DE","device_name":"X1pro","function":"29","adv_id":"2333","uuid":"","adv_service":"","id":2289}
     */

    private int ret;
    private String message;
    private int timestamp;
    private DataBean data;

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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * user_id : 18404
         * update_time : 2018-04-16 03:59:20
         * mac : DE:1A:0D:98:60:DE
         * device_name : X1pro
         * function : 29
         * adv_id : 2333
         * uuid :
         * adv_service :
         * id : 2289
         */

        private int user_id;
        private String update_time;
        private String mac;
        private String device_name;
        private String function;
        private String adv_id;
        private String uuid;
        private String adv_service;
        private int id;

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public String getUpdate_time() {
            return update_time;
        }

        public void setUpdate_time(String update_time) {
            this.update_time = update_time;
        }

        public String getMac() {
            return mac;
        }

        public void setMac(String mac) {
            this.mac = mac;
        }

        public String getDevice_name() {
            return device_name;
        }

        public void setDevice_name(String device_name) {
            this.device_name = device_name;
        }

        public String getFunction() {
            return function;
        }

        public void setFunction(String function) {
            this.function = function;
        }

        public String getAdv_id() {
            return adv_id;
        }

        public void setAdv_id(String adv_id) {
            this.adv_id = adv_id;
        }

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public String getAdv_service() {
            return adv_service;
        }

        public void setAdv_service(String adv_service) {
            this.adv_service = adv_service;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}
