package com.sn.app.net.data.app.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 作者:东芝(2018/6/14).
 * 功能:问题
 */

public class GetQuestionsBean implements Serializable{

    /**
     * ret : 0
     * message :
     * timestamp : 1528937160
     * data : {"is_set":1,"question":["问题1","问题2"]}
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

    public static class DataBean implements Serializable {
        /**
         * is_set : 1
         * question : ["问题1","问题2"]
         */

        private int is_set;
        private List<String> question;

        public void setIs_set(int is_set) {
            this.is_set = is_set;
        }

        public void setQuestion(List<String> question) {
            this.question = question;
        }

        public int getIs_set() {
            return is_set;
        }

        public List<String> getQuestion() {
            return question;
        }
    }
}
