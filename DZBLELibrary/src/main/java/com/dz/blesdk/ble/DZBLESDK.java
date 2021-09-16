package com.dz.blesdk.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import java.lang.ref.WeakReference;

/**
 * 作者:东芝(2017/11/18).
 * 功能:蓝牙SDK基类
 */

public class DZBLESDK {
    private static BluetoothManager mBluetoothManager;
    private static WeakReference<Context> context;
    private static BluetoothAdapter mBluetoothAdapter;

    public static void init(Context context) {
        DZBLESDK.context = new WeakReference<Context>(context.getApplicationContext());
        mBluetoothManager = (BluetoothManager) context
                .getSystemService(Context.BLUETOOTH_SERVICE);
        if (mBluetoothManager != null ) {
            mBluetoothAdapter = mBluetoothManager.getAdapter();
        }

        DZBLEScanner.init(DZBLESDK.context.get());
        BleHelper.init(DZBLESDK.context.get());
        BleControl.init(DZBLESDK.context.get());
    }


    public static void close(Context context) {
        BleControl.close(context);
    }

    /**
     * 检查是否已经初始化
     *
     * @return
     */
    public static boolean isInitialized() {
        return DZBLESDK.context != null && mBluetoothManager != null;
    }

    /**
     * 是否在4.3以上
     *
     * @return
     */
    public static boolean isBluetoothSupportVersion() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2;
    }

    /**
     * 是否支持蓝牙BLE
     *
     * @return
     */
    public static boolean isBluetoothSupportBLE() {
        if (!isBluetoothSupportVersion() || mBluetoothAdapter == null || !context.get().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_BLUETOOTH_LE)) {
            return false;
        }
        return true;
    }

    /**
     * 蓝牙是否开启
     *
     * @return
     */
    public static boolean isBluetoothEnable() {
        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
            return true;
        }
        return false;
    }

    /**
     * 取得蓝牙适配器
     *
     * @return
     */
    public static BluetoothAdapter getBluetoothAdapter() {
        mBluetoothManager = getBluetoothManager();
        if (mBluetoothManager != null ) {
            mBluetoothAdapter = mBluetoothManager.getAdapter();
        }
        return mBluetoothAdapter==null?BluetoothAdapter.getDefaultAdapter():mBluetoothAdapter;
    }

    /**
     * 取得蓝牙管理器
     *
     * @return
     */
    public static BluetoothManager getBluetoothManager() {
        if(mBluetoothManager==null){
            if( DZBLESDK.context!=null) {
                Context context = DZBLESDK.context.get();
                if (context != null) {
                    mBluetoothManager = (BluetoothManager) context
                            .getSystemService(Context.BLUETOOTH_SERVICE);
                }
            }
        }
        return mBluetoothManager;
    }

    /**
     * 连接之前需要扫描的手机型号
     */
    public static boolean isDeviceNeedScan() {
        String manufacturer = android.os.Build.MANUFACTURER;
        boolean needScanBeforeConnect = false;
        // 目前小米和华为
        if (manufacturer.equalsIgnoreCase("xiaomi")
                || manufacturer.equalsIgnoreCase("huawei")) {
            needScanBeforeConnect = true;
        }
        return needScanBeforeConnect;
    }

}
