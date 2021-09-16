package com.sn.blesdk.ble;

import android.text.TextUtils;

import com.dz.blesdk.ble.DZBLEScanner;
import com.dz.blesdk.entity.BleDevice;
import com.dz.blesdk.interfaces.OnScanBleListener;
import com.sn.app.BuildConfig;
import com.sn.blesdk.entity.SNBLEDevice;
import com.sn.blesdk.net.bean.DeviceInfo;


/**
 * 作者:东芝(2017/11/20).
 * 功能:蓝牙设备扫描
 */

public class SNBLEScanner {
    private static OnScanBleListener listener;

    public static boolean isScanning() {
        return DZBLEScanner.isScanning();
    }

    public static void startScan( OnScanBleListener listener) {
        SNBLEScanner.listener = listener;
        DZBLEScanner.startScan(scanBleListener);
    }
    private static com.dz.blesdk.interfaces.OnScanBleListener scanBleListener = new com.dz.blesdk.interfaces.OnScanBleListener<BleDevice>() {
        @Override
        public void onScanStart() {
            if (listener != null) {
                listener.onScanStart();
            }
        }

        @Override
        public void onScanning(BleDevice device) {


            String name = device.mDeviceName;
            boolean isUseManufacturerIdGetDeviceInfoSuccess = false;
            if (device.mParsedAd != null) {//根据厂商定义的广播数据 重新确定设备名称




                //通过厂商ID取出设备信息
                DeviceInfo deviceInfo = DeviceType.getDeviceInfo(device.mParsedAd.manufacturers);
                boolean checkManufacturerIsOurDevice = deviceInfo != null;
                //通过厂商ID判断 是否是我们家的设备
                if (checkManufacturerIsOurDevice) {


                    //按 BluetoothDevice 名称
                    if(TextUtils.isEmpty(name)){
                        name = device.mDevice!=null?device.mDevice.getName():null;
                    }
                    //上面没有 则按 设备广播上的名称
                    if(TextUtils.isEmpty(name)){
                        name = device.mParsedAd!=null?device.mParsedAd.localName:null;
                    }
                    //上面没有 最后按 网络上的名称
                    if(TextUtils.isEmpty(name)){
                        name  = deviceInfo.getDevice_name();
                    }

                    //能取得设备信息
                    isUseManufacturerIdGetDeviceInfoSuccess = true;
                }
            }
            if(!isUseManufacturerIdGetDeviceInfoSuccess&&BuildConfig.APP_ID==1&&BuildConfig.DEBUG){
                isUseManufacturerIdGetDeviceInfoSuccess = true;
            }

            // 如果上面的逻辑无法通过厂商ID 取出设备信息, 这下子只能粗略地判断设备名了
            if (!isUseManufacturerIdGetDeviceInfoSuccess) {
                DeviceInfo deviceInfo = DeviceType.getDeviceInfo(name);
                boolean checkNameIsOurDevice = deviceInfo != null ;
                //通过设备名判断 是否是我们家的设备
                if (!checkNameIsOurDevice) {
                    //同时也不是OTA模式
                    if (!DeviceType.isDFUModel(name)) {
                        return;
                    }
                }
            }

            if (listener != null) {
                listener.onScanning( new SNBLEDevice(device));
            }
        }

        @Override
        public void onScanStop() {
            if (listener != null) {
                listener.onScanStop();
            }
        }

        @Override
        public void onScanTimeout() {
            if (listener != null) {
                listener.onScanTimeout();
            }
        }
    };
    public static void stopScan() {
        DZBLEScanner.stopScan();
    }
}
