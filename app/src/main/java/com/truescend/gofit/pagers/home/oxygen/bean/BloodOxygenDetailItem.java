package com.truescend.gofit.pagers.home.oxygen.bean;

/**
 * 作者:东芝(2017/12/18).
 * 功能:血氧详情
 */

public class BloodOxygenDetailItem {
    private String time;
    private int value;

    public BloodOxygenDetailItem() {
    }


    public BloodOxygenDetailItem(String time, int value) {
        this.time = time;
        this.value = value;

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

}
