package com.dz.blesdk.ble;

import android.bluetooth.BluetoothDevice;
import android.os.Build;

/**
 * 作者:东芝(2018/4/20).
 * 功能:BLE扫描兼容
 */

public abstract class BluetoothLeScannerCompat {
    private static BluetoothLeScannerCompat mInstance;


    public static BluetoothLeScannerCompat getScanner() {
        if (mInstance != null)
            return mInstance;
        //新扫描API(21~26) 我发现 新api扫描方式虽好 有各种错误回调, 可是!有bug啊, 比如周围明明有某个设备,却扫描到别的设备 距离最近的设备居然有较大的几率扫描不到!!!! 有病啊
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return mInstance = new BluetoothLeScannerImplLollipop();
        }
        //旧扫描API(18~20)
        return mInstance = new BluetoothLeScannerImplJB();
    }

    public abstract void stopScan(final ScanCallback callback);
    public abstract void startScan(final ScanCallback callback);
    public abstract boolean isScan();




    public static abstract class ScanCallback {
        /**
         * Fails to start scan as BLE scan with the same settings is already started by the app.
         */
        public static final int SCAN_FAILED_ALREADY_STARTED = 1;

        /**
         * Fails to start scan as app cannot be registered.
         */
        public static final int SCAN_FAILED_APPLICATION_REGISTRATION_FAILED = 2;

        /**
         * Fails to start scan due an internal error
         */
        public static final int SCAN_FAILED_INTERNAL_ERROR = 3;

        /**
         * Fails to start power optimized scan as this feature is not supported.
         */
        public static final int SCAN_FAILED_FEATURE_UNSUPPORTED = 4;

        /**
         * Fails to start scan as it is out of hardware resources.
         */
        public static final int SCAN_FAILED_OUT_OF_HARDWARE_RESOURCES = 5;
        public static final int SCAN_FAILED_OUT_OF_COUNT = 101;


        public void onScanResult(BluetoothDevice device, int rssi, byte[] scanRecord) {
        }

        /**
         * Callback when scan could not be started.
         *
         * @param errorCode Error code (one of SCAN_FAILED_*) for scan failure.
         */
        public void onScanFailed(int errorCode) {
        }
    }

}
