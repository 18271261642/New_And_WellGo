package com.truescend.gofit.pagers.device;

import com.truescend.gofit.pagers.base.BasePresenter;

/**
 * Created by Admin
 * Date 2021/9/8
 */
public class WifiPresenterIml extends BasePresenter<IWifiContract.IView> implements IWifiContract.IPresenter{


    private IWifiContract.IView iView;

    public WifiPresenterIml(IWifiContract.IView iView) {
        this.iView = iView;
    }

    @Override
    public void menuV() {

    }
}
