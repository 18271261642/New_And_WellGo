package com.dz.bleota.phy.sdk.ble;

import android.bluetooth.BluetoothDevice;

/**
 * OTAUtilsCallBack
 *
 * @author:zhoululu
 * @date:2018/7/13
 */

public interface OTAUtilsCallBack {

    public void onDeviceSearch(BluetoothDevice device, int rssi, byte[] scanRecord);
    public void onConnectChange(boolean isConnected);
    public void onProcess(float process);
    public void onError(int code);
    public void onOTAFinish();

}
