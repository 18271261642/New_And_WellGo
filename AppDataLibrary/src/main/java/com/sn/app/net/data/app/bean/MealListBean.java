package com.sn.app.net.data.app.bean;

import java.util.List;

/**
 * 作者:东芝(2018/11/23).
 * 功能:餐 列表
 */

public class MealListBean {


    /**
     * ret : 0
     * message :
     * timestamp : 1543173848
     * data : {"user_id":233082,"date":"2018-11-26","calory":"8910.00","goal_calory":"1920.00","weight":"80.00","goal_weight":"31.00","meals":[{"id":99,"user_id":233082,"date":"2018-11-26","calory":370,"foods":[{"amount":"5","calory":"175","name":"鸡爪","unit":"kg"},{"amount":"3","calory":"195","name":"牛筋","unit":"kg"}],"create_time":"2018-11-26 01:46:27"},{"id":100,"user_id":233082,"date":"2018-11-26","calory":370,"foods":[{"amount":"5","calory":"175","name":"鸡爪","unit":"kg"},{"amount":"3","calory":"195","name":"牛筋","unit":"kg"}],"create_time":"2018-11-26 01:46:30"},{"id":101,"user_id":233082,"date":"2018-11-26","calory":370,"foods":[{"amount":"5","calory":"175","name":"鸡爪","unit":"kg"},{"amount":"3","calory":"195","name":"牛筋","unit":"kg"}],"create_time":"2018-11-26 01:46:59"},{"id":102,"user_id":233082,"date":"2018-11-26","calory":370,"foods":[{"amount":"5","calory":"175","name":"鸡爪","unit":"kg"},{"amount":"3","calory":"195","name":"牛筋","unit":"kg"}],"create_time":"2018-11-26 01:47:16"},{"id":103,"user_id":233082,"date":"2018-11-26","calory":370,"foods":[{"amount":"5","calory":"175","name":"鸡爪","unit":"kg"},{"amount":"3","calory":"195","name":"牛筋","unit":"kg"}],"create_time":"2018-11-26 01:47:17"},{"id":104,"user_id":233082,"date":"2018-11-26","calory":165,"foods":[{"amount":"1","calory":"35","name":"鸡爪","unit":"kg"},{"amount":"2","calory":"130","name":"牛筋","unit":"kg"}],"create_time":"2018-11-26 01:53:45"},{"id":105,"user_id":233082,"date":"2018-11-26","calory":165,"foods":[{"amount":"1","calory":"35","name":"鸡爪","unit":"kg"},{"amount":"2","calory":"130","name":"牛筋","unit":"kg"}],"create_time":"2018-11-26 01:53:50"},{"id":106,"user_id":233082,"date":"2018-11-26","calory":165,"foods":[{"amount":"1","calory":"35","name":"鸡爪","unit":"kg"},{"amount":"2","calory":"130","name":"牛筋","unit":"kg"}],"create_time":"2018-11-26 01:53:53"},{"id":107,"user_id":233082,"date":"2018-11-26","calory":245,"foods":[{"amount":"2","calory":"140","name":"鸡","unit":"kg"},{"amount":"3","calory":"105","name":"鸡爪","unit":"kg"}],"create_time":"2018-11-26 01:58:58"},{"id":108,"user_id":233082,"date":"2018-11-26","calory":245,"foods":[{"amount":"2","calory":"140","name":"鸡","unit":"kg"},{"amount":"3","calory":"105","name":"鸡爪","unit":"kg"}],"create_time":"2018-11-26 01:59:13"},{"id":109,"user_id":233082,"date":"2018-11-26","calory":265,"foods":[{"amount":"2","calory":"70","name":"鸡爪","unit":"kg"},{"amount":"3","calory":"195","name":"牛筋","unit":"kg"}],"create_time":"2018-11-26 02:03:07"},{"id":110,"user_id":233082,"date":"2018-11-26","calory":365,"foods":[{"amount":"3","calory":"105","name":"鸡爪","unit":"kg"},{"amount":"4","calory":"260","name":"牛筋","unit":"kg"}],"create_time":"2018-11-26 02:07:29"},{"id":111,"user_id":233082,"date":"2018-11-26","calory":915,"foods":[{"amount":12,"calory":840,"name":"鸡","unit":"kg"},{"amount":3,"calory":75,"name":"鸡翅","unit":"kg"}],"create_time":"2018-11-26 02:12:20"},{"id":112,"user_id":233082,"date":"2018-11-26","calory":915,"foods":[{"amount":12,"calory":840,"name":"鸡","unit":"kg"},{"amount":3,"calory":75,"name":"鸡翅","unit":"kg"}],"create_time":"2018-11-26 02:12:22"},{"id":113,"user_id":233082,"date":"2018-11-26","calory":3615,"foods":[{"amount":3,"calory":105,"name":"鸡爪","unit":"kg"},{"amount":54,"calory":3510,"name":"牛筋","unit":"kg"}],"create_time":"2018-11-26 02:20:41"}]}
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
         * user_id : 233082
         * date : 2018-11-26
         * calory : 8910.00
         * goal_calory : 1920.00
         * weight : 80.00
         * goal_weight : 31.00
         * meals : [{"id":99,"user_id":233082,"date":"2018-11-26","calory":370,"foods":[{"amount":"5","calory":"175","name":"鸡爪","unit":"kg"},{"amount":"3","calory":"195","name":"牛筋","unit":"kg"}],"create_time":"2018-11-26 01:46:27"},{"id":100,"user_id":233082,"date":"2018-11-26","calory":370,"foods":[{"amount":"5","calory":"175","name":"鸡爪","unit":"kg"},{"amount":"3","calory":"195","name":"牛筋","unit":"kg"}],"create_time":"2018-11-26 01:46:30"},{"id":101,"user_id":233082,"date":"2018-11-26","calory":370,"foods":[{"amount":"5","calory":"175","name":"鸡爪","unit":"kg"},{"amount":"3","calory":"195","name":"牛筋","unit":"kg"}],"create_time":"2018-11-26 01:46:59"},{"id":102,"user_id":233082,"date":"2018-11-26","calory":370,"foods":[{"amount":"5","calory":"175","name":"鸡爪","unit":"kg"},{"amount":"3","calory":"195","name":"牛筋","unit":"kg"}],"create_time":"2018-11-26 01:47:16"},{"id":103,"user_id":233082,"date":"2018-11-26","calory":370,"foods":[{"amount":"5","calory":"175","name":"鸡爪","unit":"kg"},{"amount":"3","calory":"195","name":"牛筋","unit":"kg"}],"create_time":"2018-11-26 01:47:17"},{"id":104,"user_id":233082,"date":"2018-11-26","calory":165,"foods":[{"amount":"1","calory":"35","name":"鸡爪","unit":"kg"},{"amount":"2","calory":"130","name":"牛筋","unit":"kg"}],"create_time":"2018-11-26 01:53:45"},{"id":105,"user_id":233082,"date":"2018-11-26","calory":165,"foods":[{"amount":"1","calory":"35","name":"鸡爪","unit":"kg"},{"amount":"2","calory":"130","name":"牛筋","unit":"kg"}],"create_time":"2018-11-26 01:53:50"},{"id":106,"user_id":233082,"date":"2018-11-26","calory":165,"foods":[{"amount":"1","calory":"35","name":"鸡爪","unit":"kg"},{"amount":"2","calory":"130","name":"牛筋","unit":"kg"}],"create_time":"2018-11-26 01:53:53"},{"id":107,"user_id":233082,"date":"2018-11-26","calory":245,"foods":[{"amount":"2","calory":"140","name":"鸡","unit":"kg"},{"amount":"3","calory":"105","name":"鸡爪","unit":"kg"}],"create_time":"2018-11-26 01:58:58"},{"id":108,"user_id":233082,"date":"2018-11-26","calory":245,"foods":[{"amount":"2","calory":"140","name":"鸡","unit":"kg"},{"amount":"3","calory":"105","name":"鸡爪","unit":"kg"}],"create_time":"2018-11-26 01:59:13"},{"id":109,"user_id":233082,"date":"2018-11-26","calory":265,"foods":[{"amount":"2","calory":"70","name":"鸡爪","unit":"kg"},{"amount":"3","calory":"195","name":"牛筋","unit":"kg"}],"create_time":"2018-11-26 02:03:07"},{"id":110,"user_id":233082,"date":"2018-11-26","calory":365,"foods":[{"amount":"3","calory":"105","name":"鸡爪","unit":"kg"},{"amount":"4","calory":"260","name":"牛筋","unit":"kg"}],"create_time":"2018-11-26 02:07:29"},{"id":111,"user_id":233082,"date":"2018-11-26","calory":915,"foods":[{"amount":12,"calory":840,"name":"鸡","unit":"kg"},{"amount":3,"calory":75,"name":"鸡翅","unit":"kg"}],"create_time":"2018-11-26 02:12:20"},{"id":112,"user_id":233082,"date":"2018-11-26","calory":915,"foods":[{"amount":12,"calory":840,"name":"鸡","unit":"kg"},{"amount":3,"calory":75,"name":"鸡翅","unit":"kg"}],"create_time":"2018-11-26 02:12:22"},{"id":113,"user_id":233082,"date":"2018-11-26","calory":3615,"foods":[{"amount":3,"calory":105,"name":"鸡爪","unit":"kg"},{"amount":54,"calory":3510,"name":"牛筋","unit":"kg"}],"create_time":"2018-11-26 02:20:41"}]
         */

        private int user_id;
        private String date;
        private float calory;
        private float goal_calory;
        private float weight;
        private float goal_weight;
        private List<MealBean> meals;

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public void setCalory(float calory) {
            this.calory = calory;
        }

        public void setGoal_calory(float goal_calory) {
            this.goal_calory = goal_calory;
        }

        public void setWeight(float weight) {
            this.weight = weight;
        }

        public void setGoal_weight(float goal_weight) {
            this.goal_weight = goal_weight;
        }

        public void setMeals(List<MealBean> meals) {
            this.meals = meals;
        }

        public int getUser_id() {
            return user_id;
        }

        public String getDate() {
            return date;
        }

        public float getCalory() {
            return calory;
        }

        public float getGoal_calory() {
            return goal_calory;
        }

        public float getWeight() {
            return weight;
        }

        public float getGoal_weight() {
            return goal_weight;
        }

        public List<MealBean> getMeals() {
            return meals;
        }

    }
}
