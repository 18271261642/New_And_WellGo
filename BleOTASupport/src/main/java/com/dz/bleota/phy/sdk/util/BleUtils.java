package com.dz.bleota.phy.sdk.util;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.util.Log;

import com.dz.bleota.phy.sdk.beans.BleConstant;
import com.dz.bleota.phy.sdk.beans.FirmWareFile;
import com.dz.bleota.phy.sdk.beans.Partition;

import java.util.UUID;

/**
 * BleUtils
 *
 * @author:zhoululu
 * @date:2018/7/7
 */

public class BleUtils {

    public static boolean enableNotifications(BluetoothGatt bluetoothGatt){
        BluetoothGattService bluetoothGattService = bluetoothGatt.getService(UUID.fromString(BleConstant.SERVICE_UUID));

        if(bluetoothGattService == null){
            return false;
        }

        BluetoothGattCharacteristic bluetoothGattCharacteristic = bluetoothGattService.getCharacteristic(UUID.fromString(BleConstant.CHARACTERISTIC_WRITE_UUID));

        bluetoothGatt.setCharacteristicNotification(bluetoothGattCharacteristic,true);

        BluetoothGattDescriptor bluetoothGattDescriptor = bluetoothGattCharacteristic.getDescriptor(UUID.fromString(BleConstant.DESCRIPTOR_UUID));
        bluetoothGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        bluetoothGatt.writeDescriptor(bluetoothGattDescriptor);

        return true;
    }

    public static boolean enableIndicateNotifications(BluetoothGatt bluetoothGatt){
        BluetoothGattService bluetoothGattService = bluetoothGatt.getService(UUID.fromString(BleConstant.SERVICE_OTA_UUID));

        if(bluetoothGattService == null){
            return false;
        }

        BluetoothGattCharacteristic bluetoothGattCharacteristic = bluetoothGattService.getCharacteristic(UUID.fromString(BleConstant.CHARACTERISTIC_OTA_INDICATE_UUID));

        bluetoothGatt.setCharacteristicNotification(bluetoothGattCharacteristic,true);

        BluetoothGattDescriptor bluetoothGattDescriptor = bluetoothGattCharacteristic.getDescriptor(UUID.fromString(BleConstant.DESCRIPTOR_UUID));
        bluetoothGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        bluetoothGatt.writeDescriptor(bluetoothGattDescriptor);

        return true;
    }

    public static boolean checkIsOTA(BluetoothGatt bluetoothGatt){
        BluetoothGattService bluetoothGattService = bluetoothGatt.getService(UUID.fromString(BleConstant.SERVICE_OTA_UUID));

        if(bluetoothGattService == null){
            return false;
        }

        BluetoothGattCharacteristic bluetoothGattCharacteristic = bluetoothGattService.getCharacteristic(UUID.fromString(BleConstant.CHARACTERISTIC_OTA_DATA_WRITE_UUID));
        if(bluetoothGattCharacteristic != null){
            return true;
        }else {
            return false;
        }
    }


    public static boolean sendOTACommand(BluetoothGatt bluetoothGatt, String commd,boolean respons){
        BluetoothGattService bluetoothGattService = bluetoothGatt.getService(UUID.fromString(BleConstant.SERVICE_OTA_UUID));
        if(bluetoothGattService == null){
            return false;
        }

        BluetoothGattCharacteristic bluetoothGattCharacteristic = bluetoothGattService.getCharacteristic(UUID.fromString(BleConstant.CHARACTERISTIC_OTA_WRITE_UUID));
        if(!respons){
            bluetoothGattCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        }else{
            bluetoothGattCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
        }
        bluetoothGattCharacteristic.setValue(HexString.parseHexString(commd));
        bluetoothGatt.writeCharacteristic(bluetoothGattCharacteristic);

        Log.d("send ota commond", commd);

        return true;

       // LogUtil.getLogUtilInstance().save("send ota commond: "+commd);
    }


    public static String getOTAMac(String deviceAddress){
        final String firstBytes = deviceAddress.substring(0, 15);
        final String lastByte = deviceAddress.substring(15); // assuming that the device address is correct
        final String lastByteIncremented = String.format("%02X", (Integer.valueOf(lastByte, 16) + 1) & 0xFF);

        return firstBytes + lastByteIncremented;
    }

    public static boolean sendOTADate(BluetoothGatt bluetoothGatt,String cmd){
        BluetoothGattService bluetoothGattService = bluetoothGatt.getService(UUID.fromString(BleConstant.SERVICE_OTA_UUID));
        if(bluetoothGattService == null){
            Log.e(" OTA service", "service is null");
            return false;
        }

        BluetoothGattCharacteristic bluetoothGattCharacteristic = bluetoothGattService.getCharacteristic(UUID.fromString(BleConstant.CHARACTERISTIC_OTA_DATA_WRITE_UUID));

        bluetoothGattCharacteristic.setValue(HexString.parseHexString(cmd.toLowerCase()));
        bluetoothGatt.writeCharacteristic(bluetoothGattCharacteristic);

        Log.d("send ota data", cmd);

        return true;
    }

    public static String make_part_cmd(int index,int flash_addr,String run_addr,int size,int checksum){
        String fa = Util.translateStr(Util.strAdd0(Integer.toHexString(flash_addr),8));
        String ra = Util.translateStr(Util.strAdd0(run_addr,8));
        String sz = Util.translateStr(Util.strAdd0(Integer.toHexString(size),8));
        String cs = Util.translateStr(Util.strAdd0(Integer.toHexString(checksum),4));
        String in = Util.strAdd0(Integer.toHexString(index),2);

        return "02"+ in +fa + ra + sz + cs;
    }

    public static boolean sendPartition(BluetoothGatt gatt, FirmWareFile firmWareFile, int partitionIndex, int flash_addr){
        Partition partition = firmWareFile.getList().get(partitionIndex);
        int checsum = getPartitionCheckSum(partition);
        String cmd = make_part_cmd(partitionIndex, flash_addr, partition.getAddress(), partition.getPartitionLength(), checsum);

        return sendOTACommand(gatt,cmd, true);
    }

    public static int getPartitionCheckSum(Partition partition){
        /*List<List<String>> blocks = partition.getBlocks();
        int check = 0;
        for (int i =0;i<blocks.size();i++){
            List<String> block = blocks.get(i);
            for (int j = 0;j<block.size();j++){
                String data = block.get(i);
                byte[] bytes = HexString.parseHexString(data);

                check = checkSum(check,bytes);
            }
        }

        return check;*/

        return checkSum(0,HexString.parseHexString(partition.getData()));
    }

    private static int checkSum(int crc, byte[] data) {
        byte[] buf = new byte[data.length];// 存储需要产生校验码的数据
        for (int i = 0; i < data.length; i++) {
            buf[i] = data[i];
        }
        int len = buf.length;

        for (int pos = 0; pos < len; pos++) {
            if (buf[pos] < 0) {
                crc ^= (int) buf[pos] + 256; // XOR byte into least sig. byte of
                // crc
            } else {
                crc ^= (int) buf[pos]; // XOR byte into least sig. byte of crc
            }
            for (int i = 8; i != 0; i--) { // Loop over each bit
                if ((crc & 0x0001) != 0) { // If the LSB is set
                    crc >>= 1; // Shift right and XOR 0xA001
                    crc ^= 0xA001;
                } else{
                    // Else LSB is not set
                    crc >>= 1; // Just shift right
                }
            }
        }

        return crc;
    }


}
