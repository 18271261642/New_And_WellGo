package com.truescend.gofit.views.bean;

/**
 * 自定义日历
 * 功能：存储每天的运动状态
 * Author:Created by 泽鑫 on 2017/12/1 16:14.
 */

public class DayCompletion {
    private int mDay;
    private int targetStep;
    private int currentStep;

    public DayCompletion(int day, int current, int target){
        this.mDay = day;
        this.currentStep = current;
        this.targetStep = target;
    }

    public int getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(int currentStep) {
        this.currentStep = currentStep;
    }

    public int getTargetStep() {
        return targetStep;
    }

    public void setTargetStep(int targetStep) {
        this.targetStep = targetStep;
    }

    public int getDay() {
        return mDay;
    }

    public void setDay(int mDay) {
        this.mDay = mDay;
    }
}
