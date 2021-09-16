package com.dz.blesdk.interfaces;

/**
 * 作者:东芝(2017/11/18).
 * 功能:蓝牙通知数据接收
 */

public interface NotifyReceiverListener extends CommunicationListener {
    void onReceive(byte[] buffer);
}
