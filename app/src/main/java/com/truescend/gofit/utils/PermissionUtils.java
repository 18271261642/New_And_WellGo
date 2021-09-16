package com.truescend.gofit.utils;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.preference.PreferenceManager;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;

import androidx.appcompat.app.AlertDialog;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.ArraySet;

import com.sn.utils.SNLog;
import com.truescend.gofit.BuildConfig;
import com.truescend.gofit.R;
import com.truescend.gofit.service.reminder.SNAccessibilityService;
import com.truescend.gofit.service.reminder.SNNotificationService;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * 作者:东芝(2018/3/12).
 * 功能:5.0+权限申请工具
 * 为了实时兼容,所以自己写权限工具
 * 试了AndPermission\RxPermission\PermissionUtil 各种权限框架 全是垃圾,根本就不适配
 */

public class PermissionUtils {

    /**
     * Build.MANUFACTURER
     * 不管如何,它的值请全部大写
     */
    public static final String MANUFACTURER_HUAWEI = "HUAWEI";//华为
    public static final String MANUFACTURER_MEIZU = "MEIZU";//魅族
    public static final String MANUFACTURER_XIAOMI = "XIAOMI";//小米
    public static final String MANUFACTURER_SONY = "SONY";//索尼
    public static final String MANUFACTURER_OPPO = "OPPO";
    public static final String MANUFACTURER_LG = "LG";
    public static final String MANUFACTURER_VIVO = "VIVO";
    public static final String MANUFACTURER_SAMSUNG = "SAMSUNG";//三星
    public static final String MANUFACTURER_LETV = "LETV";//乐视
    public static final String MANUFACTURER_ZTE = "ZTE";//中兴
    public static final String MANUFACTURER_YULONG = "YULONG";//酷派
    public static final String MANUFACTURER_LENOVO = "LENOVO";//联想

    private static final int REQUEST_CODE_REQUEST_NATIVE_PERMISSIONS = 0x1245;

    //拒绝列表
    private static final List<String> mNativeDeniedList = new ArrayList<>();
    //有原生权限权限,但第三方权限拒绝列表
    private static final List<String> mAppOpsDeniedList = new ArrayList<>();
    //所有权限
    private static final List<String> mAllPermissionsList = new ArrayList<>();
    //上面的原生权限和AppOps权限明明显示被允许,但实际上没有权限, 蛋疼!, 此时开发者的你可以强制设置认为没有权限,通常是解决第三方权限管理工具 比如360卫士,腾讯管家, i管家等
    private static List<String> mIthinkDeniedList = new ArrayList<>();

    private static OnPermissionGrantedListener listener;
    private static boolean isReRequestIfNeed;
    private static String lastActivityName;
    private static AlertDialog dialog;


    public static void addDeniedPermission(Context context, String permission) {
        if (!mIthinkDeniedList.contains(permission)) {
            mIthinkDeniedList.add(permission);
            //存放
            PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).edit().putStringSet("permissions", new ArraySet<>(mIthinkDeniedList)).apply();
        }
    }

    public static void clearDeniedPermission(Context context) {
        mIthinkDeniedList.clear();
        //存放
        PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).edit().putStringSet("permissions", new ArraySet<>(new ArrayList<String>())).apply();

    }
//
//    /**
//     * 请求其他权限 请在onResume使用
//     *
//     * @param activity
//     * @param listener
//     */
//    public static void requestOtherPermissions(Activity activity, final OnPermissionGrantedListener listener) {
//        SNAsyncTask.execute(new SNVTaskCallBack(activity) {
//            AlertDialog dialog;
//            SpannableStringBuilder message;
//            int mDeniedType = -1;
//
//            @Override
//            public void run() throws Throwable {
//                sleep(500);
//                Activity target = this.getTarget();
//                if (target == null || target.isDestroyed() || target.isFinishing()) return;
//
//                String permissionNames = "";
//
//
//                if (!PermissionUtils.hasNotificationPushPermission(target, AppDataNotifyUtil.MAIN_CHANNEL_ID)) {
//                    permissionNames += "\n[通知栏推送等级被设置为不显示]\n用于寻找手机,以及运动数据显示等功能\n";
//                    mDeniedType = 2;
//                } else if (!PermissionUtils.hasNotificationPushPermission(target, BandCallPhoneNotifyUtil.FOUND_PHONE_CHANNEL_ID)) {
//                    permissionNames += "\n[通知栏推送等级被设置为不显示]\n用于寻找手机,以及运动数据显示等功能\n";
//                    mDeniedType = 3;
//                }
//                if (!PermissionUtils.hasNotificationEnablePermission(target)) {
//                    permissionNames += "\n[通知栏推送显示权限被关闭]\n用于寻找手机,以及运动数据显示等功能\n";
//                    mDeniedType = 4;
//                }
//                //通知监听权限
//                if (!PermissionUtils.hasNotificationListenPermission(target)) {
//                    permissionNames += "\n[通知栏读取权限]\n用于推送内容到手环上\n";
//                    mDeniedType = 1;
//                }
//
//                message = new SpannableStringBuilder("请必须允许授权这些权限才能继续使用:\n" + permissionNames);
//                message.setSpan(new ForegroundColorSpan(Color.RED), message.length() - permissionNames.length(), message.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            }
//
//            @Override
//            public void done() {
//                if (mDeniedType == -1) {
//                    if (listener != null) {
//                        listener.onGranted();
//                    }
//                    return;
//                }
//                final Activity target = this.getTarget();
//                if (target == null || target.isDestroyed() || target.isFinishing()) return;
//                if (dialog != null && dialog.isShowing()) {
//                    dialog.dismiss();
//                }
//                // 这些权限被用户总是拒绝。
//                dialog = new AlertDialog.Builder(target)
//                        .setCancelable(true)
//                        .setTitle("授权")
//                        .setMessage(message)
//                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
//                            @Override
//                            public void onDismiss(DialogInterface dialog) {
//                                if (listener != null) {
//                                    listener.onDenied();
//                                }
//                            }
//                        })
//                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
//                            @Override
//                            public void onCancel(DialogInterface dialog) {
//                                if (listener != null) {
//                                    listener.onDenied();
//                                }
//                            }
//                        })
//                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                if (listener != null) {
//                                    listener.onDenied();
//                                }
//                            }
//                        })
//                        .setPositiveButton("下一步", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                switch (mDeniedType) {
//                                    case 1:
//                                        PermissionUtils.startToNotificationListenSetting(target);
//                                        break;
//                                    case 2:
//                                        PermissionUtils.startToNotificationPushSetting(target, AppDataNotifyUtil.MAIN_CHANNEL_ID);
//                                        break;
//                                    case 3:
//                                        PermissionUtils.startToNotificationPushSetting(target, BandCallPhoneNotifyUtil.FOUND_PHONE_CHANNEL_ID);
//                                        break;
//                                    case 4:
//                                        PermissionUtils.startToNotificationPushSetting(target, BandCallPhoneNotifyUtil.FOUND_PHONE_CHANNEL_ID);
//                                        break;
//                                }
//                            }
//                        }).dialog();
//
//            }
//        });
//
//    }

    public static void requestPermissions(Activity activity, List<String> permissions, final OnPermissionGrantedListener listener) {
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        //存放
        mIthinkDeniedList = new ArrayList<>(PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext()).getStringSet("permissions", new ArraySet<String>()));
        lastActivityName = activity.getClass().getCanonicalName();
        internalRequestPermissions(activity, permissions, listener);
        Application application = activity.getApplication();
        application.unregisterActivityLifecycleCallbacks(activityLifecycleCallbacks);
        application.registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
    }

    public static void requestPermissions(Activity activity, String[] permissions, final OnPermissionGrantedListener listener) {
        requestPermissions(activity, Arrays.asList(permissions), listener);
    }

    private static void internalRequestPermissions(Activity activity, List<String> permissions, final OnPermissionGrantedListener listener) {
        PermissionUtils.listener = listener;
        mAllPermissionsList.clear();
        mAllPermissionsList.addAll(permissions);
        refreshNativeDeniedList(activity, mAllPermissionsList);
        //如果没有一个原生权限被拒绝 则再判断第三方权限是否被拒绝 (注意 判断第三方权限的时候 会自动弹出授权窗口)
        if (mNativeDeniedList.isEmpty()) {
            //(注意 判断第三方权限的时候 会自动弹出授权窗口)
            refreshAppOpsDeniedList(activity, mAllPermissionsList);

            //第三方权限也没有任何权限被拒绝,这时候 原生和第三方都允许了所有权限,可以返回成功了
            if (mAppOpsDeniedList.isEmpty()) {
                if (listener != null) {
                    listener.onGranted();
                }
            } else {
                //TODO 第三方权限 介入
                shouldShowRequestPermissionRationaleTips(activity, mAppOpsDeniedList);
            }
        } else {
            //仍然有原生权限被拒绝 ,重新请求授权
            requestNativePermissions(activity, mNativeDeniedList);
        }

    }


    /**
     * 刷新原生权限拒绝列表
     *
     * @param activity
     * @param permissions
     */
    private static void refreshNativeDeniedList(Activity activity, List<String> permissions) {
        mNativeDeniedList.clear();
        for (String permission : permissions) {
            boolean hasNativePermission = hasNativePermission(activity, permission);
            //原生权限
            if (!hasNativePermission) {
                if (!mNativeDeniedList.contains(permission)) {
                    mNativeDeniedList.add(permission);
                }
            }
        }
    }

    /**
     * 刷新第三方拒绝列表
     *
     * @param activity
     * @param permissions
     */
    private static void refreshAppOpsDeniedList(Activity activity, List<String> permissions) {
        mAppOpsDeniedList.clear();
        //原生有权限 但是否也有第三方权限这不一定 得重新判断
        for (String permission : permissions) {
            boolean hasNativePermission = hasNativePermission(activity, permission);
            boolean hasAppOpsPermission = hasAppOpsPermission(activity, permission);
            //如果原生权限是有的 但第三方权限是没有的 这种是属于第三方私自做的权限管理
            if (hasNativePermission && !hasAppOpsPermission) {
                if (!mAppOpsDeniedList.contains(permission)) {
                    mAppOpsDeniedList.add(permission);
                }
            }
            //开发者(你)认为, 其实就算都有权限,实际上还是没有权限... 去他的vivo oppo!
            if (hasNativePermission && hasAppOpsPermission && mIthinkDeniedList.contains(permission)) {
                mAppOpsDeniedList.add(permission);
            }
        }
    }

    private static void requestNativePermissions(final Activity activity, List<String> permissions) {
        String[] list = new String[permissions.size()];
        permissions.toArray(list);
        ActivityCompat.requestPermissions(activity, list, REQUEST_CODE_REQUEST_NATIVE_PERMISSIONS);

    }

    /**
     * 原生权限授权回调
     *
     * @param activity
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public static void onRequestPermissionsResult(final Activity activity, int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE_REQUEST_NATIVE_PERMISSIONS) {
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                //拒绝的权限 现在被允许了,移除掉
                if (mNativeDeniedList.contains(permission) && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    mNativeDeniedList.remove(permission);
                }
            }
            //如果没有一个原生权限被拒绝 则再判断第三方权限是否被拒绝 (注意 判断第三方权限的时候 会自动弹出授权窗口)
            if (mNativeDeniedList.isEmpty()) {
                //(注意 判断第三方权限的时候 会自动弹出授权窗口)
                refreshAppOpsDeniedList(activity, mAllPermissionsList);

                //第三方权限也没有任何权限被拒绝,这时候 原生和第三方都允许了所有权限,可以返回成功了
                if (mAppOpsDeniedList.isEmpty()) {
                    if (listener != null) {
                        listener.onGranted();
                    }
                } else {
                    //TODO 第三方权限 介入
                    shouldShowRequestPermissionRationaleTips(activity, mAppOpsDeniedList);
                }
            } else {
                if (!mNativeDeniedList.isEmpty() || !mAppOpsDeniedList.isEmpty()) {
                    List<String> all = new ArrayList<>();
                    all.addAll(mNativeDeniedList);
                    all.addAll(mAppOpsDeniedList);
                    shouldShowRequestPermissionRationaleTips(activity, all);
                }
            }

        }
    }

    /**
     * 重新检测权限
     * 已遗弃,不需要了 我自己会自动监听生命周期
     *
     * @param activity
     */
    @Deprecated
    public static void onRestart(final Activity activity) {
        if (activity != null) {
            internalRequestPermissions(activity, new ArrayList<>(mAllPermissionsList), listener);
        }
    }


    private static void shouldShowRequestPermissionRationaleTips(final Activity activity, final List<String> permissions) {
        String permissionNames = Arrays.toString(transformText(activity, permissions).toArray());

        SpannableStringBuilder message = new SpannableStringBuilder(ResUtil.getString(R.string.content_permission_need) + "\n" + permissionNames);
        message.setSpan(new ForegroundColorSpan(Color.RED), message.length() - permissionNames.length(), message.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // 这些权限被用户总是拒绝。
        dialog = new AlertDialog.Builder(activity,R.style.AppThemeDialog)
                .setCancelable(false)
                .setTitle(ResUtil.getString(R.string.content_authorized))
                .setMessage(message)//TODO    小米MIUI9,建议加一条在最后,但是好像没多少用户遇到这个问题 先不放上去,所以写下此注释: 如果你已经授权但仍然提示未授权,你需要先拒绝授权一次,然后再重新允许授权,这样才会生效.该问题在某些系统才会发生"
                .setNegativeButton(ResUtil.getString(R.string.content_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (listener != null) {
                            listener.onDenied();
                        }
                    }
                })
                .setPositiveButton(ResUtil.getString(R.string.content_approve), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (shouldShowRequestPermissionRationale(activity, permissions)) {
                            if (mNativeDeniedList.isEmpty() && !mAppOpsDeniedList.isEmpty()) {
                                //再次请求第三方权限
                                refreshAppOpsDeniedList(activity, mAllPermissionsList);
                                //如果用户仍然拒绝 则跳转到设置界面
                                if (!mAppOpsDeniedList.isEmpty()) {
                                    isReRequestIfNeed = true;
                                    clearDeniedPermission(activity);
                                    startToAppSetting(activity);
                                } else {
                                    if (listener != null) {
                                        listener.onGranted();
                                    }
                                }
                            } else {
                                requestNativePermissions(activity, permissions);
                            }
                        } else {
                            isReRequestIfNeed = true;
                            // 原生权限被拒绝,跳转到设置
                            clearDeniedPermission(activity);
                            startToAppSetting(activity);
                        }
                    }
                }).show();

    }

    private static boolean shouldShowRequestPermissionRationale(Activity activity, List<String> permissions) {
        for (String permission : permissions) {
            if (!mIthinkDeniedList.isEmpty() && permissions.containsAll(mIthinkDeniedList)) {
                return true;
            }
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                return true;
            }
        }
        return false;
    }

    public static String[] getPermissions(Activity activity) {
        PackageManager pm = activity.getPackageManager();
        try {
            return pm.getPackageInfo(BuildConfig.APPLICATION_ID, PackageManager.GET_PERMISSIONS)
                    .requestedPermissions;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return new String[]{};
        }
    }


    /**
     * 此函数可以自己定义
     *
     * @param context
     */
    public static void startToAppSetting(Context context) {
        try {
            switch (Build.MANUFACTURER.toUpperCase()) {
                case MANUFACTURER_HUAWEI:
                    startToHuaweiSetting(context);
                    break;
                case MANUFACTURER_VIVO:
                    startToVivoSetting(context);
                    break;
                case MANUFACTURER_MEIZU:
                    startToMeizuSetting(context);
                    break;
                case MANUFACTURER_XIAOMI:
                    startToXiaomiSetting(context);
                    break;
                case MANUFACTURER_SONY:
                    startToSonySetting(context);
                    break;
                case MANUFACTURER_OPPO:
                    startToOPPOSetting(context);
                    break;
                case MANUFACTURER_LG:
                    startToLGSetting(context);
                    break;
                case MANUFACTURER_LETV:
                    startToLetvSetting(context);
                    break;
                default:
                    startToDefaultAppInfoSetting(context);
                    break;
            }
        } catch (Throwable e) {
            startToDefaultAppInfoSetting(context);
        }
    }


    public static boolean isPhone(String MANUFACTURER_TYPE) {
        return Build.MANUFACTURER.equalsIgnoreCase(MANUFACTURER_TYPE);
    }

    public static void startToVivoSetting(Context context) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("packagename", BuildConfig.APPLICATION_ID);
        try {
            intent.putExtra("title", context.getPackageManager().getApplicationLabel(context.getApplicationInfo()).toString());
        } catch (Exception e) {
            intent.putExtra("title", BuildConfig.APPLICATION_ID);
        }
        //i管家包名 6.0有不相同的两款手机
        intent.setComponent(ComponentName.unflattenFromString("com.iqoo.secure/.safeguard.SoftPermissionDetailActivity"));
        if (intent.resolveActivity(context.getPackageManager()) == null) {
            return;
        }
        intent.setComponent(ComponentName.unflattenFromString("com.vivo.permissionmanager/.activity.SoftPermissionDetailActivity"));
        if (intent.resolveActivity(context.getPackageManager()) == null) {
            return;
        }
        context.startActivity(intent);
    }


    private static void startToHuaweiSetting(Context context) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
        ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity");
        intent.setComponent(comp);
        context.startActivity(intent);
    }

    private static void startToMeizuSetting(Context context) {
        Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
        context.startActivity(intent);
    }

    private static void startToXiaomiSetting(Context context) {
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        intent.putExtra("extra_pkgname", BuildConfig.APPLICATION_ID);
        context.startActivity(intent);
    }

    private static void startToSonySetting(Context context) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
        ComponentName comp = new ComponentName("com.sonymobile.cta", "com.sonymobile.cta.SomcCTAMainActivity");
        intent.setComponent(comp);
        context.startActivity(intent);
    }

    private static void startToOPPOSetting(Context context) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
        ComponentName comp = new ComponentName("com.color.safecenter", "com.color.safecenter.permission.PermissionManagerActivity");
        intent.setComponent(comp);
        context.startActivity(intent);
    }

    private static void startToLGSetting(Context context) {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
        ComponentName comp = new ComponentName("com.android.settings", "com.android.settings.Settings$AccessLockSummaryActivity");
        intent.setComponent(comp);
        context.startActivity(intent);
    }

    private static void startToLetvSetting(Context context) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
        ComponentName comp = new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.PermissionAndApps");
        intent.setComponent(comp);
        context.startActivity(intent);
    }

    /**
     * 只能打开到自带安全软件
     *
     * @param activity
     */
    private static void _360(Activity activity) {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
        ComponentName comp = new ComponentName("com.qihoo360.mobilesafe", "com.qihoo360.mobilesafe.ui.index.AppEnterActivity");
        intent.setComponent(comp);
        activity.startActivity(intent);
    }

    /**
     * 应用信息界面
     *
     * @param context
     */
    private static void startToDefaultAppInfoSetting(Context context) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            intent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
        }
        context.startActivity(intent);


    }

    /**
     * 跳转到通知内容读取权限设置
     *
     * @param context
     */
    public static void startToNotificationListenSetting(Context context) {
        try {
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            try {
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.Settings$NotificationAccessSettingsActivity");
                intent.setComponent(cn);
                intent.putExtra(":settings:show_fragment", "NotificationAccessSettings");
                context.startActivity(intent);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * 跳转到位置设置
     *
     * @param context
     */
    public static void startToLocationSetting(Context context) {
        try {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {

        }
    }

    /**
     * 跳转到app通知推送权限设置
     *
     * @param context
     * @param channelId
     */

    public static void startToNotificationPushSetting(Context context, String channelId) {

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (channelId != null && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            try {
                // for Android O
                intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                intent.putExtra("android.provider.extra.APP_PACKAGE", context.getPackageName());
            } catch (Exception e) {
                startToDefaultAppInfoSetting(context);
            }
        } else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            //参考https://stackoverflow.com/questions/32366649/any-way-to-link-to-the-android-notification-settings-for-my-app
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", context.getPackageName());
            intent.putExtra("app_uid", context.getApplicationInfo().uid);
            try {
                context.startActivity(intent);
            } catch (Exception e1) {
                startToDefaultAppInfoSetting(context);
            }
        }
    }

    /**
     * 跳转到app通知推送显示权限设置
     *
     * @param context
     * @param channelId
     */

    public static void startToNotificationEnableSetting(Context context, String channelId) {

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            try {
                //参考https://stackoverflow.com/questions/48853948/intent-to-open-the-notification-channel-settings-from-my-app
                if (channelId != null) {
                    intent.setAction(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
                    intent.putExtra(Settings.EXTRA_CHANNEL_ID, channelId);
                } else {
                    intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
                }

                context.startActivity(intent);
            } catch (Exception e) {
                startToDefaultAppInfoSetting(context);
            }
        } else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //参考https://stackoverflow.com/questions/32366649/any-way-to-link-to-the-android-notification-settings-for-my-app
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", context.getPackageName());
            intent.putExtra("android.provider.extra.APP_PACKAGE", context.getPackageName());
            intent.putExtra("app_uid", context.getApplicationInfo().uid);
            try {
                context.startActivity(intent);
            } catch (Exception e1) {
                startToDefaultAppInfoSetting(context);
            }
        }
    }

    /**
     * 判断服务是否运行
     *
     * @param context
     * @param cls
     * @return
     */
    public static boolean isServiceRunning(Context context, Class<? extends Service> cls) {
        boolean isServiceRunning = false;
        ComponentName collectorComponent = new ComponentName(context, cls);
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        if (manager != null) {
            List<ActivityManager.RunningServiceInfo> runningServices = manager.getRunningServices(Integer.MAX_VALUE);
            if (runningServices != null) {
                for (ActivityManager.RunningServiceInfo service : runningServices) {
                    if (service.service.equals(collectorComponent)) {
                        if (service.pid == Process.myPid()) {
                            isServiceRunning = true;
                        }
                    }
                }
            }
        }
        return isServiceRunning;
    }

    public static boolean isApplicationInBackground(Context context) {

        ActivityManager am  = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        try {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            return !componentInfo.getPackageName().equals(context.getPackageName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<ActivityManager.RunningAppProcessInfo> appProcesses = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                if (appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }


    public static boolean isAppInstalled(Context context,String packageName){
        try {
            final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
            List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
            if (pinfo != null) {
                for (int i = 0; i < pinfo.size(); i++) {
                    String pn = pinfo.get(i).packageName.toLowerCase(Locale.ENGLISH);
                    if (pn.equals(packageName)) {
                        return true;
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return false;

    }

    public static boolean isNotificationListenerHavePermissionButIsDead(Context context){
        boolean b = hasNotificationListenPermission(context);
        boolean serviceRunning = isServiceRunning(context, SNNotificationService.class);
        System.out.println("权限="+b+",服务状态="+serviceRunning);
        return b && !serviceRunning;
    }


    /**
     * 请求刷新通知监听服务
     * 迁移到SNNotificationListener依赖库
     *
     * @param context
     * @return
     */
    public static void requestRebindNotificationListenerService(final Context context) {
        PackageManager pm = context.getPackageManager();
        ComponentName componentName = new ComponentName(context, SNNotificationService.class);

        if (isNotificationListenerHavePermissionButIsDead(context)) {
            //如果服务没在运行 重新请求绑定服务
            //有些手机的通知服务开启较慢

            pm.setComponentEnabledSetting(componentName,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            pm.setComponentEnabledSetting(componentName,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);


        }

    }


    /**
     * 有通知/监听读取权限
     * 迁移到SNNotificationListener依赖库
     *
     * @param context
     * @return
     */
    public static boolean hasNotificationListenPermission(Context context) {
        Set<String> packageNames = NotificationManagerCompat.getEnabledListenerPackages(context);
        return !packageNames.isEmpty() && packageNames.contains(context.getPackageName());
    }

    /**
     * 是否有通知推送等级权限
     *
     * @param context
     * @param channelId 通知通道id,8.0用
     * @return
     */
    public static boolean hasNotificationPushPermission(Context context, @NonNull String channelId) {
        //参考https://stackoverflow.com/questions/46928874/android-oreo-notifications-check-if-specific-channel-enabled
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!TextUtils.isEmpty(channelId)) {
                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                if (manager != null) {
                    NotificationChannel channel = manager.getNotificationChannel(channelId);
                    if (channel != null) {
                        return channel.getImportance() != NotificationManager.IMPORTANCE_NONE;
                    }
                }
            }//通道为空时 认为是未申请通道id,所以查不到  所以先认为有权限!
            return true;
        }
        return true;

    }

    /**
     * 是否有通知可见权限
     *
     * @param context
     * @return
     */
    public static boolean hasNotificationEnablePermission(Context context) {
        return NotificationManagerCompat.from(context).areNotificationsEnabled();

    }


    /**
     * 位置权限是否开启
     * 反编译后 参考 nrf connect的源码 (nrf connect不开源)
     *
     * @param context
     * @return
     */
    public static boolean hasLocationEnablePermission(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        int locationMode = Settings.Secure.LOCATION_MODE_OFF;
        try {
            locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
        } catch (Exception ignored) {
        }
        if (locationMode != Settings.Secure.LOCATION_MODE_OFF) {
            return true;
        }
        return false;
    }


    /**
     * 跳转到打开辅助服务页面
     *
     * @param context
     */
    public static void openAccessibility(Context context) {
        context.startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
    }

    /**
     * 检查辅助服务是否打开
     *
     * @param context 上下文参数
     * @return 返回布尔值
     */
    public static boolean isAccessibilitySettingsOn(Context context) {
        int accessibilityEnabled = 0;
        final String service = context.getPackageName() + "/" + SNAccessibilityService.class.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    context.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            SNLog.e("辅助服务是否开启 %s", accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            SNLog.e("辅助服务找不到，具体信息如下：\n %s", e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');
        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString(
                    context.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        SNLog.e("辅助服务已经打开");
                        return true;
                    }
                }
            }
        } else {
            return false;
        }
        return false;
    }

    /**
     * 是否有权限
     *
     * @param context
     * @param permission
     * @return
     */
    public static boolean hasPermission(@NonNull Context context, @NonNull String permission) {
        return hasNativePermission(context, permission) && hasAppOpsPermission(context, permission);
    }


    /**
     * 是否有原生权限
     *
     * @param context
     * @param permission
     * @return
     */
    public static boolean hasNativePermission(@NonNull Context context, @NonNull String permission) {
        return ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 是否有第三方权限
     *
     * @param context
     * @param permission
     * @return
     */
    public static boolean hasAppOpsPermission(@NonNull Context context, @NonNull String permission) {

        int result = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            String op = AppOpsManager.permissionToOp(permission);
            if (TextUtils.isEmpty(op)) {
                return true;
            }

            AppOpsManager appOpsManager = context.getSystemService(AppOpsManager.class);
            if (appOpsManager != null) {
                result = appOpsManager.noteProxyOp(op, context.getPackageName());
            }
            if (result != AppOpsManager.MODE_ALLOWED) {
                return false;
            }
        }
        if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT) {
            try {
                AppOpsManager app = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                Method startOpMethod = AppOpsManager.class.getDeclaredMethod("startOp", int.class, int.class, String.class);
                startOpMethod.setAccessible(true);
                result = (Integer) startOpMethod.invoke(app, getAppOpsValue(permission), Binder.getCallingUid(), context.getPackageName());
                if (result != AppOpsManager.MODE_ALLOWED) {
                    return false;
                }
            } catch (Exception ignored) {
            }
        }
        return true;
    }


    public static int getAppOpsValue(String permission) {
        switch (permission) {
            case Manifest.permission.ACCESS_COARSE_LOCATION:
                return AppOpsUtils.OP_COARSE_LOCATION;
//            case Manifest.permission.FINE_LOCATION:
//                return AppOpsUtils.OP_FINE_LOCATION;
//            case Manifest.permission.GPS:
//                return AppOpsUtils.OP_GPS;
            case Manifest.permission.VIBRATE:
                return AppOpsUtils.OP_VIBRATE;
            case Manifest.permission.READ_CONTACTS:
                return AppOpsUtils.OP_READ_CONTACTS;
            case Manifest.permission.WRITE_CONTACTS:
                return AppOpsUtils.OP_WRITE_CONTACTS;
            case Manifest.permission.READ_CALL_LOG:
                return AppOpsUtils.OP_READ_CALL_LOG;
            case Manifest.permission.WRITE_CALL_LOG:
                return AppOpsUtils.OP_WRITE_CALL_LOG;
            case Manifest.permission.READ_CALENDAR:
                return AppOpsUtils.OP_READ_CALENDAR;
            case Manifest.permission.WRITE_CALENDAR:
                return AppOpsUtils.OP_WRITE_CALENDAR;
//            case Manifest.permission.WIFI_SCAN:
//                return AppOpsUtils.OP_WIFI_SCAN;
//            case Manifest.permission.POST_NOTIFICATION:
//                return AppOpsUtils.OP_POST_NOTIFICATION;
//            case Manifest.permission.NEIGHBORING_CELLS:
//                return AppOpsUtils.OP_NEIGHBORING_CELLS;
            case Manifest.permission.CALL_PHONE:
                return AppOpsUtils.OP_CALL_PHONE;
            case Manifest.permission.READ_SMS:
                return AppOpsUtils.OP_READ_SMS;
//            case Manifest.permission.WRITE_SMS:
//                return AppOpsUtils.OP_WRITE_SMS;
            case Manifest.permission.RECEIVE_SMS:
                return AppOpsUtils.OP_RECEIVE_SMS;
//            case Manifest.permission.RECEIVE_EMERGECY_SMS:
//                return AppOpsUtils.OP_RECEIVE_EMERGECY_SMS;
            case Manifest.permission.RECEIVE_MMS:
                return AppOpsUtils.OP_RECEIVE_MMS;
            case Manifest.permission.RECEIVE_WAP_PUSH:
                return AppOpsUtils.OP_RECEIVE_WAP_PUSH;
            case Manifest.permission.SEND_SMS:
                return AppOpsUtils.OP_SEND_SMS;
//            case Manifest.permission.READ_ICC_SMS:
//                return AppOpsUtils.OP_READ_ICC_SMS;
//            case Manifest.permission.WRITE_ICC_SMS:
//                return AppOpsUtils.OP_WRITE_ICC_SMS;
            case Manifest.permission.WRITE_SETTINGS:
                return AppOpsUtils.OP_WRITE_SETTINGS;
            case Manifest.permission.SYSTEM_ALERT_WINDOW:
                return AppOpsUtils.OP_SYSTEM_ALERT_WINDOW;
//            case Manifest.permission.ACCESS_NOTIFICATIONS:
//                return AppOpsUtils.OP_ACCESS_NOTIFICATIONS;
            case Manifest.permission.CAMERA:
                return AppOpsUtils.OP_CAMERA;
            case Manifest.permission.RECORD_AUDIO:
                return AppOpsUtils.OP_RECORD_AUDIO;
//            case Manifest.permission.PLAY_AUDIO:
//                return AppOpsUtils.OP_PLAY_AUDIO;
//            case Manifest.permission.READ_CLIPBOARD:
//                return AppOpsUtils.OP_READ_CLIPBOARD;
//            case Manifest.permission.WRITE_CLIPBOARD:
//                return AppOpsUtils.OP_WRITE_CLIPBOARD;
//            case Manifest.permission.TAKE_MEDIA_BUTTONS:
//                return AppOpsUtils.OP_TAKE_MEDIA_BUTTONS;
//            case Manifest.permission.TAKE_AUDIO_FOCUS:
//                return AppOpsUtils.OP_TAKE_AUDIO_FOCUS;
//            case Manifest.permission.AUDIO_MASTER_VOLUME:
//                return AppOpsUtils.OP_AUDIO_MASTER_VOLUME;
//            case Manifest.permission.AUDIO_VOICE_VOLUME:
//                return AppOpsUtils.OP_AUDIO_VOICE_VOLUME;
//            case Manifest.permission.AUDIO_RING_VOLUME:
//                return AppOpsUtils.OP_AUDIO_RING_VOLUME;
//            case Manifest.permission.AUDIO_MEDIA_VOLUME:
//                return AppOpsUtils.OP_AUDIO_MEDIA_VOLUME;
//            case Manifest.permission.AUDIO_ALARM_VOLUME:
//                return AppOpsUtils.OP_AUDIO_ALARM_VOLUME;
//            case Manifest.permission.AUDIO_NOTIFICATION_VOLUME:
//                return AppOpsUtils.OP_AUDIO_NOTIFICATION_VOLUME;
//            case Manifest.permission.AUDIO_BLUETOOTH_VOLUME:
//                return AppOpsUtils.OP_AUDIO_BLUETOOTH_VOLUME;
            case Manifest.permission.WAKE_LOCK:
                return AppOpsUtils.OP_WAKE_LOCK;
//            case Manifest.permission.MONITOR_LOCATION:
//                return AppOpsUtils.OP_MONITOR_LOCATION;
//            case Manifest.permission.MONITOR_HIGH_POWER_LOCATION:
//                return AppOpsUtils.OP_MONITOR_HIGH_POWER_LOCATION;
//            case Manifest.permission.GET_USAGE_STATS:
//                return AppOpsUtils.OP_GET_USAGE_STATS;
//            case Manifest.permission.MUTE_MICROPHONE:
//                return AppOpsUtils.OP_MUTE_MICROPHONE;
//            case Manifest.permission.TOAST_WINDOW:
//                return AppOpsUtils.OP_TOAST_WINDOW;
//            case Manifest.permission.PROJECT_MEDIA:
//                return AppOpsUtils.OP_PROJECT_MEDIA;
//            case Manifest.permission.ACTIVATE_VPN:
//                return AppOpsUtils.OP_ACTIVATE_VPN;
//            case Manifest.permission.WRITE_WALLPAPER:
//                return AppOpsUtils.OP_WRITE_WALLPAPER;
//            case Manifest.permission.ASSIST_STRUCTURE:
//                return AppOpsUtils.OP_ASSIST_STRUCTURE;
//            case Manifest.permission.ASSIST_SCREENSHOT:
//                return AppOpsUtils.OP_ASSIST_SCREENSHOT;
            case Manifest.permission.READ_PHONE_STATE:
                return AppOpsUtils.OP_READ_PHONE_STATE;
            case Manifest.permission.ADD_VOICEMAIL:
                return AppOpsUtils.OP_ADD_VOICEMAIL;
            case Manifest.permission.USE_SIP:
                return AppOpsUtils.OP_USE_SIP;
            case Manifest.permission.PROCESS_OUTGOING_CALLS:
                return AppOpsUtils.OP_PROCESS_OUTGOING_CALLS;
            case Manifest.permission.USE_FINGERPRINT:
                return AppOpsUtils.OP_USE_FINGERPRINT;
            case Manifest.permission.BODY_SENSORS:
                return AppOpsUtils.OP_BODY_SENSORS;
//            case Manifest.permission.READ_CELL_BROADCASTS:
//                return AppOpsUtils.OP_READ_CELL_BROADCASTS;
//            case Manifest.permission.MOCK_LOCATION:
//                return AppOpsUtils.OP_MOCK_LOCATION;
            case Manifest.permission.READ_EXTERNAL_STORAGE:
                return AppOpsUtils.OP_READ_EXTERNAL_STORAGE;
            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                return AppOpsUtils.OP_WRITE_EXTERNAL_STORAGE;
//            case Manifest.permission.TURN_SCREEN_ON:
//                return AppOpsUtils.OP_TURN_SCREEN_ON;
        }
        return AppOpsUtils.OP_NONE;
    }


    public interface OnPermissionGrantedListener {
        void onGranted();

        void onDenied();
    }

    private static Application.ActivityLifecycleCallbacks activityLifecycleCallbacks = new Application.ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {
            if (activity != null && lastActivityName != null && lastActivityName.equalsIgnoreCase(activity.getClass().getCanonicalName())) {
                if (isReRequestIfNeed) {
                    isReRequestIfNeed = false;
                    internalRequestPermissions(activity, new ArrayList<>(mAllPermissionsList), listener);
                }
            }
        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    };

    private static List<String> transformText(Context context, List<String> permissions) {
        List<String> textList = new ArrayList<>();
        for (String permission : permissions) {
            switch (permission) {
                case Manifest.permission.READ_CALENDAR:
                case Manifest.permission.WRITE_CALENDAR: {
                    String message = context.getString(R.string.content_calendar);
                    if (!textList.contains(message)) {
                        textList.add(message);
                    }
                    break;
                }

                case Manifest.permission.CAMERA: {
                    String message = context.getString(R.string.content_camera);
                    if (!textList.contains(message)) {
                        textList.add(message);
                    }
                    break;
                }
                case Manifest.permission.READ_CONTACTS:
                case Manifest.permission.WRITE_CONTACTS:
                case Manifest.permission.GET_ACCOUNTS: {
                    String message = context.getString(R.string.content_contacts);
                    if (!textList.contains(message)) {
                        textList.add(message);
                    }
                    break;
                }
                case Manifest.permission.ACCESS_FINE_LOCATION:
                case Manifest.permission.ACCESS_COARSE_LOCATION: {
                    String message = context.getString(R.string.content_location);
                    if (!textList.contains(message)) {
                        textList.add(message);
                    }
                    break;
                }
                case Manifest.permission.RECORD_AUDIO: {
                    String message = context.getString(R.string.content_microphone);
                    if (!textList.contains(message)) {
                        textList.add(message);
                    }
                    break;
                }
                case Manifest.permission.READ_PHONE_STATE:
                case Manifest.permission.CALL_PHONE:
                case Manifest.permission.READ_CALL_LOG:
                case Manifest.permission.WRITE_CALL_LOG:
                case Manifest.permission.USE_SIP:
                case Manifest.permission.PROCESS_OUTGOING_CALLS: {
                    String message = context.getString(R.string.content_phone);
                    if (!textList.contains(message)) {
                        textList.add(message);
                    }
                    break;
                }
                case Manifest.permission.BODY_SENSORS: {
                    String message = context.getString(R.string.content_sensor);
                    if (!textList.contains(message)) {
                        textList.add(message);
                    }
                    break;
                }
                case Manifest.permission.SEND_SMS:
                case Manifest.permission.RECEIVE_SMS:
                case Manifest.permission.READ_SMS:
                case Manifest.permission.RECEIVE_WAP_PUSH:
                case Manifest.permission.RECEIVE_MMS: {
                    String message = context.getString(R.string.content__sms);
                    if (!textList.contains(message)) {
                        textList.add(message);
                    }
                    break;
                }
                case Manifest.permission.READ_EXTERNAL_STORAGE:
                case Manifest.permission.WRITE_EXTERNAL_STORAGE: {
                    String message = context.getString(R.string.content_storage);
                    if (!textList.contains(message)) {
                        textList.add(message);
                    }
                    break;
                }
                default:

                    break;
            }
        }
        return textList;
    }


    private class AppOpsUtils {
        public static final int OP_NONE = -1;
        public static final int OP_COARSE_LOCATION = 0;
        public static final int OP_FINE_LOCATION = 1;
        public static final int OP_GPS = 2;
        public static final int OP_VIBRATE = 3;
        public static final int OP_READ_CONTACTS = 4;
        public static final int OP_WRITE_CONTACTS = 5;
        public static final int OP_READ_CALL_LOG = 6;
        public static final int OP_WRITE_CALL_LOG = 7;
        public static final int OP_READ_CALENDAR = 8;
        public static final int OP_WRITE_CALENDAR = 9;
        public static final int OP_WIFI_SCAN = 10;
        public static final int OP_POST_NOTIFICATION = 11;
        public static final int OP_NEIGHBORING_CELLS = 12;
        public static final int OP_CALL_PHONE = 13;
        public static final int OP_READ_SMS = 14;
        public static final int OP_WRITE_SMS = 15;
        public static final int OP_RECEIVE_SMS = 16;
        public static final int OP_RECEIVE_EMERGECY_SMS = 17;
        public static final int OP_RECEIVE_MMS = 18;
        public static final int OP_RECEIVE_WAP_PUSH = 19;
        public static final int OP_SEND_SMS = 20;
        public static final int OP_READ_ICC_SMS = 21;
        public static final int OP_WRITE_ICC_SMS = 22;
        public static final int OP_WRITE_SETTINGS = 23;
        public static final int OP_SYSTEM_ALERT_WINDOW = 24;
        public static final int OP_ACCESS_NOTIFICATIONS = 25;
        public static final int OP_CAMERA = 26;
        public static final int OP_RECORD_AUDIO = 27;
        public static final int OP_PLAY_AUDIO = 28;
        public static final int OP_READ_CLIPBOARD = 29;
        public static final int OP_WRITE_CLIPBOARD = 30;
        public static final int OP_TAKE_MEDIA_BUTTONS = 31;
        public static final int OP_TAKE_AUDIO_FOCUS = 32;
        public static final int OP_AUDIO_MASTER_VOLUME = 33;
        public static final int OP_AUDIO_VOICE_VOLUME = 34;
        public static final int OP_AUDIO_RING_VOLUME = 35;
        public static final int OP_AUDIO_MEDIA_VOLUME = 36;
        public static final int OP_AUDIO_ALARM_VOLUME = 37;
        public static final int OP_AUDIO_NOTIFICATION_VOLUME = 38;
        public static final int OP_AUDIO_BLUETOOTH_VOLUME = 39;
        public static final int OP_WAKE_LOCK = 40;
        public static final int OP_MONITOR_LOCATION = 41;
        public static final int OP_MONITOR_HIGH_POWER_LOCATION = 42;
        public static final int OP_GET_USAGE_STATS = 43;
        public static final int OP_MUTE_MICROPHONE = 44;
        public static final int OP_TOAST_WINDOW = 45;
        public static final int OP_PROJECT_MEDIA = 46;
        public static final int OP_ACTIVATE_VPN = 47;
        public static final int OP_WRITE_WALLPAPER = 48;
        public static final int OP_ASSIST_STRUCTURE = 49;
        public static final int OP_ASSIST_SCREENSHOT = 50;
        public static final int OP_READ_PHONE_STATE = 51;
        public static final int OP_ADD_VOICEMAIL = 52;
        public static final int OP_USE_SIP = 53;
        public static final int OP_PROCESS_OUTGOING_CALLS = 54;
        public static final int OP_USE_FINGERPRINT = 55;
        public static final int OP_BODY_SENSORS = 56;
        public static final int OP_READ_CELL_BROADCASTS = 57;
        public static final int OP_MOCK_LOCATION = 58;
        public static final int OP_READ_EXTERNAL_STORAGE = 59;
        public static final int OP_WRITE_EXTERNAL_STORAGE = 60;
        public static final int OP_TURN_SCREEN_ON = 61;

    }




    private static HashMap<String, List<String>> autoStartPathMap = new HashMap<String, List<String>>() {
        {
            put("Xiaomi", Arrays.asList(
                    "com.miui.securitycenter/com.miui.permcenter.autostart.AutoStartManagementActivity",//MIUI10_9.8.1(9.0)
                    "com.miui.securitycenter"
            ));

            put("samsung", Arrays.asList(
                    "com.samsung.android.sm_cn/com.samsung.android.sm.ui.ram.AutoRunActivity",
                    "com.samsung.android.sm_cn/com.samsung.android.sm.ui.appmanagement.AppManagementActivity",
                    "com.samsung.android.sm_cn/com.samsung.android.sm.ui.cstyleboard.SmartManagerDashBoardActivity",
                    "com.samsung.android.sm_cn/.ui.ram.RamActivity",
                    "com.samsung.android.sm_cn/.app.dashboard.SmartManagerDashBoardActivity",

                    "com.samsung.android.sm/com.samsung.android.sm.ui.ram.AutoRunActivity",
                    "com.samsung.android.sm/com.samsung.android.sm.ui.appmanagement.AppManagementActivity",
                    "com.samsung.android.sm/com.samsung.android.sm.ui.cstyleboard.SmartManagerDashBoardActivity",
                    "com.samsung.android.sm/.ui.ram.RamActivity",
                    "com.samsung.android.sm/.app.dashboard.SmartManagerDashBoardActivity",

                    "com.samsung.android.lool/com.samsung.android.sm.ui.battery.BatteryActivity",
                    "com.samsung.android.sm_cn",
                    "com.samsung.android.sm"
            ));


            put("HUAWEI", Arrays.asList(
                    "com.huawei.systemmanager/.startupmgr.ui.StartupNormalAppListActivity",//EMUI9.1.0(方舟,9.0)
                    "com.huawei.systemmanager/.appcontrol.activity.StartupAppControlActivity",
                    "com.huawei.systemmanager/.optimize.process.ProtectActivity",
                    "com.huawei.systemmanager/.optimize.bootstart.BootStartActivity",
                    "com.huawei.systemmanager"
            ));

            put("vivo", Arrays.asList(
                    "com.iqoo.secure/.ui.phoneoptimize.BgStartUpManager",
                    "com.iqoo.secure/.safeguard.PurviewTabActivity",
                    "com.vivo.permissionmanager/.activity.BgStartUpManagerActivity",
//                    "com.iqoo.secure/.ui.phoneoptimize.AddWhiteListActivity", //这是白名单, 不是自启动
                    "com.iqoo.secure",
                    "com.vivo.permissionmanager"
            ));

            put("Meizu", Arrays.asList(
                    "com.meizu.safe/.permission.SmartBGActivity",//Flyme7.3.0(7.1.2)
                    "com.meizu.safe/.permission.PermissionMainActivity",//网上的
                    "com.meizu.safe"
            ));

            put("OPPO", Arrays.asList(
                    "com.coloros.safecenter/.startupapp.StartupAppListActivity",
                    "com.coloros.safecenter/.permission.startup.StartupAppListActivity",
                    "com.oppo.safe/.permission.startup.StartupAppListActivity",
                    "com.coloros.oppoguardelf/com.coloros.powermanager.fuelgaue.PowerUsageModelActivity",
                    "com.coloros.safecenter/com.coloros.privacypermissionsentry.PermissionTopActivity",
                    "com.coloros.safecenter",
                    "com.oppo.safe",
                    "com.coloros.oppoguardelf"
            ));

            put("oneplus", Arrays.asList(
                    "com.oneplus.security/.chainlaunch.view.ChainLaunchAppListActivity",
                    "com.oneplus.security"
            ));
            put("letv", Arrays.asList(
                    "com.letv.android.letvsafe/.AutobootManageActivity",
                    "com.letv.android.letvsafe/.BackgroundAppManageActivity",//应用保护
                    "com.letv.android.letvsafe"
            ));
            put("zte", Arrays.asList(
                    "com.zte.heartyservice/.autorun.AppAutoRunManager",
                    "com.zte.heartyservice"
            ));
            //金立
            put("F", Arrays.asList(
                    "com.gionee.softmanager/.MainActivity",
                    "com.gionee.softmanager"
            ));

            //以下为未确定(厂商名也不确定)
            put("smartisanos", Arrays.asList(
                    "com.smartisanos.security/.invokeHistory.InvokeHistoryActivity",
                    "com.smartisanos.security"
            ));
            //360
            put("360", Arrays.asList(
                    "com.yulong.android.coolsafe/.ui.activity.autorun.AutoRunListActivity",
                    "com.yulong.android.coolsafe"
            ));
            //360
            put("ulong", Arrays.asList(
                    "com.yulong.android.coolsafe/.ui.activity.autorun.AutoRunListActivity",
                    "com.yulong.android.coolsafe"
            ));
            //酷派
            put("coolpad"/*厂商名称不确定是否正确*/, Arrays.asList(
                    "com.yulong.android.security/com.yulong.android.seccenter.tabbarmain",
                    "com.yulong.android.security"
            ));
            //联想
            put("lenovo"/*厂商名称不确定是否正确*/, Arrays.asList(
                    "com.lenovo.security/.purebackground.PureBackgroundActivity",
                    "com.lenovo.security"
            ));
            put("htc"/*厂商名称不确定是否正确*/, Arrays.asList(
                    "com.htc.pitroad/.landingpage.activity.LandingPageActivity",
                    "com.htc.pitroad"
            ));
            //华硕
            put("asus"/*厂商名称不确定是否正确*/, Arrays.asList(
                    "com.asus.mobilemanager/.MainActivity",
                    "com.asus.mobilemanager"
            ));

        }
    };

    public static boolean startToAutoStartSetting(Context context) {
        Set<Map.Entry<String, List<String>>> entries = autoStartPathMap.entrySet();
        boolean has = false;
        for (Map.Entry<String, List<String>> entry : entries) {
            String manufacturer = entry.getKey();
            List<String> actCompatList = entry.getValue();
            if (Build.MANUFACTURER.equalsIgnoreCase(manufacturer)) {
                for (String act : actCompatList) {
                    try {
                        Intent intent;
                        if (act.contains("/")) {
                            intent = new Intent();
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            ComponentName componentName = ComponentName.unflattenFromString(act);
                            intent.setComponent(componentName);
                        } else {
                            //找不到? 网上的做法都是跳转到设置... 这基本上是没意义的 基本上自启动这个功能是第三方厂商自己写的安全管家类app
                            //所以我是直接跳转到对应的安全管家/安全中心
                            intent = context.getPackageManager().getLaunchIntentForPackage(act);
                        }
                        context.startActivity(intent);
                        has = true;
                        break;
                    } catch (Exception e) {
                        e.printStackTrace();
                        has = false;
                    }
                }
            }
        }
        return has;
    }

}
