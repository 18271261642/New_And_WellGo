package com.sn.app.db.data.clock;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.sn.app.db.data.base.AppBean;

/**
 * 闹钟列表
 * Author:Created by 泽鑫 on 2018/1/20 11:08.
 */
@DatabaseTable(tableName = AlarmClockBean.TABLE_NAME)
public class AlarmClockBean extends AppBean{

    public static final String TABLE_NAME = "AlarmClockBean";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_SWITCH = "switch";
    public static final String COLUMN_WEEK = "week";


    /**
     * 日期
     */
    @DatabaseField(columnName = COLUMN_DATE)
    private String date;

    /**
     * 闹钟开关
     */
    @DatabaseField(columnName = COLUMN_SWITCH,dataType = DataType.BOOLEAN, defaultValue = "false")
    private boolean switchStatues;

    /**
     * 重复周期：星期(日->六)
     */
    @DatabaseField(columnName = COLUMN_WEEK, dataType = DataType.SERIALIZABLE)
    private boolean[] week = new boolean[]{false,false,false,false,false,false,false};


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isSwitchStatues() {
        return switchStatues;
    }

    public void setSwitchStatues(boolean switchStatues) {
        this.switchStatues = switchStatues;
    }

    public boolean[] getWeek() {
        return week;
    }

    public void setWeek(boolean[] week) {
        this.week = week;
    }

    public boolean isSingle(){
        int i =0;
        for (boolean open : week) {
            if(!open){//如果全部都是关闭的 视为单次闹钟
                i++;
            }
        }
        return i==week.length;
    }

}
