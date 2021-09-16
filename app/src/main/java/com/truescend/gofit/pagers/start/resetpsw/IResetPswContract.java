package com.truescend.gofit.pagers.start.resetpsw;

public class IResetPswContract {
    interface IView{
        void updateResetStatue(boolean isSuccess);
        void updateGetAuthCodeStatue(boolean isSuccess);
        void onShowLoading(boolean show);
        void onShowMessage(String msg);
    }

    interface IPresenter{
        void requestReset(String email, String answer1, String answer2, int indexFromQuestion1, int indexFromQuestion2, String password);
        void requestReset(String email,  String authCode, String password);
        void requestAuthCode(String email);
    }
}
