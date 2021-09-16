package com.truescend.gofit.pagers.device.setting.update;

import android.content.Context;

import com.sn.blesdk.net.bean.DeviceInfo;

/**
 * 作者:东芝(2018/3/7).
 * 功能:固件更新
 */

public class IBandUpdateContract {

    interface IView {
        void onDialogLoading(String msg);
        void onDialogDismiss();
        void onDeviceVersion(int localVersion, int newVersion);
        void onFailed(boolean finish, String msg);
        void onOTAProgressChanged(int cur, int total);
        void onOTAStarted();
        void onOTAProcessing(String msg);
        void onOTAFailed(String msg);
        void onOTASuccessful();
    }

    interface IPresenter {
        /**
         * 请求获取设备版本
         */
        void requestCheckVersionAndUpdate(Context context, DeviceInfo into, String mac);

        /**
         * 救砖
         * @param context
         * @param into
         * @param mac
         * @param upgradeid
         */
        void requestFixDeviceUpdate(Context context, DeviceInfo into, String mac, int upgradeid);
        void requestStartOTA();
        void requestAbortOTA();
    }
}
