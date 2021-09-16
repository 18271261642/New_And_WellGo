package com.sn.app.net.data.app.bean;

import java.io.Serializable;

/**
 * 作者:东芝(2019/6/18).
 * 功能:
 */
public class TmallGenieAuthCodeBean {

    private int ret;
    private String message;
    private int timestamp;
    private TmallGenieAuthCodeBean.DataBean data;

    public void setRet(int ret) {
        this.ret = ret;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public void setData(TmallGenieAuthCodeBean.DataBean data) {
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

    public TmallGenieAuthCodeBean.DataBean getData() {
        return data;
    }

    public static class DataBean implements Serializable {
        private String auth_code;

        public String getAuth_code() {
            return auth_code;
        }

        public void setAuth_code(String auth_code) {
            this.auth_code = auth_code;
        }
    }
}
