package com.dz.blesdk.interfaces;

/**
 * 作者:东芝(2017/11/21).
 * 功能:系统蓝牙状态监听
 */

public interface BluetoothStatusListener {
    /**
     *  BluetoothAdapter.STATE_TURNING_ON:蓝牙正在开
     *  BluetoothAdapter.STATE_ON: 蓝牙开
     *  BluetoothAdapter.STATE_TURNING_OFF:蓝牙正在关
     *  BluetoothAdapter.STATE_OFF: 蓝牙关
     * @param state
     */
    void onBluetoothStatusChange(int state);
}
