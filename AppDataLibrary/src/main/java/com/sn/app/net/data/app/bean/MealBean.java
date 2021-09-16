package com.sn.app.net.data.app.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 作者:东芝(2018/11/23).
 * 功能:一餐
 */
public class MealBean implements Serializable{
    /**
     * id : 2
     * user_id : 93913
     * date : 2018-10-14
     * calory : 99
     * foods : [{"name":"food1","unit":"kg","calory":"9.0","amount":"1.5"},{"name":"food2","unit":"kg","calory":"7.2","amount":"1.2"}]
     */

    private int id;
    private int user_id;
    private String date;
    private float calory;
    private String create_time;
    private List<FoodsBean> foods;

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public float getCalory() {
        return calory;
    }

    public void setCalory(float calory) {
        this.calory = calory;
    }


    public List<FoodsBean> getFoods() {
        return foods;
    }

    public void setFoods(List<FoodsBean> foods) {
        this.foods = foods;
    }

}