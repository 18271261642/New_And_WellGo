package com.truescend.gofit.pagers.user.fit.google_fit;

import android.content.Context;

/**
 * Author Created by 泽鑫 on 2018/6/1.
 */
public class ConnectGoogleFitContract {
    interface IView{
        void updateDisconnectSuccessful();
        void updateDisconnectFailure(String msg);
    }

    interface IPresenter{
        void requestDisconnectGoogleFit(Context context);
    }

}
