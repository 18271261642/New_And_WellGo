package com.truescend.gofit.pagers.device.camera;

import com.sn.blesdk.ble.SNBLEHelper;
import com.sn.blesdk.cmd.SNCMD;
import com.sn.blesdk.utils.eventbus.SNBLEEvent;
import com.sn.utils.eventbus.SNEvent;
import com.sn.utils.eventbus.SNEventBus;
import com.truescend.gofit.pagers.base.BasePresenter;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Author:Created by 泽鑫 on 2017/12/15 14:47.
 */

public class RemoteCameraPresenterImpl extends BasePresenter<IRemoteCameraContract.IView> implements IRemoteCameraContract.IPresenter {
    private IRemoteCameraContract.IView view;

    public  RemoteCameraPresenterImpl(IRemoteCameraContract.IView view){
        this.view = view;
    }


    @Override
    public void requestStartTakePhoto() {
        SNBLEHelper.sendCMD(SNCMD.get().setCameraStatus(1));
    }

    @Override
    public void requestExitTakePhoto() {
        SNBLEHelper.sendCMD(SNCMD.get().setCameraStatus(0));
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
            case SNBLEEvent.EVENT_BLE_CAMERA_TAKE_PHOTO:
                getView().updateTakePhoto();
                break;
            case SNBLEEvent.EVENT_BLE_STATUS_BLUETOOTH_OFF:
            case SNBLEEvent.EVENT_BLE_STATUS_DISCONNECTED:
            case SNBLEEvent.EVENT_BLE_STATUS_CONNECT_FAILED:
            case SNBLEEvent.EVENT_BLE_STATUS_CONNECTED:
                getView().exitRemoteCamera();
                break;
        }
    }
}
