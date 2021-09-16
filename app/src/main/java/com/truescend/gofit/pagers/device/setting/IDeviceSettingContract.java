package com.truescend.gofit.pagers.device.setting;

import com.sn.app.db.data.config.DeviceConfigBean;

/**
 * Author:Created by 泽鑫 on 2017/12/16 16:54.
 */

public class IDeviceSettingContract {
    interface IView{
        void onUpdateDeviceConfig(DeviceConfigBean bean);
        void onShowLoading(boolean show);
        void onUnblockBand(boolean success);
    }

    interface IPresenter{
        void requestResetBand();
        void requestClearCache();
        void requestUnblockBand();
        void requestDeviceConfig();
        void requestChangeDeviceConfigData(DeviceConfigBean bean);
    }
}
