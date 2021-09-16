package com.dz.blesdk.ble;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.Build;

import com.dz.blesdk.utils.BLESupport;

/**
 * 作者:东芝(2018/4/20).
 * 功能:扫描兼容api 21~26
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class BluetoothLeScannerImplLollipop extends BluetoothLeScannerCompat {
    private ScanCallback callback;
    private boolean isScan;
    private int error_count;

    @Override
    public void stopScan(ScanCallback callback) {
        this.callback = callback;
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null || !adapter.isEnabled()) return;
        BluetoothLeScanner scanner = adapter.getBluetoothLeScanner();
        if (scanner == null) return;
        scanner.stopScan(scanCallback);
        isScan = false;
    }

    @Override
    public void startScan(ScanCallback callback) {
        this.callback = callback;
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null || !adapter.isEnabled()) return;
        BluetoothLeScanner scanner = adapter.getBluetoothLeScanner();
        if (scanner == null) return;
        startScanCompat(scanner);
        isScan = true;
        if (!BLESupport.isScanClientInitialize(scanCallback)) {
            error_count++;
            isScan =false;
            fixScanError();
        }else{
            error_count = 0;
        }
    }

    @Override
    public boolean isScan() {
        return isScan;
    }

    private void fixScanError() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null || !adapter.isEnabled()) return;
        BluetoothLeScanner scanner = adapter.getBluetoothLeScanner();
        if (scanner == null) return;
        System.out.println("修复扫描问题new....");
        BLESupport.releaseAllScanClient();
        System.out.println("修复扫描问题完成....");

        if(error_count>=5) {
            error_count = 0;
        }else{
            stopScan(callback);
            isScan = false;
            if (callback != null) {
                callback.onScanFailed(ScanCallback.SCAN_FAILED_OUT_OF_COUNT);
            }
        }
    }

    private void startScanCompat(BluetoothLeScanner scanner) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ScanSettings settings = new ScanSettings.Builder()
                        .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
                try {
                    scanner.startScan(null, settings, scanCallback);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ScanSettings settings = new ScanSettings.Builder()
                        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
                scanner.startScan(null, settings, scanCallback);
            }
        } catch (IllegalStateException e) {
            //HTC Desire系列会出现 BT Adapter is not turned ON,无法处理
        }
    }

    private android.bluetooth.le.ScanCallback scanCallback = new android.bluetooth.le.ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            if (result != null) {
                isScan = true;
                ScanRecord scanRecord = result.getScanRecord();
                byte[] bytes = scanRecord == null ? new byte[]{} : scanRecord.getBytes();
                callback.onScanResult(result.getDevice(), result.getRssi(), bytes);
            }
        }

        @Override
        public void onScanFailed(final int errorCode) {
            isScan = false;
            if (callback != null) {
                callback.onScanFailed(errorCode);
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (errorCode == android.bluetooth.le.ScanCallback.SCAN_FAILED_APPLICATION_REGISTRATION_FAILED) {

                    } else if (errorCode == android.bluetooth.le.ScanCallback.SCAN_FAILED_ALREADY_STARTED) {
                        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                        if (adapter == null || !adapter.isEnabled()) return;
                        BluetoothLeScanner scanner = adapter.getBluetoothLeScanner();
                        if (scanner == null) return;
                        isScan =false;
                        //scanner.stopScan(scanCallback);
                        startScanCompat(scanner);
                    }
                }
            }).start();
        }
    };
}
