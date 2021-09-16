package com.sn.blesdk.db.data.sport;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.dao.Dao;
import com.sn.blesdk.control.SportDataDecodeHelper;
import com.sn.blesdk.db.data.base.SNBLEDao;
import com.sn.utils.DateUtil;
import com.sn.utils.IF;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * 作者:东芝(2017/11/27).
 * 功能:步数大数据 数据库操作类
 */

public class SportDao extends SNBLEDao<SportBean, Integer> {

    /**
     * 连续达标天数
     * 这里不用纯数据库语法实现,有点麻烦,后面再考虑实现
     *
     * @param user_id
     * @return
     */
    public Map<String[], Long> queryBestContinueDay(int user_id) {
        HashMap<String[], Long> map = new HashMap<>();
        //自动排序的Map
        TreeMap<Integer, List<SportBean>> mBestDays = new TreeMap<>(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o2 - o1;
            }
        });
        List<SportBean> sportBeans = queryForAll(user_id);
        int size = sportBeans.size();
        int count = 0;
        String last_date = null;
        for (int i = 0; i < size; i++) {
            SportBean val = sportBeans.get(i);
            int stepTotal = val.getStepTotal();
            int stepTarget = val.getStepTarget();
            String date = val.getDate();
            //断续判断,出现达标断点 则重置计数
            if (stepTotal < stepTarget || (last_date != null && DateUtil.getDateOffset(date, last_date) > 1)) {
                count = 0;
            }
            last_date = date;
            //已达标
            if (stepTotal >= stepTarget) {
                //取出历史 ,如果没有 就new,如果有则add
                List<SportBean> temp = mBestDays.get(count);
                if (temp == null) {
                    temp = new ArrayList<>();
                }
                temp.add(val);
                mBestDays.remove(count);
                count++;
                //存放最新统计
                mBestDays.put(count, temp);
            }
        }

        int mBestDay = 0;
        String mBestDayBeginDate = null;
        String mBestDayEndData = null;
        for (int key : mBestDays.keySet()) {
            List<SportBean> beans = mBestDays.get(key);
            if (beans != null && !beans.isEmpty()) {
                mBestDay = beans.size();
                mBestDayBeginDate = beans.get(0).getDate();
                mBestDayEndData = beans.get(mBestDay - 1).getDate();
                break;
            }
        }
        if (mBestDay > 0 && mBestDayBeginDate != null && mBestDayEndData != null) {
            map.put(new String[]{mBestDayBeginDate, mBestDayEndData}, (long) mBestDay);
        }

        return map;
    }


    /**
     * 最佳月份
     *
     * @param user_id
     * @return
     */
    public Map<String[], Long> queryBestMonth(int user_id) {
        //select * from (select id, user_id,sum(stepTotal) total,count(*) days, min(date) start_date,max(date) end_date,group_concat(date) from SportBean where user_id=698 group by strftime('%Y-%m',date))t order by total desc ,end_date desc limit 0,1  String sql = String.format(
        String sql = "select * from (select id, user_id,sum(stepTotal) total,count(*) days, min(date) start_date,max(date) end_date,group_concat(date) from SportBean where user_id=" + user_id + " group by strftime('%Y-%m',date))t order by total desc ,end_date desc limit 0,1";

        HashMap<String[], Long> map = new HashMap<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            long stepTotal = cursor.getLong(cursor.getColumnIndex("total"));
            String start_date = cursor.getString(cursor.getColumnIndex("start_date"));
            String end_date = cursor.getString(cursor.getColumnIndex("end_date"));
            map.put(new String[]{start_date, end_date}, stepTotal);
            break;
        }
        cursor.close();
        return map;
    }


    /**
     * 最佳星期
     *
     * @param user_id
     * @return
     */
    public Map<String[], Long> queryBestWeek(int user_id) {
        //select * from (select id,user_id,sum(stepTotal) total,count(*) days, min(date) start_date,max(date) end_date from SportBean group by strftime('%W',date))t where t.user_id = 698 order by total desc ,end_date desc limit 0,1

        String sql = "select * from (select id,user_id,sum(stepTotal) total,count(*) days, min(date) start_date,max(date) end_date,group_concat(date)  from SportBean where user_id = " + user_id + " group by strftime('%Y-%m-%d %W',date,'weekday 6'))t order by total desc ,end_date desc limit 0,1";
        HashMap<String[], Long> map = new HashMap<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            long stepTotal = cursor.getLong(cursor.getColumnIndex("total"));
            String start_date = cursor.getString(cursor.getColumnIndex("start_date"));
            String end_date = cursor.getString(cursor.getColumnIndex("end_date"));
            map.put(new String[]{start_date, end_date}, stepTotal);
            break;
        }
        cursor.close();
        return map;
    }

    /**
     * 查询最佳记录
     *
     * @param user_id
     * @return
     */
    public Map<String, Long> queryBestDay(int user_id) {
        //select stepTotal,date from  SportBean where stepTotal=(select max(stepTotal) from SportBean where user_id=698)  and user_id=698
        String sql = "select stepTotal,date from SportBean where stepTotal=(select max(stepTotal) from SportBean where user_id=" + user_id + ")  and user_id=" + user_id;
        HashMap<String, Long> map = new HashMap<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            long stepTotal = cursor.getLong(cursor.getColumnIndex(SportBean.COLUMN_STEP_TOTAL));
            String date = cursor.getString(cursor.getColumnIndex(SportBean.COLUMN_DATE));
            map.put(date, stepTotal);
            break;
        }
        cursor.close();
        return map;
    }


    public boolean updateStepTarget(int user_id, String date, int stepTarget) throws SQLException {
        Dao<SportBean, Integer> dao = getDao();
        //语法: update SportBean set isUploaded=1 where date='2018-01-21';
        int executeRawNoArgs = dao.executeRawNoArgs(String.format(Locale.ENGLISH, "update %s set %s=%d where %s='%s' and %s=%d;", dao.getTableName(), SportBean.COLUMN_STEP_TARGET, stepTarget, SportBean.COLUMN_DATE, date, SportBean.COLUMN_USER_ID, user_id));
        return executeRawNoArgs > 0;
    }


    /**
     * 时间间隔
     */
    private static final int EVERY_MINUTES = SportDataDecodeHelper.EVERY_MINUTES;
    /**
     * 一天的运动数据最大长度
     */
    private static final int DAY_SPORT_LENGTH = SportDataDecodeHelper.DAY_SPORT_LENGTH;

    public void insertOrUpdate(int user_id, int sportTarget, String mac, int real_time_step, int real_time_distance, int real_time_calorie) throws java.sql.SQLException {

        Calendar currentCalendar = DateUtil.getCurrentCalendar();
        String date = DateUtil.getDate(DateUtil.YYYY_MM_DD, currentCalendar);
        int timeIndex = DateUtil.convertTimeToIndex(currentCalendar, EVERY_MINUTES);
        List<SportBean> queryForDay = queryForDay(user_id, date);

        if (IF.isEmpty(queryForDay)) {//没有历史数据时 却先拿到实时步数  我们得造一个'假'的历史步数 这种情况少有,基本连上了 就被历史步数覆盖了,但还是得考虑
            SportBean sportBean = new SportBean();
            sportBean.setUser_id(user_id);
            sportBean.setStepTarget(sportTarget);
            sportBean.setMac(mac);
            //把该天的起始位置 的时间去掉时分秒 只要日期,然后昨晚该天的数据  这个SportBean.COLUMN_DATE 是唯一的
            sportBean.setDate(date);//实时数据日期为当天
            sportBean.setSteps(createSteps(real_time_step, timeIndex));
            sportBean.setCalories(createCalories(real_time_calorie, timeIndex));
            sportBean.setDistances(createDistances(real_time_distance, timeIndex));
            //设置总数数据 供快速查询
            sportBean.setStepTotal(SportDataDecodeHelper.getTotal(sportBean.getSteps()));
            sportBean.setCalorieTotal(SportDataDecodeHelper.getTotal(sportBean.getCalories()));
            sportBean.setDistanceTotal(SportDataDecodeHelper.getTotal(sportBean.getDistances()));
            //更新或插入
            insert(sportBean);
        } else {
            SportBean mOldSportBean = queryForDay.get(0);
            mOldSportBean.setStepTarget(sportTarget);
            SportBean.StepBean stepBean = mOldSportBean.getSteps().get(timeIndex);
            SportBean.CalorieBean calorieBean = mOldSportBean.getCalories().get(timeIndex);
            SportBean.DistanceBean distanceBean = mOldSportBean.getDistances().get(timeIndex);
            //单次
            int singleStep = real_time_step - (mOldSportBean.getStepTotal() - stepBean.getValue());
            int singleCalorie = real_time_calorie - (mOldSportBean.getCalorieTotal() - calorieBean.getValue());
            int singleDistance = real_time_distance - (mOldSportBean.getDistanceTotal() - distanceBean.getValue());


            //重新把实时数据合并放进去
            stepBean.setValue(singleStep);
            calorieBean.setValue(singleCalorie);
            distanceBean.setValue(singleDistance);

            mOldSportBean.setStepTotal(SportDataDecodeHelper.getTotal(mOldSportBean.getSteps()));
            mOldSportBean.setCalorieTotal(SportDataDecodeHelper.getTotal(mOldSportBean.getCalories()));
            mOldSportBean.setDistanceTotal(SportDataDecodeHelper.getTotal(mOldSportBean.getDistances()));

            //更新或插入
            insertOrUpdate(user_id, mOldSportBean);
        }
    }

    /**
     * 把实时数据插入到实际历史数据index的里
     *
     * @param val
     * @param timeIndex
     * @return
     */
    private ArrayList<SportBean.StepBean> createSteps(int val, int timeIndex) {
        ArrayList<SportBean.StepBean> list = new ArrayList<>(DAY_SPORT_LENGTH);
        for (int i = 0; i < DAY_SPORT_LENGTH; i++) {
            DateUtil.HMS hms = DateUtil.convertIndexToTime(i, EVERY_MINUTES);
            Calendar calendar = DateUtil.getCurrentCalendar();
            calendar.set(Calendar.HOUR_OF_DAY, hms.getHour());
            calendar.set(Calendar.MINUTE, hms.getMinute());
            calendar.set(Calendar.SECOND, hms.getSecond());
            String date = DateUtil.getDate(DateUtil.YYYY_MM_DD_HH_MM_SS, calendar);
            if (i == timeIndex) {
                list.add(new SportBean.StepBean(i, date, val));
            } else {
                list.add(new SportBean.StepBean(i, date, 0));
            }
        }
        return list;
    }

    /**
     * 把实时数据插入到实际历史数据index的里
     *
     * @param val
     * @param timeIndex
     * @return
     */
    private ArrayList<SportBean.DistanceBean> createDistances(int val, int timeIndex) {
        ArrayList<SportBean.DistanceBean> list = new ArrayList<>(DAY_SPORT_LENGTH);
        for (int i = 0; i < DAY_SPORT_LENGTH; i++) {
            DateUtil.HMS hms = DateUtil.convertIndexToTime(i, EVERY_MINUTES);
            Calendar calendar = DateUtil.getCurrentCalendar();
            calendar.set(Calendar.HOUR_OF_DAY, hms.getHour());
            calendar.set(Calendar.MINUTE, hms.getMinute());
            calendar.set(Calendar.SECOND, hms.getSecond());
            String date = DateUtil.getDate(DateUtil.YYYY_MM_DD_HH_MM_SS, calendar);
            if (i == timeIndex) {
                list.add(new SportBean.DistanceBean(i, date, val));
            } else {
                list.add(new SportBean.DistanceBean(i, date, 0));
            }
        }
        return list;
    }

    /**
     * 把实时数据插入到实际历史数据index的里
     *
     * @param val
     * @param timeIndex
     * @return
     */
    private ArrayList<SportBean.CalorieBean> createCalories(int val, int timeIndex) {
        ArrayList<SportBean.CalorieBean> list = new ArrayList<>(DAY_SPORT_LENGTH);
        for (int i = 0; i < DAY_SPORT_LENGTH; i++) {
            DateUtil.HMS hms = DateUtil.convertIndexToTime(i, EVERY_MINUTES);
            Calendar calendar = DateUtil.getCurrentCalendar();
            calendar.set(Calendar.HOUR_OF_DAY, hms.getHour());
            calendar.set(Calendar.MINUTE, hms.getMinute());
            calendar.set(Calendar.SECOND, hms.getSecond());
            String date = DateUtil.getDate(DateUtil.YYYY_MM_DD_HH_MM_SS, calendar);
            if (i == timeIndex) {
                list.add(new SportBean.CalorieBean(i, date, val));
            } else {
                list.add(new SportBean.CalorieBean(i, date, 0));
            }
        }
        return list;
    }
}
