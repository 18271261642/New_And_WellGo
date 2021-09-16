package com.sn.blesdk.control;

import com.sn.blesdk.ble.SNBLEHelper;
import com.sn.blesdk.interfaces.IDataDecodeHelper;
import com.sn.blesdk.storage.DeviceStorage;
import com.sn.blesdk.utils.eventbus.SNBLEEvent;
import com.sn.utils.eventbus.SNEventBus;

/**
 * 作者:东芝(2017/11/24).
 * 功能:设备信息解析
 */
public class DeviceInfoDataDecodeHelper implements IDataDecodeHelper {


    @Override
    public void decode(byte[] buffer) {

        //解析设备基本信息
        if (SNBLEHelper.startWith(buffer, "050102")) {

            int mDeviceID = SNBLEHelper.subBytesToInt(buffer, 2, 3, 4);
            int mDeviceVersion = buffer[5] & 0xFF;
            int mDeviceBatteryStatus = buffer[6] & 0xFF;
            int mDeviceBatteryLevel = buffer[7] & 0xFF;
            int mCustomerID = SNBLEHelper.subBytesToInt(buffer, 2, 8, 9);
            int mDeviceBatteryLevelStep = buffer[11] & 0xFF;
            //设备支持的功能定义位
//            int mDeviceFeatures = buffer[10] & 0xFF;
//            BLELog.d("mDeviceID=" + mDeviceID);
//            BLELog.d("mDeviceVersion=" + mDeviceVersion);
//            BLELog.d("mDeviceBatteryStatus=" + mDeviceBatteryStatus);
//            BLELog.d("mDeviceBatteryLevel=" + mDeviceBatteryLevel);
//            BLELog.d("mCustomerID=" + mCustomerID);
//            BLELog.d("mDeviceFeatures=" + mDeviceFeatures);
//            boolean isSupportHeartRateHistory = DataAnalysisUtil.get1Bit(mDeviceFeatures, 0) == 1;
//            boolean isSupportBloodOxygenHistory = DataAnalysisUtil.get1Bit(mDeviceFeatures, 1) == 1;
//            boolean isSupportBloodPressureHistory = DataAnalysisUtil.get1Bit(mDeviceFeatures, 2) == 1;
//            BLELog.d("心率大数据支持=" + isSupportHeartRateHistory);
//            BLELog.d("血氧大数据支持=" + isSupportBloodOxygenHistory);
//            BLELog.d("血压大数据支持=" + isSupportBloodPressureHistory);

            DeviceStorage.setDeviceCustomerId(mCustomerID);
            DeviceStorage.setDeviceVersion(mDeviceVersion);
            DeviceStorage.setDeviceUpgradeId(mDeviceID);
            SNEventBus.sendEvent(SNBLEEvent.EVENT_DEVICE_INFO_0, new int[]{mDeviceID,mDeviceVersion,mDeviceBatteryStatus,mDeviceBatteryLevel,mCustomerID,mDeviceBatteryLevelStep});

        }
        if (SNBLEHelper.startWith(buffer, "050101")) {

            StringBuilder sb = new StringBuilder();
            for (int i = 3; i <=8; i++) {
                sb.append(String.format("%02X", buffer[i]));
                if(i<8) {
                    sb.append(":");
                }
            }
            String mac = sb.toString();
            sb.setLength(0);
            for (int i = 9; i <=14; i++) {
                sb.append(String.format("%02X", buffer[i]));
                if(i<14) {
                    sb.append(":");
                }
            }
            String src_mac = sb.toString();
            if(src_mac.equals("00:00:00:00:00:00")){
                src_mac = null;
            }
            SNEventBus.sendEvent(SNBLEEvent.EVENT_DEVICE_INFO_MAC, new String[]{mac,src_mac});
        }
    }

}
