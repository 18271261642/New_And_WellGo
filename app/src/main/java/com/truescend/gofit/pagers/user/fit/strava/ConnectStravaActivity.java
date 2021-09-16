package com.truescend.gofit.pagers.user.fit.strava;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import androidx.appcompat.app.AppCompatDelegate;
import android.view.View;
import android.widget.Button;

import com.sn.utils.SNToast;
import com.sweetzpot.stravazpot.authenticaton.ui.StravaLoginButton;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BaseActivity;
import com.truescend.gofit.pagers.common.dialog.LoadingDialog;
import com.truescend.gofit.utils.StravaTool;
import com.truescend.gofit.views.TitleLayout;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 作者:东芝(2018/7/20).
 * 功能:Strava 授权与关联
 */

public class ConnectStravaActivity extends BaseActivity<ConnectStravaPresenterImpl, ConnectStravaContract.IView> implements ConnectStravaContract.IView {

    public static final File FILE = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "/test.gpx");
    @BindView(R.id.btCancelConnected)
    Button btCancelConnected;
    @BindView(R.id.btConnect)
    StravaLoginButton btConnect;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected ConnectStravaPresenterImpl initPresenter() {
        return new ConnectStravaPresenterImpl(this);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_strava;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {

        if (StravaTool.isAuthorized()) {
            btCancelConnected.setVisibility(View.VISIBLE);
            btConnect.setVisibility(View.GONE);
        } else {
            btCancelConnected.setVisibility(View.GONE);
            btConnect.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void onCreateTitle(TitleLayout titleLayout) {
        titleLayout.setTitle(R.string.content_connect_strava);
        titleLayout.setLeftIconFinishActivity(this);
    }


    @Override
    public void updateDisconnectSuccessful() {
        btCancelConnected.setVisibility(View.GONE);
        btConnect.setVisibility(View.VISIBLE);
        LoadingDialog.dismiss();
    }

    @Override
    public void updateDisconnectFailure(String msg) {
        LoadingDialog.dismiss();
    }

    @OnClick({R.id.btCancelConnected, R.id.btConnect})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btCancelConnected:
                LoadingDialog.loading(this);
                getPresenter().requestDisconnectStrava();
                break;
            case R.id.btConnect:
                StravaTool.startAuth(this);
                break;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        StravaTool.onActivityResult(requestCode, resultCode, data, new StravaTool.OnStravaAuthListener() {
            @Override
            public void isAuthorization() {
                LoadingDialog.loading(ConnectStravaActivity.this);
            }

            @Override
            public void isAuthorized() {
                if(isFinished())return;
                LoadingDialog.dismiss();
                btCancelConnected.setVisibility(View.VISIBLE);
                btConnect.setVisibility(View.GONE);
            }

            @Override
            public void isUnAuthorized() {
            }

            @Override
            public void failed(Throwable e) {
                if(isFinished())return;
                SNToast.toast(e.getMessage());
                LoadingDialog.dismiss();
            }
        });

    }


}
