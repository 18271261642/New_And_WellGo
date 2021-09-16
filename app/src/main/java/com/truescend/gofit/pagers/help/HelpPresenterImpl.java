package com.truescend.gofit.pagers.help;

import com.truescend.gofit.pagers.base.BasePresenter;

public class HelpPresenterImpl extends BasePresenter<IHelpContract.IView> implements IHelpContract.IPresenter {
    private IHelpContract.IView view;


    public HelpPresenterImpl(IHelpContract.IView view) {
        this.view = view;
    }


}
