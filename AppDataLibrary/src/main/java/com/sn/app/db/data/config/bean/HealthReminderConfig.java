package com.sn.app.db.data.config.bean;

import androidx.annotation.IntDef;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 作者:东芝(2018/2/5).
 * 功能:健康提醒配置
 */

public class HealthReminderConfig implements Serializable {

    /**
     * 久坐提醒
     */
    private HealthReminder remindSedentary = defaultConfig(HealthReminder.TYPE_SEDENTARY);
    /**
     * 喝水提醒
     */
    private HealthReminder remindDrink = defaultConfig(HealthReminder.TYPE_DRINK);

    public HealthReminder getRemindSedentary() {
        return remindSedentary;
    }

    public void setRemindSedentary(HealthReminder remindSedentary) {
        this.remindSedentary = remindSedentary;
    }

    public HealthReminder getRemindDrink() {
        return remindDrink;
    }

    public void setRemindDrink(HealthReminder remindDrink) {
        this.remindDrink = remindDrink;
    }

    private HealthReminder defaultConfig(int itemType) {
        HealthReminder healthReminder = new HealthReminder();
        healthReminder.setItemType(itemType);
        healthReminder.setItemIntervalTime(30);
        healthReminder.setItemStartTimeHour(9);
        healthReminder.setItemStartTimeMinute(0);
        healthReminder.setItemEndTimeHour(18);
        healthReminder.setItemEndTimeMinute(0);
        healthReminder.setWeek(new boolean[]{true, true, true, true, true, true, true});
        return healthReminder;
    }

    public static class HealthReminder implements Serializable {


        public HealthReminder() {
        }

        public static final int TYPE_SEDENTARY = 1;
        public static final int TYPE_DRINK = 2;

        @IntDef(flag = true, value = {
                TYPE_SEDENTARY,
                TYPE_DRINK
        })
        @Retention(RetentionPolicy.SOURCE)
        public @interface Type {}


        private @Type int itemType;//对象：久坐提醒还是喝水提醒
        private int itemIntervalTime;//间隔时间
        private int itemStartTimeHour;//开始时间(时)
        private int itemStartTimeMinute;//开始时间(分)
        private int itemEndTimeHour;//结束时间(时)
        private int itemEndTimeMinute;//结束时间(分)
        /**
         * 重复周期：星期(日->六)
         */
        private boolean[] week;

        private boolean on;//开关

        public @Type int getItemType() {
            return itemType;
        }

        public void setItemType(@Type int itemType) {
            this.itemType = itemType;
        }

        public int getItemIntervalTime() {
            return itemIntervalTime;
        }

        public void setItemIntervalTime(int itemIntervalTime) {
            this.itemIntervalTime = itemIntervalTime;
        }

        public int getItemStartTimeHour() {
            return itemStartTimeHour;
        }

        public void setItemStartTimeHour(int itemStartTimeHour) {
            this.itemStartTimeHour = itemStartTimeHour;
        }

        public int getItemStartTimeMinute() {
            return itemStartTimeMinute;
        }

        public void setItemStartTimeMinute(int itemStartTimeMinute) {
            this.itemStartTimeMinute = itemStartTimeMinute;
        }

        public int getItemEndTimeHour() {
            return itemEndTimeHour;
        }

        public void setItemEndTimeHour(int itemEndTimeHour) {
            this.itemEndTimeHour = itemEndTimeHour;
        }

        public int getItemEndTimeMinute() {
            return itemEndTimeMinute;
        }

        public void setItemEndTimeMinute(int itemEndTimeMinute) {
            this.itemEndTimeMinute = itemEndTimeMinute;
        }

        public boolean[] getWeek() {
            return week;
        }

        public void setWeek(boolean[] week) {
            this.week = week;
        }

        public boolean isOn() {
            return on;
        }

        public void setOn(boolean on) {
            this.on = on;
        }
    }


}
