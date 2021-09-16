package com.truescend.gofit.pagers.scan;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.dz.bleota.base.OTA;
import com.dz.bleota.nrf.nRF;
import com.dz.bleota.phy.Phy;
import com.dz.blesdk.interfaces.BluetoothStatusListener;
import com.dz.blesdk.interfaces.ConnectListener;
import com.dz.blesdk.interfaces.OnScanBleListener;
import com.dz.blesdk.utils.BLELog;
import com.sn.app.db.data.user.UserBean;
import com.sn.app.net.AppNetReq;
import com.sn.app.net.callback.OnResponseListener;
import com.sn.app.net.data.app.bean.HWVersionBean;
import com.sn.app.utils.AppUserUtil;
import com.sn.blesdk.ble.DeviceType;
import com.sn.blesdk.ble.SNBLEControl;
import com.sn.blesdk.ble.SNBLEHelper;
import com.sn.blesdk.ble.SNBLESDK;
import com.sn.blesdk.ble.SNBLEScanner;
import com.sn.blesdk.entity.SNBLEDevice;
import com.sn.blesdk.net.bean.DeviceInfo;
import com.sn.blesdk.storage.DeviceStorage;
import com.sn.utils.IF;
import com.sn.utils.SNToast;
import com.sn.utils.task.SNAsyncTask;
import com.sn.utils.task.SNVTaskCallBack;
import com.sn.utils.tuple.TupleFour;
import com.truescend.gofit.BuildConfig;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BaseActivity;
import com.truescend.gofit.pagers.base.dialog.BaseDialog;
import com.truescend.gofit.pagers.common.dialog.CommonDialog;
import com.truescend.gofit.pagers.common.dialog.LoadingDialog;
import com.truescend.gofit.pagers.scan.adapter.BLEDevicesAdapter;
import com.truescend.gofit.utils.PageJumpUtil;
import com.truescend.gofit.utils.PermissionUtils;
import com.truescend.gofit.utils.RecycleViewUtil;
import com.truescend.gofit.views.EmptyRecyclerView;
import com.truescend.gofit.views.TitleLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 功能：蓝牙扫描绑定界面
 * Author:Created by 泽鑫 on 2017/12/13 20:43.
 */

public class ScanningAndBindActivity extends BaseActivity<ScanningAndBindPresenterImpl, IScanningAndBindContract.IView> implements IScanningAndBindContract.IView, OnScanBleListener<SNBLEDevice>, AdapterView.OnItemClickListener, BluetoothStatusListener {
    private static final int VIEW_TIMEOUT_TEXT_TIPS = 1;
    private static final int VIEW_PROGRESS_BAR_TIPS = 0;
    @BindView(R.id.tlTitle)
    TitleLayout tlTitle;
    @BindView(R.id.rvScanningDevicesList)
    EmptyRecyclerView rvScanningDevicesList;
    @BindView(R.id.tvScanningDevices)
    TextView tvScanningDevices;
    @BindView(R.id.vsEmptyContent)
    ViewSwitcher vsEmptyContent;
    private String filter;
    private int mErrorRetryCount;

    private BLEDevicesAdapter bleDevicesAdapter;
    private List<SNBLEDevice> mDatas = new ArrayList<>();
    private boolean isRefreshing;
    private AlertDialog dialog;
    private String mSelectedDeviceAddress;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SNBLEScanner.stopScan();
        //恢复
        //重新开启自动重连
        //取消 [开发主动断开]  否则不会重连!
        SNBLEHelper.setIsUserDisconnected(false);
        //启用[自动重连]
        SNBLEHelper.setAutoReConnect(true);

        SNBLEControl.removeBluetoothStatusListener(this);
        SNBLEHelper.removeConnectListener(connectListener);
    }

    @Override
    protected ScanningAndBindPresenterImpl initPresenter() {
        return new ScanningAndBindPresenterImpl(this);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_scanning_and_bind;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {


        initTitle();
        initScanListView();
        PermissionUtils.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, new PermissionUtils.OnPermissionGrantedListener() {
            @Override
            public void onGranted() {

                initBLE();
            }

            @Override
            public void onDenied() {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtils.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    private void initScanListView() {
        bleDevicesAdapter = new BLEDevicesAdapter(this, mDatas);
        RecycleViewUtil.setAdapter(rvScanningDevicesList, bleDevicesAdapter);
        bleDevicesAdapter.setOnItemClickListener(this);
        rvScanningDevicesList.setEmptyView(vsEmptyContent);
    }

    @Override
    protected void onCreateTitle(TitleLayout titleLayout) {
        super.onCreateTitle(titleLayout);
        if(BuildConfig.DEBUG){
            titleLayout.addRightItem(
                    TitleLayout.ItemBuilder.Builder()
                            .setText("过滤")
                            .setTextViewTag("filter")
                            .setTextSize(16)
                            .setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    final EditText et = new EditText(ScanningAndBindActivity.this);
                                    et.setHint("请输入MAC/手环名/广播ID 任意一种");
                                    et.setText(filter);
                                    final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ScanningAndBindActivity.this);
                                    if(TextUtils.isEmpty(filter)) {
                                        et.setText(sp.getString("test_filter_input", ""));
                                    }
                                    new AlertDialog.Builder(ScanningAndBindActivity.this)
                                            .setCancelable(true)
                                            .setTitle("过滤手环(正式版无此功能)")
                                            .setView(et)
                                            .setNegativeButton("取消", null)
                                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    filter = et.getText().toString();
                                                    Toast.makeText(getApplicationContext(),"优先过滤:"+ filter,Toast.LENGTH_LONG).show();
                                                    tlTitle.setTextWithTag("filter", "优先过滤:"+filter);
                                                    sp.edit().putString("test_filter_input",filter).apply();
                                                    if (checkBLEPermission()) return;

                                                    if (SNBLEScanner.isScanning()) {
                                                        SNBLEScanner.stopScan();
                                                    }
                                                    vsEmptyContent.setDisplayedChild(VIEW_PROGRESS_BAR_TIPS);
                                                    SNBLEScanner.startScan(ScanningAndBindActivity.this);
                                                }
                                            }).show();
                                }
                            }));
        }
    }

    private void initTitle() {
        tlTitle.setTitle(getString(R.string.title_scan_band));
    }

    @Override
    public void onBluetoothStatusChange(int state) {
        switch (state) {
            case BluetoothAdapter.STATE_ON:
                SNBLEScanner.startScan(this);
                break;
            case BluetoothAdapter.STATE_OFF:
                initBLE();//重新检测
                break;
        }
    }

    private void initBLE() {
        if (checkBLEPermission()) return;
        //禁用自动重连
        SNBLEHelper.setAutoReConnect(false);
        SNBLEScanner.startScan(this);
    }

    private boolean checkBLEPermission() {
        if (!SNBLESDK.isBluetoothSupportBLE()) {
            Toast.makeText(this, R.string.toast_un_support_ble, Toast.LENGTH_SHORT).show();
            finish();
            return true;
        }
        if (!SNBLESDK.isBluetoothEnable()) {
            Toast.makeText(this, R.string.toast_bluetooth_is_close, Toast.LENGTH_SHORT).show();
            SNBLEControl.setBluetoothOpen(this);
            SNBLEControl.addBluetoothStatusListener(this);
            return true;
        }
        if (!PermissionUtils.hasLocationEnablePermission(ScanningAndBindActivity.this)) {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            String permissionNames = getString(R.string.content_permission_location);
            SpannableStringBuilder message = new SpannableStringBuilder(getString(R.string.content_authorized_to_use) + "\n" + permissionNames);
            message.setSpan(new ForegroundColorSpan(Color.RED), message.length() - permissionNames.length(), message.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            dialog = new AlertDialog.Builder(ScanningAndBindActivity.this)
                    .setCancelable(true)
                    .setTitle(R.string.content_authorized)
                    .setMessage(message)
                    .setNegativeButton(getString(R.string.content_cancel), null)
                    .setPositiveButton(getString(R.string.content_approve), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            PermissionUtils.startToLocationSetting(ScanningAndBindActivity.this);
                        }
                    }).show();
            return false;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //救砖成功! 返回
        if (requestCode == PageJumpUtil.REQUEST_CODE_RESULT && resultCode == Activity.RESULT_OK) {
            finish();
        }
        SNBLEControl.onActivityResult(requestCode, resultCode, data);

    }


    @OnClick({R.id.tvScanningDevices, R.id.llScanningTimeout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.llScanningTimeout:
            case R.id.tvScanningDevices:

                if (checkBLEPermission()) return;

                if (SNBLEScanner.isScanning()) {
                    return;
                }
                vsEmptyContent.setDisplayedChild(VIEW_PROGRESS_BAR_TIPS);
                SNBLEScanner.startScan(this);
                break;
        }
    }

    @Override
    public void onScanStart() {
        if (isFinished()) {
            return;
        }
        mDatas.clear();
        bleDevicesAdapter.notifyDataSetChanged();
        tvScanningDevices.setText(R.string.content_scanning);

    }

    @Override
    public void onScanning(final SNBLEDevice scanResult) {

        if (isFinished()) {
            return;
        }
        if (!isRefreshing) {
            isRefreshing = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (scanResult != null) {
                        int size = mDatas.size();
                        try {
                            Collections.sort(mDatas);
                        } catch (Exception ignored) {
                        }


                        if(BuildConfig.DEBUG&&filter!=null&&scanResult.mParsedAd!=null) {

                            boolean isFind = false;
                            try {
                                DeviceInfo deviceInfo = DeviceType.getDeviceInfo(scanResult.mParsedAd.manufacturers);
                                if(deviceInfo!=null) {
                                    if (scanResult.mDeviceName.toLowerCase().contains(filter.toLowerCase()) ||
                                            scanResult.mDeviceAddress.toLowerCase().contains(filter.toLowerCase()) ||
                                            String.valueOf(deviceInfo.getAdv_id()).toLowerCase().equals(filter.toLowerCase()) ||
                                            String.format("%04X",deviceInfo.getAdv_id()).toLowerCase().equals(filter.toLowerCase())
                                    ) {
                                        scanResult.mRssi = 0;
                                        mDatas.add(0,scanResult);
                                        isFind = true;
                                    }
                                }
                            } catch (Exception ignored) {
                            }
                            //最多只显示10个
                            if (size >= 15) {
                                if (!DeviceType.isDFUModel(mDatas.get(size - 1).mDeviceName)) {
                                    mDatas.remove(size - 1);
                                }
                            }
                            if(!isFind) {
                                mDatas.add(scanResult);
                            }
                        }else{
                            //最多只显示10个
                            if (size >= 15) {
                                if (!DeviceType.isDFUModel(mDatas.get(size - 1).mDeviceName)) {
                                    mDatas.remove(size - 1);
                                }
                            }
                            mDatas.add(scanResult);
                        }

                        try {
                            Collections.sort(mDatas);
                        } catch (Exception ignored) {
                        }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!isFinished()) {
                                if (bleDevicesAdapter != null) {
                                    bleDevicesAdapter.notifyDataSetChanged();
                                }
                            }
                            isRefreshing = false;
                        }
                    });
                }
            }).start();
        }
    }

    @Override
    public void onScanStop() {
        if (isFinished()) {
            return;
        }
        tvScanningDevices.setText(R.string.content_scanning_start);
    }

    @Override
    public void onScanTimeout() {
        if (isFinished()) {
            return;
        }
        vsEmptyContent.setDisplayedChild(VIEW_TIMEOUT_TEXT_TIPS);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (!SNBLESDK.isBluetoothEnable()) {
            SNBLEControl.setBluetoothOpen(this);
            return;
        }
        final SNBLEDevice device = bleDevicesAdapter.getItem(position);

        //记住mac地址
        final UserBean user = AppUserUtil.getUser();
        if (user == null) return;

        //记录mac地址 等下方便出问题时自动重新尝试连接
        mSelectedDeviceAddress = device.mDeviceAddress;

        //如果需要救砖
        if (DeviceType.isDFUModel(device.mDeviceName)) {

            final String otaMac = DeviceType.getOTAAdvMac(device.mScanRecord);

            if (TextUtils.isEmpty(otaMac)) {
                //旧固件的手环的处理
                String mac = user.getMac();
                String adv_id = user.getAdv_id();
                int upgradeid = -1;
                DeviceInfo saveBandDeviceInfo = null;
                if (IF.isEmpty(mac)) {
                    TupleFour<String, String, Integer, DeviceInfo> deviceInfoTupleFour = DeviceStorage.findDeviceInfoConnectHistory(device.mDeviceAddress);
                    if (deviceInfoTupleFour == null) {
                        deviceInfoTupleFour = DeviceStorage.findDeviceInfoConnectHistory(OTA.convertDfuToNormalMacAddress(device.mDeviceAddress));
                    }
                    //找到了 就补充缺失的信息
                    if (deviceInfoTupleFour != null) {
                        mac = deviceInfoTupleFour.getV2();
                        upgradeid = deviceInfoTupleFour.getV3();
                        adv_id = String.valueOf(deviceInfoTupleFour.getV4().getAdv_id());
                        saveBandDeviceInfo = deviceInfoTupleFour.getV4();
                    }
                }
                if (!IF.isEmpty(mac)) {
                    boolean is_nRF_DFU = device.mDeviceAddress.equalsIgnoreCase(nRF.convertToDFUMacAddress(mac));
                    boolean is_OTA = device.mDeviceAddress.equalsIgnoreCase(mac);
                    if (is_OTA || is_nRF_DFU) {

                        if (!IF.isEmpty(adv_id)) {
                            int adv_id_int = Integer.parseInt(adv_id);
                            //若广播id被清除了或没记录, 则拿本地上次的记录
                            if (adv_id_int == 0 || adv_id_int == 1) {
                                boolean isOtherDevice = mac.equalsIgnoreCase(DeviceType.getDeviceMac());
                                boolean isnRFDevice = mac.equalsIgnoreCase(nRF.convertToDFUMacAddress(DeviceType.getDeviceMac()));
                                boolean isPhyDevice = mac.equalsIgnoreCase(Phy.convertToDFUMacAddress(DeviceType.getDeviceMac()));
                                if (isOtherDevice || isnRFDevice || isPhyDevice) {
                                    adv_id_int = DeviceType.getDeviceAdvId();
                                    //若本地上次的mac记录恰好是这次的mac nrf递增后的mac 说明这个mac被递增过,需要等于旧mac,非nrf则不用处理
                                    if (isnRFDevice || isPhyDevice) {
                                        mac = DeviceType.getDeviceMac();
                                    }
                                }
                            }
                            DeviceInfo deviceInfo;
                            if (saveBandDeviceInfo == null) {
                                deviceInfo = DeviceType.getDeviceInfo(adv_id_int);
                            } else {
                                deviceInfo = saveBandDeviceInfo;
                            }

                            //如果已连接 则需要断开一下
                            if (SNBLEHelper.isConnected()) {
                                SNBLEHelper.disconnect();
                            }
                            //开始连接前,需要停止扫描,否则可能在连接设备时刚好刷新蓝牙设备缓存 导致无缓存 无法连接上
                            SNBLEScanner.stopScan();
                            //禁止自动重连,责任交给这里的逻辑来处理
                            SNBLEHelper.setAutoReConnect(false);
                            //因为扫描到了,释放蓝牙标志肯定是false,这样不用重新扫描 直接可以连接 更快 更省
                            SNBLEHelper.setIsBluetoothRecycled(false);

                            PageJumpUtil.startBandUpdateFixBandActivityForResult(this, deviceInfo, mac, upgradeid);
                            return;
                        }
                    }
                }

                CommonDialog.create(ScanningAndBindActivity.this,
                        getString(R.string.content_fix_band_failed),
                        getString(R.string.content_fix_band_failed_tips),
                        getString(R.string.content_cancel), getString(R.string.content_feedback),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        },
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                PageJumpUtil.startFeedbackActivity(ScanningAndBindActivity.this);
                            }
                        }
                ).show();
            } else {
                //新固件的手环的处理
                LoadingDialog.show(this, R.string.content_loading);
                AppNetReq.getApi().checkHWVersionFixFromMac(otaMac,otaMac).enqueue(new OnResponseListener<HWVersionBean>() {
                    @Override
                    public void onResponse(HWVersionBean body) throws Throwable {

                        HWVersionBean.DataBean data = body.getData();
                        //System.out.println("otajson=" + data.getDownload_url());
                        String device_name = data.getDevice_name();
                        int adv_id = data.getAdv_id();
                        int upgradeid = data.getUpgradeid();
                        int customid = data.getCustomid();
                        String mac = otaMac;
                        if (!IF.isEmpty(mac)) {
                            boolean is_nRF_DFU = device.mDeviceAddress.equalsIgnoreCase(nRF.convertToDFUMacAddress(mac));//地址加1的ota
                            boolean is_OTA = device.mDeviceAddress.equalsIgnoreCase(mac);//普通ota
                            if (is_OTA || is_nRF_DFU) {

                                if (adv_id < 1) {
                                    //若广播id被清除了或没记录, 则拿本地上次的记录
                                    boolean isOtherDevice = mac.equalsIgnoreCase(DeviceType.getDeviceMac());
                                    boolean isnRFDevice = mac.equalsIgnoreCase(nRF.convertToDFUMacAddress(DeviceType.getDeviceMac()));
                                    boolean isPhyDevice = mac.equalsIgnoreCase(Phy.convertToDFUMacAddress(DeviceType.getDeviceMac()));
                                    if (isOtherDevice || isnRFDevice || isPhyDevice) {
                                        //若本地上次的mac记录恰好是这次的mac nrf递增后的mac 说明这个mac被递增过,需要等于旧mac,非nrf则不用处理
                                        if (isnRFDevice || isPhyDevice) {
                                            mac = DeviceType.getDeviceMac();
                                        }
                                    }
                                }
                                DeviceInfo deviceInfo = DeviceType.getDeviceInfo(adv_id);
                                //如果已连接 则需要断开一下
                                if (SNBLEHelper.isConnected()) {
                                    SNBLEHelper.disconnect();
                                }
                                //开始连接前,需要停止扫描,否则可能在连接设备时刚好刷新蓝牙设备缓存 导致无缓存 无法连接上
                                SNBLEScanner.stopScan();
                                //禁止自动重连,责任交给这里的逻辑来处理
                                SNBLEHelper.setAutoReConnect(false);
                                //因为扫描到了,释放蓝牙标志肯定是false,这样不用重新扫描 直接可以连接 更快 更省
                                SNBLEHelper.setIsBluetoothRecycled(false);


                                //更新改变后的用户数据
//                                user.setMac(device.mDeviceAddress);
//                                AppUserUtil.setUser(user);
//                                DeviceStorage.setDeviceMac(mac);
                                DeviceStorage.setDeviceName(device_name);
                                DeviceStorage.setDeviceAdvId(adv_id);
                                DeviceStorage.setDeviceCustomerId(customid);

                                PageJumpUtil.startBandUpdateFixBandActivityForResult(ScanningAndBindActivity.this, deviceInfo, mac, upgradeid);
                            }
                        }
                        LoadingDialog.dismiss();

                    }

                    @Override
                    public void onFailure(int ret, String msg) {
                        LoadingDialog.dismiss();
                        CommonDialog.create(ScanningAndBindActivity.this,
                                getString(R.string.content_fix_band_failed),
                                getString(R.string.content_fix_band_failed_tips) + "\nError Message:" + msg,
                                getString(R.string.content_cancel), getString(R.string.content_feedback),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                    }
                                },
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        PageJumpUtil.startFeedbackActivity(ScanningAndBindActivity.this);
                                    }
                                }
                        ).show();
                    }
                });
            }
            return;
        }
        user.setMac(device.mDeviceAddress);
        //更新改变后的用户数据
        AppUserUtil.setUser(user);

        LoadingDialog.show(this, R.string.content_binding);

        mErrorRetryCount = 0;
        DeviceStorage.setDeviceMac(device.mDeviceAddress);
        DeviceStorage.setDeviceName(device.mDeviceName);
        DeviceInfo deviceInfo = DeviceType.getDeviceInfo(device.mParsedAd.manufacturers);

        //能以设备为准 就以设备为准, 如果广播值只有一个, 那就直接存储, 如果有多个 那没法判断哪个是广播值了(有些芯片有多个值的)
        if (device.mParsedAd != null && device.mParsedAd.manufacturers != null && device.mParsedAd.manufacturers.size() == 1) {
            int deviceAdvId = device.mParsedAd.manufacturers.get(0);
            DeviceStorage.setDeviceAdvId(deviceAdvId);
        } else if (deviceInfo != null) {
            DeviceStorage.setDeviceAdvId(deviceInfo.getAdv_id());
        }
        if (deviceInfo != null) {
            DeviceStorage.setDeviceCustomerId(deviceInfo.getCustomid());

            AppUserUtil.uploadUserDevice(device.mDeviceAddress, deviceInfo.getDevice_name(), deviceInfo.getFunction(), DeviceStorage.getDeviceAdvId(), new AppUserUtil.OnOperationListener() {
                @Override
                public void success() {
                    startConnect();
                }

                @Override
                public void failed(String msg) {
                    SNToast.toast(msg);
                    LoadingDialog.dismiss();
                }
            });
            return;
        }
        startConnect();
    }

    private void startConnect() {
        //如果已连接 则需要断开一下
        if (SNBLEHelper.isConnected()) {
            SNBLEHelper.disconnect();
        }
        //开始连接前,需要停止扫描,否则可能在连接设备时刚好刷新蓝牙设备缓存 导致无缓存 无法连接上
        if (/*isnRF && */Build.VERSION.SDK_INT >= 29/*安卓10*/ && "HUAWEI".equals(Build.MANUFACTURER))
        {
            //TODO 华为可能无法连接的问题
        }else{
            SNBLEScanner.stopScan();
        }
        //开始连接
        SNBLEHelper.connect(mSelectedDeviceAddress,true);
        //禁止自动重连,责任交给这里的逻辑来处理
        SNBLEHelper.setAutoReConnect(false);
        //因为扫描到了,释放蓝牙标志肯定是false,这样不用重新扫描 直接可以连接 更快 更省
        SNBLEHelper.setIsBluetoothRecycled(false);
        SNBLEHelper.addConnectListener(connectListener);
    }

    private BaseDialog mDialogFixBle;
    private ConnectListener connectListener = new ConnectListener() {
        @Override
        public void onConnected() {
            if (isFinished()) {
                return;
            }
            LoadingDialog.dismiss();
            finish();//回到原来的界面
        }

        @Override
        public void onNotifyEnable() {

        }

        @Override
        public void onDisconnected() {

        }

        @Override
        public void onFailed(int errorType) {
            if (isFinished()) {
                return;
            }
            if (mSelectedDeviceAddress == null) {
                return;
            }
            mErrorRetryCount++;

            BLELog.d("mErrorRetryCount=" + mErrorRetryCount);

            //错误次数超过5次以上 则提示修复
            if (mErrorRetryCount >= 5) {
                SNBLEHelper.disconnect();
                mErrorRetryCount = 0;
                LoadingDialog.dismiss();
                showFixBluetooth();
            } else
            {

                //开始连接
                SNBLEHelper.connect(mSelectedDeviceAddress);
                //仍然禁止自动重连,责任交给这里的逻辑来处理
                SNBLEHelper.setAutoReConnect(false);

            }
        }


        private void showFixBluetooth() {
            if (mDialogFixBle == null) {
                mDialogFixBle = CommonDialog.create(ScanningAndBindActivity.this,
                        getString(R.string.content_connect_failed),
                        getString(R.string.content_connect_failed_fix_tips),
                        getString(R.string.content_cancel), getString(R.string.content_try_to_fix),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        },
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                SNAsyncTask.execute(new SNVTaskCallBack(ScanningAndBindActivity.this) {

                                    @Override
                                    public void prepare() {
                                        BaseActivity target = getTarget();
                                        if (target == null || target.isFinished()) {
                                            return;
                                        }
                                        LoadingDialog.show(target, R.string.loading);
                                    }

                                    @Override
                                    public void run() throws Throwable {
                                        //修复逻辑很简单,就是关闭再打开蓝牙
                                        SNBLEControl.setBluetoothOpen(ScanningAndBindActivity.this, false);
                                        sleep(5000);
                                        SNBLEControl.setBluetoothOpen(ScanningAndBindActivity.this, true);
                                        sleep(1000);
                                    }

                                    @Override
                                    public void done() {
                                        BaseActivity target = getTarget();
                                        if (target == null || target.isFinished()) {
                                            return;
                                        }
                                        LoadingDialog.dismiss();
                                        tvScanningDevices.performClick();
                                    }
                                });


                            }
                        }
                );
            }
            if (mDialogFixBle != null && !mDialogFixBle.isShowing()) {
                mDialogFixBle.show();
            }
        }

    };


    int up = 0, down = 0;
    boolean downOK, upOK;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            down++;
            if (down == 3) {
                downOK = true;
            } else if (down == 4 && upOK) {
                Toast.makeText(this, "彩蛋", Toast.LENGTH_SHORT).show();
                bleDevicesAdapter.setDev(true);
            } else {
                downOK = false;
            }
        }
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            if (downOK) {
                up++;
                if (up == 2) {
                    upOK = true;
                } else {
                    upOK = false;
                }
            }

        }
        return super.onKeyDown(keyCode, event);
    }
}