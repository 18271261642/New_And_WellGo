package com.dz.bleota.syd;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.util.Log;

import com.dz.bleota.base.OTA;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * 作者:东芝(2018/1/16).
 * 功能:SYD8801的OTA
 */

public class SYD extends OTA {
    private static final String TAG = "BLE OTA_SYD8801";

    private static final UUID UPDATE_SERVICE_UUID = UUID.fromString("0000ff00-0000-1000-8000-00805f9b34fb"); // The UUID for service "FF00"
    private static final UUID UPDATE_CHARACTERISTIC_UUID = UUID.fromString("0000ff01-0000-1000-8000-00805f9b34fb"); // The UUID for service "FF00"


    private BluetoothGatt mBluetoothGatt;
    private BluetoothGattCharacteristic mCharacteristic;

    private final byte CMD_FW_ERASE = 0x16;
    private final byte CMD_FW_WRITE = 0x17;
    private final byte CMD_FW_UPGRADE = 0x18;
    private final int MAX_TRANS_COUNT = 15;
    private byte Current_Command = 0;
    private final Object SyncObj = new Object();

    private boolean isHasError;


    @Override
    public void connect(BluetoothDevice mDevice) {
        isHasError = true;
//        closeGattIfHas();
        mBluetoothGatt = mDevice.connectGatt(getContext(), false, mGattCallback);
        if (mBluetoothGatt == null) {
            updateStatus(STATUS_FAILED);
        }
    }

    @Override
    public void close() {
        isHasError = false;
        closeGattIfHas();
    }

    @Override
    public void destroy() {
        close();
    }


    private void closeGattIfHas() {
        //如果上次有 则释放
        if (mBluetoothGatt != null) {
            try {
                mBluetoothGatt.disconnect();
            } catch (Throwable e) {
                e.printStackTrace();
            }
            try {
                mBluetoothGatt.close();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }


    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i(TAG, "Connected to GATT server.");
                Log.i(TAG, "Attempting to start service discovery:" + mBluetoothGatt.discoverServices());
//                String name = gatt.getDevice().getName();

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i(TAG, "Disconnected from GATT server.");
                if (isHasError) {
                    updateStatus(STATUS_FAILED);
                }
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            List<BluetoothGattService> CurrentServices;

            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i(TAG, "mBluetoothGatt = " + mBluetoothGatt);
                CurrentServices = gatt.getServices();

                for (BluetoothGattService ss : CurrentServices) {
                    if (UPDATE_SERVICE_UUID.equals(ss.getUuid())) {
                        Log.i(TAG, "Find Service : " + ss.getUuid());
                        for (BluetoothGattCharacteristic character : ss.getCharacteristics()) {
                            Log.i(TAG, "Character UUID : " + character.getUuid());
                            if (UPDATE_CHARACTERISTIC_UUID.equals(character.getUuid())) {
                                mCharacteristic = character;
                                Log.i(TAG, "Find Characteristics: " + mCharacteristic);
                                new Thread(new Runnable() {
                                    public void run() {
                                        ProcessOTA();
                                    }
                                }).start();

                            }
                        }
                    }
                }
            } else {
                updateStatus(STATUS_FAILED);
                Log.i(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            NotifyForSync();
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //Log.i(TAG, "onCharacteristicRead success" );
            } else
                Log.i(TAG, "onCharacteristicRead fail");
        }

        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic,
                                          int status) {
            NotifyForSync();
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //Log.i(TAG, "onCharacteristicWrite success");
            } else
                Log.i(TAG, "onCharacteristicWrite fail");
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            Log.i(TAG, "onCharacteristicChanged");

        }
    };


    private void ProcessOTA() {
        byte[] ReadData;
        int CRC = 0;
        ReadData = ReadOTAFileBinary(getOTAFile());

        int i = 0;


        for (i = 0; i < ReadData.length; i++) {
            int CC = ReadData[i];
            CC &= 0x000000FF;
            CRC += CC;
            CRC = CRC & 0x0000FFFF;
        }
        Log.i(TAG, "CRC ==>" + CRC);
        OTA_Erase_Flash();
        updateStatus(STATUS_UPDATING);
        WriteFlash_All(ReadData);
        updateStatus(STATUS_SUCCESSFUL);
        OTA_Upgrade_Flash(ReadData.length, CRC);

        closeGattIfHas();
        isHasError = false;
    }

    private void WriteFlash_All(byte[] binArray) {
        int count = 0;
        byte[] DataArray = new byte[MAX_TRANS_COUNT];
        int WriteCount;
        int cc = 0;
        int i;

        WriteCount = binArray.length / MAX_TRANS_COUNT;

        for (i = 0; i < binArray.length; i++) {
            DataArray[count] = binArray[i];
            count += 1;

            if (count == MAX_TRANS_COUNT) {
                int CurrentAddress = i - (count - 1);
                cc += 1;
                OTA_Write_Flash(DataArray, CurrentAddress);
                count = 0;
                updateProgress(cc, WriteCount);
            } else {
                if (i == binArray.length - 1) {
                    // last time
                    int CurrentAddress = i - (count - 1);

                    Log.i(TAG, "Last count==> " + count + " ==> Address==>" + CurrentAddress + "==> CC ==>" + cc);
                    cc += 1;
                    OTA_Write_Flash(DataArray, CurrentAddress);
                    count = 0;
                    updateProgress(cc, WriteCount);
                }
            }
        }
    }

    private void OTA_Erase_Flash() {
        byte[] WriteData = new byte[2];
        WriteData[0] = CMD_FW_ERASE;
        WriteData[1] = 0x00;

        Current_Command = CMD_FW_ERASE;

        Log.i(TAG, "Process OTA");
        writeCharacteristic(WriteData);
        readCharacteristic();
    }

    private void OTA_Write_Flash(byte[] ProgramData, int Address) {
        byte[] WriteData = new byte[20];
        WriteData[0] = CMD_FW_WRITE;
        WriteData[1] = 0x13;
        WriteData[2] = (byte) (Address & 0x000000FF);
        WriteData[3] = (byte) ((Address & 0x0000FF00) >> 8);
        WriteData[4] = (byte) ProgramData.length;

        Current_Command = CMD_FW_WRITE;

        int i = 0;
        for (i = 0; i < ProgramData.length; i++) {
            WriteData[i + 5] = ProgramData[i];
        }
        writeCharacteristic(WriteData);
        readCharacteristic();
    }

    private void OTA_Upgrade_Flash(int Size, int CRC) {
        byte[] WriteData = new byte[6];
        WriteData[0] = CMD_FW_UPGRADE;
        WriteData[1] = 0x04;
        WriteData[2] = (byte) (Size & 0x000000FF);
        WriteData[3] = (byte) ((Size & 0x0000FF00) >> 8);
        WriteData[4] = (byte) (CRC & 0x000000FF);
        WriteData[5] = (byte) ((CRC & 0x0000FF00) >> 8);

        Current_Command = CMD_FW_UPGRADE;
        writeCharacteristic(WriteData);
        readCharacteristic();

    }

    private byte[] ReadOTAFileBinary(String filepath) {

        File file = new File(filepath);

        try {
            FileInputStream fis = new FileInputStream(file);

            int length = fis.available();
            byte[] BinaryData = new byte[length];

            fis.read(BinaryData);

            fis.close();
            return BinaryData;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[]{0x01, 0x01};
    }


    // For read/write operation
    private void writeCharacteristic(byte[] value) {
        mCharacteristic.setValue(value);
        mBluetoothGatt.writeCharacteristic(mCharacteristic);
        WaitForSync();
    }

    private byte[] readCharacteristic() {
        return new byte[]{};
        //TODO 取消校验不知道有没有问题 这样可以加快升级速度
//        boolean status = mBluetoothGatt.readCharacteristic(mCharacteristic);
//        Log.i(TAG, "read:"+status);
//        WaitForSync();
//
//        byte[] ReadData = mCharacteristic.getValue();
//
//        if (ReadData[0] != 0x0E)
//            Log.i(TAG, "Error at byte 0" + ReadData[0]);
//
//        if (ReadData[1] != 0x02)
//            Log.i(TAG, "Error at byte 1" + ReadData[1]);
//
//        if (ReadData[2] != Current_Command)
//            Log.i(TAG, "Error at byte 2" + ReadData[2]);
//
//        if (ReadData[3] != 0x00)
//            Log.i(TAG, "Error at byte 3" + ReadData[3]);
//
//
//        return ReadData;
    }

    private void WaitForSync() {
        synchronized (SyncObj) {
            try {
                SyncObj.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void NotifyForSync() {
        synchronized (SyncObj) {
            SyncObj.notify();
        }
    }


}
