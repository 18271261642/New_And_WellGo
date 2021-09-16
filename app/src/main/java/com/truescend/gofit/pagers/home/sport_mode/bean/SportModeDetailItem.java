package com.truescend.gofit.pagers.home.sport_mode.bean;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 作者:东芝(2019/6/5).
 * 功能:
 */
public class SportModeDetailItem {

    public static final int STATISTICAL_TYPE_WEEK = 1;
    public static final int STATISTICAL_TYPE_DATE_RANGE = 2;
    private @StatisticalType int statisticalType = STATISTICAL_TYPE_WEEK;
    @IntDef(flag = true, value = {
            STATISTICAL_TYPE_WEEK,
            STATISTICAL_TYPE_DATE_RANGE
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface StatisticalType {}


    private String date;
    private String beginDateTime;
    private int modeType;
    private int count;
    private long takeMinutes;
    private long stepTotal;
    private long calorie;
    private long distance;
    private float hrMax;
    private float hrAvg;

    public SportModeDetailItem(@StatisticalType int statisticalType) {
        this.statisticalType = statisticalType;
    }

    public @StatisticalType int getStatisticalType() {
        return statisticalType;
    }

    public float getHrMax() {
        return hrMax;
    }

    public void setHrMax(float hrMax) {
        this.hrMax = hrMax;
    }

    public float getHrAvg() {
        return hrAvg;
    }

    public void setHrAvg(float hrAvg) {
        this.hrAvg = hrAvg;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBeginDateTime() {
        return beginDateTime;
    }

    public void setBeginDateTime(String beginDateTime) {
        this.beginDateTime = beginDateTime;
    }

    public int getModeType() {
        return modeType;
    }

    public void setModeType(int modeType) {
        this.modeType = modeType;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public long getTakeMinutes() {
        return takeMinutes;
    }

    public void setTakeMinutes(long takeMinutes) {
        this.takeMinutes = takeMinutes;
    }

    public long getStepTotal() {
        return stepTotal;
    }

    public void setStepTotal(long stepTotal) {
        this.stepTotal = stepTotal;
    }

    public long getCalorie() {
        return calorie;
    }

    public void setCalorie(long calorie) {
        this.calorie = calorie;
    }

    public long getDistance() {
        return distance;
    }

    public void setDistance(long distance) {
        this.distance = distance;
    }



}
