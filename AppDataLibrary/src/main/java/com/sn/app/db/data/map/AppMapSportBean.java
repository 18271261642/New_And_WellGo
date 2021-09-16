//package com.sn.app.db.data.map;
//
//import com.j256.ormlite.field.DataType;
//import com.j256.ormlite.field.DatabaseField;
//import com.j256.ormlite.table.DatabaseTable;
//import com.sn.app.db.data.base.AppBean;
//
///**
// * 作者:东芝(2018/1/5).
// * 功能:
// */
//@Deprecated
//@DatabaseTable(tableName = AppMapSportBean.TABLE_NAME)
//public class AppMapSportBean extends AppBean{
//
//    public static final String TABLE_NAME = "AppMapSportBean";
//    public static final String COLUMN_DATE = "date";
//    public static final String COLUMN_IS_UPLOADED = "isUploaded";
//    public static final String COLUMN_SCREENSHOT_DATA = "screenshotData";
//    public static final String COLUMN_ADDRESS = "address";
//    public static final String COLUMN_DISTANCE_TOTAL = "distanceTotal";
//    public static final String COLUMN_SPEND_TIME_TOTAL = "spendTimeTotal";
//    public static final String COLUMN_SPEED_AVG_TOTAL = "speedAvgTotal";
//    public static final String COLUMN_SPEED_AVG_PACE_TOTAL = "speedAvgPaceTotal";
//    public static final String COLUMN_SPEED_MAX_TOTAL = "speedMaxTotal";
//    public static final String COLUMN_CALORIES = "calories";
//
//
//    /**
//     * 截图
//     */
//    @DatabaseField(columnName = COLUMN_SCREENSHOT_DATA, dataType = DataType.SERIALIZABLE)
//    private byte[] screenshotData;
//
//
//    /**
//     * 日期
//     */
//    @DatabaseField(columnName = COLUMN_DATE)
//    private String date;
//
//    /**
//     * 地址
//     */
//    @DatabaseField(columnName = COLUMN_ADDRESS)
//    private String address;
//
//    /**
//     * 总距离(米)
//     */
//    @DatabaseField(columnName = COLUMN_DISTANCE_TOTAL)
//    private float distanceTotal;
//
//    /**
//     * 总耗时(秒)
//     */
//    @DatabaseField(columnName = COLUMN_SPEND_TIME_TOTAL)
//    private long spendTimeTotal;
//
//    /**
//     * 平均时速(米/秒)
//     */
//    @DatabaseField(columnName = COLUMN_SPEED_AVG_TOTAL)
//    private float speedAvgTotal;
//
//    /**
//     * 平均配速 00"00"
//     */
//    @DatabaseField(columnName = COLUMN_SPEED_AVG_PACE_TOTAL)
//    private float speedAvgPaceTotal;
//
//    /**
//     * 最快时速(米/秒)
//     */
//    @DatabaseField(columnName = COLUMN_SPEED_MAX_TOTAL)
//    private float speedMaxTotal;
//
//    /**
//     * 卡路里
//     */
//    @DatabaseField(columnName = COLUMN_CALORIES)
//    private float calories;
//
//    /**
//     * 是否已上传
//     */
//    @DatabaseField(columnName = COLUMN_IS_UPLOADED, dataType = DataType.BOOLEAN)
//    public boolean isUploaded;
//
//
//    public String getAddress() {
//        return address;
//    }
//
//    public void setAddress(String address) {
//        this.address = address;
//    }
//
//    public float getDistanceTotal() {
//        return distanceTotal;
//    }
//
//    public void setDistanceTotal(float distanceTotal) {
//        this.distanceTotal = distanceTotal;
//    }
//
//    public long getSpendTimeTotal() {
//        return spendTimeTotal;
//    }
//
//    public void setSpendTimeTotal(long spendTimeTotal) {
//        this.spendTimeTotal = spendTimeTotal;
//    }
//
//    public float getSpeedAvgTotal() {
//        return speedAvgTotal;
//    }
//
//    public void setSpeedAvgTotal(float speedAvgTotal) {
//        this.speedAvgTotal = speedAvgTotal;
//    }
//
//    public float getSpeedAvgPaceTotal() {
//        return speedAvgPaceTotal;
//    }
//
//    public void setSpeedAvgPaceTotal(float speedAvgPaceTotal) {
//        this.speedAvgPaceTotal = speedAvgPaceTotal;
//    }
//
//    public float getSpeedMaxTotal() {
//        return speedMaxTotal;
//    }
//
//    public void setSpeedMaxTotal(float speedMaxTotal) {
//        this.speedMaxTotal = speedMaxTotal;
//    }
//
//    public float getCalories() {
//        return calories;
//    }
//
//    public void setCalories(float calories) {
//        this.calories = calories;
//    }
//
//
//    public byte[] getScreenshotData() {
//        return screenshotData;
//    }
//
//    public void setScreenshotData(byte[] screenshotData) {
//        this.screenshotData = screenshotData;
//    }
//
//    public String getDate() {
//        return date;
//    }
//
//    public void setDate(String date) {
//        this.date = date;
//    }
//
//    public boolean isUploaded() {
//        return isUploaded;
//    }
//
//    public void setUploaded(boolean uploaded) {
//        isUploaded = uploaded;
//    }
//}
