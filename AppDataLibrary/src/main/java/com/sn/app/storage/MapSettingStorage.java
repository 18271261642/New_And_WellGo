package com.sn.app.storage;

import com.sn.utils.storage.SNStorage;

/**
 * 作者:东芝(2018/3/15).
 * 功能:地图存储
 */
public class MapSettingStorage extends SNStorage {


    private final static String KEEP_SCREEN_ENABLE = "KEEP_SCREEN_ENABLE";
    private final static String BEGIN_VIBRATION_ENABLE = "BEGIN_VIBRATION_ENABLE";
    private final static String END_VIBRATION_ENABLE = "END_VIBRATION_ENABLE";
    private final static String WEATHER_ENABLE = "WEATHER_ENABLE";

    public static void setKeepScreenEnable(boolean keepScreenEnable) {
        setValue(KEEP_SCREEN_ENABLE, keepScreenEnable);
    }

    public static boolean isKeepScreenEnable() {
        return getValue(KEEP_SCREEN_ENABLE, true);
    }


    public static void setWeatherEnable(boolean weatherEnable) {
        setValue(WEATHER_ENABLE, weatherEnable);
    }

    public static boolean isWeatherEnable() {
        return getValue(WEATHER_ENABLE, true);
    }

    public static void setBeginVibrationEnable(boolean beginVibrationEnable) {
        setValue(BEGIN_VIBRATION_ENABLE, beginVibrationEnable);
    }

    public static boolean isBeginVibrationEnable() {
        return getValue(BEGIN_VIBRATION_ENABLE, false);
    }

    public static void setEndVibrationEnable(boolean endVibrationEnable) {
        setValue(END_VIBRATION_ENABLE, endVibrationEnable);
    }

    public static boolean isEndVibrationEnable() {
        return getValue(END_VIBRATION_ENABLE, false);
    }
}
