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
 * δ½θ:δΈθ(2017/11/17).
 * εθ½:
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


        //θΎθ?―BuglyεΌεΈΈζθ·
        if (!BuildConfig.isDev) {
            //ζ΅θ―ιΆζ?΅η¬¬δΈδΈͺεζ°θ?Ύη½?δΈΊtrueοΌεεΈζΆθ¦θ?Ύη½?δΈΊfalse
            CrashReport.initCrashReport(getApplicationContext(), BuildConfig.buglyKey, false);

        }

        mSelectedCalendar = DateUtil.getCurrentCalendar();
        context = new WeakReference<>(getApplicationContext());

        //εε§ε θηζ‘ζΆSDK
        SNBLESDK.init(this, BleSyncService.class);


        //εε§ε ζ°ζ?εΊζ‘ζΆSDK
        initDataBases();

        //AppSDK
        AppSDK.init(this);

//        //η¬¬δΈζΉη»ε½
//        LoginSDK.init(this);

        //ε¨ε±Toast ζ δΈδΈζ ε·₯ε·
        SNToast.init(this);
        //ε·ζ°θ?Ύε€ζ―ζεθ‘¨
        DeviceType.asyncReLoadDeviceInfo();


        loginStatusHelper.register();
        //δΈδΈδΌ ζ₯εΏ
        // LogRecorder.uploadLog(getContext(), UserStorage.getUserId(),-1/*δΈδΌ ζ¨ε€©ηζ°ζ?*/,null);

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
        SNDBSDK.init(getContext(), "SNDB.db", 51/*ζ°ζ?εΊεηΊ§ηε·δ½ζ₯εΏδΈε?θ¦ε,εΉΆδΈεε¨δΈι’ηonUpgradeδΈ­*/, new ArrayList<Class<?>>() {
            {
                ////////////////////////////////
                //----------θηθ?Ύε€ζ°ζ?---------
                ////////////////////////////////

                //θΏε¨εε²ζ°ζ?
                add(SportBean.class);
                //η‘η εε²ζ°ζ?
                add(SleepBean.class);
                //εΏηζ°ζ?
                add(HeartRateBean.class);
                //δ½ζΈ©ζ°ζ?
                add(TemperatureBean.class);
                //θ‘ζ°§ζ°ζ?
                add(BloodOxygenBean.class);
                //θ‘εζ°ζ?
                add(BloodPressureBean.class);

                ////////////////////////////////
                //----------APPηΈε³ζ°ζ?----------
                ////////////////////////////////
                //η¨ζ·
                add(UserBean.class);
                //ιΉι
                add(AlarmClockBean.class);
                //ζ₯η¨ζι
                add(ScheduleBean.class);
                //θ?Ύε€θ?Ύη½? ιη½?ζδ»Ά
                add(DeviceConfigBean.class);

                //ι£θ°±ζ°ζ?θ‘¨
                add(DietBean.class);
                //θΏε¨ζ¨‘εΌ
                add(SportModeBean.class);
            }
        }, new DBHelper.OnUpgradeListener() {
            @Override
            public void onUpgrade(SQLiteDatabase db, ConnectionSource cs, int oldVersion, int newVersion, int version) {
                //θΏιδ½Ώη¨forεΎͺη―ηζζζ― δ» oldVersion ε° newVersion ηappζ΄ζ°θΏη¨δΈ­ ,η¨ζ·ε―θ½ιθΏδΊnδΈͺηζ¬
                //ε ζ­€ δΈθ½η΄ζ₯ε€ζ­oldVersion ζ₯ζ΄ζ°ζ°ζ?εΊ θζ―ζ¨‘ζδΈδΈͺδΎζ¬‘ζ΄ζ°ηζζ,θ?©θ·¨ηζ¬ζͺζ§θ‘ηsqlε¨ι½εΎζ§θ‘δΈι δ½ζ§θ½ε―θ½ζζδΈι
                switch (version) {
                    case 36:// 36εηΊ§ε°37ζΆ DeviceConfigBean ε’ε δΊδΈδΈͺ autoSyncDeviceData ε­ζ?΅, δΈΊ ζη―ζ°ζ?θͺε¨εζ­₯εθ½
                        DatabaseUtil.upgradeTable(db, cs, DeviceConfigBean.class, DatabaseUtil.TYPE.FIELD_ADD);
                        break;
                    case 37:// 37εηΊ§ε°38ζΆ UserBean ε’ε δΊδΈδΈͺ sport_days ε­ζ?΅, δΈΊ θΏε¨ε€©ζ°,η¨δΊε―Ήζ―ζ¬ε°ζ―ε¦ιθ¦δΈθ½½η½η»ε€§ζ°ζ?
                        DatabaseUtil.upgradeTable(db, cs, UserBean.class, DatabaseUtil.TYPE.FIELD_ADD);
                        break;
                    case 38:// 38εηΊ§ε°39ζΆ BloodOxygenDetailsBean/BloodPressureDetailsBean/HeartRateDetailsBean ε’ε δΊδΈδΈͺ type ε­ζ?΅, δΈΊ ζ―ε¦θͺε¨ζ£ζ΅ ηζ°ζ?,ε¦εζ―ζε¨ (ι»θ?€0=ζε¨ζ£ζ΅,1=θͺε¨ζ£ζ΅)
                        //2018-04-06 ε³ε? ε δΈΊζ₯θ―’θΏε¨ζ°ζ?ζ°ιδΈζε‘ε¨δΈηΈεζΆ appιθ¦ιζ°εζ­₯ε¨ι¨ζ°ζ?,  η°ε¨θΏδΈε¦ζΉζ ζΉθ‘¨/ζ΄ζ°ζ°ζ?εΊηζ¬ζΆ,η΄ζ₯ζΈι€ε¨ι¨ζ°ζ?εΊζ°ζ?  ηΆειε¨ι¨ζδΈζ₯
                        //ε ι€εΉΆιζ°εε»Ί
                        DatabaseUtil.upgradeTable(db, cs, SportBean.class, DatabaseUtil.TYPE.TABLE_DELETE_AND_RECREATE);
                        DatabaseUtil.upgradeTable(db, cs, BloodOxygenBean.class, DatabaseUtil.TYPE.TABLE_DELETE_AND_RECREATE);
                        DatabaseUtil.upgradeTable(db, cs, BloodPressureBean.class, DatabaseUtil.TYPE.TABLE_DELETE_AND_RECREATE);
                        DatabaseUtil.upgradeTable(db, cs, HeartRateBean.class, DatabaseUtil.TYPE.TABLE_DELETE_AND_RECREATE);
                        break;
                    case 39:
                        //39εηΊ§ε°40ζΆοΌζ΄ζΉδΊδΉεζιεεζ°΄ζιηι»θ?€εΌοΌι»θ?€ιδΈ­ζ―δΈε€©οΌεε ζ―η‘¬δ»Άζ²‘ζεεζ¬‘ζιηεθ½

                        // 39εηΊ§ε°40ζΆ UserBean ε’ε δΊδΈδΈͺ adv_id/device_name ε­ζ?΅ η¨δΊεΊδ»Άζη η­ζδ½
                        DatabaseUtil.upgradeTable(db, cs, UserBean.class, DatabaseUtil.TYPE.FIELD_ADD);
                        break;
                    case 40://η»η¨ζ·δΊδΈδΈͺιͺι ε―θ½ε―Όθ΄ζ°ζ?εΊεηΊ§δΈδΊζ ιζ°εηΊ§δΈΊ41δΈζ¬‘
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
                    case 42://42εηΊ§ε°43ζΆ DeviceConfigBean#UnitConfig ζ·»ε δΊδΈδΈͺtimeUnitε­ζ?΅
                        //ζ ιε€η,ζ°ζ?εΊδΌθͺε¨ιεΊ
                        break;
                    case 43:// 43εηΊ§ε°44ζΆ UserBean ε’ε δΊ  target_calory target_weight ε­ζ?΅
                        DatabaseUtil.upgradeTable(db, cs, UserBean.class, DatabaseUtil.TYPE.FIELD_ADD);
                        break;
                    case 44:// 44εηΊ§ε°45ζΆ UserBean ε’ε δΊ  total_meal_day first_meal_date ε­ζ?΅
                        DatabaseUtil.upgradeTable(db, cs, UserBean.class, DatabaseUtil.TYPE.FIELD_ADD);
                        break;
                    case 45:// 45εηΊ§ε°46ζΆ  ε’ε δΊ θ‘¨DietBean
                        try {
                            TableUtils.createTableIfNotExists(cs, DietBean.class);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 46:// 46εηΊ§ε°47ζΆ  ε’ε δΊ ε€δΈͺEmail
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
                                            "com.google.android.email",//θ°·ζ­ε?εθͺεΈ¦ι?δ»Ά
                                            "com.samsung.android.email.provider",//Samsung Email
                                            "com.yahoo.mobile.client.android.mail",//Yahoo Email
                                            "com.tencent.androidqqmail",//QQι?η?±
                                            "com.netease.mobimail",//η½ζι?η?±
                                            "cn.cj.pe"//139ι?η?±
                                    ), "file:///android_asset/icon_email_reminder.png", true));
                                }

                                remindConfig.setRemindAppPushList(remindAppPushList);
                                deviceConfigBean.setRemindConfig(remindConfig);
                                deviceConfigDao.update(deviceConfigBean);
                            }
                        } catch (Exception ignored) {
                        }
                        break;
                    case 47:// 47εηΊ§ε°48ζΆ  ε’ε δΊ θ‘¨SportModeBean
                        try {
                            TableUtils.createTableIfNotExists(cs, SportModeBean.class);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 48:// 47εηΊ§ε°48ζΆ  ε’ε δΊ Instagram
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
                    case 49:// 48εηΊ§ε°49ζΆ DeviceConfigBean ε’ε δΊδΈδΈͺ temperatureAutoCheckConfig ε­ζ?΅, δΈΊ δ½ζΈ©θͺε¨ζ£ζ΅εΌε³
                        DatabaseUtil.upgradeTable(db, cs, DeviceConfigBean.class, DatabaseUtil.TYPE.TABLE_DELETE_AND_RECREATE);
                        break;
                    case 50:// ε’ε TemperatureBean
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
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {//9.0
//            Reflection.unseal(base);
//        }
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
