package com.dz.blesdk.entity;

import android.bluetooth.BluetoothProfile;

public class BleStatus {

    public static final int DEVICE_DISCONNECTED = BluetoothProfile.STATE_DISCONNECTED;
    public static final int DEVICE_CONNECTING = BluetoothProfile.STATE_CONNECTING;
    public static final int DEVICE_CONNECTED = BluetoothProfile.STATE_CONNECTED;

}
