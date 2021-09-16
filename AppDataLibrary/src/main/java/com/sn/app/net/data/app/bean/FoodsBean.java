package com.sn.app.net.data.app.bean;

import java.io.Serializable;

/**
 * 作者:东芝(2018/11/23).
 * 功能:食物
 */

public class FoodsBean implements Serializable {

    /**
     * name : food1
     * unit : kg
     * calory : 9.0
     * amount : 1.5
     */

    private String name;
    private String unit;
    private float calory;
    private float amount;

    public FoodsBean() {
    }

    public FoodsBean(String name, String unit, float calory, float amount) {
        this.name = name;
        this.unit = unit;
        this.calory = calory;
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public float getCalory() {
        return calory;
    }

    public void setCalory(float calory) {
        this.calory = calory;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }
}
