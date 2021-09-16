package com.truescend.gofit.pagers.user.fit.strava;

import com.truescend.gofit.pagers.base.BasePresenter;
import com.truescend.gofit.utils.StravaTool;

public class ConnectStravaPresenterImpl extends BasePresenter<ConnectStravaContract.IView> implements ConnectStravaContract.IPresenter {
    private ConnectStravaContract.IView view;

    public ConnectStravaPresenterImpl(ConnectStravaContract.IView view) {
        this.view = view;
    }

    @Override
    public void requestDisconnectStrava() {
        StravaTool.clearAuth(new StravaTool.OnStravaAuthListener() {
            @Override
            public void isAuthorization() {

            }

            @Override
            public void isAuthorized() {

            }

            @Override
            public void isUnAuthorized() {
                view.updateDisconnectSuccessful();
            }

            @Override
            public void failed(Throwable e) {
                view.updateDisconnectFailure(e.getMessage());
            }
        });

    }


}
