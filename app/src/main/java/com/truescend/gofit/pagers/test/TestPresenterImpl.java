package com.truescend.gofit.pagers.test;

import com.truescend.gofit.pagers.base.BasePresenter;

/**
 * 作者:东芝(2017/11/16).
 * 功能:供复制用
 */
public class TestPresenterImpl extends BasePresenter<ITestContract.IView> implements ITestContract.IPresenter {
    private ITestContract.IView view;


    public TestPresenterImpl(ITestContract.IView view) {
        this.view = view;
    }


}
