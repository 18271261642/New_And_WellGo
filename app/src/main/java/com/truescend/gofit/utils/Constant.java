package com.truescend.gofit.utils;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Environment;

import com.truescend.gofit.BuildConfig;

import java.io.File;
import java.lang.reflect.Method;

/**
 * 作者:东芝(2017/11/16).
 * 功能:静态常量
 */

public class Constant {

    /**
     * 是模拟器调试模式
     * 如果你是开发者 如果你使用其他安卓模拟器 请把 Build.USER 打印出来的值放这里作为判断依据
     * 你不写我也不拦你
     */
    private static boolean isEmulator;

    public static boolean isEmulator() {
        if (!BuildConfig.DEBUG) return isEmulator = false;

        if (isEmulator) {
            return true;
        }

        //雷电模拟器
        if (Build.USER.equalsIgnoreCase("denglibo")) {
            return isEmulator = true;
        }

        String ro_hardware = null;
        try {
            @SuppressLint("PrivateApi") Class<?> systemPropertyClazz = Class.forName("android.os.SystemProperties");
            Method method = systemPropertyClazz.getDeclaredMethod("get", String.class);
            method.setAccessible(true);
            ro_hardware = (String) method.invoke(systemPropertyClazz, "ro.hardware");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (ro_hardware == null) {
            return isEmulator = false;
        }
        //天天模拟器
        if ("ttVM_x86".equalsIgnoreCase(ro_hardware)||"android_x86".equalsIgnoreCase(ro_hardware)) {
            return isEmulator = true;
        }
        return isEmulator = false;
    }


    public static class URL {
        public static final String HELP = "http://note.youdao.com/noteshare?id=1061a3c5ea7196eaa5b2dc32d44811e0&type=note#/";
        public static final String HELP_VERIFICATION_CODE = "http://note.youdao.com/noteshare?id=07f6424259fe39e764edd257c0cb4b74";
        public static final String HELP_PRIVACY_POLICY = "http://note.youdao.com/share/?id=3b09c2ef7c4039449a503ffd4fd6c4dc&type=note#/";
    }

    //
    public static class Path {

        /**
         * sd卡根目录
         */
        public static final String SDCARD = Environment.getExternalStorageDirectory().getAbsolutePath();
        /**
         * 项目根目录
         */
        public static final String ROOT_PATH = mkdirs(SDCARD +  "/"+BuildConfig.APP_UPDATE_APPNAME);

        /**
         * 图片缓存位置
         */
        public static final String CACHE_IMAGE = mkdirs(ROOT_PATH + "/cache_image");

        /**
         * 下载Apk缓存
         */
        public static final String CACHE_APP_DOWNLOAD = mkdirs(ROOT_PATH + "/cache_app_download");
        /**
         * OTA文件下载地址
         */
        public static final String CACHE_OTA_DOWNLOAD = mkdirs(ROOT_PATH + "/cache_ota_download");

        /**
         * 崩溃日志保存位置
         */
        public static final String CACHE_LOG = mkdirs(ROOT_PATH + "/cache_log");

        /**
         * 分享图片保存位置
         */
        public static final String CACHE_SHARE_IMAGE = mkdirs(ROOT_PATH + "/cache_share");

        /**
         * 地图缓存位置
         */
        public static final String CACHE_MAP = mkdirs(ROOT_PATH + "/cache_map");
        /**
         * 地图轨迹文件位置
         */
        public static final String CACHE_MAP_GPX_PATH = mkdirs(ROOT_PATH + "/cache_map/gpx");

        /**
         * 远程拍照存储的位置
         */
        public static final File CAMERA = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);

        private static String mkdirs(String path) {
            File file = new File(path);
            if (file.isFile()) {
                if (!file.getParentFile().exists()) {
                    boolean mkdirs = file.getParentFile().mkdirs();
                }
            } else if (!file.exists()) {
                boolean mkdirs = file.mkdirs();
            }
            return path;
        }

    }
//    ////
//
//    public static class SharePerferencesKey {
//
//    }
//
//    public static class KEY {
//
//    }
//
//    public static class Action {
//
//    }
}
