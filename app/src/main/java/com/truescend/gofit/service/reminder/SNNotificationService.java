package com.truescend.gofit.service.reminder;

import android.annotation.TargetApi;
import android.app.Notification;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import androidx.annotation.RequiresApi;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.sn.utils.IF;
import com.sn.utils.SNLog;
import com.sn.utils.SystemUtil;
import com.truescend.gofit.utils.PermissionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * 功能：通知栏信息监听服务
 * 监听各个App在通知栏推送的信息
 * Author:Created by 泽鑫 on 2018/2/23 16:12.
 * 修改:东芝  1.别重写super,  否则NotificationListenerService不兼容4.3,报抽象错误, 具体原因看源码
 * 2.别重写onBind(), 否则服务不会创建 除非你super.onBind()
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class SNNotificationService extends NotificationListenerService {
    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SNLog.e("通知监听:NotificationService#onCreate");

    }


    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if (sbn == null) return;
        Notification notification = sbn.getNotification();
        if (notification == null) return;
        try {
            String packName = sbn.getPackageName();
            String title = null;
            String content = null;
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                try {
//                    adaptCallPhone(notification, packName);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Bundle extras = notification.extras;
                if (extras != null) {
                    try {
                        title = extras.getString(Notification.EXTRA_TITLE, null);
                        content = extras.getString(Notification.EXTRA_TEXT, null) ;
                        if (IF.isEmpty(content)) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                content = extras.getString(Notification.EXTRA_BIG_TEXT, null) ;
                            }
                        }
                    } catch (Exception ignored) {
                    }
                    //小米推送
                    if (PermissionUtils.isPhone(PermissionUtils.MANUFACTURER_XIAOMI)) {
                        if(SystemUtil.isMIUI12()){
                            try {
                                ApplicationInfo info = (ApplicationInfo) extras.get("android.appInfo");
                                packName = info.packageName ;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }else if (extras.containsKey("target_package") && !TextUtils.isEmpty(extras.getCharSequence("target_package", null))) {
                            packName = extras.getCharSequence("target_package", null).toString();
                        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT_WATCH) {
                            String group = notification.getGroup();
                            if (!TextUtils.isEmpty(group)) {
                                packName = group;
                            }
                        }
                    }
                }
            }

            if (IF.isEmpty(content) && !IF.isEmpty(notification.tickerText)) {
                content = notification.tickerText.toString();
            }

            SNNotificationPushHelper.getInstance().handleMessage(SNNotificationPushHelper.TYPE_NOTIFICATION_LISTENER_SERVICE, packName, title, content);
        } catch (
                Exception ignored) {
            //try住防止解析闪退 导致服务挂掉
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void adaptCallPhone(Notification notification, String packName) throws NoSuchFieldException, IllegalAccessException {
        //适配三星9.0来电号码获取
        if ("com.samsung.android.incallui".equalsIgnoreCase(packName) && "CHANNEL_ID_INCOMING_CALL".equalsIgnoreCase(notification.getChannelId())) {
            RemoteViews contentView = notification.contentView;
            Field mActionsField = contentView.getClass().getDeclaredField("mActions");
            mActionsField.setAccessible(true);
            ArrayList list = (ArrayList) mActionsField.get(contentView);
            Object mRemoteViews$ReflectionAction = list.get(2);
            Field methodNameField;
            try {
                methodNameField = mRemoteViews$ReflectionAction.getClass().getDeclaredField("methodName");
            } catch (NoSuchFieldException e) {
                return;
            }
            methodNameField.setAccessible(true);
            if (methodNameField.get(mRemoteViews$ReflectionAction).equals("setText")) {
                Field valueField = mRemoteViews$ReflectionAction.getClass().getDeclaredField("value");
                valueField.setAccessible(true);
                String phoneNumber = String.valueOf(valueField.get(mRemoteViews$ReflectionAction));
                if (Pattern.matches("[0-9]+", phoneNumber.replaceAll(" ", "").replaceAll("[-+]", ""))) {
                    SNLog.i("三星来电:" + phoneNumber);
                }
            }


        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {

    }


    @Override
    public void onDestroy() {

        SNLog.e("通知监听:NotificationService#onDestroy");
        PermissionUtils.requestRebindNotificationListenerService(this);
        SNNotificationPushHelper.getInstance().recycle();
        super.onDestroy();
    }


}
