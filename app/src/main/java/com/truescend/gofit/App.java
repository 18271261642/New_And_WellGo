package com.truescend.gofit;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.multidex.MultiDex;

import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.sn.app.AppSDK;
import com.sn.app.db.data.clock.AlarmClockBean;
import com.sn.app.db.data.config.DeviceConfigBean;
import com.sn.app.db.data.config.DeviceConfigDao;
import com.sn.app.db.data.config.bean.RemindConfig;
import com.sn.app.db.data.diet.DietBean;
import com.sn.app.db.data.schedule.ScheduleBean;
import com.sn.app.db.data.user.UserBean;
import com.sn.app.storage.MapStorage;
import com.sn.app.storage.UserStorage;
import com.sn.app.utils.LoginStatusHelper;
import com.sn.blesdk.ble.DeviceType;
import com.sn.blesdk.ble.SNBLEHelper;
import com.sn.blesdk.ble.SNBLESDK;
import com.sn.blesdk.cmd.SNCMD;
import com.sn.blesdk.db.data.health.blood_oxygen.BloodOxygenBean;
import com.sn.blesdk.db.data.health.blood_pressure.BloodPressureBean;
import com.sn.blesdk.db.data.health.heart_rate.HeartRateBean;
import com.sn.blesdk.db.data.health.temperature.TemperatureBean;
import com.sn.blesdk.db.data.sleep.SleepBean;
import com.sn.blesdk.db.data.sport.SportBean;
import com.sn.blesdk.db.data.sport_mode.SportModeBean;
import com.sn.db.sdk.SNDBSDK;
import com.sn.db.utils.DBHelper;
import com.sn.db.utils.DatabaseUtil;
import com.sn.utils.DateUtil;
import com.sn.utils.SNToast;
import com.tencent.bugly.crashreport.CrashReport;
import com.truescend.gofit.service.sync.BleSyncService;
import com.truescend.gofit.utils.MapType;
import com.truescend.gofit.utils.PageJumpUtil;
import com.truescend.gofit.utils.PermissionUtils;

import java.lang.ref.WeakReference;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import me.weishu.reflection.Reflection;

/**
 * 作者:东芝(2017/11/17).
 * 功能:
 */

public class App extends Application implements LoginStatusHelper.LoginStatusListener {


    private static WeakReference<Context> context;
    private static Calendar mSelectedCalendar;
    private LoginStatusHelper loginStatusHelper = new LoginStatusHelper(this);
    private static final int BACKGROUND = 1;
    private static final int FOREGROUND = 2;
    private static final int UNKNOWN = 0;
    private static int status = UNKNOWN;

    public static Calendar getSelectedCalendar() {
        return mSelectedCalendar;
    }

    public static void setSelectedCalendar(Calendar newCalendar) {
        App.mSelectedCalendar = newCalendar;
    }

    @Override
    public void onCreate() {
        super.onCreate();


        //腾讯Bugly异常捕获
        if (!BuildConfig.isDev) {
            //测试阶段第三个参数设置为true，发布时要设置为false
            CrashReport.initCrashReport(getApplicationContext(), BuildConfig.buglyKey, false);

        }

        mSelectedCalendar = DateUtil.getCurrentCalendar();
        context = new WeakReference<>(getApplicationContext());

        //初始化 蓝牙框架SDK
        SNBLESDK.init(this, BleSyncService.class);


        //初始化 数据库框架SDK
        initDataBases();

        //AppSDK
        AppSDK.init(this);

//        //第三方登录
//        LoginSDK.init(this);

        //全局Toast 无上下文 工具
        SNToast.init(this);
        //刷新设备支持列表
        DeviceType.asyncReLoadDeviceInfo();


        loginStatusHelper.register();
        //不上传日志
        // LogRecorder.uploadLog(getContext(), UserStorage.getUserId(),-1/*上传昨天的数据*/,null);

        MapType.refresh(this, new MapType.OnMapTypeCallback() {
            @Override
            public void callback(MapType.TYPE type) {
                switch (type) {
                    case A_MAP:
                        MapStorage.setLastSmartMapType(0);
                        break;
                    case GOOGLE_MAP:
                        MapStorage.setLastSmartMapType(1);
                        break;
                }
            }
        });


        registerActivityLifecycleCallbacks(activityLifecycleCallbacks);

    }



    private void initDataBases() {
        SNDBSDK.init(getContext(), "SNDB.db", 51/*数据库升级的具体日志一定要写,并且写在下面的onUpgrade中*/, new ArrayList<Class<?>>() {
            {
                ////////////////////////////////
                //----------蓝牙设备数据---------
                ////////////////////////////////

                //运动历史数据
                add(SportBean.class);
                //睡眠历史数据
                add(SleepBean.class);
                //心率数据
                add(HeartRateBean.class);
                //体温数据
                add(TemperatureBean.class);
                //血氧数据
                add(BloodOxygenBean.class);
                //血压数据
                add(BloodPressureBean.class);

                ////////////////////////////////
                //----------APP相关数据----------
                ////////////////////////////////
                //用户
                add(UserBean.class);
                //闹钟
                add(AlarmClockBean.class);
                //日程提醒
                add(ScheduleBean.class);
                //设备设置 配置文件
                add(DeviceConfigBean.class);

                //食谱数据表
                add(DietBean.class);
                //运动模式
                add(SportModeBean.class);
            }
        }, new DBHelper.OnUpgradeListener() {
            @Override
            public void onUpgrade(SQLiteDatabase db, ConnectionSource cs, int oldVersion, int newVersion, int version) {
                //这里使用for循环的意思是 从 oldVersion 到 newVersion 的app更新过程中 ,用户可能错过了n个版本
                //因此 不能直接判断oldVersion 来更新数据库 而是模拟一个依次更新的效果,让跨版本未执行的sql全都得执行一遍 但性能可能有所下降
                switch (version) {
                    case 36:// 36升级到37时 DeviceConfigBean 增加了一个 autoSyncDeviceData 字段, 为 手环数据自动同步功能
                        DatabaseUtil.upgradeTable(db, cs, DeviceConfigBean.class, DatabaseUtil.TYPE.FIELD_ADD);
                        break;
                    case 37:// 37升级到38时 UserBean 增加了一个 sport_days 字段, 为 运动天数,用于对比本地是否需要下载网络大数据
                        DatabaseUtil.upgradeTable(db, cs, UserBean.class, DatabaseUtil.TYPE.FIELD_ADD);
                        break;
                    case 38:// 38升级到39时 BloodOxygenDetailsBean/BloodPressureDetailsBean/HeartRateDetailsBean 增加了一个 type 字段, 为 是否自动检测 的数据,否则是手动 (默认0=手动检测,1=自动检测)
                        //2018-04-06 决定 因为查询运动数据数量与服务器不相同时 app需要重新同步全部数据,  现在还不如改成 改表/更新数据库版本时,直接清除全部数据库数据  然后重全部拉下来
                        //删除并重新创建
                        DatabaseUtil.upgradeTable(db, cs, SportBean.class, DatabaseUtil.TYPE.TABLE_DELETE_AND_RECREATE);
                        DatabaseUtil.upgradeTable(db, cs, BloodOxygenBean.class, DatabaseUtil.TYPE.TABLE_DELETE_AND_RECREATE);
                        DatabaseUtil.upgradeTable(db, cs, BloodPressureBean.class, DatabaseUtil.TYPE.TABLE_DELETE_AND_RECREATE);
                        DatabaseUtil.upgradeTable(db, cs, HeartRateBean.class, DatabaseUtil.TYPE.TABLE_DELETE_AND_RECREATE);
                        break;
                    case 39:
                        //39升级到40时，更改了久坐提醒和喝水提醒的默认值，默认选中每一天，原因是硬件没有做单次提醒的功能

                        // 39升级到40时 UserBean 增加了一个 adv_id/device_name 字段 用于固件救砖等操作
                        DatabaseUtil.upgradeTable(db, cs, UserBean.class, DatabaseUtil.TYPE.FIELD_ADD);
                        break;
                    case 40://给用户了一个闪退 可能导致数据库升级不了故 重新升级为41一次
                    case 41:
                        try {
                            DeviceConfigDao deviceConfigDao = DeviceConfigDao.get(DeviceConfigDao.class);
                            DeviceConfigBean deviceConfigBean = deviceConfigDao.queryForUser(UserStorage.getUserId());
                            if (deviceConfigBean != null) {
                                RemindConfig remindConfig = deviceConfigBean.getRemindConfig();
                                List<RemindConfig.Apps> remindAppPushList = remindConfig.getRemindAppPushList();
                                if (remindConfig.findRemindAppPush("com.whatsapp") == null) {
                                    remindAppPushList.add(new RemindConfig().new Apps("WhatsApp", "com.whatsapp", "file:///android_asset/icon_whatsapp_reminder.png", true));
                                }
                                if (remindConfig.findRemindAppPush("com.viber.voip") == null) {
                                    remindAppPushList.add(new RemindConfig().new Apps("Viber", "com.viber.voip", "file:///android_asset/icon_whatsapp_reminder.png", true));
                                }
                                RemindConfig.Apps facebook = remindConfig.findRemindAppPush("com.facebook.katana");
                                if (facebook != null) {
                                    List<String> packageNames = facebook.getPackageNames();
                                    if (!packageNames.contains("com.facebook.orca")) {
                                        packageNames.add("com.facebook.orca");
                                    }
                                }
                                remindConfig.setRemindAppPushList(remindAppPushList);
                                deviceConfigBean.setRemindConfig(remindConfig);
                                deviceConfigDao.update(deviceConfigBean);
                            }
                        } catch (Exception ignored) {
                        }
                        break;
                    case 42://42升级到43时 DeviceConfigBean#UnitConfig 添加了一个timeUnit字段
                        //无需处理,数据库会自动适应
                        break;
                    case 43:// 43升级到44时 UserBean 增加了  target_calory target_weight 字段
                        DatabaseUtil.upgradeTable(db, cs, UserBean.class, DatabaseUtil.TYPE.FIELD_ADD);
                        break;
                    case 44:// 44升级到45时 UserBean 增加了  total_meal_day first_meal_date 字段
                        DatabaseUtil.upgradeTable(db, cs, UserBean.class, DatabaseUtil.TYPE.FIELD_ADD);
                        break;
                    case 45:// 45升级到46时  增加了 表DietBean
                        try {
                            TableUtils.createTableIfNotExists(cs, DietBean.class);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 46:// 46升级到47时  增加了 多个Email
                        try {
                            DeviceConfigDao deviceConfigDao = DeviceConfigDao.get(DeviceConfigDao.class);
                            DeviceConfigBean deviceConfigBean = deviceConfigDao.queryForUser(UserStorage.getUserId());
                            if (deviceConfigBean != null) {
                                RemindConfig remindConfig = deviceConfigBean.getRemindConfig();
                                List<RemindConfig.Apps> remindAppPushList = remindConfig.getRemindAppPushList();
                                if (remindConfig.findRemindAppPush("com.microsoft.office.outlook") == null) {
                                    remindAppPushList.add(0, new RemindConfig().new Apps("Email", Arrays.asList(
                                            "com.microsoft.office.outlook",//Outlook
                                            "com.google.android.gm",//Gmail
                                            "com.google.android.email",//谷歌安卓自带邮件
                                            "com.samsung.android.email.provider",//Samsung Email
                                            "com.yahoo.mobile.client.android.mail",//Yahoo Email
                                            "com.tencent.androidqqmail",//QQ邮箱
                                            "com.netease.mobimail",//网易邮箱
                                            "cn.cj.pe"//139邮箱
                                    ), "file:///android_asset/icon_email_reminder.png", true));
                                }

                                remindConfig.setRemindAppPushList(remindAppPushList);
                                deviceConfigBean.setRemindConfig(remindConfig);
                                deviceConfigDao.update(deviceConfigBean);
                            }
                        } catch (Exception ignored) {
                        }
                        break;
                    case 47:// 47升级到48时  增加了 表SportModeBean
                        try {
                            TableUtils.createTableIfNotExists(cs, SportModeBean.class);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 48:// 47升级到48时  增加了 Instagram
                        try {
                            DeviceConfigDao deviceConfigDao = DeviceConfigDao.get(DeviceConfigDao.class);
                            DeviceConfigBean deviceConfigBean = deviceConfigDao.queryForUser(UserStorage.getUserId());
                            if (deviceConfigBean != null) {
                                RemindConfig remindConfig = deviceConfigBean.getRemindConfig();
                                List<RemindConfig.Apps> remindAppPushList = remindConfig.getRemindAppPushList();
                                if (remindConfig.findRemindAppPush("com.instagram.android") == null) {
                                    remindAppPushList.add(0, new RemindConfig().new Apps("Instagram","com.instagram.android","file:///android_asset/icon_instagram_reminder.png",true));
                                }
                                remindConfig.setRemindAppPushList(remindAppPushList);
                                deviceConfigBean.setRemindConfig(remindConfig);
                                deviceConfigDao.update(deviceConfigBean);
                            }
                        } catch (Exception ignored) {
                        }
                        break;
                    case 49:// 48升级到49时 DeviceConfigBean 增加了一个 temperatureAutoCheckConfig 字段, 为 体温自动检测开关
                        DatabaseUtil.upgradeTable(db, cs, DeviceConfigBean.class, DatabaseUtil.TYPE.TABLE_DELETE_AND_RECREATE);
                        break;
                    case 50:// 增加TemperatureBean
                        try {
                            TableUtils.createTableIfNotExists(cs, TemperatureBean.class);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        break;
                }

            }
        });
    }

    public static Context getContext() {
        return context.get();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {//9.0
            Reflection.unseal(base);
        }
        MultiDex.install(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        SNBLESDK.close();
        SNDBSDK.close();
        loginStatusHelper.unregist();
        unregisterActivityLifecycleCallbacks(activityLifecycleCallbacks);
    }

    @Override
    public void onLogout() {
        PageJumpUtil.startLoginActivity(this, true);
    }


    private ActivityLifecycleCallbacks activityLifecycleCallbacks = new ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {
            requestAutoRealTimeSportSync(false);
        }

        @Override
        public void onActivityPaused(Activity activity) {
            requestAutoRealTimeSportSync(false);
        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    };

    private static Handler mHandler = new Handler();


    public static void requestAutoRealTimeSportSync(boolean isForce) {
        if (mHandler != null) {
            if(isForce){
                status = UNKNOWN;
            }
            mHandler.removeCallbacks(delayedRun);
            mHandler.postDelayed(delayedRun, 2000);
        }
    }

    private static Runnable delayedRun = new Runnable() {
        @Override
        public void run() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (SNBLEHelper.isConnected()) {
                        if (PermissionUtils.isApplicationInBackground(App.getContext().getApplicationContext())) {
                            if (status != BACKGROUND) {
                                SNBLEHelper.sendCMD(SNCMD.get().setSyncRealTimeSportDataRealTimeCallback(false));
                                status = BACKGROUND;
                            }
                        } else {
                            if (status != FOREGROUND) {
                                SNBLEHelper.sendCMD(SNCMD.get().setSyncRealTimeSportDataRealTimeCallback(true));
                                status = FOREGROUND;
                            }
                        }
                    }
                }
            }).start();
        }
    };
}
