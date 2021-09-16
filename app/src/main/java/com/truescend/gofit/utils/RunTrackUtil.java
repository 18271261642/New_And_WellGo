package com.truescend.gofit.utils;

import com.sn.map.bean.SNLocation;
import com.sn.utils.DateUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 作者:东芝(2018/8/1).
 * 功能:轨迹数据转换工具
 */

public class RunTrackUtil {
    public static String convertToJson(LinkedList<SNLocation> locations) {
        if (locations != null && !locations.isEmpty()) {
            JSONArray array = new JSONArray();
            try {
                for (SNLocation location : locations) {
                    JSONObject object = new JSONObject();
                    object.put("ele", String.valueOf(location.getAltitude()));
                    object.put("lat", String.valueOf(location.getLatitude()));
                    object.put("lon", String.valueOf(location.getLongitude()));
                    object.put("time", DateUtil.getDate(DateUtil.YYYY_MM_DD_HH_MM_SS, location.getTime()));
                    array.put(object);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return array.toString();
        }
        return null;
    }

    public static List<GPXCreator.GPX> convertToGPXs(LinkedList<SNLocation> locations) {
        List<GPXCreator.GPX> gpxList = new ArrayList<>();
        for (int i = 0; i < locations.size(); i++) {
            SNLocation location = locations.get(i);
            gpxList.add(new GPXCreator.GPX(location.getLatitude(), location.getLongitude(), location.getAltitude(), location.getTime()));
        }
        return gpxList;
    }

    public static List<GPXCreator.GPX> convertToGPXs(String json) {
        List<GPXCreator.GPX> gpxList = new LinkedList<>();
        if (json != null) {
            try {
                JSONArray array = new JSONArray(json);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    long time = DateUtil.convertStringToLong(DateUtil.YYYY_MM_DD_HH_MM_SS, object.optString("time"));
                    double lat = object.optDouble("lat");
                    double lon = object.optDouble("lon");
                    double ele = object.optDouble("ele");
                    gpxList.add(new GPXCreator.GPX(lat, lon, ele, time));
                }
            } catch (Exception e) {
                e.printStackTrace();
                gpxList.clear();
            }
        }
        return gpxList;
    }

}
