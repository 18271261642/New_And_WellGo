package com.sn.app.storage;

import com.sn.app.net.data.app.bean.WeatherListBean;
import com.sn.net.utils.JsonUtil;
import com.sn.utils.storage.SNStorage;

/**
 * 作者:东芝(2018/1/19).
 * 功能:天气存储
 */

public class WeatherStorage extends SNStorage {


    private final static String CITY = "city";
    private final static String LATITUDE = "latitude";
    private final static String LONGITUDE = "longitude";
    private final static String WEATHER_BEAN_LIST = "weatherBeanList";
    private final static String LASTWEATHERTIME = "lastWeatherTime";


    public static String getCity() {
        return getValue(CITY, ((String) null));
    }

    public static void setCity(String city) {
        setValue(CITY, city);
    }

    public static void setLatitude(double latitude) {
        setValue(LATITUDE, String.valueOf(latitude));
    }

    public static double getLatitude() {
        return Double.parseDouble(getValue(LATITUDE, "0"));
    }

    public static void setLongitude(double longitude) {
        setValue(LONGITUDE, String.valueOf(longitude));
    }

    public static double getLongitude() {
        return Double.parseDouble(getValue(LONGITUDE, "0"));
    }

    public static void setWeatherListBean(WeatherListBean weatherListBean) {
        setValue(WEATHER_BEAN_LIST, JsonUtil.toJson(weatherListBean));
    }

    public static WeatherListBean getWeatherListBean() {
        String value = getValue(WEATHER_BEAN_LIST, "");
        try {
            return JsonUtil.toBean(value, WeatherListBean.class);
        } catch (Exception e) {
            return null;
        }
    }

    public static void setLastWeatherTime(long lastWeatherTime) {
        setValue(LASTWEATHERTIME, lastWeatherTime);
    }

    public static long getLastWeatherTime() {
        return getValue(LASTWEATHERTIME, 0L);
    }
}
