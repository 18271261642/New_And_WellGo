/*
 *******************************************************************************
 *
 * Copyright (C) 2016 Dialog Semiconductor, unpublished work. This computer
 * program includes Confidential, Proprietary Information and is a Trade
 * Secret of Dialog Semiconductor. All use, disclosure, and/or reproduction
 * is prohibited unless authorized in writing. All Rights Reserved.
 *
 * bluetooth.support@diasemi.com
 *
 *******************************************************************************
 */

package com.dz.bleota.dialog.bluetooth;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

public class BluetoothGattSingleton {
    private static BluetoothGatt gatt = null;
    private static BluetoothGattCharacteristic spotaMemInfoCharacteristic = null;

    public static BluetoothGatt getGatt() {
        return gatt;
    }

    public static void setGatt(BluetoothGatt newGatt) {
        // Ensure the previous object is closed
        if (gatt != null && newGatt != gatt) {
            gatt.disconnect();
            gatt.close();
        }
        gatt = newGatt;
    }

    public static BluetoothGattCharacteristic getSpotaMemInfoCharacteristic() {
        return spotaMemInfoCharacteristic;
    }

    public static void setSpotaMemInfoCharacteristic(BluetoothGattCharacteristic spotaMemInfoCharacteristic) {
        BluetoothGattSingleton.spotaMemInfoCharacteristic = spotaMemInfoCharacteristic;
    }
}
