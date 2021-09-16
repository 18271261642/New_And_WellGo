package com.sn.blesdk.interfaces;

import android.graphics.Bitmap;

import com.sn.blesdk.entity.WallpaperPackage;

import java.util.List;

/**
 * 作者:东芝(2017/11/22).
 * 描述:设备命令 基类
 */
public interface ICmd {
    ////////////////////////////////////////////////////////////////////////////////////////
    //--------------------------------------3. 系统命令-----------------------------------
    ////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 3.1  重启固件设备 (重启/重置/清除数据)
     *
     * @return
     */
    byte[] setDeviceReStart();

    /**
     * 3.2 空中升级
     *
     * @return
     */
    byte[] setDeviceAirUpdate();

    ////////////////////////////////////////////////////////////////////////////////////////
    //--------------------------------------4. 获取命令-----------------------------------
    ////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 4.1  获取MAC地址
     *
     * @return
     */
    byte[] getDeviceMacInfo();

    /**
     * 4.2  获取设备基本信息 0
     *
     * @return
     */
    byte[] getDeviceInfoCmd0();

    /**
     * 4.3  获取设备基本信息 1
     *
     * @return
     */
    byte[] getDeviceInfoCmd1();


    /**
     * 4.4  获取设备闹钟提醒 信息
     *
     * @return
     */
    byte[] getAlarmReminderInfo();

    /**
     * 4.5   获取设备日程提醒 信息
     *
     * @return
     */
    byte[] getScheduleReminderInfo();

    /**
     * 4.6   获取设备喝水提醒 信息
     *
     * @return
     */
    byte[] getDrinkReminderInfo();

    /**
     * 4.7  获取设备久坐提醒 信息
     *
     * @return
     */
    byte[] getSedentaryReminderInfo();

    /**
     * 4.8  获取设备当前显示的时间 信息
     *
     * @return
     */
    byte[] getDeviceTimeInfo();

    /**
     * 4.9 获取设备 提醒信息 设置， 适配于 IOS  系统
     * 略
     */

    /**
     * 4.10 获取设备其他功能设置
     */
    byte[] getDeviceOtherInfo();


    /**
     * 4.11  获取夜间勿扰模式
     */
    byte[] getDeviceNightModeInfo();

    /**
     * 4.12  获取日程提醒 标签内容
     *
     * @return
     */
    byte[] getScheduleReminderTagInfo();


    /**
     * 4.13  获取设备气压，海拔，温度
     *
     * @return
     */
    byte[] getAirPressureAltitudeTemperature();


    /**
     * 4.16  设置天气
     *
     * @return
     * @param weather
     * @param curTem
     * @param maxTem
     * @param minTem
     * @param dayOffset
     */
    byte[] setWeatherInfo(int weather, int curTem, int maxTem, int minTem, int dayOffset);
    ////////////////////////////////////////////////////////////////////////////////////////
    //--------------------------------------5.Command 0x02 设置命令 -------------------------
    ////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 5.1  设置时间 (默认是手机最新时间 所以该方法无参)
     *
     * @return
     */
    byte[] setDeviceTime();

    /**
     * 5.2设置基本信息
     *
     * @param gender          0x01男,0x02女
     * @param age             年龄
     * @param height          单位CM
     * @param weight          单位kg
     * @param handedness      0x00：左手 0x01：右手
     * @param timeUnit        0x00：24小时制,0x01：12小时制
     * @param distanceUnit    0x00：公制 0x01：英制
     * @param temperatureUnit 0x00：摄氏度 0x01：华摄氏度
     * @param clientLanguage  0x00：中文,0x01：英文
     * @param targetSteps     目标步数
     * @return
     */
    byte[] setDeviceBaseInfo(int gender, int age, float height, float weight, int handedness, int timeUnit, int distanceUnit, int temperatureUnit, int clientLanguage, int targetSteps);


    /**
     * 5.4 设置 闹钟提醒
     *
     * @param num      序号(从0开始)
     * @param status   0x01 开启,0x00关闭
     * @param cycle    重复周期 [Bit0-Bit6 则周日周一周二...周六]
     * @param hour     小时集
     * @param minute   分钟
     * @param tag      标签
     * @return
     */
    byte[] setAlarmReminderInfo(int num, int status, int cycle, int hour, int minute, int tag);

    /**
     * 5.5 设置日程提醒
     *
     * @param status 0x01 开启,0x00关闭
     * @param num    序号(从0开始)
     * @param date   格式是: yyyy-MM-dd HH:mm 没有秒
     * @param tag    标签
     * @return
     */
    //0x05(产品代号) + 0x02(command ID) + 0x05(function_Key)+序号（0-4）+年+月+日+时+分+一次(每月) + 标签
    byte[] setScheduleReminderInfo(int status, int num, String date, int tag);

    /**
     * 5.6喝水提醒
     *
     * @param status       0x01 开启,0x00关闭
     * @param cycle        重复周期 [Bit0-Bit6 则周日周一周二...周六]
     * @param sHour        开始的小时
     * @param sMinute      开始的分钟
     * @param eHour        结束的小时
     * @param eMinute      结束的分钟
     * @param intervalTime 时间间隔 (分钟)
     * @return
     */
    byte[] setDrinkReminderInfo(int status, int cycle, int sHour, int sMinute, int eHour, int eMinute, int intervalTime);

    /**
     * 5.7久坐提醒
     *
     * @param status       0x01 开启,0x00关闭
     * @param cycle        重复周期 [Bit0-Bit6 则周日周一周二...周六]
     * @param sHour        开始的小时
     * @param sMinute      开始的分钟
     * @param eHour        结束的小时
     * @param eMinute      结束的分钟
     * @param intervalTime 时间间隔 (分钟)
     * @return
     */
    byte[] setSedentaryReminderInfo(int status, int cycle, int sHour, int sMinute, int eHour, int eMinute, int intervalTime);


    /**
     * 5.8 定时体检
     * 略。
     */

    /**
     * 5.9 提醒设置 ，适配于 S IOS  系统
     * 略。
     */

    /**
     * 5.10 设备其他功能设置
     * 抬腕亮屏、低电量提醒、防丢开关、心率自动体检测开关、翻腕切屏
     *
     * @param power           0x00关闭,0x01打开
     * @param light           0x00关闭,0x01打开
     * @param antiLost        0x00关闭,0x01打开
     * @param healthAutoCheck 0x00关闭,0x01打开
     * @return
     */
    byte[] setDeviceOtherInfo(int power, int light, int antiLost, int healthAutoCheck);

    byte[] setDeviceOtherInfo(int power, int light, int antiLost, int healthAutoCheck, int temperatureAutoCheck);

    /**
     * 5.11 设置 夜间勿扰模式
     *
     * @param status 0x00关闭,0x01打开
     * @param startH 开始时
     * @param startM 开始分
     * @param endH   结束时
     * @param endM   结束分
     * @return
     */
    byte[] setDeviceNightModeInfo(int status, int startH, int startM, int endH, int endM);


    ////////////////////////////////////////////////////////////////////////////////////////
    //--------------------------------------6. 绑定命令 略-----------------------------------
    ////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////
    //--------------------------------------7.提醒命令-----------------------------------
    ////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 7.1 发送来电提醒
     *
     * @param title       标题
     * @param phoneNumber 号码(内容)
     * @return
     */
    List<byte[]> setCallReminderMessage(String title, String phoneNumber);

    /**
     * 7.2发送短信提醒
     *
     * @param title   标题
     * @param content 内容
     */
    List<byte[]> setSMSMessage(String title, String content);

    /**
     * 7.3 App消息提醒
     *
     * @param packageName 包名 ,没有传null
     * @param title       标题
     * @param content     内容
     * @return
     */
    List<byte[]> setAppMessage(String packageName, String title, String content);


    /**
     * 7.4  设置日程提醒标签内容
     *
     * @param num 最大数目为
     * @param tag 标签内容
     * @return
     */
    byte[] setScheduleReminderTagInfo(int num, String tag);


    ////////////////////////////////////////////////////////////////////////////////////////
    //--------------------------------------8. command 0x05 APP 端控制命令-------------------
    ////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 8.1 打开或关闭心率开关
     *
     * @param status 1 开，0关
     * @return
     */
    byte[] setHeartRateStatus(int status);

    /**
     * 8.2 相机开关
     *
     * @return
     */
    byte[] setCameraStatus(int status);

    /**
     * 8.3 找手环
     *
     * @return
     */
    byte[] setFindDeviceStatus(boolean status);

    /**
     * 8.4 血氧开关
     *
     * @param status 1 开，0关
     * @return
     */
    byte[] setBloodOxygenStatus(int status);

    /**
     * 8.5 血压开关
     *
     * @param status 1 开，0关
     * @return
     */
    byte[] setBloodPressureStatus(int status);


    /**
     * 8.6 来电处理状态 ， 适配于Android系统
     *
     * @param status 0x00:保留，默认状态。
     *               0x01:来电，手机已经接通，开始通话
     *               0x02 来电，手机已经挂断。
     * @return
     */
    byte[] setCallStatus(int status);


    ////////////////////////////////////////////////////////////////////////////////////////
    //--------------------------------------9. command 0x06 固件设备控制命令------------------
    ////////////////////////////////////////////////////////////////////////////////////////

    //属于接收命令 不在此处解析

    ////////////////////////////////////////////////////////////////////////////////////////
    //--------------------------------------10. command 7 0x07 健康数据命令（运动，心率和睡眠）-----------------------------------
    ////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 10.1 实时运动数据发送
     *
     * @return
     */
    byte[] syncRealTimeSportData();

    /**
     * 10.2  读取设备的实时心率数据 血氧 血压等
     *
     * @return
     */
    byte[] syncRealTimeHeartRateData();


    /**
     * 10.3  同步计步大数据 计步距离卡路里大数据
     *
     * @return
     */
    byte[] syncHistorySportData();

    /**
     * 10.4 同步睡眠大数据
     *
     * @return
     */
    byte[] syncHistorySleepData();

    /**
     * 10.5 同步心率大数据
     *
     * @return
     */
    byte[] syncHistoryHeartRateData();


    /**
     * 10.6 血压大数据
     *
     * @return
     */
    byte[] syncHistoryBloodPressureData();

    /**
     * 10.7 血氧 大数据
     *
     * @return
     */
    byte[] syncHistoryBloodOxygenData();

    /**
     * 5.17 app设置设备是否进入高速传输模式
     * (用完记得关闭)
     * @param enable
     */
    byte[] setHighSpeedTransportStatus(boolean enable);

    List<WallpaperPackage> createWallpaperPackage(Bitmap src);

    /**
     * 4.20 获取设备是否支持屏保及设备显示分辨率
     */
    byte[] getWallpaperScreenInfo();

    /**
     * 4.21 获取设备是否支持屏保的文字种类,以及对应的分辨率
     */
    byte[] getWallpaperFontInfo();

    /**
     * 5.18   APP  设置设备 是否 开启屏保 功能
     */

    byte[] setWallpaperEnable(boolean enable);


    /**
     * 5.19 APP  设置设备屏保 显示时间的 具体 信息
     * @param enable
     * @param isHorizontal
     * @param fontWidth
     * @param fontHeight
     * @param colorRgb888
     * @param x
     * @param y
     * @return
     * Ox00:保留
     * 0x01:设置成功
     * 0x02:显示方式不支持
     * 0x03:需要显示的字体分辨率不支持
     * 0x04:需要显示的字体颜色不支持
     * 0x05:X 轴，Y 轴起始坐标点异常
     * 0x06:X 轴，Y 轴起始坐标点正常，根据计算，已越界
     * 0x07:与其他屏保效果块重叠
     */
    byte[] setWallpaperTimeInfo(boolean enable, boolean isHorizontal, int fontWidth, int fontHeight, int colorRgb888, int x, int y);


    /**
     * 5.20 APP  设置设备屏保 显示 记步 的 具体 信息
     */
    byte[] setWallpaperStepInfo(boolean enable, boolean isHorizontal, int fontWidth, int fontHeight, int colorRgb888, int x, int y);


    /**
     * 5.21 APP 设置设备实时运动数据的上传方式
     */
    byte[] setSyncRealTimeSportDataRealTimeCallback(boolean realTime);

    /**
     * 10.9 运动模式大数据
     * @return
     */
    byte[] syncHistorySportModelData();

    /**
     * 体温大数据
     * @return
     */
    byte[] syncHistoryTemperatureData();
}
