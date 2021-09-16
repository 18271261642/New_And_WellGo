package com.sn.app.storage;

import com.sn.utils.storage.SNStorage;



public class AppPushStorage extends SNStorage {

    public static void setAppPushCheck(String packageName, boolean isChecked) {
        setValue(packageName, isChecked);
    }

    public static void setAppPushCheckAppName(String packageName, String appName) {
        setValue(appName, packageName);
    }
    public static String getAppPushCheckAppPackageName( String appName) {
       return getValue(appName, "");
    }
    public static boolean isAppPushCheck(String packageName) {
        return getValue(packageName, false);
    }
}
