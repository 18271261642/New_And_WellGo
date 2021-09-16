package com.truescend.gofit.utils;

import com.sn.app.net.AppNetReq;
import com.sn.app.net.callback.OnResponseListener;
import com.sn.app.net.data.app.bean.StatBean;
import com.sn.app.net.data.base.DefResponseBean;
import com.sn.app.utils.AppUserUtil;
import com.sn.blesdk.db.data.base.SNBLEDao;
import com.sn.blesdk.db.data.health.blood_oxygen.BloodOxygenBean;
import com.sn.blesdk.db.data.health.blood_oxygen.BloodOxygenDao;
import com.sn.blesdk.db.data.health.blood_pressure.BloodPressureBean;
import com.sn.blesdk.db.data.health.blood_pressure.BloodPressureDao;
import com.sn.blesdk.db.data.health.heart_rate.HeartRateBean;
import com.sn.blesdk.db.data.health.heart_rate.HeartRateDao;
import com.sn.blesdk.db.data.sleep.SleepBean;
import com.sn.blesdk.db.data.sleep.SleepDao;
import com.sn.blesdk.db.data.sport.SportBean;
import com.sn.blesdk.db.data.sport.SportDao;
import com.sn.net.utils.JsonUtil;
import com.sn.utils.DateUtil;
import com.sn.utils.IF;
import com.sn.utils.SNLog;
import com.sn.utils.task.SNAsyncTask;
import com.sn.utils.task.SNVTaskCallBack;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 作者:东芝(2018/8/22).
 * 功能:上传今天的数据给朋友看
 */

public class UploadTodayDataToMyFriendsSeeHelper {

    private static UploadTodayDataToMyFriendsSeeHelper helper;
    private int today_heart_avg = 0;
    private int today_heart_max = 0;
    private int today_heart_min = 0;
    private int today_sport_duration = 0;
    private int today_sport_calories = 0;
    private int today_sport_distance = 0;
    private int today_sport_step = 0;
    private int today_oxygen_avg = 0;
    private int today_oxygen_max = 0;
    private int today_oxygen_min = 0;
    private int today_diastolic_avg = 0;
    private int today_systolic_avg = 0;
    private int today_sleep_duration = 0;
    private int today_sleep_light = 0;
    private int today_sleep_deep = 0;
    private int today_sleep_sober = 0;
    private String blood_pressure_history;
    private String blood_oxygen_history;
    private String heart_history;
    private String sleep_history;
    private String sport_history;
    private String stat_continuous_best_days;
    private String stat_best_day;
    private String stat_best_month;
    private String stat_best_week;
    private String today_date;

    public static void upload(){
        if (helper == null) {
            helper = new UploadTodayDataToMyFriendsSeeHelper();
        }
        helper.upload_base();
    }

    private void upload_base() {
        SNAsyncTask.execute(new SNVTaskCallBack() {
            @Override
            public void run() throws Throwable {
                reset();
                today_date = DateUtil.getCurrentDate(DateUtil.YYYY_MM_DD);
                setBestStatData();
                setSportData();
                setSleepData();
                setHeartRateData();
                setBloodOxygenData();
                setBloodPressureData();
            }

            @Override
            public void done() {
                AppNetReq.getApi()
                        .uploadTodayDataToMyFriendsSee(
                                today_heart_avg,
                                today_heart_max,
                                today_heart_min,
                                today_sport_duration,
                                today_sport_calories,
                                today_sport_distance,
                                today_sport_step,
                                today_oxygen_avg,
                                today_oxygen_max,
                                today_oxygen_min,
                                today_diastolic_avg,
                                today_systolic_avg,
                                today_sleep_duration,
                                today_sleep_light,
                                today_sleep_deep,
                                today_sleep_sober,
                                stat_continuous_best_days,
                                stat_best_day,
                                stat_best_month,
                                stat_best_week,
                                blood_pressure_history,
                                blood_oxygen_history,
                                heart_history,
                                sleep_history,
                                sport_history,
                                today_date
                        )
                .enqueue(new OnResponseListener<DefResponseBean>() {
                    @Override
                    public void onResponse(DefResponseBean body) throws Throwable {
                        SNLog.i("上传用户统计成功");
                    }

                    @Override
                    public void onFailure(int ret, String msg) {
                        SNLog.i("上传用户统计失败"+msg);
                    }
                });
            }
        });

    }
    private void setBloodPressureData() {

        List<StatBean.BloodPressureHistory> histories = new ArrayList<>();
        StatBean.BloodPressureHistory history = new StatBean.BloodPressureHistory();
        List<StatBean.BloodPressureHistory.BpdatasBean> beans = new ArrayList<>();
        String dateToday = DateUtil.getCurrentDate(DateUtil.YYYY_MM_DD);
        history.setDatestring(dateToday);
        try {
            BloodPressureDao bloodPressureDao = BloodPressureDao.get(BloodPressureDao.class);
            List<BloodPressureBean> bloodPressureBeans = bloodPressureDao.queryForDay(AppUserUtil.getUser().getUser_id(),dateToday);
            if (!IF.isEmpty(bloodPressureBeans)) {
                BloodPressureBean bloodPressureBean = bloodPressureBeans.get(0);
                today_diastolic_avg = bloodPressureBean.getBloodDiastolic();
                today_systolic_avg = bloodPressureBean.getBloodSystolic();

                history.setDBPave(today_diastolic_avg);
                history.setSBPave(today_systolic_avg);

                ArrayList<BloodPressureBean.BloodPressureDetailsBean> bloodPressureDetails = bloodPressureBean.getBloodPressureDetails();
                for (BloodPressureBean.BloodPressureDetailsBean bean : bloodPressureDetails) {
                    StatBean.BloodPressureHistory.BpdatasBean bpdatasBean = new StatBean.BloodPressureHistory.BpdatasBean();
                    bpdatasBean.setTime(DateUtil.convertStringToNewString(DateUtil.YYYY_MM_DD_HH_MM_SS, DateUtil.HH_MM, bean.getDateTime()));
                    bpdatasBean.setDBPValue(bean.getBloodDiastolic());
                    bpdatasBean.setSBPValue(bean.getBloodSystolic());
                    beans.add(bpdatasBean);
                }
            }
        } catch (Exception ignored) {
        }
        history.setBpdatas(beans);
        histories.add(history);
        blood_pressure_history = JsonUtil.toJson(histories);
    }

    /**
     * 血氧
     */
    private void setBloodOxygenData() {

        List<StatBean.BloodOxygenHistory> histories = new ArrayList<>();
        StatBean.BloodOxygenHistory history = new StatBean.BloodOxygenHistory();
        List<StatBean.BloodOxygenHistory.OxdatasBean> beans = new ArrayList<>();

        String dateToday = DateUtil.getCurrentDate(DateUtil.YYYY_MM_DD);
        history.setDatestring(dateToday);
        try {
            BloodOxygenDao bloodOxygenDao = BloodOxygenDao.get(BloodOxygenDao.class);
            List<BloodOxygenBean> bloodOxygenBeans = bloodOxygenDao.queryForDay(AppUserUtil.getUser().getUser_id(),dateToday);
            if (!IF.isEmpty(bloodOxygenBeans)) {
                BloodOxygenBean bloodOxygenBean = bloodOxygenBeans.get(0);
                today_oxygen_avg = bloodOxygenBean.getAvg();
                today_oxygen_max = bloodOxygenBean.getMax();
                today_oxygen_min = bloodOxygenBean.getMin();

                history.setOxAve(today_oxygen_avg);
                history.setOxMax(today_oxygen_max);
                history.setOxMin(today_oxygen_min);

                ArrayList<BloodOxygenBean.BloodOxygenDetailsBean> bloodOxygenDetails = bloodOxygenBean.getBloodOxygenDetails();
                for (BloodOxygenBean.BloodOxygenDetailsBean bean : bloodOxygenDetails) {
                    StatBean.BloodOxygenHistory.OxdatasBean oxdatasBean = new StatBean.BloodOxygenHistory.OxdatasBean();
                    oxdatasBean.setTime(DateUtil.convertStringToNewString(DateUtil.YYYY_MM_DD_HH_MM_SS, DateUtil.HH_MM, bean.getDateTime()));
                    oxdatasBean.setValue(bean.getValue());
                    beans.add(oxdatasBean);
                }
            }
        } catch (Exception ignored) {
        }
        history.setOxdatas(beans);
        histories.add(history);
        blood_oxygen_history = JsonUtil.toJson(histories);
    }

    /**
     * 心率
     */
    private void setHeartRateData() {

        List<StatBean.HeartHistory> histories = new ArrayList<>();
        StatBean.HeartHistory history = new StatBean.HeartHistory();
        List<StatBean.HeartHistory.HeartdatasBean> beans = new ArrayList<>();

        String dateToday = DateUtil.getCurrentDate(DateUtil.YYYY_MM_DD);
        history.setDatestring(dateToday);

        try {
            HeartRateDao heartRateDao = HeartRateDao.get(HeartRateDao.class);
            List<HeartRateBean> heartRateBeans = heartRateDao.queryForDay(AppUserUtil.getUser().getUser_id(),dateToday);
            if (!IF.isEmpty(heartRateBeans)) {
                HeartRateBean heartRateBean = heartRateBeans.get(0);
                today_heart_avg = heartRateBean.getAvg();
                today_heart_max = heartRateBean.getMax();
                today_heart_min = heartRateBean.getMin();

                history.setHeartAve(today_heart_avg);
                history.setHeartMax(today_heart_max);
                history.setHeartMIn(today_heart_min);

                ArrayList<HeartRateBean.HeartRateDetailsBean> heartRateDetails = heartRateBean.getHeartRateDetails();
                for (HeartRateBean.HeartRateDetailsBean bean : heartRateDetails) {
                    StatBean.HeartHistory.HeartdatasBean heartdatasBean = new StatBean.HeartHistory.HeartdatasBean();
                    heartdatasBean.setTime(DateUtil.convertStringToNewString(DateUtil.YYYY_MM_DD_HH_MM_SS, DateUtil.HH_MM, bean.getDateTime()));
                    heartdatasBean.setValue(bean.getValue());
                    beans.add(heartdatasBean);
                }
            }
        } catch (Exception ignored) {
        }
        history.setHeartdatas(beans);
        histories.add(history);
        heart_history = JsonUtil.toJson(histories);
    }

    /**
     * 睡眠数据
     */
    private void setSleepData() {
        List<StatBean.SleepHistory> histories = new ArrayList<>();
        try {
            SleepDao sleepDao = SNBLEDao.get(SleepDao.class);
            Calendar calendar = DateUtil.getCurrentCalendarBegin();
            calendar.add(Calendar.DAY_OF_MONTH, -30);
            String date30DayAgo = DateUtil.getDate(DateUtil.YYYY_MM_DD, calendar);
            String dateToday = DateUtil.getCurrentDate(DateUtil.YYYY_MM_DD);
            List<SleepBean> sleepBeans = sleepDao.queryForBetween(AppUserUtil.getUser().getUser_id(), date30DayAgo, dateToday);
            if (!IF.isEmpty(sleepBeans)) {

                for (SleepBean sleepBean : sleepBeans) {
                    //如果是今天
                    if (sleepBean.getDate().equalsIgnoreCase(dateToday)) {
                        today_sleep_deep = sleepBean.getDeepTotal();
                        today_sleep_light = sleepBean.getLightTotal();
                        today_sleep_sober = sleepBean.getSoberTotal();
                        today_sleep_duration = today_sleep_deep+today_sleep_light+today_sleep_sober;
                    }
                    histories.add(new StatBean.SleepHistory(
                            sleepBean.getDate(),
                            sleepBean.getSoberTotal(),
                            sleepBean.getDeepTotal(),
                            sleepBean.getLightTotal())

                    );
                }
            }
        } catch (Exception ignored) {
        }
        sleep_history = JsonUtil.toJson(histories);
    }

    /**
     * 运动数据
     */
    private void setSportData() {
        List<StatBean.SportHistory> histories = new ArrayList<>();
        try {
            SportDao sportDao = SNBLEDao.get(SportDao.class);
            Calendar calendar = DateUtil.getCurrentCalendarBegin();
            calendar.add(Calendar.DAY_OF_MONTH, -30);
            String date30DayAgo = DateUtil.getDate(DateUtil.YYYY_MM_DD, calendar);
            String dateToday = DateUtil.getDate(DateUtil.YYYY_MM_DD, DateUtil.getCurrentCalendar());
            List<SportBean> sportBeans = sportDao.queryForBetween(AppUserUtil.getUser().getUser_id(), date30DayAgo, dateToday);
            if (!IF.isEmpty(sportBeans)) {


                for (SportBean sportBean : sportBeans) {
                    //如果是今天
                    if (sportBean.getDate().equalsIgnoreCase(dateToday)) {
                        today_sport_step = sportBean.getStepTotal();
                        today_sport_calories = sportBean.getCalorieTotal();
                        today_sport_distance = sportBean.getDistanceTotal();
                    }
                    histories.add(new StatBean.SportHistory(
                            sportBean.getDate(),
                            sportBean.getCalorieTotal(),
                            sportBean.getStepTotal(),
                            sportBean.getDistanceTotal(),
                            sportBean.getStepTarget(),
                            0)

                    );
                }

            }
        } catch (Exception ignored) {
        }
        sport_history = JsonUtil.toJson(histories);
    }

    /**
     * 最佳啥啥啥...
     */
    private void setBestStatData() {

        try {
            SportDao sportDao = SportDao.get(SportDao.class);
            Map<String, Long> mBestDays = sportDao.queryBestDay(AppUserUtil.getUser().getUser_id());
            if (!mBestDays.isEmpty()) {
                Iterator<Map.Entry<String, Long>> iterator = mBestDays.entrySet().iterator();
                if (iterator.hasNext()) {
                    Map.Entry<String, Long> next = iterator.next();
                    String date = next.getKey();
                    long stepTotal = next.getValue();

                    String bestDate = DateUtil.convertStringToNewString(DateUtil.YYYY_MM_DD, CalendarUtil.YYYYMMDD, date);
                    stat_best_day = JsonUtil.toJson(new StatBean.DateValue(String.valueOf(stepTotal), bestDate));
                }
            }
            Map<String[], Long> mBestContinueDay = sportDao.queryBestContinueDay(AppUserUtil.getUser().getUser_id());
            if (!mBestContinueDay.isEmpty()) {
                Iterator<Map.Entry<String[], Long>> iterator = mBestContinueDay.entrySet().iterator();
                if (iterator.hasNext()) {
                    Map.Entry<String[], Long> next = iterator.next();
                    String dates[] = next.getKey();
                    long stepTotal = next.getValue();

                    String startDate = DateUtil.convertStringToNewString(DateUtil.YYYY_MM_DD, CalendarUtil.MMDD, dates[0]);
                    String endDate = DateUtil.convertStringToNewString(DateUtil.YYYY_MM_DD, CalendarUtil.MMDD, dates[1]);
                    stat_continuous_best_days = JsonUtil.toJson(new StatBean.DateValue(String.valueOf(stepTotal), String.format("%s-%s", startDate, endDate)));
                }
            }
            Map<String[], Long> mBestWeeks = sportDao.queryBestWeek(AppUserUtil.getUser().getUser_id());
            if (!mBestWeeks.isEmpty()) {
                Iterator<Map.Entry<String[], Long>> iterator = mBestWeeks.entrySet().iterator();
                if (iterator.hasNext()) {
                    Map.Entry<String[], Long> next = iterator.next();
                    String dates[] = next.getKey();
                    long stepTotal = next.getValue();

                    Calendar calendar = DateUtil.convertStringToCalendar(DateUtil.YYYY_MM_DD, dates[0]);

                    String startDate = DateUtil.convertCalendarToString(CalendarUtil.MMDD, DateUtil.getWeekFirstDate(calendar));
                    String endDate = DateUtil.convertCalendarToString(CalendarUtil.MMDD, DateUtil.getWeekLastDate(calendar));
                    stat_best_week = JsonUtil.toJson(new StatBean.DateValue(String.valueOf(stepTotal), String.format("%s-%s", startDate, endDate)));
                }
            }
            Map<String[], Long> mBestMonths = sportDao.queryBestMonth(AppUserUtil.getUser().getUser_id());
            if (!mBestMonths.isEmpty()) {
                Iterator<Map.Entry<String[], Long>> iterator = mBestMonths.entrySet().iterator();
                if (iterator.hasNext()) {
                    Map.Entry<String[], Long> next = iterator.next();
                    String dates[] = next.getKey();
                    long stepTotal = next.getValue();
                    String endDate = DateUtil.convertStringToNewString(DateUtil.YYYY_MM_DD, CalendarUtil.YYYYMM, dates[1]);
                    stat_best_month = JsonUtil.toJson(new StatBean.DateValue(String.valueOf(stepTotal), endDate));
                }
            }
        } catch (Exception e) {
            String emptyValue = JsonUtil.toJson(new StatBean.DateValue("", ""));
            stat_continuous_best_days = emptyValue;
            stat_best_day = emptyValue;
            stat_best_month = emptyValue;
            stat_best_week = emptyValue;
        }
    }


    private void reset() {
        today_heart_avg = 0;
        today_heart_max = 0;
        today_heart_min = 0;
        today_sport_duration = 0;
        today_sport_calories = 0;
        today_sport_distance = 0;
        today_sport_step = 0;
        today_oxygen_avg = 0;
        today_oxygen_max = 0;
        today_oxygen_min = 0;
        today_diastolic_avg = 0;
        today_systolic_avg = 0;
        today_sleep_duration = 0;
        today_sleep_light = 0;
        today_sleep_deep = 0;
        today_sleep_sober = 0;
        blood_pressure_history = "";
        blood_oxygen_history = "";
        heart_history = "";
        sleep_history = "";
        sport_history = "";
        stat_continuous_best_days = "";
        stat_best_day = "";
        stat_best_month = "";
        stat_best_week = "";
        today_date = "";
    }

}
