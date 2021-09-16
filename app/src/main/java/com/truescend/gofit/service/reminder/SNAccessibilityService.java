package com.truescend.gofit.service.reminder;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;

import com.sn.utils.SNLog;

import java.util.List;


/**
 * 辅助服务，在notification服务不起作用的情况下监听通知
 * Author Created by 泽鑫 on 2018/4/19.
 * 修改:东芝
 */

public class SNAccessibilityService extends AccessibilityService {

    @Override
    public void onCreate() {
        super.onCreate();
        SNLog.e("通知监听:MyAccessibilityService#onCreate");
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event == null) return;
        try {
            CharSequence packageName = event.getPackageName();
            int eventType = event.getEventType();
            if (eventType == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
                List<CharSequence> msgList = event.getText();
                StringBuilder builder = new StringBuilder();
                for (CharSequence charSequence : msgList) {
                    builder.append(charSequence);
                }
                String content = builder.toString();

                 SNNotificationPushHelper.getInstance().handleMessage(SNNotificationPushHelper.TYPE_ACCESSIBILITY_SERVICE, packageName.toString(), null, content);
            }
        } catch (Throwable ignored) {
        }
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        SNLog.e("通知监听:MyAccessibilityService#onServiceConnected");
    }

    @Override
    public void onInterrupt() {
        SNLog.e("通知监听:MyAccessibilityService#onInterrupt");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SNLog.e("通知监听:MyAccessibilityService#onDestroy");
        SNNotificationPushHelper.getInstance().recycle();
    }


}
