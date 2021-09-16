package com.dz.blesdk.interfaces;

import com.dz.blesdk.entity.BleDevice;

/**
 * 作者:东芝(2017/11/20).
 * 功能:
 */
public interface OnScanBleListener<T extends BleDevice> {
    void onScanStart();

    void onScanning(T scanResult);

    void onScanStop();
    void onScanTimeout();
}
