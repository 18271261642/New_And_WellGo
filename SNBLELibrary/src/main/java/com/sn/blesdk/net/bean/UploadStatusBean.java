package com.sn.blesdk.net.bean;

import java.util.List;

/**
 * 作者:东芝(2018/1/21).
 * 功能:上传状态
 */

public class UploadStatusBean {

    /**
     * ret : 0
     * message :
     * timestamp : 1516492945
     * data : {"success_num":1,"success_dates":["2018-01-21"]}
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
         * success_num : 1
         * success_dates : ["2018-01-21"]
         */

        private int success_num;
        private List<String> success_dates;

        public int getSuccess_num() {
            return success_num;
        }

        public void setSuccess_num(int success_num) {
            this.success_num = success_num;
        }

        public List<String> getSuccess_dates() {
            return success_dates;
        }

        public void setSuccess_dates(List<String> success_dates) {
            this.success_dates = success_dates;
        }
    }
}
