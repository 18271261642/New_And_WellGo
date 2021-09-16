package com.sn.blesdk.db.data.sport;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.sn.blesdk.db.data.base.SNBLEBaseBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 作者:东芝(2017/11/27).
 * 功能:运动大数据
 */
@DatabaseTable(tableName = SportBean.TABLE_NAME)
public class SportBean extends SNBLEBaseBean {

    public static final String TABLE_NAME = "SportBean";
    public static final String COLUMN_STEPS = "steps";
    public static final String COLUMN_CALORIES = "calories";
    public static final String COLUMN_DISTANCES = "distances";
    public static final String COLUMN_DISTANCE_TOTAL = "distanceTotal";
    public static final String COLUMN_STEP_TOTAL = "stepTotal";
    public static final String COLUMN_CALORIE_TOTAL = "calorieTotal";
    public static final String COLUMN_STEP_TARGET = "stepTarget";

    /**
     * 步数详细
     */
    @DatabaseField(columnName = COLUMN_STEPS, dataType = DataType.SERIALIZABLE)
    private ArrayList<StepBean> steps;

    /**
     * 总步数
     */
    @DatabaseField(columnName = COLUMN_STEP_TOTAL)
    private int stepTotal;

    /**
     * 卡路里数据详细
     */
    @DatabaseField(columnName = COLUMN_CALORIES, dataType = DataType.SERIALIZABLE)
    private ArrayList<CalorieBean> calories;

    /**
     * 总卡路里
     */
    @DatabaseField(columnName = COLUMN_CALORIE_TOTAL)
    private int calorieTotal;
    /**
     * 距离数据详细
     */
    @DatabaseField(columnName = COLUMN_DISTANCES, dataType = DataType.SERIALIZABLE)
    private ArrayList<DistanceBean> distances;

    /**
     * 总距离
     */
    @DatabaseField(columnName = COLUMN_DISTANCE_TOTAL)
    private int distanceTotal;

    /**
     * 目标
     */
    @DatabaseField(columnName = COLUMN_STEP_TARGET)
    private int stepTarget;


    public int getStepTarget() {
        return stepTarget;
    }

    public void setStepTarget(int stepTarget) {
        this.stepTarget = stepTarget;
    }

    public int getStepTotal() {
        return stepTotal;
    }

    public void setStepTotal(int stepTotal) {
        this.stepTotal = stepTotal;
    }

    public int getCalorieTotal() {
        return calorieTotal;
    }

    public void setCalorieTotal(int calorieTotal) {
        this.calorieTotal = calorieTotal;
    }

    public int getDistanceTotal() {
        return distanceTotal;
    }

    public void setDistanceTotal(int distanceTotal) {
        this.distanceTotal = distanceTotal;
    }


    public List<StepBean> getSteps() {
        return steps;
    }

    public void setSteps(ArrayList<StepBean> steps) {
        this.steps = steps;
    }

    public List<CalorieBean> getCalories() {
        return calories;
    }

    public void setCalories(ArrayList<CalorieBean> calories) {
        this.calories = calories;
    }

    public List<DistanceBean> getDistances() {
        return distances;
    }

    public void setDistances(ArrayList<DistanceBean> distances) {
        this.distances = distances;
    }

    public static class AbsSportBean implements Serializable {
        public AbsSportBean(int index, String dateTime, int value) {
            this.index = index;
            this.dateTime = dateTime;
            this.value = value;
        }


        /**
         * 时间索引
         */
        private int index;
        /**
         * 日期 带时分秒
         */
        private String dateTime;
        /**
         * 具体值
         */
        private int value;

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public String getDateTime() {
            return dateTime;
        }

        public void setDateTime(String dateTime) {
            this.dateTime = dateTime;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }

    public static class StepBean extends AbsSportBean {

        public StepBean(int index, String dateTime, int value) {
            super(index, dateTime, value);
        }
    }

    public static class DistanceBean extends AbsSportBean {

        public DistanceBean(int index, String dateTime, int value) {
            super(index, dateTime, value);
        }
    }

    public static class CalorieBean extends AbsSportBean {

        public CalorieBean(int index, String dateTime, int value) {
            super(index, dateTime, value);
        }
    }
}
