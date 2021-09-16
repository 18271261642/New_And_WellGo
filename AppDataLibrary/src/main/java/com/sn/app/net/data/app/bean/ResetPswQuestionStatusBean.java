package com.sn.app.net.data.app.bean;

import java.util.List;

/**
 * 作者:东芝(2018/6/14).
 * 功能:
 */

public class ResetPswQuestionStatusBean {

    /**
     * ret : 0
     * message :
     * timestamp : 1528419531
     * data : {"is_success":0,"errors":["问题2"]}
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
         * is_success : 0
         * errors : ["问题2"]
         */

        private int is_success;
        private List<String> errors;

        public void setIs_success(int is_success) {
            this.is_success = is_success;
        }

        public void setErrors(List<String> errors) {
            this.errors = errors;
        }

        public int getIs_success() {
            return is_success;
        }

        public List<String> getErrors() {
            return errors;
        }
    }
}
