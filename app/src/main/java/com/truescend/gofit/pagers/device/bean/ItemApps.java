package com.truescend.gofit.pagers.device.bean;

import android.content.pm.ApplicationInfo;

/**
 * 作者:东芝(2019/12/18).
 * 功能:
 */
public class ItemApps {
    ApplicationInfo appInfo;
    boolean isChecked;
    String  appName;

    public ItemApps(ApplicationInfo appInfo,String appName, boolean isChecked) {
        this.appInfo = appInfo;
        this.appName = appName;
        this.isChecked = isChecked;
    }

    public ItemApps() {
    }

    public ApplicationInfo getAppInfo() {
        return appInfo;
    }

    public void setAppInfo(ApplicationInfo appInfo) {
        this.appInfo = appInfo;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
}
