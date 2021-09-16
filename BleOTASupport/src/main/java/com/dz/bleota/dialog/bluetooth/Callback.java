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
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Handler;

import android.util.Log;

import com.dz.bleota.dialog.async.DeviceConnectTask;
import com.dz.bleota.dialog.data.Statics;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;


public class Callback extends BluetoothGattCallback {
    public static String TAG = "Callback";
    DeviceConnectTask task;
    private Handler handler = new Handler();
    private int checkRefreshAttempts;
    private boolean refreshDone;
    private int refreshAttempt;

    public Callback(BluetoothManager bluetoothManager,DeviceConnectTask task) {
        this.bluetoothManager = bluetoothManager;
        this.task = task;
    }

    @Override
    public void onConnectionStateChange(final BluetoothGatt gatt, int status,
                                        int newState) {
        Log.i(TAG, "le onConnectionStateChange [" + newState + "]");
        if (newState == BluetoothProfile.STATE_CONNECTED) {
            Log.i(TAG, "le device connected");
            gatt.discoverServices();
            // Wait for cache refresh before starting service discovery.
            /*handler.post(new Runnable() {
                @Override
                public void run() {
                    if (task.refreshSucceeded() || ++checkRefreshAttempts > 20) {
                        gatt.discoverServices();
                    }
                    else {
                        Log.d(TAG, "Delay service discovery: " + checkRefreshAttempts);
                        handler.postDelayed(this, 100);
                    }
                }
            });*/
        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            Log.i(TAG, "le device disconnected");
            handler.removeCallbacksAndMessages(null);
        }
        Intent intent = new Intent();
        intent.setAction(Statics.CONNECTION_STATE_UPDATE);
        intent.putExtra("state", newState);
        LocalBroadcastManager.getInstance(task.context.get()).sendBroadcast(intent);
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        Log.i(TAG, "onServicesDiscovered");
        if (status != BluetoothGatt.GATT_SUCCESS) {
            Intent intent = new Intent();
            intent.setAction(Statics.BLUETOOTH_GATT_UPDATE);
            intent.putExtra("error", Statics.ERROR_COMMUNICATION);
            LocalBroadcastManager.getInstance(task.context.get()).sendBroadcast(intent);
            return;
        }
        // Refresh device cache. This is the safest place to initiate the procedure.
        if (!refreshDone && ++refreshAttempt <= 10) {
            refreshDone = BluetoothManager.refresh(gatt); // should not fail
            if (refreshDone)
                Log.d(TAG, "restart discovery after refresh");
            gatt.discoverServices();
            return;
        }
        // Check for SUOTA support
        BluetoothGattService suota = gatt.getService(Statics.SPOTA_SERVICE_UUID);
        if (suota == null
            || suota.getCharacteristic(Statics.SPOTA_MEM_DEV_UUID) == null
            || suota.getCharacteristic(Statics.SPOTA_GPIO_MAP_UUID) == null
            || suota.getCharacteristic(Statics.SPOTA_MEM_INFO_UUID) == null
            || suota.getCharacteristic(Statics.SPOTA_PATCH_LEN_UUID) == null
            || suota.getCharacteristic(Statics.SPOTA_PATCH_DATA_UUID) == null
            || suota.getCharacteristic(Statics.SPOTA_SERV_STATUS_UUID) == null
            || suota.getCharacteristic(Statics.SPOTA_SERV_STATUS_UUID).getDescriptor(Statics.SPOTA_DESCRIPTOR_UUID) == null
            ) {
            Intent intent = new Intent();
            intent.setAction(Statics.BLUETOOTH_GATT_UPDATE);
            intent.putExtra("error", Statics.ERROR_SUOTA_NOT_FOUND);
            LocalBroadcastManager.getInstance(task.context.get()).sendBroadcast(intent);
            return;
        }
        Intent intent = new Intent();
        intent.setAction(Statics.BLUETOOTH_GATT_UPDATE);
        intent.putExtra("step", 0);
        LocalBroadcastManager.getInstance(task.context.get()).sendBroadcast(intent);
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        boolean sendUpdate = true;
        int index = -1;
        int step = -1;

        if (characteristic.getUuid().equals(Statics.ORG_BLUETOOTH_CHARACTERISTIC_MANUFACTURER_NAME_STRING)) {
            index = 0;
        } else if (characteristic.getUuid().equals(Statics.ORG_BLUETOOTH_CHARACTERISTIC_MODEL_NUMBER_STRING)) {
            index = 1;
        } else if (characteristic.getUuid().equals(Statics.ORG_BLUETOOTH_CHARACTERISTIC_FIRMWARE_REVISION_STRING)) {
            index = 2;
        } else if (characteristic.getUuid().equals(Statics.ORG_BLUETOOTH_CHARACTERISTIC_SOFTWARE_REVISION_STRING)) {
            index = 3;
        }
        // SPOTA
        else if (characteristic.getUuid().equals(Statics.SPOTA_MEM_INFO_UUID)) {
            //int memInfoValue = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT32, 0);
            //Log.d("mem info", memInfoValue + "");
            //DeviceActivity.getInstance().logMemInfoValue(memInfoValue);
            step = 5;
        } else {
            sendUpdate = false;
        }

        if (sendUpdate) {
            Log.d(TAG, "onCharacteristicRead: " + index);
            Intent intent = new Intent();
            intent.setAction(Statics.BLUETOOTH_GATT_UPDATE);
            if (index >= 0) {
                intent.putExtra("characteristic", index);
                intent.putExtra("value", new String(characteristic.getValue()));
            } else {
                intent.putExtra("step", step);
                intent.putExtra("value", characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT32, 0));
            }
            LocalBroadcastManager.getInstance(task.context.get()).sendBroadcast(intent);
        }

        super.onCharacteristicRead(gatt, characteristic, status);
    }
    private BluetoothManager bluetoothManager;
    public BluetoothManager getBluetoothManager(){

        return bluetoothManager;
    }
    
    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        Log.d(TAG, "onCharacteristicWrite: " + characteristic.getUuid().toString());

        if (status == BluetoothGatt.GATT_SUCCESS) {
            Log.i(TAG, "write succeeded");
            int step = -1;
            // Step 2 callback: write SPOTA_MEM_DEV_UUID value
            if (characteristic.getUuid().equals(Statics.SPOTA_MEM_DEV_UUID)) {
                int currStep = getBluetoothManager().step;
                if (currStep == 2 || currStep == 3)
                    step = 3;
            }
            // Step 3 callback: write SPOTA_GPIO_MAP_UUID value
            else if (characteristic.getUuid().equals(Statics.SPOTA_GPIO_MAP_UUID)) {
                step = 4;
            }
            // Step 4 callback: set the patch length, default 240
            else if (characteristic.getUuid().equals(Statics.SPOTA_PATCH_LEN_UUID)) {
                step = getBluetoothManager().type == SuotaManager.TYPE ? 5 : 7;
            }
            else if (characteristic.getUuid().equals(Statics.SPOTA_PATCH_DATA_UUID)
                    //&& getBluetoothManager().type == SuotaManager.TYPE
                    && getBluetoothManager().chunkCounter != -1
                    ) {
                //step = getBluetoothManager().type == SuotaManager.TYPE ? 5 : 7;
                /*SUOTAFragment.getInstance().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getBluetoothManager().sendBlock();
                    }
                });*/
                //Log.d(TAG, "Next block in chunk " + DeviceActivity.getInstance().bluetoothManager.chunkCounter);
                getBluetoothManager().sendBlock();
            }

            if (step > 0) {
                Intent intent = new Intent();
                intent.setAction(Statics.BLUETOOTH_GATT_UPDATE);
                intent.putExtra("step", step);
                LocalBroadcastManager.getInstance(task.context.get()).sendBroadcast(intent);
            }
        } else {
            Log.e(TAG, "write failed: " + status);
            // Suota on remote device doesn't send write response before reboot
            if (!getBluetoothManager().rebootsignalSent) {
                Intent intent = new Intent();
                intent.setAction(Statics.BLUETOOTH_GATT_UPDATE);
                intent.putExtra("error", Statics.ERROR_COMMUNICATION);
                LocalBroadcastManager.getInstance(task.context.get()).sendBroadcast(intent);
            }
        }
        super.onCharacteristicWrite(gatt, characteristic, status);
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        super.onDescriptorWrite(gatt, descriptor, status);
        Log.d(TAG, "onDescriptorWrite");
        if (status != BluetoothGatt.GATT_SUCCESS) {
            Intent intent = new Intent();
            intent.setAction(Statics.BLUETOOTH_GATT_UPDATE);
            intent.putExtra("error", Statics.ERROR_COMMUNICATION);
            LocalBroadcastManager.getInstance(task.context.get()).sendBroadcast(intent);
            return;
        }
        if (descriptor.getCharacteristic().getUuid().equals(Statics.SPOTA_SERV_STATUS_UUID)) {
            int step = 2;

            Intent intent = new Intent();
            intent.setAction(Statics.BLUETOOTH_GATT_UPDATE);
            intent.putExtra("step", step);
            LocalBroadcastManager.getInstance(task.context.get()).sendBroadcast(intent);
        }
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        super.onCharacteristicChanged(gatt, characteristic);
        int value = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
        Log.d(TAG, String.format("SPOTA_SERV_STATUS notification: %#04x", value));

        int step = -1;
        int error = -1;
        int memDevValue = -1;
        // Set memtype callback
        if (value == 0x10) {
            step = 3;
        }
        // Successfully sent a block, send the next one
        else if (value == 0x02) {
            step = getBluetoothManager().type == SuotaManager.TYPE ? 5 : 8;
        } else if (value == 0x03 || value == 0x01) {
            memDevValue = value;
        } else {
            error = value;
        }
        if (step >= 0 || error >= 0 || memDevValue >= 0) {
            Intent intent = new Intent();
            intent.setAction(Statics.BLUETOOTH_GATT_UPDATE);
            intent.putExtra("step", step);
            intent.putExtra("error", error);
            intent.putExtra("memDevValue", memDevValue);
            LocalBroadcastManager.getInstance(task.context.get()).sendBroadcast(intent);
        }
    }
}
