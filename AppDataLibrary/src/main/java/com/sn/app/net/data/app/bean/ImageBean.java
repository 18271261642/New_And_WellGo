package com.sn.app.net.data.app.bean;

import java.util.List;

/**
 * 作者:东芝(2017/8/1).
 * 功能:图片上传
 */

public class ImageBean {

    /**
     * ret : 0
     * message :
     * timestamp : 1501541142
     * data : {"urls":["http://119.23.8.182:8081/uploads/HdKzeDjGK71x09oM.jpg"]}
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
        private List<String> urls;

        public List<String> getUrls() {
            return urls;
        }

        public void setUrls(List<String> urls) {
            this.urls = urls;
        }
    }
}
