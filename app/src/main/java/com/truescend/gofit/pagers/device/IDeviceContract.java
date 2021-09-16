package com.truescend.gofit.pagers.device;

import com.sn.blesdk.net.bean.DeviceInfo;

/**
 * 功能：设备页面接口
 * Author:Created by 泽鑫 on 2017/11/20 09:39.
 */

public class IDeviceContract {

    interface IView
    {
        void updateBandName(String name);
        void updateMacAddress(String mac);
        void updateElectric(int electric, int curLevelStep, int maxLevelStep);
        void updateUnreadMessages(int number);
        void updateDeviceInfo(DeviceInfo deviceInfo);

        void updateDeviceStatus(boolean isConnected, boolean isUnbind);
    }

    interface IPresenter
    {
        void requestGetDeviceInfo();
        void requestGetBandName();
        void requestGetMacAddress();
        void requestGetElectric();
        void requestUnreadSchedule();
    }
}
