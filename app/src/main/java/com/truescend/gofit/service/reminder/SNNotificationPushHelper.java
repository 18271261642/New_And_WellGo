package com.truescend.gofit.service.reminder;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.provider.Telephony;
import androidx.annotation.NonNull;
import android.text.TextUtils;

import com.sn.app.db.data.config.DeviceConfigDao;
import com.sn.app.db.data.config.bean.RemindConfig;
import com.sn.app.db.data.config.bean.TimeCycleSwitch;
import com.sn.app.storage.AppPushStorage;
import com.sn.app.storage.UserStorage;
import com.sn.app.utils.AppUserUtil;
import com.sn.blesdk.ble.DeviceType;
import com.sn.blesdk.ble.SNBLEHelper;
import com.sn.blesdk.cmd.SNCMD;
import com.sn.blesdk.net.bean.DeviceInfo;
import com.sn.utils.DateUtil;
import com.sn.utils.IF;
import com.sn.utils.LanguageUtil;
import com.truescend.gofit.App;
import com.truescend.gofit.BuildConfig;
import com.truescend.gofit.utils.PermissionUtils;

import java.text.ParseException;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * 作者:东芝(2018/5/3).
 * 功能:通知监听助手
 */

public class SNNotificationPushHelper {
    public static final int TYPE_NONE = 0;
    public static final int TYPE_NOTIFICATION_LISTENER_SERVICE = 1;
    public static final int TYPE_ACCESSIBILITY_SERVICE = 2;
    private static volatile SNNotificationPushHelper instance = null;
    private boolean isRunning;
    private final LinkedBlockingDeque<Object[]> blockingDeque = new LinkedBlockingDeque<>();
    private int selectedType;
    private Thread thread;
    private String contentLast;

    private SNNotificationPushHelper() {
        startThreadRunning();
    }

    public static SNNotificationPushHelper getInstance() {
        if (instance == null) {
            synchronized (SNNotificationPushHelper.class) {
                if (instance == null) {
                    instance = new SNNotificationPushHelper();
                }
            }
        }
        return instance;
    }

    public void recycle() {
        isRunning = false;
        blockingDeque.clear();
    }

    public Thread getThread() {
        return thread;
    }

    public int getType() {
        return selectedType;
    }

    private void startThreadRunning() {
        if (!isRunning) {
            thread = new Thread() {
                public void run() {
                    isRunning = true;
                    while (isRunning) {
                        try {
                            Object[] take = blockingDeque.take();
                            if (take == null) continue;
                            selectedType = (int) take[0];
                            if (selectedType == TYPE_ACCESSIBILITY_SERVICE) {
                                boolean isNotificationServiceRunning = PermissionUtils.isServiceRunning(App.getContext(), SNNotificationService.class);
                                if (isNotificationServiceRunning) {
                                    //因为辅助服务和通知监听服务都正常运行,所以不处理辅助服务的消息,优先处理通知监听 因为通知监听服务这个更稳定,也是谷歌推荐的
                                    continue;
                                }
                            }

                            String packageName = (String) take[1];
                            String title = (String) take[2];
                            String content = (String) take[3];
                            Context context = App.getContext();
                            String defaultSmsAppPackageName = getDefaultSmsAppPackageName(context);
                            if (BuildConfig.isGooglePlayVersion && title != null && (packageName.equalsIgnoreCase("com.android.mms") || packageName.equalsIgnoreCase(defaultSmsAppPackageName))) {
                                PackageManager pm = context.getPackageManager();
                                CharSequence SMSAppName = pm.getApplicationLabel(pm.getApplicationInfo(packageName, 0));
                                if (!title.toLowerCase().contains(SMSAppName.toString().toLowerCase())) {
                                    //SNLog.i("短信:title=%s,content=%s", title, content);
                                    pushSMS(title, content);
                                    return;
                                }
                            }

                            title = TextUtils.isEmpty(title) ? "" : title.trim();
                            content = TextUtils.isEmpty(content) ? "" : content.trim();

                            //去掉内容中重复出现的标题文本 导致占用内容文本空间
                            if (content.startsWith(title)) {
                                content = content.replace(title, "").trim();
                            }
                            //标题去掉表情
                            if (title.contains("[表情]")) {
                                title = title.replaceAll("\\[表情\\]", "").trim();
                            }
                            content = content.replaceAll("：", ":");
                            //去掉内容开始的冒号
                            if (content.startsWith(":")) {
                                try {
                                    content = content.substring(1, content.length()).trim();
                                } catch (Exception ignored) {
                                }
                            }
                            int indexOf = content.indexOf(":");
                            if (indexOf != -1) {
                                String content_title = content.substring(0, indexOf);
                                if (TextUtils.isEmpty(title)) {
                                    title = content_title;
                                }
                                if (title.startsWith(content_title)) {
                                    try {
                                        content = content.substring(indexOf + 1, content.length()).trim();
                                    } catch (Exception ignored) {
                                    }
                                }
                            }

                            int user_id = UserStorage.getUserId();
                            DeviceConfigDao deviceConfigDao = DeviceConfigDao.get(DeviceConfigDao.class);
                            RemindConfig remindConfig = deviceConfigDao.queryRemindConfig(user_id);
                            TimeCycleSwitch cycleSwitch = deviceConfigDao.queryDoNotDisturbConfig(user_id);
                            boolean isCanDisturbRang = isCanDisturb(cycleSwitch);
                            RemindConfig.Apps app = remindConfig.findRemindAppPush(packageName);
                            boolean case1 = app != null && app.isOn();
                            boolean case2 = false;
                            if (!case1) {
                                //其它推送
                                case2 = AppPushStorage.isAppPushCheck(packageName);
                                //如果找不到, 可能来自聚合推送服务, 如小米推送,绿色推送联盟等
                                if (!case2) {
                                    String checkAppPackageName = AppPushStorage.getAppPushCheckAppPackageName(title);
                                    if (!TextUtils.isEmpty(checkAppPackageName)) {
                                        case2 = AppPushStorage.isAppPushCheck(checkAppPackageName);
                                    }
                                }
                            }
                            if (case1 || case2) {
                                if (IF.isEmpty(title) || ":".equals(title)) {
                                    title = app.getAppName() + ":";
                                }
                                if (!title.trim().endsWith(":")) {
                                    title = title + ":";
                                }
                                if (IF.isEmpty(content)) {
                                    if (LanguageUtil.isZH()) {
                                        content = "你收到一条新的信息";
                                    } else {
                                        content = "You received a new content.";
                                    }
                                }
                                if (packageName.equals("com.tencent.mobileqq") ||
                                        packageName.equals("com.tencent.mm") ||
                                        packageName.equals("com.tencent.tim") ||
                                        packageName.equals("com.tencent.minihd.qq") ||
                                        packageName.equals("com.tencent.qqlite") ||
                                        packageName.equals("com.tencent.mobileqqi") ||
                                        packageName.equals("com.tencent.qq.kddi") ||
                                        packageName.equals("com.tencent.eim")
                                ) {
                                    if (content.contains("正在呼叫你")) {
                                        content = "语音通话";
                                    }
                                    if (content.contains("视频通话")) {
                                        content = "视频通话";
                                    }
                                    if (content.contains("语音通话")) {
                                        content = "语音通话";
                                    }
                                    if ((contentLast != null && !contentLast.trim().isEmpty() && content.equals(contentLast))) {
                                        return;
                                    }

                                }

                                contentLast = content;
                                DeviceInfo currentDeviceInfo = DeviceType.getCurrentDeviceInfo();
                                //如果是交给手环处理 就默认直接发,由手环决定是否显示
                                if (currentDeviceInfo != null && currentDeviceInfo.isSupportBandSelfSetting()) {
                                    sendMessageToDevice(packageName, title, content);
                                } else
                                    //免打扰开关
                                    if (cycleSwitch.isOn()) {
                                        //是否在免打扰范围内
                                        if (isCanDisturbRang) {
                                            sendMessageToDevice(packageName, title, content);
                                        }
                                    } else {
                                        sendMessageToDevice(packageName, title, content);
                                    }


                            }
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }

                    }
                    isRunning = false;
                }


                private void sendMessageToDevice(final String packName, final String title, final String message) {
                    List<byte[]> list = SNCMD.get().setAppMessage(packName, title, message);
                    for (byte[] bytes : list) {
                        SNBLEHelper.sendCMD(bytes);
                    }
                }

                /**
                 * 打扰范围
                 * @param cycleSwitch
                 * @return
                 */
                private boolean isCanDisturb(TimeCycleSwitch cycleSwitch) {
                    long startTime = 0;
                    long endTime = 0;
                    long currentTime = 0;
                    try {
                        startTime = DateUtil.convertStringToLong(DateUtil.HH_MM, cycleSwitch.getStartTime());
                        endTime = DateUtil.convertStringToLong(DateUtil.HH_MM, cycleSwitch.getEndTime());
                        String date = DateUtil.getCurrentDate(DateUtil.HH_MM);
                        currentTime = DateUtil.convertStringToLong(DateUtil.HH_MM, date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (startTime < endTime) {
                        return currentTime < startTime || currentTime > endTime;
                    } else {
                        return currentTime < startTime && currentTime > endTime;
                    }
                }

            };
            thread.setPriority(Thread.MAX_PRIORITY - 1);
            thread.start();
        }
    }

    /**
     * 处理消息
     *
     * @param packageName 包名
     * @param message     内容
     */
    public void handleMessage(int type, String packageName, String title, String message) {
        blockingDeque.offer(new Object[]{type, packageName, title, message});
        if (thread == null || thread.getState() == Thread.State.TERMINATED) {
//          SNLog.i("SNNotificationPushHelper的线程被杀,重启");
            thread = null;
            isRunning = false;
        }
        if (!isRunning) {
            startThreadRunning();
        }
    }

    /**
     * 推送短信
     *
     * @param name    姓名
     * @param content 内容
     */
    private void pushSMS(String name, String content) {
        DeviceConfigDao deviceConfigDao = DeviceConfigDao.get(DeviceConfigDao.class);
        int user_id = 0;
        try {
            user_id = AppUserUtil.getUser().getUser_id();
        } catch (Exception e) {
            return;
        }
        RemindConfig remindConfig = deviceConfigDao.queryRemindConfig(user_id);
        if (remindConfig != null && remindConfig.isRemindSMS()) {
            List<byte[]> list = SNCMD.get().setSMSMessage(name, content);
            for (byte[] bytes : list) {
                SNBLEHelper.sendCMD(bytes);
            }
        }
    }

    public static String getDefaultSmsAppPackageName(@NonNull Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            return Telephony.Sms.getDefaultSmsPackage(context);
        else {
            Intent intent = new Intent(Intent.ACTION_VIEW)
                    .addCategory(Intent.CATEGORY_DEFAULT).setType("vnd.android-dir/mms-sms");
            final List<ResolveInfo> resolveInfos = context.getPackageManager().queryIntentActivities(intent, 0);
            if (resolveInfos != null && !resolveInfos.isEmpty())
                return resolveInfos.get(0).activityInfo.packageName;
            return null;
        }
    }
}
