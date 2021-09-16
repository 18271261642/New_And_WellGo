package com.truescend.gofit.pagers.home.pressure.bean;

/**
 * 作者:东芝(2017/12/18).
 * 功能:血压详情
 */

public class BloodPressureDetailItem {
    public final static int TYPE_DIASTOLIC_ABNORMAL = 1;//舒张压异常:
    public final static int TYPE_NORMAL = 0;//正常
    public final static int TYPE_SYSTOLIC_ABNORMAL = -1;//收缩压异常
    private String time;
    private int diastolic;
    private int systolic;
    private int diastolic_type = TYPE_NORMAL;
    private int systolic_type = TYPE_NORMAL;

    public BloodPressureDetailItem() {
    }


    public BloodPressureDetailItem(String time, int diastolic, int systolic) {
        this.time = time;
        this.diastolic = diastolic;
        this.systolic = systolic;
        this.diastolic_type = BloodPressureDetailItem.TYPE_NORMAL;
        //收缩压90<a<140
        if (systolic < 90) {//过低
            systolic_type = BloodPressureDetailItem.TYPE_SYSTOLIC_ABNORMAL;
        } else if (systolic >= 140) {
            systolic_type = BloodPressureDetailItem.TYPE_SYSTOLIC_ABNORMAL;
        }
        //舒张压60<a<90
        if (diastolic < 60) {//过低
            diastolic_type = BloodPressureDetailItem.TYPE_DIASTOLIC_ABNORMAL;
        } else if (diastolic >= 90) {
            diastolic_type = BloodPressureDetailItem.TYPE_DIASTOLIC_ABNORMAL;
        }

    }


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getDiastolic() {
        return diastolic;
    }

    public void setDiastolic(int diastolic) {
        this.diastolic = diastolic;
    }

    public int getSystolic() {
        return systolic;
    }

    public void setSystolic(int systolic) {
        this.systolic = systolic;
    }

    public int getDiastolic_type() {
        return diastolic_type;
    }

    public void setDiastolic_type(int diastolic_type) {
        this.diastolic_type = diastolic_type;
    }

    public int getSystolic_type() {
        return systolic_type;
    }

    public void setSystolic_type(int systolic_type) {
        this.systolic_type = systolic_type;
    }
}
