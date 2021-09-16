package com.dz.blesdk.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import com.dz.blesdk.utils.BLESupport;

/**
 * 作者:东芝(2018/4/20).
 * 功能:扫描兼容api 18~20
 */

public class BluetoothLeScannerImplJB extends BluetoothLeScannerCompat {
    private ScanCallback callback;
    private boolean isScan;
    private int error_count;

    public BluetoothLeScannerImplJB() {

    }

    @Override
    public void stopScan(ScanCallback callback) {
        this.callback = callback;
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null || !adapter.isEnabled()) return;
        adapter.stopLeScan(mLeScanCallback);
        isScan =false;
    }

    @Override
    public void startScan(ScanCallback callback) {
        this.callback = callback;
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null || !adapter.isEnabled()) return;
        adapter.startLeScan(mLeScanCallback);
        isScan =true;
        //修复
        if (!BLESupport.isScanClientInitialize(mLeScanCallback)) {
            error_count++;
            isScan =false;
            if (callback != null) {
                callback.onScanFailed(ScanCallback.SCAN_FAILED_APPLICATION_REGISTRATION_FAILED);
                fixScanError();
            }
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
        System.out.println("修复扫描问题old....");
        BLESupport.releaseAllScanClient();

        if(error_count>=5) {
            error_count = 0;
        }else{
            isScan = false;
            if (callback != null) {
                callback.onScanFailed(ScanCallback.SCAN_FAILED_OUT_OF_COUNT);
            }
        }
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            if (callback != null) {
                isScan =true;
                callback.onScanResult(device, rssi, scanRecord);
            }
        }
    };
}
