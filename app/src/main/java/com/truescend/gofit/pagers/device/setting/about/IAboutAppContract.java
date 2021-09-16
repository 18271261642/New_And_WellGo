package com.truescend.gofit.pagers.device.setting.about;

import com.sn.app.db.data.config.DeviceConfigBean;

import java.io.File;

/**
 * Author:Created by 泽鑫 on 2017/12/18 18:13.
 */
public class IAboutAppContract {
    interface IView{

        void onDialogLoading(String msg);
        void onDialogDismiss();

        void onUpdateStatusChange(boolean isNeedUpdate, String newVersion, String localVersion, String description);
        void onUpdateSuccessful(File file);
        void onUpdateProgress(int progress);
        void onUpdateDeviceConfig(DeviceConfigBean bean);
    }

    interface IPresenter{
        void requestCheckVersion();
        void requestStartUpdateApp();

        //单位相关

        void requestDeviceConfig();
        void requestChangeDeviceConfigData(DeviceConfigBean bean);

    }
}

