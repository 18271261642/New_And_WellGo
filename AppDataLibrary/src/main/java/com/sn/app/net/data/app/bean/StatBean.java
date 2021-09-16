package com.sn.app.net.data.app.bean;

import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.sn.net.utils.JsonUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 作者:东芝(2018/8/15).
 * 功能:
 */

public class StatBean {

    /**
     * user_id : 31103
     * bp_history : [{"bpdatas":[],"datestring":"2018-08-21","SBPave":"0","DBPave":"0"}]
     * finish_days : 2_08/20-08/21
     * heart_history : [{"heartdatas":[{"value":"90","time":"09:30"},{"value":"60","time":"10:30"},{"value":"83","time":"11:00"},{"value":"80","time":"11:30"},{"value":"80","time":"12:00"},{"value":"78","time":"12:30"},{"value":"88","time":"13:00"},{"value":"75","time":"13:30"},{"value":"82","time":"14:00"},{"value":"78","time":"14:30"},{"value":"81","time":"15:00"}],"heartMax":"90","heartMIn":"60","heartAve":"79","datestring":"2018-08-21"}]
     * max_step : 9635_2018/07/22
     * max_month : 49241_2018/07
     * max_week : 46284_07/22-07/28
     * ox_history : [{"oxdatas":[],"oxMax":"0","oxAve":"0","datestring":"2018-08-21","oxMin":"0"}]
     * sleep_history : [{"durations":"544","quality":"0.977941","datestring":"2018-07-23","sobers":"12","deeps":"216","lights":"316"},{"durations":"407","quality":"0.859951","datestring":"2018-07-24","sobers":"57","deeps":"116","lights":"234"},{"durations":"385","quality":"0.974026","datestring":"2018-07-25","sobers":"10","deeps":"166","lights":"209"},{"durations":"367","quality":"0.940054","datestring":"2018-07-26","sobers":"22","deeps":"133","lights":"212"},{"durations":"411","quality":"0.982968","datestring":"2018-07-27","sobers":"7","deeps":"106","lights":"298"},{"durations":"333","quality":"1.000000","datestring":"2018-07-28","sobers":"0","deeps":"137","lights":"196"},{"durations":"0","quality":"0.000000","datestring":"2018-07-29","sobers":"0","deeps":"0","lights":"0"},{"durations":"0","quality":"0.000000","datestring":"2018-07-30","sobers":"0","deeps":"0","lights":"0"},{"durations":"0","quality":"0.000000","datestring":"2018-07-31","sobers":"0","deeps":"0","lights":"0"},{"durations":"0","quality":"0.000000","datestring":"2018-08-01","sobers":"0","deeps":"0","lights":"0"},{"durations":"0","quality":"0.000000","datestring":"2018-08-02","sobers":"0","deeps":"0","lights":"0"},{"durations":"0","quality":"0.000000","datestring":"2018-08-03","sobers":"0","deeps":"0","lights":"0"},{"durations":"0","quality":"0.000000","datestring":"2018-08-04","sobers":"0","deeps":"0","lights":"0"},{"durations":"0","quality":"0.000000","datestring":"2018-08-05","sobers":"0","deeps":"0","lights":"0"},{"durations":"0","quality":"0.000000","datestring":"2018-08-06","sobers":"0","deeps":"0","lights":"0"},{"durations":"0","quality":"0.000000","datestring":"2018-08-07","sobers":"0","deeps":"0","lights":"0"},{"durations":"0","quality":"0.000000","datestring":"2018-08-08","sobers":"0","deeps":"0","lights":"0"},{"durations":"558","quality":"1.000000","datestring":"2018-08-09","sobers":"0","deeps":"254","lights":"304"},{"durations":"421","quality":"0.978622","datestring":"2018-08-10","sobers":"9","deeps":"180","lights":"232"},{"durations":"0","quality":"0.000000","datestring":"2018-08-11","sobers":"0","deeps":"0","lights":"0"},{"durations":"0","quality":"0.000000","datestring":"2018-08-12","sobers":"0","deeps":"0","lights":"0"},{"durations":"0","quality":"0.000000","datestring":"2018-08-13","sobers":"0","deeps":"0","lights":"0"},{"durations":"0","quality":"0.000000","datestring":"2018-08-14","sobers":"0","deeps":"0","lights":"0"},{"durations":"0","quality":"0.000000","datestring":"2018-08-15","sobers":"0","deeps":"0","lights":"0"},{"durations":"384","quality":"1.000000","datestring":"2018-08-16","sobers":"0","deeps":"143","lights":"241"},{"durations":"0","quality":"0.000000","datestring":"2018-08-17","sobers":"0","deeps":"0","lights":"0"},{"durations":"0","quality":"0.000000","datestring":"2018-08-18","sobers":"0","deeps":"0","lights":"0"},{"durations":"0","quality":"0.000000","datestring":"2018-08-19","sobers":"0","deeps":"0","lights":"0"},{"durations":"0","quality":"0.000000","datestring":"2018-08-20","sobers":"0","deeps":"0","lights":"0"},{"durations":"0","quality":"0.000000","datestring":"2018-08-21","sobers":"0","deeps":"0","lights":"0"}]
     * sport_history : [{"datestring":"2018-07-23","calors":"269","steps":"5290","distances":"4904","steptarget":"10000","durations":"0"}]
     * today_avg :
     * today_calory : 161
     * today_dbp_avg :
     * today_deeps :
     * today_distance : 2026
     * today_duration : 20
     * today_lights :
     * today_highest :
     * today_lowest :
     * today_ox_avg :
     * today_ox_max :
     * today_ox_min :
     * today_sbp_avg :
     * today_sleeps :
     * today_step : 2312
     * today_wakes :
     * sync_time : 2018-08-21 07:04:41
     * today_date : 2018-08-21
     */

    private int user_id;
    //
//    ////////////////////////////////////////////////////////////////////////////////////////
//    //--------------------------------------最佳记录-----------------------------------
//    ////////////////////////////////////////////////////////////////////////////////////////
    private String finish_days;//连续达标天数
    private String max_step;//最佳单日记录
    private String max_month;//最佳月份
    private String max_week;//最佳周

    public DateValue getFinish_days() {
        return JsonUtil.toBean(finish_days, DateValue.class);
    }

    public DateValue getMax_step() {
        return JsonUtil.toBean(max_step, DateValue.class);
    }

    public DateValue getMax_month() {
        return JsonUtil.toBean(max_month, DateValue.class);
    }

    public DateValue getMax_week() {
        return JsonUtil.toBean(max_week, DateValue.class);
    }

    //
//    ////////////////////////////////////////////////////////////////////////////////////////
//    //--------------------------------------历史json-----------------------------------
//    ////////////////////////////////////////////////////////////////////////////////////////
    private String bp_history;//血压历史
    private String ox_history;//血氧历史
    private String heart_history;//心率历史
    private String sleep_history;//睡眠历史
    private String sport_history;//运动历史

    public List<BloodPressureHistory> getBloodPressureHistory() {
        if (TextUtils.isEmpty(bp_history)) {
            return null;
        }
        Type type = new TypeToken<ArrayList<BloodPressureHistory>>() {
        }.getType();
        return JsonUtil.toListBean(bp_history, type);
    }

    public List<BloodOxygenHistory> getBloodOxygenHistory() {
        if (TextUtils.isEmpty(ox_history)) {
            return null;
        }
        Type type = new TypeToken<ArrayList<BloodOxygenHistory>>() {
        }.getType();
        return JsonUtil.toListBean(ox_history, type);
    }



    public List<SportHistory> getSportHistory() {
        if (TextUtils.isEmpty(sport_history)) {
            return null;
        }
        Type type = new TypeToken<ArrayList<SportHistory>>() {
        }.getType();
        return JsonUtil.toListBean(sport_history, type);
    }



    public List<SleepHistory> getSleepHistory() {
        if (sleep_history == null) {
            return null;
        }
        Type type = new TypeToken<ArrayList<SleepHistory>>() {
        }.getType();
        return JsonUtil.toListBean(sleep_history, type);
    }

    public List<HeartHistory> getHeartHistory() {
        if (heart_history == null) {
            return null;
        }
        Type type = new TypeToken<ArrayList<HeartHistory>>() {
        }.getType();
        return JsonUtil.toListBean(heart_history, type);
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    //--------------------------------------今天的数据-----------------------------------
    ////////////////////////////////////////////////////////////////////////////////////////
    private int today_avg;//今天心率平均
    private int today_highest;//今天心率最高
    private int today_lowest;//今天心率最低

    private int today_duration;//今天运动时长
    private int today_calory;//今天卡路里
    private int today_distance;//今天距离值
    private int today_step;//今天步数值

    private int today_ox_avg;//今天血氧平均值
    private int today_ox_max;//今天血氧最大值
    private int today_ox_min;//今天血氧最小值

    private int today_dbp_avg;//今天平均舒张压
    private int today_sbp_avg;//今天平均收缩压

    private int today_sleeps;//今天睡眠时长
    private int today_lights;//今天睡眠浅睡分钟数
    private int today_deeps;//今天睡眠深睡分钟数
    private int today_wakes;//今天睡眠清醒分钟数

    public int getToday_avg() {
        return today_avg;
    }

    public void setToday_avg(int today_avg) {
        this.today_avg = today_avg;
    }

    public int getToday_calory() {
        return today_calory;
    }

    public void setToday_calory(int today_calory) {
        this.today_calory = today_calory;
    }

    public int getToday_dbp_avg() {
        return today_dbp_avg;
    }

    public void setToday_dbp_avg(int today_dbp_avg) {
        this.today_dbp_avg = today_dbp_avg;
    }

    public int getToday_deeps() {
        return today_deeps;
    }

    public void setToday_deeps(int today_deeps) {
        this.today_deeps = today_deeps;
    }

    public int getToday_distance() {
        return today_distance;
    }

    public void setToday_distance(int today_distance) {
        this.today_distance = today_distance;
    }

    public int getToday_duration() {
        return today_duration;
    }

    public void setToday_duration(int today_duration) {
        this.today_duration = today_duration;
    }

    public int getToday_lights() {
        return today_lights;
    }

    public void setToday_lights(int today_lights) {
        this.today_lights = today_lights;
    }

    public int getToday_highest() {
        return today_highest;
    }

    public void setToday_highest(int today_highest) {
        this.today_highest = today_highest;
    }

    public int getToday_lowest() {
        return today_lowest;
    }

    public void setToday_lowest(int today_lowest) {
        this.today_lowest = today_lowest;
    }

    public int getToday_ox_avg() {
        return today_ox_avg;
    }

    public void setToday_ox_avg(int today_ox_avg) {
        this.today_ox_avg = today_ox_avg;
    }

    public int getToday_ox_max() {
        return today_ox_max;
    }

    public void setToday_ox_max(int today_ox_max) {
        this.today_ox_max = today_ox_max;
    }

    public int getToday_ox_min() {
        return today_ox_min;
    }

    public void setToday_ox_min(int today_ox_min) {
        this.today_ox_min = today_ox_min;
    }

    public int getToday_sbp_avg() {
        return today_sbp_avg;
    }

    public void setToday_sbp_avg(int today_sbp_avg) {
        this.today_sbp_avg = today_sbp_avg;
    }

    public int getToday_sleeps() {
        return today_sleeps;
    }

    public void setToday_sleeps(int today_sleeps) {
        this.today_sleeps = today_sleeps;
    }

    public int getToday_step() {
        return today_step;
    }

    public void setToday_step(int today_step) {
        this.today_step = today_step;
    }

    public int getToday_wakes() {
        return today_wakes;
    }

    public void setToday_wakes(int today_wakes) {
        this.today_wakes = today_wakes;
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    //--------------------------------------时间或其他-----------------------------------
    ////////////////////////////////////////////////////////////////////////////////////////
    private String sync_time;
    private String today_date;

    public String getSync_time() {
        return sync_time;
    }

    public void setSync_time(String sync_time) {
        this.sync_time = sync_time;
    }

    public String getToday_date() {
        return today_date;
    }

    public void setToday_date(String today_date) {
        this.today_date = today_date;
    }

    public static class DateValue {
        private String value;
        private String date;

        public DateValue(String value, String date) {
            this.value = value;
            this.date = date;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }
    }

    public static class HeartHistory {

        private String datestring;
        private List<HeartdatasBean> heartdatas;
        private int heartMax;
        private int heartMIn;
        private int heartAve;

        public int getHeartMax() {
            return heartMax;
        }

        public void setHeartMax(int heartMax) {
            this.heartMax = heartMax;
        }

        public int getHeartMIn() {
            return heartMIn;
        }

        public void setHeartMIn(int heartMIn) {
            this.heartMIn = heartMIn;
        }

        public int getHeartAve() {
            return heartAve;
        }

        public void setHeartAve(int heartAve) {
            this.heartAve = heartAve;
        }

        public void setDatestring(String datestring) {
            this.datestring = datestring;
        }

        public void setHeartdatas(List<HeartdatasBean> heartdatas) {
            this.heartdatas = heartdatas;
        }

        public String getDatestring() {
            return datestring;
        }

        public List<HeartdatasBean> getHeartdatas() {
            return heartdatas;
        }

        public static class HeartdatasBean {
            /**
             * value : 90
             * time : 09:30
             */
            private int value;
            private String time;

            public void setValue(int value) {
                this.value = value;
            }

            public void setTime(String time) {
                this.time = time;
            }

            public int getValue() {
                return value;
            }

            public String getTime() {
                return time;
            }
        }
    }

    public static class SleepHistory {

        /**
         * durations : 544
         * datestring : 2018-07-23
         * sobers : 12
         * deeps : 216
         * lights : 316
         */

        private String datestring;
        private int sobers;
        private int deeps;
        private int lights;

        public SleepHistory(String datestring, int sobers, int deeps, int lights) {
            this.datestring = datestring;
            this.sobers = sobers;
            this.deeps = deeps;
            this.lights = lights;
        }

        public String getDatestring() {
            return datestring;
        }

        public int getSobers() {
            return sobers;
        }

        public int getDeeps() {
            return deeps;
        }

        public int getLights() {
            return lights;
        }
    }

    public static class SportHistory {

        /**
         * datestring : 2018-07-23
         * calors : 269
         * steps : 5290
         * distances : 4904
         * steptarget : 10000
         * durations : 0
         */

        private String datestring;
        private int calors;
        private int steps;
        private int distances;
        private int steptarget;
        private int durations;

        public SportHistory(String datestring, int calors, int steps, int distances, int steptarget, int durations) {
            this.datestring = datestring;
            this.calors = calors;
            this.steps = steps;
            this.distances = distances;
            this.steptarget = steptarget;
            this.durations = durations;
        }

        public String getDatestring() {
            return datestring;
        }

        public void setDatestring(String datestring) {
            this.datestring = datestring;
        }

        public int getCalors() {
            return calors;
        }

        public void setCalors(int calors) {
            this.calors = calors;
        }

        public int getSteps() {
            return steps;
        }

        public void setSteps(int steps) {
            this.steps = steps;
        }

        public int getDistances() {
            return distances;
        }

        public void setDistances(int distances) {
            this.distances = distances;
        }

        public int getSteptarget() {
            return steptarget;
        }

        public void setSteptarget(int steptarget) {
            this.steptarget = steptarget;
        }

        public int getDurations() {
            return durations;
        }

        public void setDurations(int durations) {
            this.durations = durations;
        }
    }

    public static class BloodOxygenHistory{

        private String datestring;
        private List<OxdatasBean> oxdatas;
        private int oxAve;
        private int oxMax;
        private int oxMin;

        public int getOxAve() {
            return oxAve;
        }

        public void setOxAve(int oxAve) {
            this.oxAve = oxAve;
        }

        public int getOxMax() {
            return oxMax;
        }

        public void setOxMax(int oxMax) {
            this.oxMax = oxMax;
        }

        public int getOxMin() {
            return oxMin;
        }

        public void setOxMin(int oxMin) {
            this.oxMin = oxMin;
        }

        public void setDatestring(String datestring) {
            this.datestring = datestring;
        }

        public void setOxdatas(List<OxdatasBean> oxdatas) {
            this.oxdatas = oxdatas;
        }

        public String getDatestring() {
            return datestring;
        }

        public List<OxdatasBean> getOxdatas() {
            return oxdatas;
        }

        public static class OxdatasBean {
            /**
             * value : 97
             * time : 10:02
             */

            private int value;
            private String time;

            public void setValue(int value) {
                this.value = value;
            }

            public void setTime(String time) {
                this.time = time;
            }

            public int getValue() {
                return value;
            }

            public String getTime() {
                return time;
            }
        }
    }

    public static class BloodPressureHistory{

        /**
         * bpdatas : [{"time":"10:04","SBPValue":"81","DBPValue":"130"}]
         * datestring : 2018-08-22
         * SBPave : 81 //ios黄建华写错单词,这个代表SBPAvg平均 但他说算了 不改,就这么定, 于是安卓这边也无视
         * DBPave : 130
         */

        private String datestring;
        private int SBPave;
        private int DBPave;
        private List<BpdatasBean> bpdatas;

        public int getDBPave() {
            return DBPave;
        }

        public void setDBPave(int DBPave) {
            this.DBPave = DBPave;
        }

        public int getSBPave() {
            return SBPave;
        }

        public void setSBPave(int SBPave) {
            this.SBPave = SBPave;
        }

        public void setDatestring(String datestring) {
            this.datestring = datestring;
        }

        public void setBpdatas(List<BpdatasBean> bpdatas) {
            this.bpdatas = bpdatas;
        }

        public String getDatestring() {
            return datestring;
        }

        public List<BpdatasBean> getBpdatas() {
            return bpdatas;
        }

        public static class BpdatasBean {
            /**
             * time : 10:04
             * SBPValue : 81
             * DBPValue : 130
             */

            private String time;
            private int SBPValue;
            private int DBPValue;

            public void setTime(String time) {
                this.time = time;
            }

            public void setSBPValue(int SBPValue) {
                this.SBPValue = SBPValue;
            }

            public void setDBPValue(int DBPValue) {
                this.DBPValue = DBPValue;
            }

            public String getTime() {
                return time;
            }

            public int getSBPValue() {
                return SBPValue;
            }

            public int getDBPValue() {
                return DBPValue;
            }
        }
    }
}