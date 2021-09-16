package com.truescend.gofit.pagers.device;

import com.truescend.gofit.pagers.base.BasePresenter;

/**
 * Created by Admin
 * Date 2021/9/7
 */
public class DeviceManagerPresenterImpl extends BasePresenter<IDeviceManagerContract.IView> implements IDeviceManagerContract.IPresenter{


    private IDeviceManagerContract.IView iView;

    public DeviceManagerPresenterImpl(IDeviceManagerContract.IView iView) {
        this.iView = iView;
    }

    @Override
    public void getSelectPosition() {

    }
}
