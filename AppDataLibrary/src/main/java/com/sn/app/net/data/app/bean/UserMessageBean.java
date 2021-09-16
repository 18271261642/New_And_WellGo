package com.sn.app.net.data.app.bean;

/**
 * 作者:东芝(2017/7/31).
 * 功能:网络用户
 */

public class UserMessageBean {

    /**
     * ret : 0
     * message :
     * timestamp : 1520018439
     * data : {"id":17941,"country_code":"","phone":"","email":"dn0520@163.com","nickname":"","address":"","sign":"","birthday":"0000-00-00","job":"","height":0.0,"weight":0.0,"gender":0,"portrait":"","wallpaper":"","target_sleep":0,"target_step":0,"max_step":0,"max_reach_times":2,"max_reach_date":1519862400,"create_time":"2018-02-28 07:45:53","phone_uuid":"","last_device":null,"last_weight_time":"0000-00-00"}
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
         * id : 17941
         * country_code :
         * phone :
         * email : dn0520@163.com
         * nickname :
         * address :
         * sign :
         * birthday : 0000-00-00
         * job :
         * height : 0.0
         * weight : 0.0
         * gender : 0
         * portrait :
         * wallpaper :
         * target_sleep : 0
         * target_step : 0
         * max_step : 0
         * max_reach_times : 2
         * max_reach_date : 1519862400
         * create_time : 2018-02-28 07:45:53
         * phone_uuid :
         * last_device : null
         * last_weight_time : 0000-00-00
         */

        private int id;
        private String country_code;
        private String phone;
        private String email;
        private String nickname;
        private String address;
        private String sign;
        private String birthday;
        private String job;
        private float height;
        private float weight;
        private float goal_weight;//目标体重
        private float goal_calory;//目标卡路里
        private int target_sleep;//目标睡眠
        private int target_step;//目标步数
        private int gender;
        private String portrait;
        private String wallpaper;
        private int max_step;
        private int max_reach_times;
        private int max_reach_date;
        private String create_time;
        private String phone_uuid;
        private UserDeviceBean.DataBean last_device;
        private String last_weight_time;
        private int sport_days;
        private int app_id;
        private String first_meal_date ;
        private int total_meal_day;

        public String getFirst_meal_date() {
            return first_meal_date;
        }

        public void setFirst_meal_date(String first_meal_date) {
            this.first_meal_date = first_meal_date;
        }

        public int getTotal_meal_day() {
            return total_meal_day;
        }

        public void setTotal_meal_day(int total_meal_day) {
            this.total_meal_day = total_meal_day;
        }

        public float getGoal_calory() {
            return goal_calory;
        }

        public void setGoal_calory(float goal_calory) {
            this.goal_calory = goal_calory;
        }

        public float getGoal_weight() {
            return goal_weight;
        }

        public void setGoal_weight(float goal_weight) {
            this.goal_weight = goal_weight;
        }

        public int getApp_id() {
            return app_id;
        }

        public void setApp_id(int app_id) {
            this.app_id = app_id;
        }

        public int getSport_days() {
            return sport_days;
        }

        public void setSport_days(int sport_days) {
            this.sport_days = sport_days;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getCountry_code() {
            return country_code;
        }

        public void setCountry_code(String country_code) {
            this.country_code = country_code;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }

        public String getBirthday() {
            return birthday;
        }

        public void setBirthday(String birthday) {
            this.birthday = birthday;
        }

        public String getJob() {
            return job;
        }

        public void setJob(String job) {
            this.job = job;
        }

        public float getHeight() {
            return height;
        }

        public void setHeight(float height) {
            this.height = height;
        }

        public float getWeight() {
            return weight;
        }

        public void setWeight(float weight) {
            this.weight = weight;
        }

        public int getGender() {
            return gender;
        }

        public void setGender(int gender) {
            this.gender = gender;
        }

        public String getPortrait() {
            return portrait;
        }

        public void setPortrait(String portrait) {
            this.portrait = portrait;
        }

        public String getWallpaper() {
            return wallpaper;
        }

        public void setWallpaper(String wallpaper) {
            this.wallpaper = wallpaper;
        }

        public int getTarget_sleep() {
            return target_sleep;
        }

        public void setTarget_sleep(int target_sleep) {
            this.target_sleep = target_sleep;
        }

        public int getTarget_step() {
            return target_step;
        }

        public void setTarget_step(int target_step) {
            this.target_step = target_step;
        }

        public int getMax_step() {
            return max_step;
        }

        public void setMax_step(int max_step) {
            this.max_step = max_step;
        }

        public int getMax_reach_times() {
            return max_reach_times;
        }

        public void setMax_reach_times(int max_reach_times) {
            this.max_reach_times = max_reach_times;
        }

        public int getMax_reach_date() {
            return max_reach_date;
        }

        public void setMax_reach_date(int max_reach_date) {
            this.max_reach_date = max_reach_date;
        }

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }

        public String getPhone_uuid() {
            return phone_uuid;
        }

        public void setPhone_uuid(String phone_uuid) {
            this.phone_uuid = phone_uuid;
        }

        public UserDeviceBean.DataBean getLast_device() {
            return last_device;
        }

        public void setLast_device(UserDeviceBean.DataBean last_device) {
            this.last_device = last_device;
        }

        public String getLast_weight_time() {
            return last_weight_time;
        }

        public void setLast_weight_time(String last_weight_time) {
            this.last_weight_time = last_weight_time;
        }
    }
}
