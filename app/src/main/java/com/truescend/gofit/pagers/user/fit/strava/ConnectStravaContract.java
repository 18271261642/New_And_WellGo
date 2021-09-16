package com.truescend.gofit.pagers.user.fit.strava;


public class ConnectStravaContract {
    interface IView{
        void updateDisconnectSuccessful();
        void updateDisconnectFailure(String msg);
    }

    interface IPresenter{
        void requestDisconnectStrava();
    }

}
