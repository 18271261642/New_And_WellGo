package com.sn.app.net.data.app.bean;

/**
 * 作者:东芝(2018/11/23).
 * 功能:餐 列表
 */

public class MealSingleBean {


    /**
     * ret : 0
     * message :
     * timestamp : 1543168433
     * data : {"id":106,"user_id":233082,"date":"2018-11-26","calory":"165.0","foods":[{"amount":"1","calory":"35","name":"鸡爪","unit":"kg"},{"amount":"2","calory":"130","name":"牛筋","unit":"kg"}],"create_time":"2018-11-26 01:53:53"}
     */

    private int ret;
    private String message;
    private int timestamp;
    private MealBean data;

    public void setRet(int ret) {
        this.ret = ret;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public void setData(MealBean data) {
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

    public MealBean getData() {
        return data;
    }

}
