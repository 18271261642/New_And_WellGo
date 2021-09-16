package com.truescend.gofit.pagers.start.register;


/**
 * 作者:东芝(2018/06/13).
 * 功能:新注册界面
 */

public class IRegisterContract {

    interface IView {
        void updateRegisterStatue(boolean isSuccess, String msg);
        void onShowLoading(boolean show);
    }

    interface IPresenter
    {
        void requestRegister(String email, String psw, String answer1, String answer2, int indexFromQuestion1, int indexFromQuestion2);
    }
}
