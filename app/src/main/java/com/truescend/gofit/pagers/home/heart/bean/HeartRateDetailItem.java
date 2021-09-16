package com.truescend.gofit.pagers.home.heart.bean;

/**
 * 作者:东芝(2017/12/18).
 * 功能:心率详情
 */

public class HeartRateDetailItem {
    public final static int TYPE_TOO_HIGH = 1;//过高:
    public final static int TYPE_NORMAL = 0;//正常
    public final static int TYPE_TOO_LOW = -1;//过低
    private String time;
    private int value;
    private int type = TYPE_NORMAL;

    public HeartRateDetailItem() {
    }


    public HeartRateDetailItem(String time, int value) {
        this.time = time;
        this.value = value;
        this.type = HeartRateDetailItem.TYPE_NORMAL;
        if (value <= 59) {//过低
            type = HeartRateDetailItem.TYPE_TOO_LOW;
        } else if (value >= 110) {
            type = HeartRateDetailItem.TYPE_TOO_HIGH;
        }

    }

    public static int getTypeTooHigh() {
        return TYPE_TOO_HIGH;
    }

    public static int getTypeNormal() {
        return TYPE_NORMAL;
    }

    public static int getTypeTooLow() {
        return TYPE_TOO_LOW;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
