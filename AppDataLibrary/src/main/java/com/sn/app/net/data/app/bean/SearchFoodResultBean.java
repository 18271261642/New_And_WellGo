package com.sn.app.net.data.app.bean;

import java.util.List;

/**
 * 作者:东芝(2018/11/22).
 * 功能:搜索食物结果
 */

public class SearchFoodResultBean {

    /**
     * ret : 0
     * message :
     * timestamp : 1542852826
     * data : [{"food_id":14,"food_alias_id":18,"name":"鸡","state":1,"calory":"70.0000"},{"food_id":12,"food_alias_id":16,"name":"鸡爪","state":1,"calory":"35.0000"},{"food_id":11,"food_alias_id":15,"name":"鸡翅","state":1,"calory":"25.0000"},{"food_id":8,"food_alias_id":12,"name":"鸡肉","state":1,"calory":"25.0000"},{"food_id":10,"food_alias_id":14,"name":"鸡胸肉","state":1,"calory":"30.0000"}]
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
         * food_id : 14
         * food_alias_id : 18
         * name : 鸡
         * state : 1
         * calory : 70.0000
         */

        private int food_id;
        private int food_alias_id;
        private String name;
        private int state;
        private float calory;

        public int getFood_id() {
            return food_id;
        }

        public void setFood_id(int food_id) {
            this.food_id = food_id;
        }

        public int getFood_alias_id() {
            return food_alias_id;
        }

        public void setFood_alias_id(int food_alias_id) {
            this.food_alias_id = food_alias_id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        public float getCalory() {
            return calory;
        }

        public void setCalory(float calory) {
            this.calory = calory;
        }
    }
}
