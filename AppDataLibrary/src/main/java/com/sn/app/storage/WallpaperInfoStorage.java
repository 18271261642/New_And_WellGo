package com.sn.app.storage;

import android.graphics.PointF;
import android.text.TextUtils;

import com.sn.utils.storage.SNStorage;

/**
 * 作者:东芝(2019/4/30).
 * 功能: 壁纸参数存储
 */

public class WallpaperInfoStorage extends SNStorage {

    private final static String FONT_COLOR = "font_color";
    private final static String TIME_LOCATION_X = "time_location_x";
    private final static String TIME_LOCATION_Y = "time_location_y";

    public static int getFontColor(String mac) {
        return getValue(createMacHead(mac) + FONT_COLOR, 0xFFFD9927);
    }


    public static void setFontColor(String mac, int color) {
        setValue(createMacHead(mac) + FONT_COLOR, color);
    }

    public static PointF getTimeLocation(String mac) {
        return new PointF(getValue(createMacHead(mac) + TIME_LOCATION_X, 133.0f), getValue(createMacHead(mac) + TIME_LOCATION_Y, 191.0f));
    }

    public static void setTimeLocation(String mac, PointF location) {
        setValue(createMacHead(mac) + TIME_LOCATION_X, location.x);
        setValue(createMacHead(mac) + TIME_LOCATION_Y, location.y);
    }



    private static String createMacHead(String mac) {
        if (TextUtils.isEmpty(mac)) {
            return "";
        }
        return mac.replaceAll(":", "_") + "_";
    }

//    public static int getStepLocationX() {
//    }
//
//    public static int getStepLocationY() {
//    }
}
