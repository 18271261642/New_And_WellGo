package com.truescend.gofit.pagers.main;

import java.io.File;

/**
 * 作者:东芝(2017/11/16).
 * 功能:
 */

public class IMainContract {

    interface IMainView
    {
        void onUpdateStatusChange(boolean isNeedUpdate, boolean isNecessary, String newVersion, String localVersion, String description);
        void onUpdateSuccessful(File file);
        void onUpdateProgress(int progress);

        void onDeviceFindThePhoneNow();
    }

    interface IMainPresenter
    {
        void requestCheckVersion();
        void requestStartUpdateApp();
        /**
         * 同步网络上的历史网络数据
         */
         void requestDeviceDataNetworkSync();
    }
}
