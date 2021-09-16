package com.truescend.gofit.pagers.track.setting;

import com.truescend.gofit.pagers.base.BasePresenter;

/**
 * Author:Created by 泽鑫 on 2017/12/13 14:15.
 */

public class RunSettingPresenterImpl extends BasePresenter<IRunSettingContract.IView> implements IRunSettingContract.IPresenter {
    private IRunSettingContract.IView view;
    public RunSettingPresenterImpl(IRunSettingContract.IView view){
        this.view = view;
    }

}
