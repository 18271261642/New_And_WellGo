package com.sn.app.db.data.config.bean;

import java.io.Serializable;

/**
 * 作者:东芝(2018/2/3).
 * 功能:单位
 */

public class UnitConfig implements Serializable{

    static final long serialVersionUID =-2950768866625276562L;

    public static final int DISTANCE_KM = 0/*公里*/, DISTANCE_MILES = 1;/*英里*/
    public static final int TEMPERATURE_C = 0 /*摄氏度*/, TEMPERATURE_F = 1;/*华氏度*/
    public static final int WEIGHT_KG = 0/*千克*/, WEIGHT_LB = 1;/*镑*/
    public static final int TIME_24 = 0/*24制*/, TIME_12 = 1;/*12制*/
    /**
     * 距离单位
     */
    public int distanceUnit = DISTANCE_KM;

    /**
     * 温度单位
     */
    public int temperatureUnit = TEMPERATURE_C;

    /**
     * 重量单位
     */
    public int weightUnit = WEIGHT_KG;


    public int timeUnit = TIME_24;


    public void setTimeUnit(int timeUnit) {
        this.timeUnit = timeUnit;
    }

    public int getTimeUnit() {
        return timeUnit;
    }

    public int getDistanceUnit() {
        return distanceUnit;
    }

    public void setDistanceUnit(int distanceUnit) {
        this.distanceUnit = distanceUnit;
    }

    public int getTemperatureUnit() {
        return temperatureUnit;
    }

    public void setTemperatureUnit(int temperatureUnit) {
        this.temperatureUnit = temperatureUnit;
    }

    public int getWeightUnit() {
        return weightUnit;
    }

    public void setWeightUnit(int weightUnit) {
        this.weightUnit = weightUnit;
    }
}
