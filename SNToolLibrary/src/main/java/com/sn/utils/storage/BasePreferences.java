package com.sn.utils.storage;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

/**
 * 东芝
 * 2.0
 */
class BasePreferences {
	private static SharedPreferences sharePre;

    public static void init(Context context, String name) {
        BasePreferences.sharePre = context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

	public static SharedPreferences getSharedPreferences() {
		return sharePre;
	}

    public static float getValue(String key, float defValue) {
        return BasePreferences.sharePre.getFloat(key, defValue);
    }

    public static void setValue(String key, float defValue) {
        BasePreferences.sharePre.edit().putFloat(key, defValue).apply();
    }

    public static int getValue(String key, int defValue) {
        return BasePreferences.sharePre.getInt(key, defValue);
    }

    public static void setValue(String key, int defValue) {
        BasePreferences.sharePre.edit().putInt(key, defValue).apply();
    }

    public static long getValue(String key, long defValue) {
        return BasePreferences.sharePre.getLong(key, defValue);
    }

    public static void setValue(String key, long defValue) {
        BasePreferences.sharePre.edit().putLong(key, defValue).apply();
    }

    public static String getValue(String key, String defValue) {
        return BasePreferences.sharePre.getString(key, defValue);
    }

    public static void setValue(String key, String defValue) {
        BasePreferences.sharePre.edit().putString(key, defValue).apply();
    }

    public static Set<String> getValue(String key, Set<String> defValue) {
        return BasePreferences.sharePre.getStringSet(key, defValue);
    }

    public static void setValue(String key, Set<String> defValue) {
        BasePreferences.sharePre.edit().putStringSet(key, defValue).apply();
    }

    public static boolean getValue(String key, boolean defValue) {
        return BasePreferences.sharePre.getBoolean(key, defValue);
    }

    public static void setValue(String key, boolean defValue) {
        BasePreferences.sharePre.edit().putBoolean(key, defValue).apply();
    }

    public static boolean remove(String key) {
        return sharePre.edit().remove(key).commit();
    }

    public static void clear() {
        BasePreferences.sharePre.edit().clear().apply();
    }
}