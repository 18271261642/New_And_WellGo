package com.truescend.gofit.pagers.start.resetpsw.checker;


import com.sn.app.net.data.app.bean.GetQuestionsBean;

/**
 * 作者:东芝(2017/11/16).
 * 功能:密保检测
 */

public class IResetPswCheckerContract {

    interface IView {
        void onHasSetQuestionsStatus(boolean has, String email, GetQuestionsBean.DataBean bean);
        void onShowLoading(boolean show);
        void onShowMessage(String msg);
    }

    interface IPresenter
    {
        void requestHasSetQuestionsStatus(String email);
    }
}
