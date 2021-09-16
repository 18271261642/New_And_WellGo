package com.sn.blesdk.utils.eventbus;

/**
 * 作者:东芝(2017/12/15).
 * 功能:蓝牙事件 ==> 界面/其他
 */

public class SNBLEEvent {
    ////////////////////////////////////////////////////////////////////////////////////////
    //--------------------------------------蓝牙/设备状态全局回调相关-----------------------------------
    ////////////////////////////////////////////////////////////////////////////////////////

    public static final int EVENT_BLE_STATUS_BLUETOOTH_ON = 0x120000;
    public static final int EVENT_BLE_STATUS_BLUETOOTH_OFF = 0x120001;
    public static final int EVENT_BLE_STATUS_CONNECTED = 0x120003;
    public static final int EVENT_BLE_STATUS_NOTIFY_ENABLE = 0x120004;
    public static final int EVENT_BLE_STATUS_DISCONNECTED = 0x120005;
    public static final int EVENT_BLE_STATUS_CONNECT_FAILED = 0x120006;


    ////////////////////////////////////////////////////////////////////////////////////////
    //--------------------------------------大数据刷新相关-----------------------------------
    ////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 已更新实时步数数据
     */
    public static final int EVENT_UPDATED_REAL_TIME_SPORT_DATA = 0x100000;
    /**
     * 已更新历史步数数据
     */
    public static final int EVENT_UPDATED_SPORT_DATA = 0x100001;
    /**
     * 已更新历史睡眠数据
     */
    public static final int EVENT_UPDATED_SLEEP_DATA = 0x100002;
    /**
     * 已更新心率历史数据
     */
    public static final int EVENT_UPDATED_HEART_RATE_DATA = 0x100003;

    /**
     * 已更新血氧历史数据
     */
    public static final int EVENT_UPDATED_BLOOD_OXYGEN_DATA = 0x100004;


    /**
     * 已更新血压历史数据
     */
    public static final int EVENT_UPDATED_BLOOD_PRESSURE_DATA = 0x100005;

    /**
     * 已更新健康实时数据
     */
    public static final int EVENT_UPDATED_REAL_TIME_HEALTH_DATA = 0x100007;

    /**
     * 已更新运动模式数据
     */
    public static final int EVENT_UPDATED_SPORT_MODE_DATA = 0x100008;

    /**
     * 已更新体温历史数据
     */
    public static final int EVENT_UPDATED_TEMPERATURE_DATA = 0x100009;

    ////////////////////////////////////////////////////////////////////////////////////////
    //--------------------------------------蓝牙指令接收相关-----------------------------------
    ////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 手环点击拍照接收
     */
    public static final int EVENT_BLE_CAMERA_TAKE_PHOTO = 0x200000;

    /**
     * 手环电量
     */
    public static final int EVENT_BLE_BAND_ELECTRIC = 0x200001;


    /**
     * 实时体检心率
     */
    public static final int EVENT_BLE_HEALTH_CHECK_HEART_RATE = 0x200002;

    /**
     * 实时体检血氧
     */
    public static final int EVENT_BLE_HEALTH_CHECK_BLOOD_OXYGEN = 0x200003;

    /**
     * 实时体检血压
     */
    public static final int EVENT_BLE_HEALTH_CHECK_BLOOD_PRESSURE = 0x200004;

    /**
     * 挂断电话
     */
    public static final int EVENT_BLE_HANG_UP_THE_PHONE = 0x200005;

    /**
     * 来电静音
     */
    public static final int EVENT_BLE_MUTE_CALLS = 0x200006;

    /**
     * 手环寻找手机
     */
    public static final int EVENT_BLE_BAND_CALL_PHONE = 0x200007;

    /**
     * 获取设备基本信息0
     */
    public static final int EVENT_DEVICE_INFO_0 = 0x200008;


    /**
     * 播放或者暂停音乐
     */
    public static final int EVENT_DEVICE_MUSIC_START_OR_PAUSE = 0x200009;

    /**
     * 下一首音乐
     */
    public static final int EVENT_DEVICE_MUSIC_NEXT = 0x200010;

    /**
     * 上一首音乐
     */
    public static final int EVENT_DEVICE_MUSIC_PREVIOUS = 0x200011;
    /**
     * 音量+
     */
    public static final int EVENT_DEVICE_MUSIC_VOLUME_UP = 0x200012;

    /**
     * 音量-
     */
    public static final int EVENT_DEVICE_MUSIC_VOLUME_DOWN = 0x200013;

    /**
     * 获取原生mac地址
     */
    public static final int EVENT_DEVICE_INFO_MAC = 0x200014;

}
