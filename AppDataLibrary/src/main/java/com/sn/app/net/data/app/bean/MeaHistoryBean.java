package com.sn.app.net.data.app.bean;

import java.util.List;

/**
 * 作者:东芝(2018/11/28).
 * 功能:餐 历史列表
 */

public class MeaHistoryBean {


    /**
     * ret : 0
     * message :
     * timestamp : 1543355573
     * data : [{"user_id":233082,"date":"2018-11-23","calory":"5115.00","goal_calory":"1920.00","weight":"80.00","goal_weight":"31.00","meals":[{"id":57,"user_id":233082,"date":"2018-11-23","calory":0,"foods":[{"amount":"-1","calory":"-1","name":"鸡","unit":"kg"},{"amount":"-1","calory":"-1","name":"","unit":"kg"}],"create_time":"2018-11-23 08:20:13"},{"id":58,"user_id":233082,"date":"2018-11-23","calory":465,"foods":[{"amount":"2","calory":"140","name":"鸡","unit":"kg"}],"create_time":"2018-11-23 08:32:50"},{"id":59,"user_id":233082,"date":"2018-11-23","calory":465,"foods":[{"amount":"2","calory":"140","name":"鸡","unit":"kg"}],"create_time":"2018-11-23 08:33:04"},{"id":90,"user_id":233082,"date":"2018-11-23","calory":465,"foods":[{"amount":"2","calory":"140","name":"鸡","unit":"kg"}],"create_time":"2018-11-26 01:45:53"},{"id":91,"user_id":233082,"date":"2018-11-23","calory":465,"foods":[{"amount":"2","calory":"140","name":"鸡","unit":"kg"}],"create_time":"2018-11-26 01:45:57"},{"id":92,"user_id":233082,"date":"2018-11-23","calory":465,"foods":[{"amount":"2","calory":"140","name":"鸡","unit":"kg"}],"create_time":"2018-11-26 01:46:00"},{"id":93,"user_id":233082,"date":"2018-11-23","calory":465,"foods":[{"amount":"2","calory":"140","name":"鸡","unit":"kg"}],"create_time":"2018-11-26 01:46:01"},{"id":94,"user_id":233082,"date":"2018-11-23","calory":465,"foods":[{"amount":"2","calory":"140","name":"鸡","unit":"kg"}],"create_time":"2018-11-26 01:46:02"},{"id":95,"user_id":233082,"date":"2018-11-23","calory":465,"foods":[{"amount":"2","calory":"140","name":"鸡","unit":"kg"}],"create_time":"2018-11-26 01:46:02"},{"id":96,"user_id":233082,"date":"2018-11-23","calory":465,"foods":[{"amount":"2","calory":"140","name":"鸡","unit":"kg"}],"create_time":"2018-11-26 01:46:02"},{"id":97,"user_id":233082,"date":"2018-11-23","calory":465,"foods":[{"amount":"2","calory":"140","name":"鸡","unit":"kg"}],"create_time":"2018-11-26 01:46:03"},{"id":98,"user_id":233082,"date":"2018-11-23","calory":465,"foods":[{"amount":"2","calory":"140","name":"鸡","unit":"kg"}],"create_time":"2018-11-26 01:46:03"}]},{"user_id":233082,"date":"2018-11-26","calory":"750.00","goal_calory":"2400.00","weight":"80.00","goal_weight":"60.00","meals":[{"id":148,"user_id":233082,"date":"2018-11-26","calory":400,"foods":[{"amount":5,"calory":325,"name":"牛筋","unit":"kg"},{"amount":3,"calory":75,"name":"鸡翅","unit":"kg"}],"create_time":"2018-11-26 10:32:41"},{"id":154,"user_id":233082,"date":"2018-11-26","calory":100,"foods":[{"amount":2,"calory":100,"name":"牛肉","unit":"kg"}],"create_time":"2018-11-26 10:52:19"},{"id":155,"user_id":233082,"date":"2018-11-26","calory":175,"foods":[{"amount":5,"calory":175,"name":"鸡爪","unit":"kg"}],"create_time":"2018-11-26 10:52:31"},{"id":187,"user_id":233082,"date":"2018-11-26","calory":75,"foods":[{"amount":3,"calory":75,"name":"鸡翅","unit":"kg"}],"create_time":"2018-11-27 08:43:02"}]},{"user_id":233082,"date":"2018-11-27","calory":"480.00","goal_calory":"2400.00","weight":"80.00","goal_weight":"60.00","meals":[{"id":185,"user_id":233082,"date":"2018-11-27","calory":375,"foods":[{"amount":2,"calory":50,"name":"鸡翅","unit":"kg"},{"amount":5,"calory":325,"name":"牛筋","unit":"kg"}],"create_time":"2018-11-27 08:40:46"},{"id":186,"user_id":233082,"date":"2018-11-27","calory":105,"foods":[{"amount":3,"calory":105,"name":"鸡爪","unit":"kg"}],"create_time":"2018-11-27 08:41:03"}]},{"user_id":233082,"date":"2018-11-28","calory":"1060.00","goal_calory":"2400.00","weight":"80.00","goal_weight":"60.00","meals":[{"id":188,"user_id":233082,"date":"2018-11-28","calory":1060,"foods":[{"amount":2,"calory":1060,"name":"鸡胸肉","unit":"kg"}],"create_time":"2018-11-27 08:44:53"}]}]
     */

    private int ret;
    private String message;
    private int timestamp;
    private List<MealListBean.DataBean> data;

    public void setRet(int ret) {
        this.ret = ret;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public void setData(List<MealListBean.DataBean> data) {
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

    public List<MealListBean.DataBean> getData() {
        return data;
    }

}
