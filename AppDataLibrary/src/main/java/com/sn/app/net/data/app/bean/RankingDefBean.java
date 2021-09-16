package com.sn.app.net.data.app.bean;

import java.util.List;

/**
 * 作者:东芝(2019/1/16).
 * 功能:排行对象
 */

public class RankingDefBean {

    /**
     * ret : 0
     * message :
     * timestamp : 1547574914
     * data : [{"id":1080,"section":"0-5h","left":"0.00","right":"5.00","value":864,"type":25,"create_time":"2019-01-15 17:00:01","update_time":"2019-01-15 17:00:01"},{"id":1081,"section":"5-6h","left":"5.00","right":"6.00","value":864,"type":25,"create_time":"2019-01-15 17:00:01","update_time":"2019-01-15 17:00:01"},{"id":1082,"section":"6-7h","left":"6.00","right":"7.00","value":3456,"type":25,"create_time":"2019-01-15 17:00:01","update_time":"2019-01-15 17:00:01"},{"id":1083,"section":"7-8h","left":"7.00","right":"8.00","value":3024,"type":25,"create_time":"2019-01-15 17:00:01","update_time":"2019-01-15 17:00:01"},{"id":1084,"section":"8-9h","left":"8.00","right":"9.00","value":2016,"type":25,"create_time":"2019-01-15 17:00:01","update_time":"2019-01-15 17:00:01"},{"id":1085,"section":"9-10h","left":"9.00","right":"10.00","value":1584,"type":25,"create_time":"2019-01-15 17:00:01","update_time":"2019-01-15 17:00:01"},{"id":1086,"section":"10-11h","left":"10.00","right":"11.00","value":2016,"type":25,"create_time":"2019-01-15 17:00:01","update_time":"2019-01-15 17:00:01"},{"id":1087,"section":">11h","left":"11.00","right":"24.00","value":576,"type":25,"create_time":"2019-01-15 17:00:01","update_time":"2019-01-15 17:00:01"}]
     */

    private int ret;
    private String message;
    private int timestamp;
    private List<DataBean> data;

    public void setRet(int ret) {
        this.ret = ret;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public void setData(List<DataBean> data) {
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

    public List<DataBean> getData() {
        return data;
    }

    public static class DataBean {
        /**
         * id : 1080
         * section : 0-5h
         * left : 0.00
         * right : 5.00
         * value : 864
         * type : 25
         * create_time : 2019-01-15 17:00:01
         * update_time : 2019-01-15 17:00:01
         */

        private int id;
        private String section;
        private float left;
        private float right;
        private int value;
        private int type;
        private String create_time;
        private String update_time;

        public void setId(int id) {
            this.id = id;
        }

        public void setSection(String section) {
            this.section = section;
        }

        public void setLeft(float left) {
            this.left = left;
        }

        public void setRight(float right) {
            this.right = right;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public void setType(int type) {
            this.type = type;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }

        public void setUpdate_time(String update_time) {
            this.update_time = update_time;
        }

        public int getId() {
            return id;
        }

        public String getSection() {
            return section;
        }

        public float getLeft() {
            return left;
        }

        public float getRight() {
            return right;
        }

        public int getValue() {
            return value;
        }

        public int getType() {
            return type;
        }

        public String getCreate_time() {
            return create_time;
        }

        public String getUpdate_time() {
            return update_time;
        }
    }
}
