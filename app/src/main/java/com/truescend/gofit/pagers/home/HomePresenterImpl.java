package com.truescend.gofit.pagers.home;

import com.sn.app.db.data.config.bean.UnitConfig;
import com.sn.app.db.data.diet.DietBean;
import com.sn.app.db.data.diet.DietDao;
import com.sn.app.db.data.user.UserBean;
import com.sn.app.net.data.app.bean.MealBean;
import com.sn.app.net.data.app.bean.WeatherBean;
import com.sn.app.net.data.app.bean.WeatherListBean;
import com.sn.app.storage.WeatherStorage;
import com.sn.app.utils.AppUnitUtil;
import com.sn.app.utils.AppUserUtil;
import com.sn.blesdk.ble.SNBLEHelper;
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
import com.sn.blesdk.db.data.sport_mode.SportModeBean;
import com.sn.blesdk.db.data.sport_mode.SportModeDao;
import com.sn.blesdk.utils.eventbus.SNBLEEvent;
import com.sn.utils.DateUtil;
import com.sn.utils.IF;
import com.sn.utils.SNLog;
import com.sn.utils.eventbus.SNEvent;
import com.sn.utils.eventbus.SNEventBus;
import com.sn.utils.task.SNAsyncTask;
import com.sn.utils.task.SNVTaskCallBack;
import com.truescend.gofit.App;
import com.truescend.gofit.R;
import com.truescend.gofit.net.DietDataNetworkSyncHelper;
import com.truescend.gofit.pagers.base.BasePresenter;
import com.truescend.gofit.utils.AppEvent;
import com.truescend.gofit.utils.ResUtil;
import com.truescend.gofit.utils.SleepDataUtil;
import com.truescend.gofit.utils.SportModeTypeUtil;
import com.truescend.gofit.utils.UnitConversion;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * 作者:东芝(2017/11/16).
 * 功能:主页Presenter(运动睡眠)
 */
public class HomePresenterImpl extends BasePresenter<IHomeContract.IView> implements IHomeContract.IPresenter {

    private IHomeContract.IView view;


    public HomePresenterImpl(IHomeContract.IView view) {
        this.view = view;

    }

    @Override
    public void requestLoadStepChart(final Calendar calendar) {

        SNAsyncTask.execute(new SNVTaskCallBack() {
            private int targetStepValue;
            private int currentStepValue;
            private float distanceTotal;
            private float caloriesTotal;
            private String distanceUnit;
            private List<Integer> list = new ArrayList<>();

            @Override
            public void prepare() {
                distanceUnit = ResUtil.getString(R.string.course) + " " + ResUtil.getString(R.string.unit_km);
            }

            @Override
            public void run() throws Throwable {

                SportDao sportDao = SNBLEDao.get(SportDao.class);
                String date = DateUtil.getDate(DateUtil.YYYY_MM_DD, calendar);
                List<SportBean> sportBeans = sportDao.queryForDay(AppUserUtil.getUser().getUser_id(), date);
                int stepTotal = 0;
                if (!IF.isEmpty(sportBeans)) {
                    SportBean sportBean = sportBeans.get(0);
                    List<SportBean.StepBean> steps = sportBean.getSteps();

                    for (SportBean.StepBean step : steps) {
                        int stepValue = step.getValue();
                        list.add(stepValue);
                    }
                    stepTotal += sportBean.getStepTotal();
                    targetStepValue = sportBean.getStepTarget();
                    currentStepValue = sportBean.getStepTotal();

                    // 所有的先微设备 返回的实时数据 需要注意的是
                    //1.实时步数返回的距离 单位是 当设置为公制 则返回米,除1000等于公里,英制则返回 的是 英里*1000 需要除1000 等于英里
                    //2.返回的卡路里是千卡 (大卡)
                    UnitConfig unitConfig = AppUnitUtil.getUnitConfig();
                    //如果单位是英里,则需要转一下
                    if (unitConfig.distanceUnit == UnitConfig.DISTANCE_MILES) {
                        //我不是说了 英制时手环会返回英里吗 为什么我还要当它还是千米再转英里一下? 因为 我在合并之前 就把英里转成千米了 现在要转回来 具体看SportDataDecodeHelper.asyncSaveRealTimeData
                        //为什么这么做 因为 详情界面 要显示统计 统计总距离 我不能转来转去  干脆在接收到数据就转好
                        float miles = UnitConversion.kmToMiles(sportBean.getDistanceTotal());
                        distanceTotal = UnitConversion.toBandK(miles);
                        distanceUnit = ResUtil.getString(R.string.course) + " " + ResUtil.getString(R.string.unit_mile);
                    } else {
                        distanceTotal = UnitConversion.toBandK(sportBean.getDistanceTotal());
                        distanceUnit = ResUtil.getString(R.string.course) + " " + ResUtil.getString(R.string.unit_km);
                    }
                    caloriesTotal = sportBean.getCalorieTotal();
                }
                SNLog.i("查询 Date=%s,步数:%d", date, stepTotal);
            }

            @Override
            public void done() {
                if (DateUtil.equalsToday(App.getSelectedCalendar())) {
                    SNEventBus.sendEvent(AppEvent.EVENT_UPDATED_NOTIFICATION_TITLE, String.format(Locale.CHINESE, ResUtil.getString(R.string.content_step_format), currentStepValue));
                }
                if (isUIEnable()) {
                    view.onUpdateStepChartData(targetStepValue, currentStepValue, distanceTotal, caloriesTotal, distanceUnit, list);
                }
            }
        });

    }

    @Override
    public void requestLoadSleepItemData(final Calendar calendar) {

        SNAsyncTask.execute(new SNVTaskCallBack() {
            private CharSequence title;
            private CharSequence content;
            private String quality;
            private int hours;
            private int minutes;
            private String formatTitle = "%d<small>%s</small> %d<small>%s</small>";
            private String formatContent = "%s %s <small>%s</small>";
            private String unit_h;
            private String unit_m;
            private String contentType;

            @Override
            public void prepare() {
                hours = 0;
                minutes = 0;
                quality = ResUtil.getString(R.string.content_no_data);
                contentType = ResUtil.getString(R.string.content_sleep_quality);
                unit_h = ResUtil.getString(R.string.unit_hours);
                unit_m = ResUtil.getString(R.string.unit_min);

                content = ResUtil.formatHtml("%s %s", contentType, quality);
                title = ResUtil.formatHtml(formatTitle, hours, unit_h, minutes, unit_m);
            }

            @Override
            public void run() throws Throwable {

                SleepDao sleepDao = SNBLEDao.get(SleepDao.class);
                String date = DateUtil.getDate(DateUtil.YYYY_MM_DD, calendar);
                List<SleepBean> sleepBeans = sleepDao.queryForDay(AppUserUtil.getUser().getUser_id(), date);
                if (!IF.isEmpty(sleepBeans)) {
                    SleepBean sleepBean = sleepBeans.get(0);
                    //总睡眠
                    int sleepTimeTotal = SleepDataUtil.getSleepTotal(sleepBean.getDeepTotal(), sleepBean.getLightTotal(), sleepBean.getSoberTotal());
                    if (sleepTimeTotal == 0) {
                        return;//无数据
                    }
                    if (sleepTimeTotal > 1440) {
                        SNLog.i("睡眠显示:数据异常,用户睡眠时长为" + (sleepTimeTotal / 60) + "小时,已超过24小时,不显示");
                        return;
                    }
                    quality = SleepDataUtil.getSleepQuality(sleepBean);
                    hours = sleepTimeTotal / 60;
                    minutes = sleepTimeTotal % 60;
                    content = ResUtil.formatHtml(formatContent, contentType, quality, ResUtil.format("%02d:%02d", hours, minutes));
                    title = ResUtil.formatHtml(formatTitle, hours, unit_h, minutes, unit_m);
                }

            }

            @Override
            public void done() {
                if (isUIEnable()) {
                    view.onUpdateSleepItemData(title, content);

                }
            }
        });

    }

    @Override
    public void requestLoadSportModeItemData(final Calendar calendar) {

        SNAsyncTask.execute(new SNVTaskCallBack() {
            private int iconRes;
            private CharSequence subTitle1;
            private CharSequence subTitle2;
            private CharSequence subTitle3;
            private String unit_bpm;
            private String unit_h;
            private String unit_m;
            private String lastSport;
            private String duration;
            private String avgHeart;

            @Override
            public void prepare() {
                unit_h = ResUtil.getString(R.string.unit_hours);
                unit_m = ResUtil.getString(R.string.unit_min);
                unit_bpm = ResUtil.getString(R.string.unit_heart);
                lastSport = ResUtil.getString(R.string.content_last_sport);
                duration = ResUtil.getString(R.string.content_duration);
                avgHeart = ResUtil.getString(R.string.content_average_heart);
                subTitle1 = "";
                subTitle2 = "";
                subTitle3 = "";
                //默认健步
                iconRes = SportModeTypeUtil.getIconResForSportModeType(SportModeBean.MODE_TYPE_WALKING);
            }

            @Override
            public void run() throws Throwable {
                String date = DateUtil.getDate(DateUtil.YYYY_MM_DD, calendar);
                //开始查询历史运动模式
                List<SportModeBean> sportModeBeans = SNBLEDao.get(SportModeDao.class).getDao()
                        .queryBuilder()
                        .orderBy(SportModeBean.COLUMN_BEGIN_DATE_TIME, false)
                        .where()
                        .eq(SportModeBean.COLUMN_DATE, date)
                        .and()
                        .eq(SportModeBean.COLUMN_USER_ID, AppUserUtil.getUser().getUser_id())
                        .query();
                if (!IF.isEmpty(sportModeBeans)) {
                    SportModeBean sportModeBean = sportModeBeans.get(0);
                    int modeType = sportModeBean.getModeType();
                    if (modeType > 0) {
                        subTitle1 = ResUtil.formatHtml("%s:<font color=#000000><strong>%s</strong></font>", lastSport, ResUtil.getString(SportModeTypeUtil.getLabelResForSportModeType(modeType)));
                        subTitle2 = ResUtil.formatHtml("%s:<font color=#000000><strong>%d</strong><small>%s</small><strong>%d</strong><small>%s</small></font>", duration, sportModeBean.getTakeMinutes() / 60, unit_h, sportModeBean.getTakeMinutes() % 60, unit_m);
                        subTitle3 = ResUtil.formatHtml("%s:<font color=#000000><strong>%d</strong><small>%s</small></font>", avgHeart, sportModeBean.getHeartRateAvg(), unit_bpm);
                        iconRes = SportModeTypeUtil.getIconResForSportModeType(modeType);
                    }
                }
            }

            @Override
            public void done() {
                if (isUIEnable()) {
                    view.onUpdateSportModeItemData(iconRes, subTitle1, subTitle2, subTitle3);
                }
            }
        });


    }

    @Override
    public void requestLoadHeartRateItemData(final Calendar calendar) {

        SNAsyncTask.execute(new SNVTaskCallBack() {
            private CharSequence title;
            private CharSequence content;
            private int maxTotal;
            private int minTotal;
            private String lastOneHeartRate;
            private String formatTitle = "%d - %d<small>%s</small>";
            private String formatContent = "%s %s <small>%s</small>";
            private String contentType;
            private String unit_bpm;

            @Override
            public void prepare() {
                maxTotal = 0;
                minTotal = 0;
                lastOneHeartRate = ResUtil.getString(R.string.content_no_data);
                contentType = ResUtil.getString(R.string.content_last_time);
                unit_bpm = ResUtil.getString(R.string.unit_heart);


                title = ResUtil.formatHtml(formatTitle, minTotal, maxTotal, unit_bpm);
                content = ResUtil.formatHtml(formatContent, contentType, lastOneHeartRate, "");

            }

            @Override
            public void run() throws Throwable {

                String date = DateUtil.getDate(DateUtil.YYYY_MM_DD, calendar);
                //总数据 最大1440条 包括实时心率和历史心率

                //开始查询历史心率
                List<HeartRateBean> heartRateBeans = SNBLEDao.get(HeartRateDao.class).queryForDay(AppUserUtil.getUser().getUser_id(), date);
                if (!IF.isEmpty(heartRateBeans)) {
                    HeartRateBean heartRateBean = heartRateBeans.get(0);
                    if (heartRateBean.getAvg() != 0) {
                        maxTotal = heartRateBean.getMax();
                        minTotal = heartRateBean.getMin();
                        title = ResUtil.formatHtml(formatTitle, minTotal, maxTotal, unit_bpm);
                        //添加历史心率到总数据里
                        ArrayList<HeartRateBean.HeartRateDetailsBean> heartRateDetails = heartRateBean.getHeartRateDetails();
                        //对详细数据进行反向遍历
                        for (int i = heartRateDetails.size() - 1; i >= 0; i--) {
                            HeartRateBean.HeartRateDetailsBean heartRateDetail = heartRateDetails.get(i);
                            if (heartRateDetail.getValue() != 0) {
                                //最后一次检测的数值
                                lastOneHeartRate = String.valueOf(heartRateDetail.getValue());
                                String updateTime = DateUtil.convertStringToNewString(DateUtil.YYYY_MM_DD_HH_MM_SS, DateUtil.HH_MM, heartRateDetail.getDateTime());
                                content = ResUtil.formatHtml(formatContent, contentType, lastOneHeartRate, updateTime);
                                break;
                            }
                        }
                    }
                }

            }

            @Override
            public void done() {
                if (isUIEnable()) {
                    view.onUpdateHeartRateItemData(title, content);
                }
            }
        });
    }

    @Override
    public void requestLoadBloodOxygenItemData(final Calendar calendar) {
        SNAsyncTask.execute(new SNVTaskCallBack() {
            private CharSequence title;
            private CharSequence content;
            private int maxTotal;
            private int minTotal;
            private String lastOneBloodOxygen;
            private String formatTitle = "%d - %d<small>%s</small>";
            private String formatContent = "%s %s <small>%s</small>";
            private String contentType;
            private String unit_blood_oxygen;

            @Override
            public void prepare() {
                maxTotal = 0;
                minTotal = 0;
                lastOneBloodOxygen = ResUtil.getString(R.string.content_no_data);
                contentType = ResUtil.getString(R.string.content_last_time);
                unit_blood_oxygen = "%";


                title = ResUtil.formatHtml(formatTitle, minTotal, maxTotal, unit_blood_oxygen);
                content = ResUtil.formatHtml(formatContent, contentType, lastOneBloodOxygen, "");

            }

            @Override
            public void run() throws Throwable {

                String date = DateUtil.getDate(DateUtil.YYYY_MM_DD, calendar);
                //总数据 最大1440条 包括实时血氧和历史血氧

                //开始查询历史血氧
                List<BloodOxygenBean> bloodOxygenBeans = SNBLEDao.get(BloodOxygenDao.class).queryForDay(AppUserUtil.getUser().getUser_id(), date);
                if (!IF.isEmpty(bloodOxygenBeans)) {
                    BloodOxygenBean bloodOxygenBean = bloodOxygenBeans.get(0);
                    if (bloodOxygenBean.getAvg() != 0) {
                        maxTotal = bloodOxygenBean.getMax();
                        minTotal = bloodOxygenBean.getMin();
                        title = ResUtil.formatHtml(formatTitle, minTotal, maxTotal, unit_blood_oxygen);

                        //添加历史血氧到总数据里
                        ArrayList<BloodOxygenBean.BloodOxygenDetailsBean> bloodOxygenDetails = bloodOxygenBean.getBloodOxygenDetails();
                        //对详细数据进行反向遍历
                        for (int i = bloodOxygenDetails.size() - 1; i >= 0; i--) {
                            BloodOxygenBean.BloodOxygenDetailsBean bloodOxygenDetail = bloodOxygenDetails.get(i);
                            if (bloodOxygenDetail.getValue() != 0) {
                                //最后一次检测的数值
                                lastOneBloodOxygen = ResUtil.format("%d", bloodOxygenDetail.getValue());
                                String updateTime = DateUtil.convertStringToNewString(DateUtil.YYYY_MM_DD_HH_MM_SS, DateUtil.HH_MM, bloodOxygenDetail.getDateTime());
                                content = ResUtil.formatHtml(formatContent, contentType, lastOneBloodOxygen, updateTime);
                                break;
                            }
                        }
                    }
                }

            }

            @Override
            public void done() {
                if (isUIEnable()) {
                    view.onUpdateBloodOxygenItemData(title, content);
                }
            }
        });
    }

    @Override
    public void requestLoadBloodPressureItemData(final Calendar calendar) {

        SNAsyncTask.execute(new SNVTaskCallBack() {
            private CharSequence title;
            private CharSequence content;

            private int bloodDiastolic;
            private int bloodSystolic;
            private String lastOneBloodPressure;
            private String formatTitle = "%d / %d<small>%s</small>";
            private String formatContent = "%s %s <small>%s</small>";
            private String contentType;
            private String unit_blood_pressure;


            @Override
            public void prepare() {
                bloodDiastolic = 0;
                bloodSystolic = 0;
                lastOneBloodPressure = ResUtil.getString(R.string.content_no_data);
                contentType = ResUtil.getString(R.string.content_last_time);
                unit_blood_pressure = ResUtil.getString(R.string.unit_pressure);


                title = ResUtil.formatHtml(formatTitle, bloodDiastolic, bloodSystolic, unit_blood_pressure);
                content = ResUtil.formatHtml(formatContent, contentType, lastOneBloodPressure, "");

            }

            @Override
            public void run() throws Throwable {
                String date = DateUtil.getDate(DateUtil.YYYY_MM_DD, calendar);
                //总数据 最大1440条 包括实时血压和历史血压

                //开始查询历史血压
                List<BloodPressureBean> bloodPressureBeans = SNBLEDao.get(BloodPressureDao.class).queryForDay(AppUserUtil.getUser().getUser_id(), date);
                if (!IF.isEmpty(bloodPressureBeans)) {
                    BloodPressureBean bloodPressureBean = bloodPressureBeans.get(0);

                    bloodDiastolic = bloodPressureBean.getBloodDiastolic();
                    bloodSystolic = bloodPressureBean.getBloodSystolic();
                    title = ResUtil.formatHtml(formatTitle, bloodDiastolic, bloodSystolic, unit_blood_pressure);

                    if (bloodDiastolic != 0 && bloodSystolic != 0) {
                        //添加历史血压到总数据里
                        ArrayList<BloodPressureBean.BloodPressureDetailsBean> bloodPressureDetails = bloodPressureBean.getBloodPressureDetails();
                        //对详细数据进行反向遍历
                        for (int i = bloodPressureDetails.size() - 1; i >= 0; i--) {
                            BloodPressureBean.BloodPressureDetailsBean pressureDetail = bloodPressureDetails.get(i);
                            if (pressureDetail.getBloodDiastolic() != 0 && pressureDetail.getBloodSystolic() != 0) {
                                //最后一次检测的数值
                                lastOneBloodPressure = ResUtil.format("%d/%d", pressureDetail.getBloodDiastolic(), pressureDetail.getBloodSystolic());
                                String updateTime = DateUtil.convertStringToNewString(DateUtil.YYYY_MM_DD_HH_MM_SS, DateUtil.HH_MM, pressureDetail.getDateTime());
                                content = ResUtil.formatHtml(formatContent, contentType, lastOneBloodPressure, updateTime);

                                break;
                            }
                        }
                    }
                }

            }

            @Override
            public void done() {
                if (isUIEnable()) {
                    view.onUpdateBloodPressureItemData(title, content);
                }
            }
        });
    }

    @Override
    public void requestLoadDietStatisticsItemData(final Calendar calendar) {
        SNAsyncTask.execute(new SNVTaskCallBack() {

            private CharSequence contentDietStatisticsItemData;
            private CharSequence contentDietMealDetailsItemData;

            @Override
            public void run() throws Throwable {
                String date = DateUtil.getDate(DateUtil.YYYY_MM_DD, calendar);
                DietDao dietDao = DietDao.get(DietDao.class);
                UserBean user = AppUserUtil.getUser();
                DietBean dietBean = dietDao.queryForDate(user.getUser_id(), date);
                if (dietBean != null) {
                    ArrayList<MealBean> meals = dietBean.getMeals();
                    if (meals != null) {
                        try {
                            String time = DateUtil.getDate(DateUtil.HH_MM, DateUtil.convertLongToCurTimeZoneLong(DateUtil.convertStringToLong(DateUtil.YYYY_MM_DD_HH_MM_SS, meals.get(meals.size() - 1).getCreate_time())));
                            contentDietMealDetailsItemData = ResUtil.format(ResUtil.getString(R.string.content_recorded_meal), meals.size(), time);
                        } catch (Exception ignored) {
                        }
                    }

                    if (dietBean.getTargetCalory() * 1.1f < dietBean.getTotalCalory()) {
                        contentDietStatisticsItemData = ResUtil.formatHtml("<font color=#AD204B>%.0f</font>/%.0f", dietBean.getTotalCalory(), dietBean.getTargetCalory() * 1.1f);
                    } else {
                        contentDietStatisticsItemData = ResUtil.format("%.0f/%.0f", dietBean.getTotalCalory(), dietBean.getTargetCalory() * 1.1f);
                    }

                } else {
                    contentDietStatisticsItemData = ResUtil.format("%.0f/%.0f", 0f, user.getTarget_calory() * 1.1f);
                }
            }

            @Override
            public void done() {
                if (!isUIEnable()) return;
                view.onUpdateDietStatisticsItemData(contentDietStatisticsItemData);
                view.onUpdateDietMealDetailsItemData(contentDietMealDetailsItemData);
            }
        });
    }

    @Override
    public void requestLoadNetworkDietStatisticsItemData(Calendar calendar) {
        UserBean user = AppUserUtil.getUser();
        String today = DateUtil.getCurrentDate(DateUtil.YYYY_MM_DD);
        //获取成功后,自动通过事件总线发送 EVENT_SYNC_DIET_STATISTICS_DATA_SUCCESS , 这里则重新刷新
        DietDataNetworkSyncHelper.downloadFromServer(user.getUser_id(), today, today);
    }

    @Override
    public void requestLoadDietPlanThinBodyEnableStatus() {
        SNAsyncTask.execute(new SNVTaskCallBack() {

            private boolean enable;

            @Override
            public void run() throws Throwable {
                enable = AppUserUtil.getUser().getTarget_calory() <= 0;
            }

            @Override
            public void done() {
                super.done();
                if (!isUIEnable()) {
                    return;
                }
                view.onUpdateDietPlanThinBodyEnableStatus(enable);
            }
        });
    }

    @Override
    public void requestStartDeviceDataSync() {
        //如果已断开 则直接结束刷新动画
        if (!SNBLEHelper.isConnected()) {
            view.onDeviceDataSyncSuccess();
            return;
        }
        SNEventBus.sendEvent(AppEvent.EVENT_USER_REQUEST_SYNC_DEVICE_DATA, true);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventReceived(SNEvent event) {
        boolean uiEnable = isUIEnable();

        boolean today = DateUtil.equalsToday(App.getSelectedCalendar());
        switch (event.getCode()) {
            case SNBLEEvent.EVENT_UPDATED_REAL_TIME_SPORT_DATA:
                if (today) {
                    SNEventBus.sendEvent(AppEvent.EVENT_UPDATED_NOTIFICATION_TITLE, String.format(Locale.CHINESE, ResUtil.getString(R.string.content_step_format), (int) event.getData()));
                }
            case SNBLEEvent.EVENT_UPDATED_SPORT_DATA:

                if (!uiEnable) {
                    return;
                }
                //如果当前顶部ViewPager/日历等 选择日期的是今天 就刷新数据
                if (today) {
                    requestLoadStepChart(App.getSelectedCalendar());
                }
                break;
            case SNBLEEvent.EVENT_UPDATED_SLEEP_DATA:
                if (!uiEnable) {
                    return;
                }
                //如果 选择日期的是今天 就刷新数据
                if (today) {
                    requestLoadSleepItemData(App.getSelectedCalendar());
                }
                break;
            case SNBLEEvent.EVENT_UPDATED_SPORT_MODE_DATA:
                if (!uiEnable) {
                    return;
                }
                //如果 选择日期的是今天 就刷新数据
                if (today) {
                    requestLoadSportModeItemData(App.getSelectedCalendar());
                }
                break;
            case SNBLEEvent.EVENT_UPDATED_HEART_RATE_DATA:
                if (!uiEnable) {
                    return;
                }
                //如果 选择日期的是今天 就刷新数据
                if (today) {
                    requestLoadHeartRateItemData(App.getSelectedCalendar());
                }
                break;
            case SNBLEEvent.EVENT_UPDATED_BLOOD_OXYGEN_DATA:
                if (!uiEnable) {
                    return;
                }
                //如果 选择日期的是今天 就刷新数据
                if (today) {
                    requestLoadBloodOxygenItemData(App.getSelectedCalendar());
                }
                break;
            case SNBLEEvent.EVENT_UPDATED_BLOOD_PRESSURE_DATA:
                if (!uiEnable) {
                    return;
                }
                //如果 选择日期的是今天 就刷新数据
                if (today) {
                    requestLoadBloodPressureItemData(App.getSelectedCalendar());
                }
                break;
            case AppEvent.EVENT_SYNC_DIET_STATISTICS_DATA_SUCCESS:
                if (!uiEnable) {
                    return;
                }
                //如果 选择日期的是今天 就刷新数据
                if (today) {
                    requestLoadDietStatisticsItemData(App.getSelectedCalendar());
                }
                break;
//            case AppEvent.EVENT_SYNC_DEVICE_ALL_DATA_SUCCESS:
            case AppEvent.EVENT_SYNC_DEVICE_UI_DATA_SUCCESS:
                if (!uiEnable) {
                    return;
                }
                view.onDeviceDataSyncSuccess();
                break;
            case AppEvent.EVENT_SYNC_DEVICE_ALL_DATA_SUCCESS:
                App.requestAutoRealTimeSportSync(true);
                break;
            case AppEvent.EVENT_SYNC_WEATHER_DATA_SUCCESS:
                requestWeatherData();
                break;
        }

    }


    @Override
    public void requestWeatherData() {
        SNAsyncTask.execute(new SNVTaskCallBack() {
            String weatherQuality = ResUtil.getString(R.string.content_general);
            int weatherType = 0;
            String weatherTemperatureRange;
            //从历史天气列表中取出今天的天气
            WeatherBean.DataBean data = null;
            @Override
            public void run() throws Throwable {
                WeatherListBean weatherListBean = WeatherStorage.getWeatherListBean();
                if (weatherListBean == null) return;
                List<WeatherBean.DataBean> dataList = weatherListBean.getData();
                if (IF.isEmpty(dataList)) return;

                for (WeatherBean.DataBean bean : dataList) {
                    if (DateUtil.equalsToday(bean.getDate())) {
                        data = dataList.get(0);
                    }
                }
                //如果天气历史中仍然没有今天的天气,就拿最后一次的天气 先顶顶
                if (data == null && dataList.size() > 0) {
                    data = dataList.get(dataList.size() - 1);
                }
                if (data == null) return;

                weatherType = data.getCond_code_type();
                int temperatureMin = data.getTmp_min();
                int temperatureMax = data.getTmp_max();
                UnitConfig unitConfig = AppUnitUtil.getUnitConfig();
                String format = "%d℃~%d℃";
                if (unitConfig != null && unitConfig.getTemperatureUnit() == UnitConfig.TEMPERATURE_F) {
                    format = "%d℉~%d℉";
                    temperatureMin = (int) UnitConversion.CToF(temperatureMin);
                    temperatureMax = (int) UnitConversion.CToF(temperatureMax);
                }
                weatherTemperatureRange = ResUtil.format(format, temperatureMin, temperatureMax);
            }

            @Override
            public void done() {
                if (!isUIEnable()) return;
                view.onUpdateWeatherData(weatherType, weatherTemperatureRange, weatherQuality);
                if(data==null){
                    SNEventBus.sendEvent(AppEvent.EVENT_USER_REQUEST_SYNC_WEATHER_DATA);
                }
            }

            @Override
            public void error(Throwable e)
            {
                super.error(e);
                SNEventBus.sendEvent(AppEvent.EVENT_USER_REQUEST_SYNC_WEATHER_DATA);
            }
        });

    }


    @Override
    protected void onVisible() {
        super.onVisible();
        if (!isUIEnable()) {
            return;
        }
        if (view != null) {
            view.onDeviceDataSyncSuccess();
        }
        requestLoadDietPlanThinBodyEnableStatus();
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        SNEventBus.register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SNEventBus.unregister(this);
    }
}
