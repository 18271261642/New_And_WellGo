package com.dz.blesdk.interfaces;

/**
 * 作者:东芝(2017/11/18).
 * 功能:蓝牙设备信号值
 */

public interface RssiListener extends CommunicationListener {
    void onRemoteRssi(int rssi);
}
