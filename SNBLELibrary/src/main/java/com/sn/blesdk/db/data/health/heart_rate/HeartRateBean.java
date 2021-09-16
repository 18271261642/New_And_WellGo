package com.sn.blesdk.db.data.health.heart_rate;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.sn.blesdk.db.data.base.SNBLEBaseBean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 作者:东芝(2017/12/7).
 * 功能:心率数据
 */
@DatabaseTable(tableName = HeartRateBean.TABLE_NAME)
public class HeartRateBean extends SNBLEBaseBean {
    public static final String TABLE_NAME = "HeartRateBean";

    public static final String COLUMN_HEART_RATE_DETAILS = "heartRateDetails";
    public static final String COLUMN_MAX = "max";
    public static final String COLUMN_MIN = "min";
    public static final String COLUMN_AVG = "avg";

    /**
     * 心率详细数据
     */
    @DatabaseField(columnName = COLUMN_HEART_RATE_DETAILS, dataType = DataType.SERIALIZABLE)
    private ArrayList<HeartRateBean.HeartRateDetailsBean> heartRateDetails;


    /**
     * 最大
     */
    @DatabaseField(columnName =  COLUMN_MAX)
    private int max;


    /**
     * 最小
     */
    @DatabaseField(columnName = COLUMN_MIN)
    private int min;

    /**
     * 平均
     */
    @DatabaseField(columnName = COLUMN_AVG)
    private int avg;


    public ArrayList<HeartRateDetailsBean> getHeartRateDetails() {
        return heartRateDetails;
    }

    public void setHeartRateDetails(ArrayList<HeartRateDetailsBean> heartRateDetails) {
        this.heartRateDetails = heartRateDetails;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getAvg() {
        return avg;
    }

    public void setAvg(int avg) {
        this.avg = avg;
    }

    public static class HeartRateDetailsBean implements Serializable {
        public HeartRateDetailsBean(int index, String dateTime, int value, int type) {
            this.index = index;
            this.dateTime = dateTime;
            this.value = value;
            this.type = type;
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
        /**
         * 是否自动检测 的数据,否则是手动 (默认0=手动检测,1=自动检测)
         */
        private int type;


        public void setType(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }
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
}
