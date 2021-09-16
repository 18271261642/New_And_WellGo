package com.dz.bleota.phy.sdk.ble;

import android.bluetooth.BluetoothDevice;

/**
 * OTACallBack
 *
 * @author:zhoululu
 * @date:2018/7/13
 */

public interface OTACallBack {

    //设备连接状态变化，true：连接成功，false：断开连接
    public void onConnected(boolean isConnected);
    //设置设备OTA状态。true：设备进入OTA状态，并连接成功。false：设备进入OTA状态，但未连接
    public void onOTA(boolean isConnected);
    //搜索设备
    public void onDeviceSearch(BluetoothDevice device, int rssi, byte[] scanRecord);
    //OTA进度%
    public void onProcess(float process);
    //发生错误
    public void onError(int code);
    //OTA文件发送完成
    public void onOTAFinish();
    //重启设备
    public void onReboot();

}
