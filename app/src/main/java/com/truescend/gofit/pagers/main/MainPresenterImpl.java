package com.truescend.gofit.pagers.main;

import com.sn.blesdk.utils.eventbus.SNBLEEvent;
import com.sn.utils.eventbus.SNEvent;
import com.sn.utils.eventbus.SNEventBus;
import com.truescend.gofit.pagers.base.BasePresenter;
import com.truescend.gofit.utils.AppEvent;
import com.truescend.gofit.utils.AppVersionUpdateHelper;
import com.truescend.gofit.utils.NetworkSyncHelper;
import com.truescend.gofit.utils.UploadTodayDataToMyFriendsSeeHelper;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

/**
 * 作者:东芝(2017/11/16).
 * 功能:
 */

public class MainPresenterImpl extends BasePresenter<IMainContract.IMainView> implements IMainContract.IMainPresenter, AppVersionUpdateHelper.OnUpdateStatusChangeListener {
    private IMainContract.IMainView view;
    private AppVersionUpdateHelper mUpdateHelper = new AppVersionUpdateHelper(this);
    public MainPresenterImpl(IMainContract.IMainView view) {
        this.view = view;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        SNEventBus.register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SNEventBus.unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventReceived(SNEvent event) {
        switch (event.getCode()) {
            case SNBLEEvent.EVENT_BLE_BAND_CALL_PHONE:
                if(isUIEnable()) {
                    getView().onDeviceFindThePhoneNow();
                }
                break;
        }
    }



    @Override
    public void requestCheckVersion() {
        mUpdateHelper.checkUpdate();
    }


    @Override
    public void requestStartUpdateApp() {
        mUpdateHelper.startUpdate();
    }


    @Override
    protected void onResume() {
        super.onResume();
        //首次 就查一次未读消息数
        SNEventBus.sendEvent(AppEvent.EVENT_USER_UNREAD_MESSAGE_NUMBER, true);
    }

    @Override
    public void requestDeviceDataNetworkSync() {
        NetworkSyncHelper.startAutoSync();
        UploadTodayDataToMyFriendsSeeHelper.upload();

    }

    @Override
    public void onUpdateStatusChange(boolean isNeedUpdate, boolean isNecessary, String newVersion, String localVersion, String description) {
        if(!isUIEnable())return;
        view.onUpdateStatusChange(isNeedUpdate,isNecessary, newVersion, localVersion, description);
    }

    @Override
    public void onUpdateProgressChange(int progress) {
        if(!isUIEnable())return;
        view.onUpdateProgress(progress);
    }

    @Override
    public void onUpdateDone(File apkFile) {
        if(!isUIEnable())return;
        view.onUpdateSuccessful(apkFile);
    }

    @Override
    public void onUpdateFailed(String msg) {
        if(!isUIEnable())return;
    }
}
