package com.truescend.gofit.pagers.main;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;

import androidx.appcompat.app.AlertDialog;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.widget.Toast;

import com.dz.blesdk.interfaces.BluetoothStatusListener;
import com.google.android.material.tabs.TabLayout;
import com.sn.app.utils.AppUserUtil;
import com.sn.blesdk.ble.SNBLEControl;
import com.sn.blesdk.ble.SNBLEHelper;
import com.sn.blesdk.ble.SNBLESDK;
import com.sn.blesdk.ble.SNBLEScanner;
import com.sn.utils.IF;
import com.sn.utils.SNToast;
import com.sn.utils.SystemUtil;
import com.truescend.gofit.BuildConfig;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BaseActivity;
import com.truescend.gofit.pagers.base.adapter.BaseFragmentStatePagerAdapter;
import com.truescend.gofit.pagers.base.dialog.BaseDialog;
import com.truescend.gofit.pagers.common.dialog.CommonDialog;
import com.truescend.gofit.pagers.common.dialog.ProgressDialog;
import com.truescend.gofit.service.MainService;
import com.truescend.gofit.service.reminder.SNAccessibilityService;
import com.truescend.gofit.service.sync.BleSyncService;
import com.truescend.gofit.utils.AppVersionUpdateHelper;
import com.truescend.gofit.utils.BandCallPhoneNotifyUtil;
import com.truescend.gofit.utils.Constant;
import com.truescend.gofit.utils.PageJumpUtil;
import com.truescend.gofit.utils.PermissionUtils;
import com.truescend.gofit.utils.StatusBarUtil;
import com.truescend.gofit.utils.TabLayoutManager;
import com.truescend.gofit.views.QuickViewPager;
import com.truescend.gofit.views.TitleLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


import butterknife.BindView;

/**
 * 功能：主页，统筹四大Fragment
 * Modify by 泽鑫 on 2018/03/03 10:29.
 * Compile by 东芝
 */
public class MainActivity extends BaseActivity<MainPresenterImpl, IMainContract.IMainView> implements IMainContract.IMainView, BluetoothStatusListener {

    @BindView(R.id.vpMainContainer)
    QuickViewPager vpMainContainer;
    @BindView(R.id.tlMainBottomTabs)
    TabLayout tlMainBottomTabs;

    public BaseFragmentStatePagerAdapter pagerAdapter;
    private BaseDialog alertBluetoothStatusDialog;
    private AlertDialog dialog;
    private ProgressDialog mAppUpdateDialog;


    @Override
    protected void onResume() {
        super.onResume();
        initPermission1();
        restartServiceIfNeed();

    }

    /**
     * 某些手机会杀掉下面的服务
     */
    private void restartServiceIfNeed() {
        if (!PermissionUtils.isServiceRunning(this, MainService.class)) {
            Intent intent = new Intent(this, MainService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent);
            } else {
                startService(intent);
            }
        }
        if (!PermissionUtils.isServiceRunning(this, BleSyncService.class)) {
            Intent intent = new Intent(this, BleSyncService.class);
            startService(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SNBLEControl.removeBluetoothStatusListener(this);
    }

    @Override
    protected MainPresenterImpl initPresenter() {
        return new MainPresenterImpl(this);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreateTitle(final TitleLayout titleLayout) {
        titleLayout.setTitleShow(false);
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
        initNavigationTab();
        getPresenter().requestDeviceDataNetworkSync();
        getPresenter().requestCheckVersion();
        initBLE();

        if (BuildConfig.DEBUG) {
            SNToast.toast("您在使用 " + BuildConfig.VERSION_NAME + " 版本", Toast.LENGTH_LONG);
            //PageJumpUtil.startRemoteCameraActivity(this);
        }

    }

    private void initPermission1() {
        List<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissions.add(Manifest.permission.CALL_PHONE);
        if (!SystemUtil.isMIUI12()) {
            permissions.add(Manifest.permission.READ_PHONE_STATE);
        }
        //9.0来电号码和挂电话需要正式申请权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            permissions.add(Manifest.permission.ANSWER_PHONE_CALLS);
        }
        //Google Play版本阉割功能
        if (!BuildConfig.isGooglePlayVersion) {
            permissions.add(Manifest.permission.READ_CONTACTS);
            permissions.add(Manifest.permission.RECEIVE_SMS);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                permissions.add(Manifest.permission.READ_CALL_LOG);
            }
        }
        PermissionUtils.requestPermissions(this, permissions, new PermissionUtils.OnPermissionGrantedListener() {
            @Override
            public void onGranted() {
                initPermission2();
            }

            @Override
            public void onDenied() {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionUtils.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }


    /**
     * 初始化推送服务监听
     */
    private void initPermission2() {
        //通知监听权限
        //TODO 这里如果重复判断权限==false 可能需要延迟0.5~1秒再判断, 因为系统数据库插入开关值是一个子线程操作, 回到该界面马上调用提供者 有可能获取到的还是之前的开关状态
        boolean hasNotificationPermission = PermissionUtils.hasNotificationListenPermission(this);
        boolean isAccessibilityServiceRunning = PermissionUtils.isServiceRunning(this, SNAccessibilityService.class);
        //TODO 如果没有通知权限,同时辅助服务没有运行  才提示需要授权,  否则 如果辅助服务在运行,通知服务没运行, 那就先用辅助服务顶替.
        //TODO 注意 这里判断的是两种通知监听服务,勿混淆,  逻辑是 通知服务无效则使用辅助服务,通知服务和辅助服务都有效,则优先使用通知服务作为消息推送主要数据来源
        if (!hasNotificationPermission && !isAccessibilityServiceRunning) {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            String permissionNames = getString(R.string.content_permission_notification);
            SpannableStringBuilder message = new SpannableStringBuilder(getString(R.string.content_authorized_to_use) + "\n" + permissionNames);
            message.setSpan(new ForegroundColorSpan(Color.RED), message.length() - permissionNames.length(), message.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            dialog = new AlertDialog.Builder(this)
                    .setCancelable(true)
                    .setTitle(R.string.content_authorized)
                    .setMessage(message)
                    .setNegativeButton(getString(R.string.content_cancel), null)
                    .setPositiveButton(getString(R.string.content_approve), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            PermissionUtils.startToNotificationListenSetting(MainActivity.this);
                        }
                    }).show();
            return;
        }
        //请求重新绑定 通知服务,防止未开启
        PermissionUtils.requestRebindNotificationListenerService(this);


        //位置权限,  蓝牙扫描用
        if (!PermissionUtils.hasLocationEnablePermission(this)) {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            String permissionNames = getString(R.string.content_permission_location);
            SpannableStringBuilder message = new SpannableStringBuilder(getString(R.string.content_authorized_to_use) + "\n" + permissionNames);
            message.setSpan(new ForegroundColorSpan(Color.RED), message.length() - permissionNames.length(), message.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            dialog = new AlertDialog.Builder(this)
                    .setCancelable(true)
                    .setTitle(R.string.content_authorized)
                    .setMessage(message)
                    .setNegativeButton(getString(R.string.content_cancel), null)
                    .setPositiveButton(getString(R.string.content_approve), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            PermissionUtils.startToLocationSetting(MainActivity.this);
                        }
                    }).show();
            return;
        }


        if (!PermissionUtils.hasNotificationEnablePermission(this)) {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            String permissionNames = getString(R.string.content_permission_notification_enable);
            SpannableStringBuilder message = new SpannableStringBuilder(getString(R.string.content_authorized_to_use) + "\n" + permissionNames);
            message.setSpan(new ForegroundColorSpan(Color.RED), message.length() - permissionNames.length(), message.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            dialog = new AlertDialog.Builder(this)
                    .setCancelable(true)
                    .setTitle(R.string.content_authorized)
                    .setMessage(message)
                    .setNegativeButton(getString(R.string.content_cancel), null)
                    .setPositiveButton(getString(R.string.content_approve), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            PermissionUtils.startToNotificationEnableSetting(MainActivity.this, null);
                        }
                    }).show();
            return;
        }
        //TODO 以下代码还是无法兼容 oppo, 可以说是 不可能兼容...
        //现象是这样的:
        //if(自启动权限已开+通知监听权限已开){
        //    if(用户手动杀掉){
        //      app下次打开后 无法启动通知监听 ,需要进入通知监听权限界面 关一下权限 再开一下权限 强制刷新系统才可以,
        //      还有别想了网上有 COMPONENT_ENABLED_STATE_DISABLED/ENABLE一下就可以了, 这个代码是没用的, 要是能重启服务还要这个用户手动授权界面干嘛...
        //    }else{
        //      正常
        //    }
        //}else if (通知监听权限已开&&自启动权限未开){
        //      没有自启动权限, 通知服务无法启动, 这个问题很多台手机上都有过
        //}
        //所以放弃适配了...
        //
//        if (PermissionUtils.isNotificationListenerHavePermissionButIsDead(this)) {
//            SNAsyncTask.execute(new SNVTaskCallBack(this) {
//
//                @Override
//                public void run() throws Throwable {
//                    sleep(3000);
//                }
//
//                @Override
//                public void done() {
//                    final BaseActivity act = getTarget();
//                    if (act != null && !act.isFinished()) {
//                        if (PermissionUtils.isNotificationListenerHavePermissionButIsDead(act)) {
//                            if (dialog != null && dialog.isShowing()) {
//                                dialog.dismiss();
//                            }
//                            String message = "检测到消息推送服务无法启动,应用[自启动]权限可能被关闭,将可能导致[手环无法接收消息推送]、[APP无法后台运行]等问题\n\n"  +
//                                  "解决步骤:手机[安全中心]或[**管家] > [自启动]或[后台优化]、[智能休眠] > 把["+ ResUtil.getString(R.string.app_name) +"] 选择允许[自启动]权限";
//
//                            dialog = new AlertDialog.Builder(act)
//                                    .setCancelable(true)
//                                    .setTitle(R.string.content_authorized)
//                                    .setMessage(message)
//                                    .setNegativeButton(getString(R.string.content_cancel), null)
//                                    .setPositiveButton(getString(R.string.content_try_to_fix), new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            if (PermissionUtils.startToAutoStartSetting(act)) {
//
//                                            }else{
//                                                //无法跳转
//                                            }
//                                        }
//                                    }).show();
//
//                        }
//                    }
//                }
//            });
//        }
    }

    private void initBLE() {
        if (Constant.isEmulator()) {
            return;
        }
        //是否兼容蓝牙BLE
        if (!SNBLESDK.isBluetoothSupportBLE()) {
            Toast.makeText(this, R.string.toast_un_support_ble, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        //是否已开启蓝牙,没开启就弹出开启蓝牙授权
        if (!SNBLESDK.isBluetoothEnable()) {

            Toast.makeText(this, R.string.toast_bluetooth_is_close, Toast.LENGTH_SHORT).show();
            SNBLEControl.setBluetoothOpen(this);
            SNBLEControl.addBluetoothStatusListener(this);
            return;
        }
        SNBLEControl.addBluetoothStatusListener(this);
        //取得这个用户的上次的设备MAC地址
        String mac = AppUserUtil.getUser().getMac();

        //如果上次是没有连接的痕迹,则认为首次使用 则弹出[扫描绑定设备]界面
        if (IF.isEmpty(mac)) {
            SNBLEHelper.disconnect();
            PageJumpUtil.startScanningAndBindActivity(this);
        } else {
            //如果有连接过的痕迹 同时没连接 则需要重连上次的设备
            if (!SNBLEHelper.isConnected() && !SNBLEHelper.isIsUserDisconnected() && SNBLEHelper.isAutoReConnect()) {
                //重连不用这边操心 全交给蓝牙框架处理 我们只需调用连接即可  如果此次连接失败 则自动启动重连
                //如果调用SNBLEHelper.disconnect() 则终止重连
                if (!SNBLEHelper.isConnecting()&& !SNBLEScanner.isScanning())
                {
                    SNBLEHelper.connect(mac);
                }

            }
        }

    }

    private void initNavigationTab() {
        String[] tabTitle = {
                getString(R.string.tab_home),
                getString(R.string.tab_track),
                getString(R.string.tab_device),
                getString(R.string.tab_user)
        };

        int[][] tabIcons = {
                {R.mipmap.icon_home_default, R.mipmap.icon_home_checked},
                {R.mipmap.icon_track_default, R.mipmap.icon_track_checked},
                {R.mipmap.icon_device_default, R.mipmap.icon_device_checked},
                {R.mipmap.icon_setting_default, R.mipmap.icon_setting_checked}
        };
        pagerAdapter = new BaseFragmentStatePagerAdapter(this, getSupportFragmentManager(), tabTitle, tabIcons);
        vpMainContainer.setAdapter(pagerAdapter);
        vpMainContainer.setOffscreenPageLimit(4);
//      vpMainContainer.setPageTransformer(true,new ZoomOutTranformer());
        TabLayoutManager.setupWithViewPager(tlMainBottomTabs, vpMainContainer);

        //状态栏 黑白主题 切换
        vpMainContainer.addOnPageChangeListener(new QuickViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        StatusBarUtil.setStatusBarDarkTheme(MainActivity.this, true);
                        break;
                    case 1:
                    case 2:
                    case 3:
                        StatusBarUtil.setStatusBarDarkTheme(MainActivity.this, false);
                        break;
                }
            }
        });
    }

    @Override
    public void onBluetoothStatusChange(int state) {
        if (isFinished()) {
            return;
        }
        switch (state) {
            case BluetoothAdapter.STATE_ON:
                if (alertBluetoothStatusDialog != null) {
                    alertBluetoothStatusDialog.dismiss();
                }
                break;
            case BluetoothAdapter.STATE_OFF:
                alertBluetoothStatusDialog = CommonDialog.create(this,
                        getString(R.string.content_bluetooth),
                        getString(R.string.title_open_close_bluetooth),
                        getString(R.string.content_exit),
                        getString(R.string.content_open),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                finish();
                            }
                        },
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                initBLE();//重新检测蓝牙状态 并在必要时弹出授权 或自动重连
                            }
                        }
                );
                alertBluetoothStatusDialog.show();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //监听蓝牙是否开启成功的状态
        SNBLEControl.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
        } catch (Exception e) {
            try {
                moveTaskToBack(true);
            } catch (Exception e1) {
                super.onBackPressed();
            }
        }
    }

    @Override
    public void onUpdateStatusChange(boolean isNeedUpdate, boolean isNecessary, String newVersion, String localVersion, String description) {

        if (isNeedUpdate) {
            CommonDialog.create(this,
                    getString(R.string.content_new_app_version),
                    description,
                    isNecessary ? null : getString(R.string.content_cancel),
                    getString(R.string.content_update),
                    isNecessary ? null : new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    },
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            getPresenter().requestStartUpdateApp();
                            mAppUpdateDialog = new ProgressDialog(MainActivity.this);
                            mAppUpdateDialog.setMax(100);
                            mAppUpdateDialog.setProgress(0);
                            mAppUpdateDialog.show();
                        }
                    }
            ).show();

        }
    }

    @Override
    public void onUpdateSuccessful(File file) {
        if (mAppUpdateDialog != null) {
            mAppUpdateDialog.dismiss();
            mAppUpdateDialog = null;
        }
        AppVersionUpdateHelper.startInstall(this, file);
    }

    @Override
    public void onUpdateProgress(int progress) {
        if (mAppUpdateDialog != null) {
            mAppUpdateDialog.setProgress(progress);
        }
    }

    @Override
    public void onDeviceFindThePhoneNow() {
        CommonDialog.createNoContent(this, getString(R.string.content_band_call_phone), null, getString(R.string.content_confirm),
                null,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        BandCallPhoneNotifyUtil.closeAlert();
                    }
                }
        ).show();
        PageJumpUtil.startMainActivity(this);
    }


//    /**
//     * 调试睡眠合并
//     * @param keyCode
//     * @param event
//     * @return
//     */
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if(!BuildConfig.isDev)return super.onKeyDown(keyCode, event);
//            //睡眠大数据:入睡:2018-05-10 00:43:00,醒来:2018-05-10 08:46:00
//        SleepDataDecodeHelper helper = new SleepDataDecodeHelper();
//        switch (keyCode) {
//
//            case KeyEvent.KEYCODE_VOLUME_UP:
//                //05-10 实际数据(7h45min)
//                helper.decode(SNBLEHelper.hexToBytes("0507040012050A002B12050A082E000000000000"));
//                helper.decode(SNBLEHelper.hexToBytes("050704010000000000000000000000000027400E"));
//                helper.decode(SNBLEHelper.hexToBytes("050704020030401900034012003F401E000D4010"));
//                helper.decode(SNBLEHelper.hexToBytes("050704030003400E800100B20000000000000000"));
//                helper.decode(SNBLEHelper.hexToBytes("0507FE0100000000000000000000000000000000"));
//                return true;
//            case KeyEvent.KEYCODE_VOLUME_DOWN:
//                //05-10 断电数据清空 (49min)
//                helper.decode(SNBLEHelper.hexToBytes("0507040012050A102E12050A1131000000000000"));
//                helper.decode(SNBLEHelper.hexToBytes("05070401000000000000001B400E000800000000"));
//                helper.decode(SNBLEHelper.hexToBytes("0507040200000000000000000000000000000000"));
//                helper.decode(SNBLEHelper.hexToBytes("0507040300000000000000000000000000000000"));
//                helper.decode(SNBLEHelper.hexToBytes("0507FE0100000000000000000000000000000000"));
//                return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }
//
//        @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if(!BuildConfig.isDev)return super.onKeyDown(keyCode, event);
//        switch (keyCode) {
//
//            case KeyEvent.KEYCODE_VOLUME_UP:
//                JumpToBizProfile.Req req = new JumpToBizProfile.Req();
//                req.toUserName = "gh_70226a6528ba"; // 公众号原始ID
//                req.profileType = JumpToBizProfile.JUMP_TO_HARD_WARE_BIZ_PROFILE;
////                req.extMsg = "";
//                req.profileType = JumpToBizProfile.JUMP_TO_NORMAL_BIZ_PROFILE; // 普通公众号
//                String APP_ID = "wx5cf03ed04b9338e2";
//                IWXAPI wxApi = WXAPIFactory.createWXAPI(this, APP_ID);
//                wxApi.registerApp(APP_ID);
//                wxApi.sendReq(req);
//                return true;
//            case KeyEvent.KEYCODE_VOLUME_DOWN:
//
//                return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }


}
