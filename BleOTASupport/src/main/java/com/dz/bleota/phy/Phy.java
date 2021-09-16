package com.dz.bleota.phy;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import com.dz.bleota.base.OTA;
import com.dz.bleota.phy.sdk.OTASDKUtils;
import com.dz.bleota.phy.sdk.beans.ErrorCode;
import com.dz.bleota.phy.sdk.firware.UpdateFirewareCallBack;

/**
 * 作者:东芝(2018/10/29).
 * 功能:奉加微芯片
 */
public class Phy extends OTA implements UpdateFirewareCallBack {

    private OTASDKUtils otasdkUtils;
    private boolean isUpdating;

    @Override
    protected void connect(BluetoothDevice mDevice) {
        otasdkUtils = new OTASDKUtils(getContext(), this);
//      otasdkUtils.setRetryTimes();//默认3
        otasdkUtils.updateFirware(mDevice.getAddress(),getOTAFile());
    }

    @Override
    public void startOTA(Context context, boolean scan, String mDeviceMac, String file, OnOTAUpdateStatusChangeListener listener) {
        super.startOTA(context, false, mDeviceMac, file, listener);
    }

    @Override
    protected void close() {
        if (otasdkUtils != null) {
            otasdkUtils.cancleOTA();
        }
    }

    @Override
    public void destroy() {
        close();
    }


    ////////////////////////////////////////////////////////////////////////////////////////
    //--------------------------------------奉加微sdk回调-----------------------------------
    ////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onError(int i) {
        isUpdating = false;
        switch (i) {
            case ErrorCode.OTA_RESPONSE_ERROR:
                updateStatus(STATUS_PREPARE_TIME_OUT);
                break;
            default:
                updateStatus(STATUS_FAILED);
                break;
        }

    }

    @Override
    public void onProcess(float v) {
        if(!isUpdating) {
            isUpdating = true;
            updateStatus(STATUS_UPDATING);
        }
        updateProgress(Math.round(v),100);
    }

    @Override
    public void onUpdateComplete() {
        isUpdating=false;
        updateStatus(STATUS_SUCCESSFUL);
    }
}
