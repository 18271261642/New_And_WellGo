package com.sn.app.net.data.base;

import java.util.List;

/**
 * 作者:东芝(2017/7/31).
 * 功能:公共默认响应
 */

public class DefResponseBean {

    /**
     * ret : 0
     * message :
     * timestamp : 1501441520
     * data : []
     */

    private int ret;
    private String message;
    private int timestamp;
    private List<?> data;

    public int getRet() {
        return ret;
    }
    public boolean isSuccessful() {
        return ret==0;
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

    public List<?> getData() {
        return data;
    }

    public void setData(List<?> data) {
        this.data = data;
    }
}
