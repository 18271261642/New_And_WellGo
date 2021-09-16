package com.sn.app.db.data.base;

import com.j256.ormlite.field.DatabaseField;

/**
 * 作者:东芝(2018/2/3).
 * 功能:
 */

public class AppBean {
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USER_ID = "user_id";


    /**
     * 数据库唯一性ID
     */
    @DatabaseField(generatedId = true, columnName = COLUMN_ID)
    private int id;

    @DatabaseField(columnName = COLUMN_USER_ID)
    private int user_id;


    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
