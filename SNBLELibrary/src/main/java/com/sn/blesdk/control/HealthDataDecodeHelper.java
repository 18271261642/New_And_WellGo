package com.sn.blesdk.control;

import com.dz.blesdk.utils.BLELog;
import com.sn.blesdk.ble.SNBLEHelper;
import com.sn.blesdk.db.data.health.blood_oxygen.BloodOxygenStorageHelper;
import com.sn.blesdk.db.data.health.blood_pressure.BloodPressureStorageHelper;
import com.sn.blesdk.db.data.health.heart_rate.HeartRateBean;
import com.sn.blesdk.db.data.health.heart_rate.HeartRateStorageHelper;
import com.sn.blesdk.db.data.health.temperature.TemperatureBean;
import com.sn.blesdk.db.data.health.temperature.TemperatureStorageHelper;
import com.sn.blesdk.interfaces.IDataDecodeHelper;
import com.sn.utils.DateUtil;
import com.sn.utils.IF;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * 作者:东芝(2017/11/24).
 * 功能:体检数据解析
 * 1.实时心率血氧舒张压收缩压 解析
 * 2.心率大数据解析
 * 3.血压和血氧大数据未写 因为设备还没支持
 */
public class HealthDataDecodeHelper implements IDataDecodeHelper {


    private Calendar mHeartRateDataCalendar;
    private Calendar mTemperatureDataCalendar;

    private ArrayList<HeartRateBean.HeartRateDetailsBean> heartRates = new ArrayList<>();
    private ArrayList<TemperatureBean.TemperatureDetailsBean> temperatures = new ArrayList<>();

    /**
     * @param buffer
     */
    @Override
    public void decode(byte[] buffer) {

        //解析实时心率血氧舒张压收缩压等数据
        if (SNBLEHelper.startWith(buffer, "050702")) {
            BLELog.w("实时体检数据:解析开始");
            int mHeartRate = buffer[3] & 0xFF;
            int mBloodOxygen = buffer[4] & 0xFF;
            int mBloodDiastolic = buffer[5] & 0xFF;
            int mBloodSystolic = buffer[6] & 0xFF;

            if (IF.isEquals(mHeartRate, mBloodOxygen, mBloodDiastolic, mBloodSystolic, 0)) {
                BLELog.w("实时体检数据:数据异常,心率:%d,血氧:%d,舒张压:%d,收缩压:%d", mHeartRate, mBloodOxygen, mBloodDiastolic, mBloodSystolic);
                return;
            }
            BLELog.w("实时体检数据:心率:%d,血氧:%d,舒张压:%d,收缩压:%d", mHeartRate, mBloodOxygen, mBloodDiastolic, mBloodSystolic);
            BLELog.w("实时体检数据:解析完成");




            if (mHeartRate != 0) {
                if(mHeartRate<55||mHeartRate>200)
                {
                    BLELog.w("实时体检数据:数据异常,心率:%d,血氧:%d,舒张压:%d,收缩压:%d", mHeartRate, mBloodOxygen, mBloodDiastolic, mBloodSystolic);
                    mHeartRate= 0;
                }

                HeartRateStorageHelper.asyncSaveRealTimeData(SNBLEHelper.getDeviceMacAddress(), mHeartRate);
            }
            if (mBloodOxygen != 0) {
                BloodOxygenStorageHelper.asyncSaveRealTimeData(SNBLEHelper.getDeviceMacAddress(), mBloodOxygen);
            }
            if (mBloodDiastolic != 0 && mBloodSystolic != 0) {
                BloodPressureStorageHelper.asyncSaveRealTimeData(SNBLEHelper.getDeviceMacAddress(), mBloodDiastolic, mBloodSystolic);
            }

        }
        //心率大数据解析
        if (SNBLEHelper.startWith(buffer, "050707")) {

            //心率数据 00:00~23:54 视为一天,每15分钟一个记录,一共11个序号,两天共96条数据
            //今天00:00-->今天23:54->昨天00:00->昨天23:54
            //序号
            int index = buffer[3] & 0xff;
            if (index > 11) {//防止返回超过2天的数据
                return;
            }
            if (index == 0)//开始
            {
                BLELog.w("心率大数据:解析开始");
                mHeartRateDataCalendar = DateUtil.getCurrentCalendarBegin();
                heartRates.clear();
            }
            if (mHeartRateDataCalendar == null) return;//错误 无包头
            if (index != 0 && index % 6 == 0) {//时间移动到前一天
                mHeartRateDataCalendar.add(Calendar.DAY_OF_MONTH, -2);
            }
            for (int i = 4; i < 20; i++) {

                int heart = buffer[i] & 0xff;
                int timeIndex = DateUtil.convertTimeToIndex(mHeartRateDataCalendar, 1/*1分钟为一个索引,一共1440个,为什么不是15分钟一次?因为我要存实时数据*/);
                String date = DateUtil.getDate(DateUtil.YYYY_MM_DD_HH_MM_SS, mHeartRateDataCalendar);
                if (heart != 0) {
                    BLELog.w("心率大数据:%d,index:%d,时间:%s", heart, timeIndex, date);
                    if(heart<55||heart>200)
                    {
                        BLELog.w("心率大数据:数据异常,心率:%d", heart);
                        heart= 0;
                    }

                }

                heartRates.add(new HeartRateBean.HeartRateDetailsBean(timeIndex, date, heart, 1/*(默认0=手动检测,1=自动检测)*/));
                mHeartRateDataCalendar.add(Calendar.MINUTE, +15);
            }

        }

        //心率大数据同步成功
        if (SNBLEHelper.startWith(buffer, "0507FD")) {
            BLELog.w("心率大数据:解析完成");
            HeartRateStorageHelper.asyncSaveData(SNBLEHelper.getDeviceMacAddress(), heartRates);
        }




        //体温大数据解析
        if (SNBLEHelper.startWith(buffer, "05070C")) {

            int index = buffer[3] & 0xff;
            if (index > 11) {//防止返回超过2天的数据
                return;
            }
            if (index == 0)//开始
            {
                BLELog.w("体温大数据:解析开始");
                mTemperatureDataCalendar = DateUtil.getCurrentCalendarBegin();
                temperatures.clear();
            }
            if (mTemperatureDataCalendar == null) return;//错误 无包头
            if (index != 0 && index % 6 == 0) {//时间移动到前一天
                mTemperatureDataCalendar.add(Calendar.DAY_OF_MONTH, -2);
            }
            for (int i = 4; i < 20; i+=2) {

                int temp = (((buffer[i] & 0xff)<<8) | buffer[i+1] & 0xff)&0xFFFF;

                int timeIndex = DateUtil.convertTimeToIndex(mTemperatureDataCalendar, 1/*1分钟为一个索引,一共1440个,为什么不是15分钟一次?因为我要存实时数据*/);
                String date = DateUtil.getDate(DateUtil.YYYY_MM_DD_HH_MM_SS, mTemperatureDataCalendar);
                BLELog.w("体温大数据:%d,index:%d,时间:%s", temp, timeIndex, date);

                temperatures.add(new TemperatureBean.TemperatureDetailsBean(timeIndex, date, temp, 1/*(默认0=手动检测,1=自动检测)*/));
                mTemperatureDataCalendar.add(Calendar.MINUTE, +30
                );
            }

        }

        //体温大数据解析同步成功
        if (SNBLEHelper.startWith(buffer, "0507FC")) {
            BLELog.w("体温大数据:解析完成");

            TemperatureStorageHelper.asyncSaveData(SNBLEHelper.getDeviceMacAddress(), temperatures);
        }

    }


}
