package com.sn.blesdk.db.data.sport_mode;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.sn.blesdk.db.data.base.SNBLEBaseBean;

/**
 * 作者:东芝(2019/06/04).
 * 功能:运动模式数据
 */
@DatabaseTable(tableName = SportModeBean.TABLE_NAME)
public class SportModeBean extends SNBLEBaseBean {

    public static final int MODE_TYPE_WALKING = 0x01;//健步
    public static final int MODE_TYPE_RUNNING = 0x02;//跑步
    public static final int MODE_TYPE_MOUNTAINEERING = 0x03;//登山
    public static final int MODE_TYPE_CYCLING = 0x04;//骑行
    public static final int MODE_TYPE_TABLE_TENNIS = 0x05;//乒乓球
    public static final int MODE_TYPE_BASKETBALL = 0x06;//篮球
    public static final int MODE_TYPE_FOOTBALL = 0x07;//足球
    public static final int MODE_TYPE_BADMINTON = 0x08;//羽毛球
    public static final int MODE_TYPE_TREADMILL = 0x09;//跑步机
    public static final int MODE_TYPE_TENNIS = 0x0A;//网球
    public static final int MODE_TYPE_SWIMMING = 0x0B;//游泳

    public static final String TABLE_NAME = "SportModeBean";
    public static final String COLUMN_MODE_TYPE = "mode_type";
    public static final String COLUMN_BEGIN_DATE_TIME = "begin_date_time";
    public static final String COLUMN_END_DATE_TIME = "end_date_time";
    public static final String COLUMN_TAKE_MINUTES = "take_minutes";
    public static final String COLUMN_STEP = "step";
    public static final String COLUMN_DISTANCE = "distance";
    public static final String COLUMN_CALORIE = "calorie";
    public static final String COLUMN_HEART_RATE_MAX = "heart_rate_max";
    public static final String COLUMN_HEART_RATE_MIN = "heart_rate_min";
    public static final String COLUMN_HEART_RATE_AVG = "heart_rate_avg";

    /**
     * 运动模式类型
     */
    @DatabaseField(columnName = COLUMN_MODE_TYPE)
    private int modeType;

    /**
     * 开始时间 "yyyy-MM-dd HH:mm:ss" 精确到秒
     */
    @DatabaseField(columnName = COLUMN_BEGIN_DATE_TIME)
    private String beginDateTime;

    /**
     * 结束时间 "yyyy-MM-dd HH:mm:ss" 精确到秒
     */
    @DatabaseField(columnName = COLUMN_END_DATE_TIME)
    private String endDateTime;

    /**
     * 花费的分钟数
     */
    @DatabaseField(columnName = COLUMN_TAKE_MINUTES)
    private int takeMinutes;

    /**
     * 步数
     */
    @DatabaseField(columnName = COLUMN_STEP)
    private int step;

    /**
     * 距离
     */
    @DatabaseField(columnName = COLUMN_DISTANCE)
    private int distance;

    /**
     * 卡路里
     */
    @DatabaseField(columnName = COLUMN_CALORIE)
    private int calorie;


    /**
     * 最大心率
     */
    @DatabaseField(columnName = COLUMN_HEART_RATE_MAX)
    private int heartRateMax;

    /**
     * 最小心率
     */
    @DatabaseField(columnName = COLUMN_HEART_RATE_MIN)
    private int heartRateMin;

    /**
     * 平均心率
     */
    @DatabaseField(columnName = COLUMN_HEART_RATE_AVG)
    private int heartRateAvg;


    public int getTakeMinutes() {
        return takeMinutes;
    }

    public void setTakeMinutes(int takeMinutes) {
        this.takeMinutes = takeMinutes;
    }

    public int getModeType() {
        return modeType;
    }

    public void setModeType(int modeType) {
        this.modeType = modeType;
    }

    public String getBeginDateTime() {
        return beginDateTime;
    }

    public void setBeginDateTime(String beginDateTime) {
        this.beginDateTime = beginDateTime;
    }

    public String getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(String endDateTime) {
        this.endDateTime = endDateTime;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getCalorie() {
        return calorie;
    }

    public void setCalorie(int calorie) {
        this.calorie = calorie;
    }

    public int getHeartRateMax() {
        return heartRateMax;
    }

    public void setHeartRateMax(int heartRateMax) {
        this.heartRateMax = heartRateMax;
    }

    public int getHeartRateMin() {
        return heartRateMin;
    }

    public void setHeartRateMin(int heartRateMin) {
        this.heartRateMin = heartRateMin;
    }

    public int getHeartRateAvg() {
        return heartRateAvg;
    }

    public void setHeartRateAvg(int heartRateAvg) {
        this.heartRateAvg = heartRateAvg;
    }
}
