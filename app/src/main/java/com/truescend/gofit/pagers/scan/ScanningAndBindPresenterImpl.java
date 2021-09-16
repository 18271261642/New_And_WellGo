package com.truescend.gofit.pagers.scan;

import com.truescend.gofit.pagers.base.BasePresenter;

/**
 * Author:Created by 泽鑫 on 2017/12/13 20:44.
 */

public class ScanningAndBindPresenterImpl extends BasePresenter<IScanningAndBindContract.IView> implements IScanningAndBindContract.IPresenter{
    private IScanningAndBindContract.IView view;

    public ScanningAndBindPresenterImpl(IScanningAndBindContract.IView view){
        this.view = view;
    }


}
