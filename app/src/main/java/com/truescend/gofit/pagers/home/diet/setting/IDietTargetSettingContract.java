package com.truescend.gofit.pagers.home.diet.setting;


import com.sn.app.db.data.config.DeviceConfigBean;

/**
 * 作者:东芝(2018/11/21).
 * 功能:用户饮食减肥计划目标参数设置
 */

public class IDietTargetSettingContract {

    interface IView {

        void onDialogLoading(String msg);
        void onDialogDismiss();

        void onUpdateDeviceConfig(DeviceConfigBean bean);
        void onUpdateUserDataSuccess();
        void onUpdateUserDataFailed(String msg);
    }

    interface IPresenter
    {
        void requestUpdateDietTarget(int targetStep, float curWeight,float targetWeight, float targetCalory);
        void requestDeviceConfig();
        void requestChangeDeviceConfigData(DeviceConfigBean bean);
    }
}
