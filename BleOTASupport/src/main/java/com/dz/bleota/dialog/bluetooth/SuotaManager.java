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
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.util.Date;
import java.util.List;

import com.dz.bleota.dialog.data.Statics;

public class SuotaManager extends BluetoothManager {
    public static final int TYPE = 1;

    public static final int MEMORY_TYPE_EXTERNAL_I2C = 0x12;
    public static final int MEMORY_TYPE_EXTERNAL_SPI = 0x13;

    static final String TAG = "SuotaManager";

    public SuotaManager(Context context) {
        super(context);
        type = SuotaManager.TYPE;
    }
    public void initMainScreenItems() {
        List<BluetoothGattService> services = BluetoothGattSingleton.getGatt().getServices();
        for (BluetoothGattService service : services) {
            List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
            for (BluetoothGattCharacteristic characteristic : characteristics) {
                if (characteristic.getUuid().equals(Statics.ORG_BLUETOOTH_CHARACTERISTIC_MANUFACTURER_NAME_STRING)) {
                    characteristicsQueue.add(characteristic);
                } else if (characteristic.getUuid().equals(Statics.ORG_BLUETOOTH_CHARACTERISTIC_MODEL_NUMBER_STRING)) {
                    characteristicsQueue.add(characteristic);
                } else if (characteristic.getUuid().equals(Statics.ORG_BLUETOOTH_CHARACTERISTIC_FIRMWARE_REVISION_STRING)) {
                    characteristicsQueue.add(characteristic);
                } else if (characteristic.getUuid().equals(Statics.ORG_BLUETOOTH_CHARACTERISTIC_SOFTWARE_REVISION_STRING)) {
                    characteristicsQueue.add(characteristic);
                } else if (characteristic.getUuid().equals(Statics.SPOTA_MEM_INFO_UUID)) {
                    BluetoothGattSingleton.setSpotaMemInfoCharacteristic(characteristic);
                }
            }
        }
        readNextCharacteristic();
    }
    public void processStep(Intent intent) {
        int newStep = intent.getIntExtra("step", -1);
        int error = intent.getIntExtra("error", -1);
        int memDevValue = intent.getIntExtra("memDevValue", -1);
        if (error != -1) {
            onError(error);
            return;
        }
		if(memDevValue >= 0) {
			processMemDevValue(memDevValue);
		}
        // If a step is set, change the global step to this value
        if (newStep >= 0) {
            this.step = newStep;
        }
        // If no step is set, check if Bluetooth characteristic information is set
        else {
            int index = intent.getIntExtra("characteristic", -1);
            String value = intent.getStringExtra("value");
//            context.setItemValue(index, value);
            readNextCharacteristic();
        }
        Log.d(TAG, "step " + this.step);
        switch (this.step) {
            case 0:
                initMainScreenItems();
                this.step = -1;
                break;
            // Enable notifications
            case 1:
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (Build.VERSION.SDK_INT >= 21) {
                    Log.d(TAG, "Connection parameters update request (high)");
                    BluetoothGattSingleton.getGatt().requestConnectionPriority(BluetoothGatt.CONNECTION_PRIORITY_HIGH);
                }
                enableNotifications();
                break;
            // Init mem type
            case 2:
                Log.d(TAG, "Uploading  to " + device.getName() + ".\n" +
                        "Please wait until the process is completed.");
               Log.d(TAG, String.format("Firmware CRC: %#04x", file.getCrc() & 0xff));
                String fwSizeMsg = "Upload size: " + file.getNumberOfBytes() + " bytes";
                Log.d(TAG, fwSizeMsg);
               Log.d(TAG, fwSizeMsg);
                // Acquire wake lock to keep CPU running during upload procedure
//                Log.d(TAG, "Acquire wake lock");
//                PowerManager pm = (PowerManager) context.get().getSystemService(Context.POWER_SERVICE);
//                wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "SUOTA");
//                wakeLock.acquire();
                uploadStart = new Date().getTime();
                setSpotaMemDev();
                break;
            // Set mem_type for SPOTA_GPIO_MAP_UUID
            case 3:
                // After setting SPOTAR_MEM_DEV and SPOTAR_IMG_STARTED notification is received, we must set the GPIO map.
                // The order of the callbacks is unpredictable, so the notification may be received before the write response.
                // We don't have a GATT operation queue, so the SPOTA_GPIO_MAP write will fail if the SPOTAR_MEM_DEV hasn't finished yet.
                // Since this call is synchronized, we can wait for both broadcast intents from the callbacks before proceeding.
                // The order of the callbacks doesn't matter with this implementation.
                if (++gpioMapPrereq == 2)
                    setSpotaGpioMap();
                break;
            // Set SPOTA_PATCH_LEN_UUID
            case 4:
                setPatchLength();
                break;
            // Send a block containing blocks of 20 bytes until the patch length (default 240) has been reached
            // Wait for response and repeat this action
            case 5:
                if (!lastBlock) {
                    sendBlock();
                } else {
                    if (!preparedForLastBlock) {
                        setPatchLength();
                    } else if (!lastBlockSent) {
                        sendBlock();
                    } else if (!endSignalSent) {
                        
                        sendEndSignal();
                    } else if (error == -1) {
                        onSuccess();
                    }
                }
                break;
        }
    }

    @Override
    protected int getSpotaMemDev() {
        int memTypeBase = -1;
        switch (memoryType) {
            case Statics.MEMORY_TYPE_SPI:
                memTypeBase = MEMORY_TYPE_EXTERNAL_SPI;
                break;
            case Statics.MEMORY_TYPE_I2C:
                memTypeBase = MEMORY_TYPE_EXTERNAL_I2C;
                break;
        }
        int memType = (memTypeBase << 24) | imageBank;
        return memType;
    }

    private void processMemDevValue(int memDevValue) {
        String stringValue = String.format("%#10x", memDevValue);
        Log.d(TAG, "processMemDevValue() step: " + step + ", value: " + stringValue);
        switch (step) {
            case 2:
                if (memDevValue == 0x1) {
                   Log.d(TAG, "Set SPOTA_MEM_DEV: 0x1");
                    goToStep(3);
                } else {
                    onError(0);
                }
                break;
        }
    }
}
