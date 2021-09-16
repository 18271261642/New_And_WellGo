package com.dz.blesdk.entity;

import java.util.UUID;

/**
 * 作者:东芝(2017/11/25).
 * 功能:读写数据体
 */

public class BLEWriteOrRead {
    public byte[] data;
    public UUID mGattServiceUUID;
    public UUID mGattCharacteristicUUID;
    public boolean isWrite;
    public int reTryCount;


    public BLEWriteOrRead(byte[] data, UUID mGattServiceUUID, UUID mGattCharacteristicUUID, boolean isWrite) {
        this.data = data;
        this.mGattServiceUUID = mGattServiceUUID;
        this.mGattCharacteristicUUID = mGattCharacteristicUUID;
        this.isWrite = isWrite;
    }
}