package com.truescend.gofit.views.bean;

import android.graphics.RectF;

import java.util.Date;

/**
 * 自定义日历
 * 功能：存储每个圆环的数据
 * Author:Created by 泽鑫 on 2017/12/1 15:18.
 */

public class Day {
    private int day;//日期
    private Date date;
    private int month;
    private int line;//行
    private int column;//列
    private RectF rectF; //位置

    public RectF getRectF() {
        return rectF;
    }

    public void setRectF(RectF rectF) {
        this.rectF = rectF;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getColumn() {
        return column;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getLine() {
        return line;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public String toSting(){
        return "Day: " + day + "\n" + "line: " + line + "\n" + "column: " + column + "\n";
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
