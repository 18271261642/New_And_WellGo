package com.sn.app.db.data.config.bean;

import java.io.Serializable;

/**
 * 作者:东芝(2018/2/3).
 * 功能:时间范围 开关 对象
 */
public class TimeCycleSwitch implements Serializable {
    /**
     * 开始时间 HH:mm
     */
    private String startTime;
    /**
     * 结束时间 HH:mm
     */
    private String endTime;
    /**
     * 是否开启
     */
    private boolean on;

    public TimeCycleSwitch(String startTime, String endTime, boolean on) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.on = on;

    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setOn(boolean on) {
        this.on = on;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public boolean isOn() {
        return on;
    }
}
