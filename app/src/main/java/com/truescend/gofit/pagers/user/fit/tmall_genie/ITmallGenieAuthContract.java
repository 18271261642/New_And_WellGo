package com.truescend.gofit.pagers.user.fit.tmall_genie;


/**
 * 作者:东芝(2019/6/18).
 * 功能:天猫精灵授权
 */
public class ITmallGenieAuthContract {

    interface IView {
        void onUpdateUserCode(String code);
        void onUpdateUserCodeFailed(String errMsg);
    }

    interface IPresenter
    {
        void requestGetUserCode();
    }
}
