package com.dz.bleota.phy.sdk;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import androidx.annotation.Nullable;
import android.util.Log;

import com.dz.bleota.phy.sdk.beans.ErrorCode;
import com.dz.bleota.phy.sdk.ble.OTACallBack;
import com.dz.bleota.phy.sdk.firware.UpdateFirewareCallBack;
import com.dz.bleota.phy.sdk.util.BleUtils;

/**
 * OTASDKUtils
 *
 * @date:2018/7/13
 */

public class OTASDKUtils {

    private int STATUS = 0;
    private int START_OTA = 1;
    private int OTA_CONNECTING = 2;
    private int OTA_ING = 3;
    private int REBOOT = 4;

    private OTACallBack otaCallBack;
    private OTAUtils otaUtils;
    private UpdateFirewareCallBack firewareCallBack;
    private String address;
    private String filePath;

    public static final String VERSION = "1.0.5";

    /**
     * 创建OTASDKUtils方法
     *
     * @param context
     * @param firewareCallBack
     */
    public OTASDKUtils(Context context, UpdateFirewareCallBack firewareCallBack) {
        this.firewareCallBack = firewareCallBack;

        otaCallBack = new OTACallBackImpl();
        otaUtils = new OTAUtils(context, otaCallBack);
    }

    public void updateFirware(@Nullable String address, @Nullable String filePath) {
        this.address = address;
        this.filePath = filePath;

        STATUS = 0;

        otaUtils.connectDevice(address);
    }

    public void setRetryTimes(int times) {
        if (otaUtils != null) {
            otaUtils.setRetryTimes(times);
        }
    }

    public void cancleOTA() {
        if (otaUtils != null) {
            otaUtils.cancleOTA();
        }
    }

    private void error(int code) {
        STATUS = 0;
        firewareCallBack.onError(code);
    }

    private void success() {
        STATUS = 0;
        firewareCallBack.onUpdateComplete();
    }

    private class OTACallBackImpl implements OTACallBack {
        @Override
        public void onConnected(boolean isConnected) {
            if (isConnected) {
                if (STATUS == 0) {
                    otaUtils.startOTA();
                    STATUS = START_OTA;
                } else if (STATUS == OTA_CONNECTING) {
                    otaUtils.updateFirmware(filePath);
                    STATUS = OTA_ING;
                } else {
                    Log.d("STATUS", "error:" + STATUS);
                }
            } else {
                if (STATUS == START_OTA) {
                    otaUtils.starScan();
                } else if (STATUS == OTA_CONNECTING) {
                    error(ErrorCode.OTA_CONNTEC_ERROR);
                } else if (STATUS == REBOOT) {
                    success();
                } else if (STATUS == OTA_ING) {
                    error(ErrorCode.OTA_CONNTEC_ERROR);
                } else {
                    error(ErrorCode.CONNECT_ERROR);
                }
            }
        }

        @Override
        public void onOTA(boolean isConnected) {
            if (isConnected) {
                otaUtils.updateFirmware(filePath);
                STATUS = OTA_ING;
            }
        }

        @Override
        public void onDeviceSearch(BluetoothDevice device, int rssi, byte[] scanRecord) {
            if (STATUS == START_OTA && device.getAddress().equals(BleUtils.getOTAMac(address))) {
                otaUtils.stopScan();

                otaUtils.connectDevice(device.getAddress());

                STATUS = OTA_CONNECTING;
            }
        }

        @Override
        public void onProcess(float process) {
            firewareCallBack.onProcess(process);
        }

        @Override
        public void onError(int code) {
            Log.d("onError", "error:" + code);
            error(code);
        }

        @Override
        public void onOTAFinish() {
            otaUtils.reBoot();
            STATUS = REBOOT;
        }

        @Override
        public void onReboot() {

        }
    }

    public OTAUtils getOtaUtils() {
        return otaUtils;
    }
}
