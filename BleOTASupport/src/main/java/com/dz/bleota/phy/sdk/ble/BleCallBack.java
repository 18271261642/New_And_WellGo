package com.dz.bleota.phy.sdk.ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;
import android.util.Log;

import com.dz.bleota.phy.sdk.beans.BleConstant;
import com.dz.bleota.phy.sdk.beans.ErrorCode;
import com.dz.bleota.phy.sdk.beans.FirmWareFile;
import com.dz.bleota.phy.sdk.beans.Partition;
import com.dz.bleota.phy.sdk.util.BleUtils;
import com.dz.bleota.phy.sdk.util.HexString;

import java.util.List;


/**
 * BleCallBack
 *
 * @date:2018/7/13
 */

public class BleCallBack extends BluetoothGattCallback {

    private static final String TAG = BleCallBack.class.getSimpleName();

    private OTAUtilsCallBack otaUtilsCallBack;
    private FirmWareFile firmWareFile;

    private int partitionIndex = 0;
    private int blockIndex = 0;
    private int cmdIndex = 0;
    private int flash_addr = 0;

    private List<String> cmdList;

    private boolean isResponse;
    private String response;

    private float totalSize;
    private float finshSize;

    private int retryTimes = 3;
    private int errorTimes;

    private boolean isCancle;

    public void setOtaUtilsCallBack(OTAUtilsCallBack otaUtilsCallBack) {
        this.otaUtilsCallBack = otaUtilsCallBack;
    }

    public void setFirmWareFile(FirmWareFile firmWareFile) {
        this.firmWareFile = firmWareFile;

        totalSize = firmWareFile.getLength();

        initData();
    }

    public void setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
    }

    private void initData(){
        partitionIndex = 0;
        blockIndex = 0;
        cmdIndex = 0;
        flash_addr = 0;

        cmdList = null;

        finshSize = 0;

        isCancle = false;
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        if(newState == BluetoothProfile.STATE_CONNECTED){
            gatt.discoverServices();
        }else if(newState == BluetoothProfile.STATE_DISCONNECTED){
            if(gatt != null){
                gatt.close();
            }

            otaUtilsCallBack.onConnectChange(false);
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            Log.d(TAG, "onServicesDiscovered: success");

            //开启notification,返回false,断开连接
            boolean b = true;

            if(BleUtils.checkIsOTA(gatt)){
                b = BleUtils.enableIndicateNotifications(gatt);
            }else{
                //b = BleUtils.enableNotifications(gatt);
                //如果不是OTA状态，直接返回

                otaUtilsCallBack.onConnectChange(true);
            }

            if(!b){
                gatt.disconnect();
            }

        }else{
            gatt.disconnect();
        }
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        if(characteristic.getUuid().toString().equals(BleConstant.CHARACTERISTIC_OTA_WRITE_UUID)){
            //0081，开始ota，发送partition命令
            if(("0081").equals(response) && isResponse) {

                sendPartition(gatt,firmWareFile,partitionIndex,flash_addr);

                blockIndex = 0;

                //0084  发送partition命令respons，开始发送ota数据
            }else if(("0084").equals(response) && isResponse) {
                cmdIndex = 0;

                cmdList = firmWareFile.getList().get(partitionIndex).getBlocks().get(blockIndex);

                sendOTAData(gatt,cmdList.get(cmdIndex));
            }

            isResponse = false;

        }else if(characteristic.getUuid().toString().equals(BleConstant.CHARACTERISTIC_OTA_DATA_WRITE_UUID)){
            //一条ota数据发送成功
            if(status == 0){

                if(errorTimes > 0){
                    errorTimes = 0;
                }

                finshSize += characteristic.getValue().length;

                otaUtilsCallBack.onProcess(finshSize*100/totalSize);

                cmdIndex ++;
                if(cmdIndex < cmdList.size()){
                    sendOTAData(gatt,cmdList.get(cmdIndex));
                }

            }else{
                retryCmd(gatt,ErrorCode.OTA_DATA_WRITE_ERROR);
                //otaUtilsCallBack.onError(ErrorCode.OTA_DATA_WRITE_ERROR);
            }

            //LogUtil.getLogUtilInstance().save("send ota data status: "+status);
        }
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        String uuid = characteristic.getUuid().toString();

        if(uuid.equals(BleConstant.CHARACTERISTIC_OTA_INDICATE_UUID)){
            Log.d("OTA", HexString.parseStringHex(characteristic.getValue()));

            response = HexString.parseStringHex(characteristic.getValue());

            //0087  一组16*20 ota数据发送成功，开始下一组
            if(("0087").equals(response)) {

                if(errorTimes > 0){
                    errorTimes = 0;
                }

                blockIndex++;
                cmdIndex = 0;

                if (blockIndex < firmWareFile.getList().get(partitionIndex).getBlocks().size()) {

                    cmdList = firmWareFile.getList().get(partitionIndex).getBlocks().get(blockIndex);
                    sendOTAData(gatt,cmdList.get(cmdIndex));
                }
                //0085  一个partition 数据发送成功，发送下一个partition命令
            }else if(("0085").equals(response)){
                partitionIndex++;

                blockIndex = 0;

                if(partitionIndex < firmWareFile.getList().size()){
                    //后面地址由前一个长度决定
                    Partition prePartition = firmWareFile.getList().get(partitionIndex-1);
                    flash_addr = flash_addr + prePartition.getPartitionLength() + 16 - (prePartition.getPartitionLength()+4)%4;

                    sendPartition(gatt,firmWareFile,partitionIndex,flash_addr);
                }
                //0083 所有ota数据发送成功
            }else if(("0083").equals(response)){
                otaUtilsCallBack.onOTAFinish();
            }

            isResponse = true;

            if(!("0081").equals(response) && !("0083").equals(response) && !("0084").equals(response) && !("0085").equals(response) && !("0087").equals(response)){
                if(!isCancle){
                    retry(gatt,ErrorCode.OTA_RESPONSE_ERROR);
                }
            }
        }
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        if(BleConstant.DESCRIPTOR_UUID.equals(descriptor.getUuid().toString().toLowerCase())){
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //连接设备，在discoverService和设置notification之后返回
                otaUtilsCallBack.onConnectChange(true);
            }else{
                gatt.disconnect();
            }
        }
    }

    private void sendOTAData(BluetoothGatt gatt,String data){

        if(isCancle){
            return;
        }

        boolean success = BleUtils.sendOTADate(gatt,data);
        if(!success){
            otaUtilsCallBack.onError(ErrorCode.OTA_DATA_SERVICE_NOT_FOUND);
        }
    }

    private void sendPartition(BluetoothGatt gatt,FirmWareFile firmWareFile,int partitionIndex,int flash_addr){
        if(isCancle){
            return;
        }

        boolean success = BleUtils.sendPartition(gatt,firmWareFile,partitionIndex,flash_addr);
        if(!success){
            otaUtilsCallBack.onError(ErrorCode.OTA_SERVICE_NOT_FOUND);
        }
    }

    public void setCancle(){
        isCancle = true;
    }

    private void retry(BluetoothGatt gatt,int errorCode){
        Log.d(TAG, "retry:");

        if(errorTimes < retryTimes){
            cmdIndex = 0;
            cmdList = firmWareFile.getList().get(partitionIndex).getBlocks().get(blockIndex);
            sendOTAData(gatt,cmdList.get(cmdIndex));

            retryTimes++;
        }else{
            otaUtilsCallBack.onError(errorCode);
        }
    }

    private void retryCmd(BluetoothGatt gatt,int errorCode){
        Log.d(TAG, "retryCmd: ");

        if(errorTimes < retryTimes){
            sendOTAData(gatt,cmdList.get(cmdIndex));

            retryTimes++;
        }else{
            otaUtilsCallBack.onError(errorCode);
        }
    }

}
