package com.dz.blesdk.entity;

import android.bluetooth.BluetoothDevice;
import androidx.annotation.NonNull;

import java.io.Serializable;

/**
 * 作者:东芝(2017/11/20).
 * 功能:蓝牙对象
 */

public   class BleDevice implements Serializable ,Comparable<BleDevice> {
    private static final long serialVersionUID = -5217710157640312976L;


    @Override
    public String toString() {
        return "BleDevice [mDeviceName=" + mDeviceName + ", mDeviceAddress="
                + mDeviceAddress + ", mRssi=" + mRssi + "]";
    }

    public BleDevice() {
    }

    public BleDevice(String mDeviceName, String mDeviceAddress, int mRssi,
                     byte[] scanRecord) {
        super();
        this.mDeviceName = mDeviceName;
        this.mDeviceAddress = mDeviceAddress;
        this.mRssi = mRssi;
        this.mScanRecord = scanRecord;
    }

    public String mDeviceName;
    public String mDeviceAddress;
    public int mRssi;
    public byte[] mScanRecord;
    public BluetoothDevice mDevice;
    public ParsedAd mParsedAd;

    @Override
    public int compareTo(@NonNull BleDevice o) {
        return o.mRssi-this.mRssi;
    }
}