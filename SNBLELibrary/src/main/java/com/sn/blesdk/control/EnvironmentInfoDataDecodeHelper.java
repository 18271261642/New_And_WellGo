package com.sn.blesdk.control;

import com.dz.blesdk.utils.BLELog;
import com.sn.blesdk.ble.SNBLEHelper;
import com.sn.blesdk.interfaces.IDataDecodeHelper;

/**
 * 作者:东芝(2017/11/24).
 * 功能:环境信息解析 气压，海拔，温度
 */
public class EnvironmentInfoDataDecodeHelper implements IDataDecodeHelper {


    @Override
    public void decode(byte[] buffer) {

        //解析 气压，海拔，温度
        if (SNBLEHelper.startWith(buffer, "05010D")) {

            int mAirPressure = SNBLEHelper.subBytesToInt(buffer, 4, 3, 6);
            int mAltitudeUnit = buffer[7]&0xFF;//海拔制式 0x00公制 0x01英制
            int mAltitude = SNBLEHelper.subBytesToInt(buffer, 4, 8, 11);
            int mTemperatureUnit = buffer[12]&0xFF;//温度制式 0x00摄氏度 0x01华摄氏度
            int mTemperature = SNBLEHelper.subBytesToInt(buffer, 4, 13, 16);
            BLELog.d("气压=" + mAirPressure);
            BLELog.d("海拔=" + mAltitude+" "+(mAltitudeUnit==0?"km":"mile"));
            BLELog.d("温度=" +  mTemperature+" "+(mTemperatureUnit==0?"°C":"°F"));
        }
    }

}
