package com.truescend.gofit.pagers.track.bean;


import java.io.Serializable;

/**
 * 作者:东芝(2018/2/24).
 * 功能:地图记录Item的数据
 */

public class PathMapItem implements Serializable{
    private String screenshotUrl;
    private String address;
    private String dateTime;
    private String distanceTotal;
    private String spendTimeTotal;
    private String speedAvgTotal;
    private String speedAvgPaceTotal;
    private String speedMaxTotal;
    private String calories;

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getScreenshotUrl() {
        return screenshotUrl;
    }

    public void setScreenshotUrl(String screenshotUrl) {
        this.screenshotUrl = screenshotUrl;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public String getDistanceTotal() {
        return distanceTotal;
    }

    public void setDistanceTotal(String distanceTotal) {
        this.distanceTotal = distanceTotal;
    }

    public String getSpendTimeTotal() {
        return spendTimeTotal;
    }

    public void setSpendTimeTotal(String spendTimeTotal) {
        this.spendTimeTotal = spendTimeTotal;
    }

    public String getSpeedAvgTotal() {
        return speedAvgTotal;
    }

    public void setSpeedAvgTotal(String speedAvgTotal) {
        this.speedAvgTotal = speedAvgTotal;
    }

    public String getSpeedAvgPaceTotal() {
        return speedAvgPaceTotal;
    }

    public void setSpeedAvgPaceTotal(String speedAvgPaceTotal) {
        this.speedAvgPaceTotal = speedAvgPaceTotal;
    }

    public String getSpeedMaxTotal() {
        return speedMaxTotal;
    }

    public void setSpeedMaxTotal(String speedMaxTotal) {
        this.speedMaxTotal = speedMaxTotal;
    }

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }
}
