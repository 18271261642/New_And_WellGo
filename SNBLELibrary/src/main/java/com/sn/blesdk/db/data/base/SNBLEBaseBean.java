package com.sn.blesdk.db.data.base;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

/**
 * 作者:东芝(2017/12/8).
 * 功能:数据基类 仅用于设备数据存储
 */
public class SNBLEBaseBean {


    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_MAC = "mac";
    public static final String COLUMN_IS_UPLOADED = "isUploaded";

    /**
     * 递增id
     */
    @DatabaseField(generatedId = true, columnName = COLUMN_ID)
    public int id;

    /**
     * 用户id
     */
    @DatabaseField( columnName = COLUMN_USER_ID )
    public int user_id;

    /**
     * 设备mac
     */
    @DatabaseField(columnName = COLUMN_MAC)
    public String mac;

    /**
     * 是否已上传
     */
    @DatabaseField(columnName = COLUMN_IS_UPLOADED, dataType = DataType.BOOLEAN)
    public boolean isUploaded;

    /**
     * 该天 yyyy-MM-dd 不要带秒
     */
    @DatabaseField(columnName = COLUMN_DATE, canBeNull = false/*不能为空*/)
    public String date;




    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public boolean isUploaded() {
        return isUploaded;
    }

    public void setUploaded(boolean uploaded) {
        isUploaded = uploaded;
    }

    public String getDate() {
        return date;
    }

    /**
     * 日期 不包括时分秒 用于查询当天的数据 用
     * @param date
     */
    public void setDate(String date) {
        this.date = date;
    }


    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getUser_id() {
        return user_id;
    }
}
