package com.sn.app.db.data.user;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.sn.app.db.data.base.AppBean;

/**
 * 作者:东芝(2018/1/24).
 * 功能:
 */
@DatabaseTable(tableName = UserBean.TABLE_NAME)
public class UserBean extends AppBean {
    public static final String TABLE_NAME = "UserBean";

    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_NICKNAME = "nickname";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_SIGN = "sign";
    public static final String COLUMN_BIRTHDAY = "birthday";
    public static final String COLUMN_JOB = "job";
    public static final String COLUMN_HEIGHT = "height";
    public static final String COLUMN_WEIGHT = "weight";
    public static final String COLUMN_TARGET_WEIGHT = "target_weight";
    public static final String COLUMN_TARGET_CALORIES = "target_calories";
    public static final String COLUMN_WEIGHT_MEASURING_DATE = "weight_measuring_date";
    public static final String COLUMN_GENDER = "gender";
    public static final String COLUMN_PORTRAIT = "portrait";
    public static final String COLUMN_WALLPAPER = "wallpaper";
    public static final String COLUMN_TARGET_SLEEP = "target_sleep";
    public static final String COLUMN_TARGET_STEP = "target_step";
    public static final String COLUMN_MAX_STEP = "max_step";
    public static final String COLUMN_MAX_REACH_TIMES = "max_reach_times";
    public static final String COLUMN_MAX_REACH_DATE = "max_reach_date";
    public static final String COLUMN_MAC = "mac";
    public static final String COLUMN_UNIQUE_ID = "unique_id";
    public static final String COLUMN_SPORT_DAYS = "sport_days";
    public static final String COLUMN_DEVICE_NAME = "device_name";
    public static final String COLUMN_ADV_ID = "adv_id";
    public static final String COLUMN_FIRST_MEAL_DATE = "first_meal_date";
    public static final String COLUMN_TOTAL_MEAL_DAY = "total_meal_day";



    ////////////////////////////////////////////////////////////////////////////////////////
    //--------------------------------------网络用户数据(字段自己看服务器API文档)-----------------------------------
    ////////////////////////////////////////////////////////////////////////////////////////


    @DatabaseField(columnName = COLUMN_EMAIL)
    private String email;

    @DatabaseField(columnName = COLUMN_NICKNAME)
    private String nickname;

    @DatabaseField(columnName = COLUMN_ADDRESS)
    private String address;

    @DatabaseField(columnName = COLUMN_SIGN)
    private String sign;

    @DatabaseField(columnName = COLUMN_BIRTHDAY)
    private String birthday;

    @DatabaseField(columnName = COLUMN_JOB)
    private String job;

    @DatabaseField(columnName = COLUMN_HEIGHT)
    private float height;

    @DatabaseField(columnName = COLUMN_WEIGHT)
    private float weight;

    @DatabaseField(columnName = COLUMN_WEIGHT_MEASURING_DATE)
    private String weight_measure_date;

    @DatabaseField(columnName = COLUMN_TARGET_WEIGHT)
    private float target_weight;//目标体重
    @DatabaseField(columnName = COLUMN_TARGET_CALORIES)
    private float target_calory;//目标卡路里

    @DatabaseField(columnName = COLUMN_GENDER)
    private int gender;

    @DatabaseField(columnName = COLUMN_PORTRAIT)
    private String portrait;

    @DatabaseField(columnName = COLUMN_WALLPAPER)
    private String wallpaper;

    @DatabaseField(columnName = COLUMN_TARGET_SLEEP)
    private int target_sleep;

    @DatabaseField(columnName = COLUMN_TARGET_STEP)
    private int target_step;




    /**
     * 最大步数
     */
    @DatabaseField(columnName = COLUMN_MAX_STEP)
    private int max_step;

    /**
     * 最大步数连续达标天数
     */
    @DatabaseField(columnName = COLUMN_MAX_REACH_TIMES)
    private int max_reach_times;


    @DatabaseField(columnName = COLUMN_MAX_REACH_DATE)
    private int max_reach_date;

    /**
     * 手机唯一标识
     */
    @DatabaseField(columnName = COLUMN_UNIQUE_ID)
    private String unique_id;

    ////////////////////////////////////////////////////////////////////////////////////////
    //--------------------------------------网络用户数据-----------------------------------
    ////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 首次上传食谱的日期
     */
    @DatabaseField(columnName = COLUMN_FIRST_MEAL_DATE)
    private String first_meal_date;

    /**
     * 上传食谱的天数
     */
    @DatabaseField(columnName = COLUMN_TOTAL_MEAL_DAY)
    private int total_meal_day;


    /**
     * 运动天数
     */
    @DatabaseField(columnName = COLUMN_SPORT_DAYS)
    private int sport_days;


    ////////////////////////////////////////////////////////////////////////////////////////
    //--------------------------------------用户的手环-----------------------------------
    ////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 上次连接的设备 MAC地址
     */
    @DatabaseField(columnName = COLUMN_MAC)
    private String mac;

    @DatabaseField(columnName = COLUMN_DEVICE_NAME)
    private String device_name;

    @DatabaseField(columnName = COLUMN_ADV_ID)
    private String adv_id;


    ////////////////////////////////////////////////////////////////////////////////////////
    //--------------------------------------用户的手环-----------------------------------
    ////////////////////////////////////////////////////////////////////////////////////////


    public String getFirst_meal_date() {
        return first_meal_date;
    }

    public void setFirst_meal_date(String first_meal_date) {
        this.first_meal_date = first_meal_date;
    }

    public int getTotal_meal_day() {
        return total_meal_day;
    }

    public void setTotal_meal_day(int total_meal_day) {
        this.total_meal_day = total_meal_day;
    }

    public float getTarget_weight() {
        return target_weight;
    }

    public void setTarget_weight(float target_weight) {
        this.target_weight = target_weight;
    }

    public float getTarget_calory() {
        return target_calory;
    }

    public void setTarget_calory(float target_calory) {
        this.target_calory = target_calory;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    public void setAdv_id(String adv_id) {
        this.adv_id = adv_id;
    }

    public String getAdv_id() {
        return adv_id;
    }

    public String getDevice_name() {
        return device_name;
    }

    public int getSport_days() {
        return sport_days;
    }

    public void setSport_days(int sport_days) {
        this.sport_days = sport_days;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getUnique_id() {
        return unique_id;
    }

    public void setUnique_id(String unique_id) {
        this.unique_id = unique_id;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public String getWeight_measure_date() {
        return weight_measure_date;
    }

    public void setWeight_measure_date(String weight_measure_date) {
        this.weight_measure_date = weight_measure_date;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getWallpaper() {
        return wallpaper;
    }

    public void setWallpaper(String wallpaper) {
        this.wallpaper = wallpaper;
    }

    public int getTarget_sleep() {
        return target_sleep;
    }

    public void setTarget_sleep(int target_sleep) {
        this.target_sleep = target_sleep;
    }

    public int getTarget_step() {
        return target_step;
    }

    public void setTarget_step(int target_step) {
        this.target_step = target_step;
    }

    public int getMax_step() {
        return max_step;
    }

    public void setMax_step(int max_step) {
        this.max_step = max_step;
    }

    public int getMax_reach_times() {
        return max_reach_times;
    }

    public void setMax_reach_times(int max_reach_times) {
        this.max_reach_times = max_reach_times;
    }

    public int getMax_reach_date() {
        return max_reach_date;
    }

    public void setMax_reach_date(int max_reach_date) {
        this.max_reach_date = max_reach_date;
    }

}
