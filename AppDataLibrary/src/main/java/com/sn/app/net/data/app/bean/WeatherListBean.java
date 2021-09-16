package com.sn.app.net.data.app.bean;

import java.util.List;

public class WeatherListBean {
    private int ret;
    private String message;
    private int timestamp;
    private List<WeatherBean.DataBean> data;

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

    public List<WeatherBean.DataBean> getData() {
        return data;
    }

    public void setData(List<WeatherBean.DataBean> data) {
        this.data = data;
    }
}
