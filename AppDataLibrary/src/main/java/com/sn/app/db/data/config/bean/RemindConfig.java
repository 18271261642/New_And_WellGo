package com.sn.app.db.data.config.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 作者:东芝(2018/2/3).
 * 功能:消息推送设置
 */
public class RemindConfig implements Serializable {


    /**
     * 防丢提醒
     */
    private boolean remindLost = false;

    /**
     * 来电
     */
    private boolean remindCall = true;

    /**
     * 短信
     */
    private boolean remindSMS = true;

    /**
     * 应用推送提醒设置
     */
    private boolean remindAppPush = true;


    /**
     * 总应用推送列表
     */
    private List<Apps> remindAppPushList = new ArrayList<Apps>() {
        {

            add(new Apps("Instagram","com.instagram.android","file:///android_asset/icon_instagram_reminder.png",true));
            add(new Apps("Email", Arrays.asList(
                    "com.microsoft.office.outlook",//Outlook
                    "com.google.android.gm",//Gmail
                    "com.google.android.email",//谷歌安卓自带邮件
                    "com.samsung.android.email.provider",//Samsung Email
                    "com.yahoo.mobile.client.android.mail",//Yahoo Email
                    "com.tencent.androidqqmail",//QQ邮箱
                    "com.netease.mobimail",//网易邮箱
                    "cn.cj.pe"//139邮箱
            ),"file:///android_asset/icon_email_reminder.png", true));

            add(new Apps("FaceBook", Arrays.asList("com.facebook.katana","com.facebook.orca"),"file:///android_asset/icon_facebook_reminder.png", true));
            add(new Apps("Wechat",  "com.tencent.mm","file:///android_asset/icon_wechat_reminder.png", true));
            add(new Apps("Line",  Arrays.asList("jp.naver.line.android","com.linecorp.linelite","com.linecorp.lineat.android"), "file:///android_asset/icon_line_reminder.png", true));
            add(new Apps("Weibo", "com.sina.weibo","file:///android_asset/icon_weibo_reminder.png",  true));
            add(new Apps("Linkedln", "com.linkedin.android","file:///android_asset/icon_linkedln_reminder.png",  true));
            add(new Apps("QQ", Arrays.asList("com.tencent.mobileqq","com.tencent.tim", "com.tencent.minihd.qq","com.tencent.qqlite","com.tencent.mobileqqi","com.tencent.qq.kddi","com.tencent.eim"), "file:///android_asset/icon_qq_reminder.png", true));
            add(new Apps("GooglePlus", "com.google.android.apps.plus", "file:///android_asset/icon_googleplus_reminder.png", true));
            add(new Apps("WhatsApp","com.whatsapp", "file:///android_asset/icon_whatsapp_reminder.png", true));
            add(new Apps("Viber","com.viber.voip","file:///android_asset/icon_viber_reminder.png",true));


        }
    };


    /**
     * 通过包名寻找推送的app对象
     *
     * @param packageName
     * @return
     */
    public Apps findRemindAppPush(String packageName) {
        List<Apps> remindAppPushList = getRemindAppPushList();
        for (Apps apps : remindAppPushList) {
            if (apps.isThisApp(packageName)) {
                return apps;
            }
        }
        return null;
    }

    public List<Apps> getRemindAppPushList() {
        return remindAppPushList;
    }

    public void setRemindAppPushList(List<Apps> remindAppPushList) {
        this.remindAppPushList = remindAppPushList;
    }

    public boolean isRemindCall() {
        return remindCall;
    }

    public void setRemindCall(boolean remindCall) {
        this.remindCall = remindCall;
    }

    public boolean isRemindLost() {
        return remindLost;
    }

    public void setRemindLost(boolean remindLost) {
        this.remindLost = remindLost;
    }

    public boolean isRemindSMS() {
        return remindSMS;
    }

    public void setRemindSMS(boolean remindSMS) {
        this.remindSMS = remindSMS;
    }

    public boolean isRemindAppPush() {
        return remindAppPush;
    }

    public void setRemindAppPush(boolean remindAppPush) {
        this.remindAppPush = remindAppPush;
    }

    /**
     * 应用推送
     */
    public class Apps implements Serializable {

        /**
         * 应用包名(为什么是list? 因为有多开 或国内/国际版)
         */
        private List<String> packageNames = new ArrayList<>();
        private String appName;
        private String appIconFile;
        private boolean on;

        public Apps(String appName, String packageName,String appIconFile, boolean on) {
            this.appIconFile = appIconFile;
            this.packageNames.clear();
            this.packageNames.add(packageName);
            this.appName = appName;
            this.on = on;
        }

        public Apps(String appName, List<String> packageName,String appIconFile, boolean on) {
            this.appIconFile = appIconFile;
            this.packageNames.clear();
            this.packageNames.addAll(packageName);
            this.appName = appName;
            this.on = on;
        }

        public List<String>  getPackageNames() {
            return packageNames;
        }


        public String getAppIconFile() {
            return appIconFile;
        }


        /**
         * 是这个app吗
         *
         * @param packageName
         * @return
         */
        public boolean isThisApp(String packageName) {
            return this.packageNames.contains(packageName);
        }

        public String getAppName() {
            return appName;
        }


        public boolean isOn() {
            return on;
        }

        public void setOn(boolean on) {
            this.on = on;
        }
    }
}
