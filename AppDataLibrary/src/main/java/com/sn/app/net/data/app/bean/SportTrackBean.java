package com.sn.app.net.data.app.bean;

import java.util.List;

/**
 * 作者:东芝(2018/3/1).
 * 功能:运动轨迹
 */

public class SportTrackBean {

    /**
     * ret : 0
     * message :
     * timestamp : 1519858531
     * data : [{"id":24,"user_id":698,"date":"2018-03-01 14:55:27","distance":6762.6586914,"duration":30,"average_speed":0,"average_pace":0,"fast_speed":0,"cal":490428,"track_image":"http://api.core.iwear88.com/uploads/9prh00BPDZw9Lzf6.jpg"}]
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
         * id : 24
         * user_id : 698
         * date : 2018-03-01 14:55:27
         * distance : 6762.6586914
         * duration : 30
         * average_speed : 0.0
         * average_pace : 0
         * fast_speed : 0.0
         * cal : 490428
         * location :
         * track_image : http://api.core.iwear88.com/uploads/9prh00BPDZw9Lzf6.jpg
         */

        private int id;
        private int user_id;
        private String date;
        private double distance;
        private int duration;
        private double average_speed;
        private int average_pace;
        private double fast_speed;
        private int cal;
        private String track_image;
        private String location;
        private String data;

        public String getData() {
            return data;
        }



        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public double getDistance() {
            return distance;
        }

        public void setDistance(double distance) {
            this.distance = distance;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public double getAverage_speed() {
            return average_speed;
        }

        public void setAverage_speed(double average_speed) {
            this.average_speed = average_speed;
        }

        public int getAverage_pace() {
            return average_pace;
        }

        public void setAverage_pace(int average_pace) {
            this.average_pace = average_pace;
        }

        public double getFast_speed() {
            return fast_speed;
        }

        public void setFast_speed(double fast_speed) {
            this.fast_speed = fast_speed;
        }

        public int getCal() {
            return cal;
        }

        public void setCal(int cal) {
            this.cal = cal;
        }

        public String getTrack_image() {
            return track_image;
        }

        public void setTrack_image(String track_image) {
            this.track_image = track_image;
        }
    }
}
