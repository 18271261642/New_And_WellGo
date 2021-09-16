package com.sn.blesdk.db.data.health.blood_oxygen;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.sn.blesdk.db.data.base.SNBLEBaseBean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 作者:东芝(2017/12/7).
 * 功能:血氧数据
 */
@DatabaseTable(tableName = BloodOxygenBean.TABLE_NAME)
public class BloodOxygenBean extends SNBLEBaseBean {
    public static final String TABLE_NAME = "BloodOxygenBean";

    public static final String COLUMN_BLOOD_OXYGEN_DETAILS = "bloodOxygenDetails";
    public static final String COLUMN_MAX = "max";
    public static final String COLUMN_MIN = "min";
    public static final String COLUMN_AVG = "avg";



    /**
     * 血氧详细数据
     */
    @DatabaseField(columnName = COLUMN_BLOOD_OXYGEN_DETAILS, dataType = DataType.SERIALIZABLE)
    private ArrayList<BloodOxygenDetailsBean> bloodOxygenDetails;

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

    public ArrayList<BloodOxygenDetailsBean> getBloodOxygenDetails() {
        return bloodOxygenDetails;
    }

    public void setBloodOxygenDetails(ArrayList<BloodOxygenDetailsBean> bloodOxygenDetails) {
        this.bloodOxygenDetails = bloodOxygenDetails;
    }

    public static class BloodOxygenDetailsBean implements Serializable {
        public BloodOxygenDetailsBean(int index, String dateTime, int value, int type) {
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
         * 日期
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
