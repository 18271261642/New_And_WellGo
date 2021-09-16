package com.dz.blesdk.interfaces;

import android.bluetooth.BluetoothGattCharacteristic;

/**
 * 作者:东芝(2017/11/18).
 * 功能:蓝牙读取 监听
 */

public interface ReadListener extends CommunicationListener {
    void onReadSuccessful(BluetoothGattCharacteristic characteristic);
}
