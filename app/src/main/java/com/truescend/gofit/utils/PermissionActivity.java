//package com.truescend.gofit.utils;
//
//import android.Manifest;
//import android.app.Activity;
//import android.app.AppOpsManager;
//import android.content.ComponentName;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.graphics.Color;
//import android.net.Uri;
//import android.os.Binder;
//import android.os.Build;
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.v4.app.ActivityCompat;
//import android.support.v7.app.AlertDialog;
//import android.text.Spannable;
//import android.text.SpannableStringBuilder;
//import android.text.TextUtils;
//import android.text.style.ForegroundColorSpan;
//import android.view.View;
//import android.view.Window;
//import android.view.WindowManager;
//
//import com.truescend.gofit.BuildConfig;
//
//import java.lang.reflect.Method;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
///**
// * 作者:东芝(2018/3/14).
// * 功能:权限透明授权界面 但是有bug 暂时遗弃 请使用PermissionUtil
// */
//@Deprecated
//public class PermissionActivity extends Activity {
//    /**
//     * Build.MANUFACTURER
//     * 不管如何,它的值请全部大写
//     */
//    private static final String MANUFACTURER_HUAWEI = "HUAWEI";//华为
//    private static final String MANUFACTURER_MEIZU = "MEIZU";//魅族
//    private static final String MANUFACTURER_XIAOMI = "XIAOMI";//小米
//    private static final String MANUFACTURER_SONY = "SONY";//索尼
//    private static final String MANUFACTURER_OPPO = "OPPO";
//    private static final String MANUFACTURER_LG = "LG";
//    private static final String MANUFACTURER_VIVO = "VIVO";
//    private static final String MANUFACTURER_SAMSUNG = "SAMSUNG";//三星
//    private static final String MANUFACTURER_LETV = "LETV";//乐视
//    private static final String MANUFACTURER_ZTE = "ZTE";//中兴
//    private static final String MANUFACTURER_YULONG = "YULONG";//酷派
//    private static final String MANUFACTURER_LENOVO = "LENOVO";//联想
//
//    //拒绝列表
//    private static final List<String> mNativeDeniedList = new ArrayList<>();
//    //有原生权限权限,但第三方权限拒绝列表
//    private static final List<String> mAppOpsDeniedList = new ArrayList<>();
//    private static final List<String> mAllPermissionsList = new ArrayList<>();
//
//    private static final String KEY_INPUT_PERMISSIONS = "KEY_INPUT_PERMISSIONS";
//    private static OnPermissionGrantedListener listener;
//    private static final int REQUEST_CODE_REQUEST_NATIVE_PERMISSIONS = 0x1245;
//    private static boolean isReRequestIfNeed =false;
//
//    @Deprecated
//    public static void requestPermission(Activity context, String[] permissions, OnPermissionGrantedListener listener) {
//        PermissionActivity.listener = listener;
//        Intent intent = new Intent(context, PermissionActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.putExtra(KEY_INPUT_PERMISSIONS, permissions);
//        context.startActivity(intent);
//    }
//
//    private void invasionStatusBar(Activity activity) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = activity.getWindow();
//            View decorView = window.getDecorView();
//            decorView.setSystemUiVisibility(decorView.getSystemUiVisibility()
//                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);
//        }
//    }
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        invasionStatusBar(this);
//
//        Intent intent = getIntent();
//        String[] permissions = intent.getStringArrayExtra(KEY_INPUT_PERMISSIONS);
//        requestPermissions(permissions);
//    }
//
//    private void requestPermissions(String[] permissions) {
//        if (permissions != null && listener != null) {
//            mAllPermissionsList.clear();
//            mAllPermissionsList.addAll(Arrays.asList(permissions));
//            refreshNativeDeniedList(this, mAllPermissionsList);
//            //如果没有一个原生权限被拒绝 则再判断第三方权限是否被拒绝 (注意 判断第三方权限的时候 会自动弹出授权窗口)
//            if (mNativeDeniedList.isEmpty()) {
//                //(注意 判断第三方权限的时候 会自动弹出授权窗口)
//                refreshAppOpsDeniedList(this, mAllPermissionsList);
//
//                //第三方权限也没有任何权限被拒绝,这时候 原生和第三方都允许了所有权限,可以返回成功了
//                if (mAppOpsDeniedList.isEmpty()) {
//                    if (listener != null) {
//                        finish(PermissionActivity.this);
//                        listener.onGranted();
//                    }
//                } else {
//                    shouldShowRequestPermissionRationaleTips(this, mAppOpsDeniedList);
//                }
//            } else {
//                //仍然有原生权限被拒绝 ,重新请求授权
//                requestNativePermissions(this, mNativeDeniedList);
//            }
//        } else if (listener != null) {
//            finish(PermissionActivity.this);
//            listener.onDenied();
//        }
//    }
//
//
//    /**
//     * 刷新原生权限拒绝列表
//     *
//     * @param activity
//     * @param permissions
//     */
//    private static void refreshNativeDeniedList(Activity activity, List<String> permissions) {
//        mNativeDeniedList.clear();
//        for (String permission : permissions) {
//            boolean hasNativePermission = hasNativePermission(activity, permission);
//            //原生权限
//            if (!hasNativePermission) {
//                if (!mNativeDeniedList.contains(permission)) {
//                    mNativeDeniedList.add(permission);
//                }
//            }
//        }
//    }
//
//    /**
//     * 刷新第三方拒绝列表
//     *
//     * @param activity
//     * @param permissions
//     */
//    private static void refreshAppOpsDeniedList(Activity activity, List<String> permissions) {
//        mAppOpsDeniedList.clear();
//        //原生有权限 但是否也有第三方权限这不一定 得重新判断
//        for (String permission : permissions) {
//            boolean hasNativePermission = hasNativePermission(activity, permission);
//            boolean hasAppOpsPermission = hasAppOpsPermission(activity, permission);
//            //如果原生权限是有的 但第三方权限是没有的 这种是属于第三方私自做的权限管理
//            if (hasNativePermission && !hasAppOpsPermission) {
//                if (!mAppOpsDeniedList.contains(permission)) {
//                    mAppOpsDeniedList.add(permission);
//                }
//            }
//        }
//    }
//
//    private static void requestNativePermissions(final Activity activity, List<String> permissions) {
//        String[] list = new String[permissions.size()];
//        permissions.toArray(list);
//        ActivityCompat.requestPermissions(activity, list, REQUEST_CODE_REQUEST_NATIVE_PERMISSIONS);
//    }
//
//    /**
//     * 原生权限授权回调
//     *
//     * @param activity
//     * @param requestCode
//     * @param permissions
//     * @param grantResults
//     */
//    public static void onRequestPermissionsResult(final Activity activity, int requestCode, String[] permissions, int[] grantResults) {
//        if (requestCode == REQUEST_CODE_REQUEST_NATIVE_PERMISSIONS) {
//            for (int d = 0; d < permissions.length; d++) {
//                String permission = permissions[d];
//                //拒绝的权限 现在被允许了,移除掉
//                if (mNativeDeniedList.contains(permission) && grantResults[d] == PackageManager.PERMISSION_GRANTED) {
//                    mNativeDeniedList.remove(permission);
//                }
//            }
//            //如果没有一个原生权限被拒绝 则再判断第三方权限是否被拒绝 (注意 判断第三方权限的时候 会自动弹出授权窗口)
//            if (mNativeDeniedList.isEmpty()) {
//                //(注意 判断第三方权限的时候 会自动弹出授权窗口)
//                refreshAppOpsDeniedList(activity, mAllPermissionsList);
//
//                //第三方权限也没有任何权限被拒绝,这时候 原生和第三方都允许了所有权限,可以返回成功了
//                if (mAppOpsDeniedList.isEmpty()) {
//                    if (listener != null) {
//                        finish(activity);
//                        listener.onGranted();
//                    }
//                } else {
//
//                    shouldShowRequestPermissionRationaleTips(activity, mAppOpsDeniedList);
//                }
//            } else {
//                if (!mNativeDeniedList.isEmpty() || !mAppOpsDeniedList.isEmpty()) {
//                    List<String> all = new ArrayList<>();
//                    all.addAll(mNativeDeniedList);
//                    all.addAll(mAppOpsDeniedList);
//                    shouldShowRequestPermissionRationaleTips(activity, all);
//                }
//            }
//
//        }
//    }
//
//    private static void shouldShowRequestPermissionRationaleTips(final Activity activity, final List<String> permissions) {
//        String permissionNames = Arrays.toString(transformText(activity, permissions).toArray());
//        SpannableStringBuilder message = new SpannableStringBuilder("你拒绝了app必须的一些权限,应用将无法正常使用,请必须允许授权这些权限才能继续使用:\n" + permissionNames);
//        message.setSpan(new ForegroundColorSpan(Color.RED), message.length() - permissionNames.length(), message.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//        // 这些权限被用户总是拒绝。
//        new AlertDialog.Builder(activity)
//                .setCancelable(false)
//                .setTitle("授权")
//                .setMessage(message)
//                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        if (listener != null) {
//                            finish(activity);
//                            listener.onDenied();
//                        }
//                    }
//                })
//                .setPositiveButton("下一步", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        if (shouldShowRequestPermissionRationale(activity, permissions)) {
//                            if (mNativeDeniedList.isEmpty() && !mAppOpsDeniedList.isEmpty()) {
//                                //再次请求第三方权限
//                                refreshAppOpsDeniedList(activity, mAllPermissionsList);
//                                //如果用户仍然拒绝 则跳转到设置界面
//                                if (mAppOpsDeniedList.isEmpty()) {
//                                    isReRequestIfNeed = true;
//                                    startToSetting(activity);
//                                }
//                            } else {
//                                requestNativePermissions(activity, permissions);
//                            }
//                        } else {
//                            isReRequestIfNeed = true;
//                            // 原生权限被拒绝,跳转到设置
//                            startToSetting(activity);
//                        }
//                    }
//                }).show();
//
//    }
//
//    private static void finish(Activity activity) {
//        if (!activity.isFinishing() || !activity.isDestroyed()) {
//            activity.finish();
//        }
//    }
//
//    private static boolean shouldShowRequestPermissionRationale(Activity activity, List<String> permissions) {
//        for (String permission : permissions) {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//
//    public interface OnPermissionGrantedListener {
//        void onGranted();
//
//        void onDenied();
//    }
//
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode == REQUEST_CODE_REQUEST_NATIVE_PERMISSIONS) {
//            onRequestPermissionsResult(this, requestCode, permissions, grantResults);
//        }
//
//    }
//
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if(isReRequestIfNeed ) {
//            isReRequestIfNeed = false;
//            String[] list = new String[mAllPermissionsList.size()];
//            mAllPermissionsList.toArray(list);
//            requestPermissions(list);
//        }
//
//    }
//
//
//
//    @Override
//    public void onBackPressed() {
//        //拦截返回键
//    }
//
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        listener = null;
//        try {
//            mAppOpsDeniedList.clear();
//        } catch (Exception w) {
//            w.printStackTrace();
//        }
//        try {
//            mNativeDeniedList.clear();
//        } catch (Exception w) {
//            w.printStackTrace();
//        }
//        try {
//            mAllPermissionsList.clear();
//        } catch (Exception w) {
//            w.printStackTrace();
//        }
//    }
//
//    /**
//     * 此函数可以自己定义
//     *
//     * @param activity
//     */
//    private static void startToSetting(Activity activity) {
//
//        try {
//            switch (Build.MANUFACTURER.toUpperCase()) {
//                case MANUFACTURER_HUAWEI:
//                    startToHuaweiSetting(activity);
//                    break;
//                case MANUFACTURER_MEIZU:
//                    startToMeizuSetting(activity);
//                    break;
//                case MANUFACTURER_XIAOMI:
//                    startToXiaomiSetting(activity);
//                    break;
//                case MANUFACTURER_SONY:
//                    startToSonySetting(activity);
//                    break;
//                case MANUFACTURER_OPPO:
//                    startToOPPOSetting(activity);
//                    break;
//                case MANUFACTURER_LG:
//                    startToLGSetting(activity);
//                    break;
//                case MANUFACTURER_LETV:
//                    startToLetvSetting(activity);
//                    break;
//                default:
//                    startToDefaultAppInfoSetting(activity);
//                    break;
//            }
//        } catch (Throwable w) {
//            startToDefaultAppInfoSetting(activity);
//        }
//    }
//
//    private static void startToHuaweiSetting(Activity activity) {
//        Intent intent = new Intent();
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
//        ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity");
//        intent.setComponent(comp);
//        activity.startActivity(intent);
//    }
//
//    private static void startToMeizuSetting(Activity activity) {
//        Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
//        intent.addCategory(Intent.CATEGORY_DEFAULT);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
//        activity.startActivity(intent);
//    }
//
//    private static void startToXiaomiSetting(Activity activity) {
//        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.putExtra("extra_pkgname", BuildConfig.APPLICATION_ID);
//        activity.getApplicationContext().startActivity(intent);
//    }
//
//    private static void startToSonySetting(Activity activity) {
//        Intent intent = new Intent();
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
//        ComponentName comp = new ComponentName("com.sonymobile.cta", "com.sonymobile.cta.SomcCTAMainActivity");
//        intent.setComponent(comp);
//        activity.startActivity(intent);
//    }
//
//    private static void startToOPPOSetting(Activity activity) {
//        Intent intent = new Intent();
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
//        ComponentName comp = new ComponentName("com.color.safecenter", "com.color.safecenter.permission.PermissionManagerActivity");
//        intent.setComponent(comp);
//        activity.startActivity(intent);
//    }
//
//    private static void startToLGSetting(Activity activity) {
//        Intent intent = new Intent("android.intent.action.MAIN");
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
//        ComponentName comp = new ComponentName("com.android.settings", "com.android.settings.Settings$AccessLockSummaryActivity");
//        intent.setComponent(comp);
//        activity.startActivity(intent);
//    }
//
//    private static void startToLetvSetting(Activity activity) {
//        Intent intent = new Intent();
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
//        ComponentName comp = new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.PermissionAndApps");
//        intent.setComponent(comp);
//        activity.startActivity(intent);
//    }
//
//    /**
//     * 只能打开到自带安全软件
//     *
//     * @param activity
//     */
//    private static void _360(Activity activity) {
//        Intent intent = new Intent("android.intent.action.MAIN");
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
//        ComponentName comp = new ComponentName("com.qihoo360.mobilesafe", "com.qihoo360.mobilesafe.ui.index.AppEnterActivity");
//        intent.setComponent(comp);
//        activity.startActivity(intent);
//    }
//
//    /**
//     * 应用信息界面
//     *
//     * @param activity
//     */
//    private static void startToDefaultAppInfoSetting(Activity activity) {
//        Intent intent = new Intent();
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        if (Build.VERSION.SDK_INT >= 9) {
//            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
//            intent.setData(Uri.fromParts("package", activity.getPackageName(), null));
//        } else if (Build.VERSION.SDK_INT <= 8) {
//            intent.setAction(Intent.ACTION_VIEW);
//            intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
//            intent.putExtra("com.android.settings.ApplicationPkgName", activity.getPackageName());
//        }
//        activity.startActivity(intent);
//
//
//    }
//
//    /**
//     * 是否有原生权限
//     *
//     * @param context
//     * @param permission
//     * @return
//     */
//    public static boolean hasNativePermission(@NonNull Context context, @NonNull String permission) {
//        return ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
//    }
//
//    /**
//     * 是否有第三方权限
//     *
//     * @param context
//     * @param permission
//     * @return
//     */
//    public static boolean hasAppOpsPermission(@NonNull Context context, @NonNull String permission) {
//
//        int result = 0;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
//            String op = AppOpsManager.permissionToOp(permission);
//            if (TextUtils.isEmpty(op)) {
//                return true;
//            }
//
//            AppOpsManager appOpsManager = context.getSystemService(AppOpsManager.class);
//            if (appOpsManager != null) {
//                result = appOpsManager.noteProxyOp(op, context.getPackageName());
//            }
//            if (result != AppOpsManager.MODE_ALLOWED) {
//                return false;
//            }
//        }
//        if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT) {
//            try {
//                AppOpsManager app = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
//                Method startOpMethod = AppOpsManager.class.getDeclaredMethod("startOp", int.class, int.class, String.class);
//                startOpMethod.setAccessible(true);
//                result = (Integer) startOpMethod.invoke(app, getAppOpsValue(permission), Binder.getCallingUid(), context.getPackageName());
//                if (result != AppOpsManager.MODE_ALLOWED) {
//                    return false;
//                }
//            } catch (Exception w) {
//                w.printStackTrace();
//            }
//        }
//        return true;
//    }
//
//    public static int getAppOpsValue(String permission) {
//        switch (permission) {
//            case Manifest.permission.ACCESS_COARSE_LOCATION:
//                return AppOpsUtils.OP_COARSE_LOCATION;
////            case Manifest.permission.FINE_LOCATION:
////                return AppOpsUtils.OP_FINE_LOCATION;
////            case Manifest.permission.GPS:
////                return AppOpsUtils.OP_GPS;
//            case Manifest.permission.VIBRATE:
//                return AppOpsUtils.OP_VIBRATE;
//            case Manifest.permission.READ_CONTACTS:
//                return AppOpsUtils.OP_READ_CONTACTS;
//            case Manifest.permission.WRITE_CONTACTS:
//                return AppOpsUtils.OP_WRITE_CONTACTS;
//            case Manifest.permission.READ_CALL_LOG:
//                return AppOpsUtils.OP_READ_CALL_LOG;
//            case Manifest.permission.WRITE_CALL_LOG:
//                return AppOpsUtils.OP_WRITE_CALL_LOG;
//            case Manifest.permission.READ_CALENDAR:
//                return AppOpsUtils.OP_READ_CALENDAR;
//            case Manifest.permission.WRITE_CALENDAR:
//                return AppOpsUtils.OP_WRITE_CALENDAR;
////            case Manifest.permission.WIFI_SCAN:
////                return AppOpsUtils.OP_WIFI_SCAN;
////            case Manifest.permission.POST_NOTIFICATION:
////                return AppOpsUtils.OP_POST_NOTIFICATION;
////            case Manifest.permission.NEIGHBORING_CELLS:
////                return AppOpsUtils.OP_NEIGHBORING_CELLS;
//            case Manifest.permission.CALL_PHONE:
//                return AppOpsUtils.OP_CALL_PHONE;
//            case Manifest.permission.READ_SMS:
//                return AppOpsUtils.OP_READ_SMS;
////            case Manifest.permission.WRITE_SMS:
////                return AppOpsUtils.OP_WRITE_SMS;
//            case Manifest.permission.RECEIVE_SMS:
//                return AppOpsUtils.OP_RECEIVE_SMS;
////            case Manifest.permission.RECEIVE_EMERGECY_SMS:
////                return AppOpsUtils.OP_RECEIVE_EMERGECY_SMS;
//            case Manifest.permission.RECEIVE_MMS:
//                return AppOpsUtils.OP_RECEIVE_MMS;
//            case Manifest.permission.RECEIVE_WAP_PUSH:
//                return AppOpsUtils.OP_RECEIVE_WAP_PUSH;
//            case Manifest.permission.SEND_SMS:
//                return AppOpsUtils.OP_SEND_SMS;
////            case Manifest.permission.READ_ICC_SMS:
////                return AppOpsUtils.OP_READ_ICC_SMS;
////            case Manifest.permission.WRITE_ICC_SMS:
////                return AppOpsUtils.OP_WRITE_ICC_SMS;
//            case Manifest.permission.WRITE_SETTINGS:
//                return AppOpsUtils.OP_WRITE_SETTINGS;
//            case Manifest.permission.SYSTEM_ALERT_WINDOW:
//                return AppOpsUtils.OP_SYSTEM_ALERT_WINDOW;
////            case Manifest.permission.ACCESS_NOTIFICATIONS:
////                return AppOpsUtils.OP_ACCESS_NOTIFICATIONS;
//            case Manifest.permission.CAMERA:
//                return AppOpsUtils.OP_CAMERA;
//            case Manifest.permission.RECORD_AUDIO:
//                return AppOpsUtils.OP_RECORD_AUDIO;
////            case Manifest.permission.PLAY_AUDIO:
////                return AppOpsUtils.OP_PLAY_AUDIO;
////            case Manifest.permission.READ_CLIPBOARD:
////                return AppOpsUtils.OP_READ_CLIPBOARD;
////            case Manifest.permission.WRITE_CLIPBOARD:
////                return AppOpsUtils.OP_WRITE_CLIPBOARD;
////            case Manifest.permission.TAKE_MEDIA_BUTTONS:
////                return AppOpsUtils.OP_TAKE_MEDIA_BUTTONS;
////            case Manifest.permission.TAKE_AUDIO_FOCUS:
////                return AppOpsUtils.OP_TAKE_AUDIO_FOCUS;
////            case Manifest.permission.AUDIO_MASTER_VOLUME:
////                return AppOpsUtils.OP_AUDIO_MASTER_VOLUME;
////            case Manifest.permission.AUDIO_VOICE_VOLUME:
////                return AppOpsUtils.OP_AUDIO_VOICE_VOLUME;
////            case Manifest.permission.AUDIO_RING_VOLUME:
////                return AppOpsUtils.OP_AUDIO_RING_VOLUME;
////            case Manifest.permission.AUDIO_MEDIA_VOLUME:
////                return AppOpsUtils.OP_AUDIO_MEDIA_VOLUME;
////            case Manifest.permission.AUDIO_ALARM_VOLUME:
////                return AppOpsUtils.OP_AUDIO_ALARM_VOLUME;
////            case Manifest.permission.AUDIO_NOTIFICATION_VOLUME:
////                return AppOpsUtils.OP_AUDIO_NOTIFICATION_VOLUME;
////            case Manifest.permission.AUDIO_BLUETOOTH_VOLUME:
////                return AppOpsUtils.OP_AUDIO_BLUETOOTH_VOLUME;
//            case Manifest.permission.WAKE_LOCK:
//                return AppOpsUtils.OP_WAKE_LOCK;
////            case Manifest.permission.MONITOR_LOCATION:
////                return AppOpsUtils.OP_MONITOR_LOCATION;
////            case Manifest.permission.MONITOR_HIGH_POWER_LOCATION:
////                return AppOpsUtils.OP_MONITOR_HIGH_POWER_LOCATION;
////            case Manifest.permission.GET_USAGE_STATS:
////                return AppOpsUtils.OP_GET_USAGE_STATS;
////            case Manifest.permission.MUTE_MICROPHONE:
////                return AppOpsUtils.OP_MUTE_MICROPHONE;
////            case Manifest.permission.TOAST_WINDOW:
////                return AppOpsUtils.OP_TOAST_WINDOW;
////            case Manifest.permission.PROJECT_MEDIA:
////                return AppOpsUtils.OP_PROJECT_MEDIA;
////            case Manifest.permission.ACTIVATE_VPN:
////                return AppOpsUtils.OP_ACTIVATE_VPN;
////            case Manifest.permission.WRITE_WALLPAPER:
////                return AppOpsUtils.OP_WRITE_WALLPAPER;
////            case Manifest.permission.ASSIST_STRUCTURE:
////                return AppOpsUtils.OP_ASSIST_STRUCTURE;
////            case Manifest.permission.ASSIST_SCREENSHOT:
////                return AppOpsUtils.OP_ASSIST_SCREENSHOT;
//            case Manifest.permission.READ_PHONE_STATE:
//                return AppOpsUtils.OP_READ_PHONE_STATE;
//            case Manifest.permission.ADD_VOICEMAIL:
//                return AppOpsUtils.OP_ADD_VOICEMAIL;
//            case Manifest.permission.USE_SIP:
//                return AppOpsUtils.OP_USE_SIP;
//            case Manifest.permission.PROCESS_OUTGOING_CALLS:
//                return AppOpsUtils.OP_PROCESS_OUTGOING_CALLS;
//            case Manifest.permission.USE_FINGERPRINT:
//                return AppOpsUtils.OP_USE_FINGERPRINT;
//            case Manifest.permission.BODY_SENSORS:
//                return AppOpsUtils.OP_BODY_SENSORS;
////            case Manifest.permission.READ_CELL_BROADCASTS:
////                return AppOpsUtils.OP_READ_CELL_BROADCASTS;
////            case Manifest.permission.MOCK_LOCATION:
////                return AppOpsUtils.OP_MOCK_LOCATION;
//            case Manifest.permission.READ_EXTERNAL_STORAGE:
//                return AppOpsUtils.OP_READ_EXTERNAL_STORAGE;
//            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
//                return AppOpsUtils.OP_WRITE_EXTERNAL_STORAGE;
////            case Manifest.permission.TURN_SCREEN_ON:
////                return AppOpsUtils.OP_TURN_SCREEN_ON;
//        }
//        return AppOpsUtils.OP_NONE;
//    }
//
//
//    private class AppOpsUtils {
//        public static final int OP_NONE = -1;
//        public static final int OP_COARSE_LOCATION = 0;
//        public static final int OP_FINE_LOCATION = 1;
//        public static final int OP_GPS = 2;
//        public static final int OP_VIBRATE = 3;
//        public static final int OP_READ_CONTACTS = 4;
//        public static final int OP_WRITE_CONTACTS = 5;
//        public static final int OP_READ_CALL_LOG = 6;
//        public static final int OP_WRITE_CALL_LOG = 7;
//        public static final int OP_READ_CALENDAR = 8;
//        public static final int OP_WRITE_CALENDAR = 9;
//        public static final int OP_WIFI_SCAN = 10;
//        public static final int OP_POST_NOTIFICATION = 11;
//        public static final int OP_NEIGHBORING_CELLS = 12;
//        public static final int OP_CALL_PHONE = 13;
//        public static final int OP_READ_SMS = 14;
//        public static final int OP_WRITE_SMS = 15;
//        public static final int OP_RECEIVE_SMS = 16;
//        public static final int OP_RECEIVE_EMERGECY_SMS = 17;
//        public static final int OP_RECEIVE_MMS = 18;
//        public static final int OP_RECEIVE_WAP_PUSH = 19;
//        public static final int OP_SEND_SMS = 20;
//        public static final int OP_READ_ICC_SMS = 21;
//        public static final int OP_WRITE_ICC_SMS = 22;
//        public static final int OP_WRITE_SETTINGS = 23;
//        public static final int OP_SYSTEM_ALERT_WINDOW = 24;
//        public static final int OP_ACCESS_NOTIFICATIONS = 25;
//        public static final int OP_CAMERA = 26;
//        public static final int OP_RECORD_AUDIO = 27;
//        public static final int OP_PLAY_AUDIO = 28;
//        public static final int OP_READ_CLIPBOARD = 29;
//        public static final int OP_WRITE_CLIPBOARD = 30;
//        public static final int OP_TAKE_MEDIA_BUTTONS = 31;
//        public static final int OP_TAKE_AUDIO_FOCUS = 32;
//        public static final int OP_AUDIO_MASTER_VOLUME = 33;
//        public static final int OP_AUDIO_VOICE_VOLUME = 34;
//        public static final int OP_AUDIO_RING_VOLUME = 35;
//        public static final int OP_AUDIO_MEDIA_VOLUME = 36;
//        public static final int OP_AUDIO_ALARM_VOLUME = 37;
//        public static final int OP_AUDIO_NOTIFICATION_VOLUME = 38;
//        public static final int OP_AUDIO_BLUETOOTH_VOLUME = 39;
//        public static final int OP_WAKE_LOCK = 40;
//        public static final int OP_MONITOR_LOCATION = 41;
//        public static final int OP_MONITOR_HIGH_POWER_LOCATION = 42;
//        public static final int OP_GET_USAGE_STATS = 43;
//        public static final int OP_MUTE_MICROPHONE = 44;
//        public static final int OP_TOAST_WINDOW = 45;
//        public static final int OP_PROJECT_MEDIA = 46;
//        public static final int OP_ACTIVATE_VPN = 47;
//        public static final int OP_WRITE_WALLPAPER = 48;
//        public static final int OP_ASSIST_STRUCTURE = 49;
//        public static final int OP_ASSIST_SCREENSHOT = 50;
//        public static final int OP_READ_PHONE_STATE = 51;
//        public static final int OP_ADD_VOICEMAIL = 52;
//        public static final int OP_USE_SIP = 53;
//        public static final int OP_PROCESS_OUTGOING_CALLS = 54;
//        public static final int OP_USE_FINGERPRINT = 55;
//        public static final int OP_BODY_SENSORS = 56;
//        public static final int OP_READ_CELL_BROADCASTS = 57;
//        public static final int OP_MOCK_LOCATION = 58;
//        public static final int OP_READ_EXTERNAL_STORAGE = 59;
//        public static final int OP_WRITE_EXTERNAL_STORAGE = 60;
//        public static final int OP_TURN_SCREEN_ON = 61;
//
//    }
//
//}