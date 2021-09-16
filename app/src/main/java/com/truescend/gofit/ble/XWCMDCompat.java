package com.truescend.gofit.ble;

import com.sn.app.db.data.clock.AlarmClockBean;
import com.sn.app.db.data.clock.AlarmClockDao;
import com.sn.app.db.data.config.DeviceConfigBean;
import com.sn.app.db.data.config.DeviceConfigDao;
import com.sn.app.db.data.config.bean.HealthReminderConfig;
import com.sn.app.db.data.config.bean.TimeCycleSwitch;
import com.sn.app.db.data.config.bean.UnitConfig;
import com.sn.app.db.data.schedule.ScheduleBean;
import com.sn.app.db.data.schedule.ScheduleDao;
import com.sn.app.db.data.user.UserBean;
import com.sn.app.utils.AppUnitUtil;
import com.sn.app.utils.AppUserUtil;
import com.sn.blesdk.ble.DeviceType;
import com.sn.blesdk.ble.SNBLEHelper;
import com.sn.blesdk.cmd.SNCMD;
import com.sn.blesdk.net.bean.DeviceInfo;
import com.sn.blesdk.utils.DataAnalysisUtil;
import com.sn.utils.DateUtil;
import com.sn.utils.LanguageUtil;

import java.text.ParseException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * 作者:东芝(2018/3/27).
 * 功能:先微命令实现
 */

class XWCMDCompat extends CMDCompat {

    private HashMap<String, Integer> languageTableList = new HashMap<String, Integer>() {
        {
            put("zh",0x00);//中文
            put("cn",0x00);//中文
            put("en",0x01);//英文
            put("de",0x02);//德语
            put("it",0x03);//意大利语
            put("tw",0x04);//繁体中文 台湾
            put("hk",0x04);//繁体中文 香港
            put("mo",0x04);//繁体中文 澳门
            put("es",0x05);//西班牙语
            put("pt",0x06);//葡萄牙语
            put("ja",0x07);//日语
            put("fr",0x08);//法语
            put("ru",0x09);//俄语
            put("tr",0x0A);//土耳其语
            put("uk",0x0B);//乌克兰
            put("ko",0x0C);//朝鲜语

            put("ar",0x0D);//阿拉伯语
            put("hi",0x0E);//印地语
            put("vi",0x0F);//越南语
            put("te",0x10);//泰卢固语
            put("ta",0x11);//泰米尔语
            put("ur",0x12);//乌尔都语
            put("pl",0x13);//波兰语
            put("fa",0x14);//波斯语
            put("ro",0x15);//罗马尼亚话
            put("th",0x16);//泰语
            put("nl",0x17);//荷兰语

            put("sk",0x18);//斯洛伐克语
            put("cs",0x19);//捷克语
            put("hu",0x1A);//匈牙利
            put("sl",0x1B);//斯洛文尼亚文
        }
    };

    @Override
    public boolean setUserInfo() {
        UserBean user = AppUserUtil.getUser();
        UnitConfig config = AppUnitUtil.getUnitConfig();
        int gender = user.getGender() == 1/*1=男*/ ? 0x01 : 0x02;
        int age;
        try {
            Calendar low = DateUtil.convertStringToCalendar(DateUtil.YYYY_MM_DD, user.getBirthday());
            Calendar high = DateUtil.getCurrentCalendar();
            age = DateUtil.getDateOffset(low, high);
        } catch (ParseException e) {
            age = 25;
        }
        float height = user.getHeight();
        float weight = user.getWeight();
        int handedness = 0x01/*左手*/; //0x00：左手 0x01：右手
        int timeUnit = config.timeUnit == UnitConfig.TIME_24 ? 0x00 : 0x01/*24小时制*/;//0x00：24小时制,0x01：12小时制
        int distanceUnit = config.distanceUnit == UnitConfig.DISTANCE_MILES ? 0x01/*英制*/ : 0x00/*公制*/; // 0x00：公制 0x01：英制
        int temperatureUnit = config.temperatureUnit == UnitConfig.TEMPERATURE_F ? 0x01/*华摄氏度*/ : 0x00/*摄氏度*/;//0x00：摄氏度 0x01：华摄氏度

        int language = 0x01;//英文

        try {
            List<Integer> languageList = DeviceType.getCurrentDeviceInfo().getExtra().getLanguage();
            Integer systemLangIndex = languageTableList.get(LanguageUtil.getCurrentLanguage());
            for (int i = 0; i < languageList.size(); i++) {
                int serverLangIndex = languageList.get(i);
                if (i == 0) {//第0个为默认语言
                    language = serverLangIndex;
                }else if(systemLangIndex!=null&&systemLangIndex==serverLangIndex){
                    //如果当前语言 手环是支持的 就选当前语言 否则还是默认语言
                    language = serverLangIndex;
                }
            }
        } catch (Exception ignored) {

        }

        int targetSteps = user.getTarget_step();
        return SNBLEHelper.sendCMD(SNCMD.get().setDeviceBaseInfo(gender, age, height, weight, handedness, timeUnit, distanceUnit, temperatureUnit, language, targetSteps));

    }

    @Override
    public boolean setScheduleReminderInfo() {
        boolean isSend = true;
        int i = 0;
        List<ScheduleBean> list = ScheduleDao.get(ScheduleDao.class).queryForFuture();
        //把有日程的设置到设备
        for (ScheduleBean bean : list) {
            String date = bean.getDate();
            if (!SNBLEHelper.sendCMD(SNCMD.get().setScheduleReminderInfo(1, i, date, 1))) {
                //只要有一次失败 都是失败
                isSend = false;
            }
            i++;
        }
        //剩下的重置为关闭
        for (int j = 0; j < 5 - list.size(); j++) {
            //随便给个时间 因为已关闭,时间没意义
            String date = DateUtil.getCurrentDate(DateUtil.YYYY_MM_DD_HH_MM);
            if (!SNBLEHelper.sendCMD(SNCMD.get().setScheduleReminderInfo(0, i, date, 1))) {
                //只要有一次失败 都是失败
                isSend = false;
            }
            i++;
        }
        return isSend;
    }

    @Override
    public boolean setAlarmClockReminderInfo() {
        boolean isSend = true;
        int i = 0;
        List<AlarmClockBean> list = AlarmClockDao.get(AlarmClockDao.class).queryForSend(false);
        for (AlarmClockBean alarmClockBean : list) {
            int cycle = DataAnalysisUtil.getWeekCycle(alarmClockBean.getWeek());
            int statue = alarmClockBean.isSwitchStatues() ? 1 : 0;

            if(alarmClockBean.isSwitchStatues()) {
                try {
                    //如果是单次闹钟同时时间不是当天, 就取消闹钟
                    if (alarmClockBean.isSingle() && !DateUtil.equalsToday(DateUtil.convertStringToLong(DateUtil.YYYY_MM_DD_HH_MM, alarmClockBean.getDate()))) {
                        statue = 0;//因为是单次闹钟,同时跨天了, 于是关闭闹钟
                    }
                } catch (Exception ignored) {
                }
            }

            int hour = 0;
            int minute = 0;
            try {
                hour = DateUtil.getHour(DateUtil.convertStringToLong(DateUtil.YYYY_MM_DD_HH_MM, alarmClockBean.getDate()));
                minute = DateUtil.getMinute(DateUtil.convertStringToLong(DateUtil.YYYY_MM_DD_HH_MM, alarmClockBean.getDate()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (!SNBLEHelper.sendCMD(SNCMD.get().setAlarmReminderInfo(i, statue, cycle, hour, minute, 0))) {
                //只要有一次失败 都是失败
                isSend = false;
            }
            i++;
        }
//        for (int j = 0; j < 5 - list.size(); j++) {
//            if (!SNBLEHelper.sendCMD(SNCMD.get().setAlarmReminderInfo(i, 0, 128, 0, 0, 0))) {
//                //只要有一次失败 都是失败
//                isSend = false;
//            }
//            i++;
//        }
        return isSend;
    }

    @Override
    public boolean setDrinkReminderInfo() {
        int user_id = AppUserUtil.getUser().getUser_id();
        HealthReminderConfig healthReminderConfig = DeviceConfigDao.get(DeviceConfigDao.class).queryHealthReminderConfig(user_id);
        //喝水提醒
        HealthReminderConfig.HealthReminder remindDrink = healthReminderConfig.getRemindDrink();
        byte[] data = SNCMD.get().setDrinkReminderInfo(
                remindDrink.isOn() ? 1 : 0,
                DataAnalysisUtil.getWeekCycle(remindDrink.getWeek()),
                remindDrink.getItemStartTimeHour(),
                remindDrink.getItemStartTimeMinute(),
                remindDrink.getItemEndTimeHour(),
                remindDrink.getItemEndTimeMinute(),
                remindDrink.getItemIntervalTime());
        return SNBLEHelper.sendCMD(data);
    }

    @Override
    public boolean setSedentaryReminderInfo() {
        int user_id = AppUserUtil.getUser().getUser_id();
        HealthReminderConfig healthReminderConfig = DeviceConfigDao.get(DeviceConfigDao.class).queryHealthReminderConfig(user_id);
        //久坐提醒
        HealthReminderConfig.HealthReminder remindSedentary = healthReminderConfig.getRemindSedentary();
        byte[] data = SNCMD.get().setSedentaryReminderInfo(
                remindSedentary.isOn() ? 1 : 0,
                DataAnalysisUtil.getWeekCycle(remindSedentary.getWeek()),
                remindSedentary.getItemStartTimeHour(),
                remindSedentary.getItemStartTimeMinute(),
                remindSedentary.getItemEndTimeHour(),
                remindSedentary.getItemEndTimeMinute(),
                remindSedentary.getItemIntervalTime());
        return SNBLEHelper.sendCMD(data);

    }

    @Override
    public boolean setDeviceOtherInfo() {
        DeviceConfigBean bean = DeviceConfigDao.get(DeviceConfigDao.class).queryForUser(AppUserUtil.getUser().getUser_id());
        int power = 0;
        int light = bean.getLiftWristBrightScreenConfig().isOn() ? 1 : 0;
        boolean remindLostEnable;
        try {
            DeviceInfo.ExtraInfo extra = DeviceType.getCurrentDeviceInfo().getExtra();
            remindLostEnable = extra.isRemindLostEnable();
        } catch (Exception e) {
            remindLostEnable = true;
        }

        int antiLost = (bean.getRemindConfig().isRemindLost() && remindLostEnable) ? 1 : 0;
        int healthAutoCheck = bean.getHeartRateAutoCheckConfig().isOn() ? 1 : 0;
        return SNBLEHelper.sendCMD(SNCMD.get().setDeviceOtherInfo(power, light, antiLost, healthAutoCheck));
    }

    @Override
    public boolean setDeviceNightModeInfo() {
        try {
            DeviceConfigBean bean = DeviceConfigDao.get(DeviceConfigDao.class).queryForUser(AppUserUtil.getUser().getUser_id());
            TimeCycleSwitch doNotDisturb = bean.getDoNotDisturb();
            int status = doNotDisturb.isOn() ? 0x01 : 0x00;//0x00:关闭,0x01:开启
            DateUtil.HMS start_hms = DateUtil.getHMSFromString(DateUtil.HH_MM, doNotDisturb.getStartTime());
            DateUtil.HMS end_hms = DateUtil.getHMSFromString(DateUtil.HH_MM, doNotDisturb.getEndTime());
            return SNBLEHelper.sendCMD(SNCMD.get().setDeviceNightModeInfo(status, start_hms.getHour(), start_hms.getMinute(), end_hms.getHour(), end_hms.getMinute()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }
}
