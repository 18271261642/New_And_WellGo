package com.sn.blesdk.net.bean;

import java.util.List;

/**
 * 作者:东芝(2017/8/2).
 * 描述:心率
 */
public class HealthNetBean {

    /**
     * ret : 0
     * message :
     * timestamp : 1517191321
     * data : [{"user_id":52,"type":2,"max":98,"min":57,"average":81,"times":10,"date":"2018-01-27","data":"","mac":""},{"user_id":52,"type":2,"max":99,"min":69,"average":6,"times":14,"date":"2018-01-28","data":"[{\"time\":\"14:45\",\"value\":74},{\"time\":\"14:49\",\"value\":78},{\"time\":\"14:50\",\"value\":87},{\"time\":\"14:53\",\"value\":94},{\"time\":\"15:00\",\"value\":69},{\"time\":\"15:15\",\"value\":72},{\"time\":\"15:30\",\"value\":98},{\"time\":\"15:45\",\"value\":72},{\"time\":\"16:00\",\"value\":75},{\"time\":\"16:15\",\"value\":80},{\"time\":\"16:30\",\"value\":81}]","mac":""},{"user_id":52,"type":2,"max":130,"min":78,"average":24,"times":4,"date":"2018-01-29","data":"[{\"time\":\"14:49\",\"value\":78},{\"time\":\"14:50\",\"value\":87},{\"time\":\"14:53\",\"value\":94},{\"time\":\"17:30\",\"value\":130}]","mac":"81:FA:CA:DE:AD:13"}]
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
         * user_id : 52
         * type : 2
         * max : 98
         * min : 57
         * average : 81
         * times : 10
         * date : 2018-01-27
         * data :
         * mac :
         */

        private int user_id;
        private int type;
        private int max;
        private int min;
        private int average;
        private int times;
        private String date;
        private String data;
        private String mac;

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getMax() {
            return max;
        }

        public void setMax(int max) {
            this.max = max;
        }

        public int getMin() {
            return min;
        }

        public void setMin(int min) {
            this.min = min;
        }

        public int getAverage() {
            return average;
        }

        public void setAverage(int average) {
            this.average = average;
        }

        public int getTimes() {
            return times;
        }

        public void setTimes(int times) {
            this.times = times;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public String getMac() {
            return mac;
        }

        public void setMac(String mac) {
            this.mac = mac;
        }
    }
}
