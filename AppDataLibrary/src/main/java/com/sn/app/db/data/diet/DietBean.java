package com.sn.app.db.data.diet;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.sn.app.db.data.base.AppBean;
import com.sn.app.net.data.app.bean.MealBean;

import java.util.ArrayList;

/**
 * 作者:东芝(2018/11/28).
 * 功能:食谱数据表
 */
@DatabaseTable(tableName = DietBean.TABLE_NAME)
public class DietBean extends AppBean {
    public static final String TABLE_NAME = "DietBean";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_WEIGHT = "weight";
    public static final String COLUMN_TARGET_WEIGHT = "target_weight";
    public static final String COLUMN_TARGET_CALORIES = "target_calories";
    public static final String COLUMN_TOTAL_CALORIES = "total_calories";
    public static final String COLUMN_BASIC_CALORIE = "basic_calorie";
    public static final String COLUMN_MEALS = "meals";

    @DatabaseField(columnName = COLUMN_DATE)
    private String date;

    /**
     * 当前体重
     */
    @DatabaseField(columnName = COLUMN_WEIGHT)
    private float weight;

    /**
     * 目标体重
     */
    @DatabaseField(columnName = COLUMN_TARGET_WEIGHT)
    private float targetWeight;

    /**
     * 目标卡路里
     */
    @DatabaseField(columnName = COLUMN_TARGET_CALORIES)
    private float targetCalory;

    /**
     * 总摄入卡路里
     */
    @DatabaseField(columnName = COLUMN_TOTAL_CALORIES)
    private float totalCalory;

    /**
     * 基础代谢 26 * mCurWeight;
     */
    @DatabaseField(columnName = COLUMN_BASIC_CALORIE)
    private float basicMetabolismCalorie;

    /**
     * 餐列表
     */
    @DatabaseField(columnName = COLUMN_MEALS, dataType = DataType.SERIALIZABLE)
    private ArrayList<MealBean> meals;


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getTargetWeight() {
        return targetWeight;
    }

    public void setTargetWeight(float targetWeight) {
        this.targetWeight = targetWeight;
    }

    public float getTargetCalory() {
        return targetCalory;
    }

    public void setTargetCalory(float targetCalory) {
        this.targetCalory = targetCalory;
    }

    public float getTotalCalory() {
        return totalCalory;
    }

    public void setTotalCalory(float totalCalory) {
        this.totalCalory = totalCalory;
    }

    public float getBasicMetabolismCalorie() {
        return basicMetabolismCalorie;
    }

    public void setBasicMetabolismCalorie(float basicMetabolismCalorie) {
        this.basicMetabolismCalorie = basicMetabolismCalorie;
    }

    public ArrayList<MealBean> getMeals() {
        return meals;
    }

    public void setMeals(ArrayList<MealBean> meals) {
        this.meals = meals;
    }
}
