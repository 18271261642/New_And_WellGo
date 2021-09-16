package com.truescend.gofit.pagers.device.setting.update;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.sn.blesdk.ble.DeviceType;
import com.sn.blesdk.ble.SNBLEHelper;
import com.sn.blesdk.net.bean.DeviceInfo;
import com.sn.utils.SNToast;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BaseActivity;
import com.truescend.gofit.pagers.common.dialog.CommonDialog;
import com.truescend.gofit.pagers.common.dialog.LoadingDialog;
import com.truescend.gofit.pagers.common.dialog.ProgressDialog;
import com.truescend.gofit.utils.ResUtil;
import com.truescend.gofit.utils.StatusBarUtil;
import com.truescend.gofit.views.TitleLayout;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 功能：固件升级界面
 * 兼容SYD/nRF/Dialog的固件升级功能
 */
public class BandUpdateActivity extends BaseActivity<BandUpdatePresenterImpl, IBandUpdateContract.IView> implements IBandUpdateContract.IView {

    public static final String KEY_IS_FIX_BAND = "isFixBand";
    public static final String KEY_INFO = "info";
    public static final String KEY_MAC = "mac";
    public static final String KEY_UPGRADEID = "upgradeid";

    @BindView(R.id.tlTitle)
    TitleLayout tlTitle;
    @BindView(R.id.ivBandUpdateBackIcon)
    ImageView ivBandUpdateBackIcon;
    @BindView(R.id.tvBandUpdateCurrentVersion)
    TextView tvBandUpdateCurrentVersion;
    @BindView(R.id.tvBandUpdateLastVersion)
    TextView tvBandUpdateLastVersion;
    @BindView(R.id.tvBandUpdate)
    TextView tvBandUpdate;
    private ProgressDialog progressDialog;
    private boolean isFixBand;


    @Override
    protected BandUpdatePresenterImpl initPresenter() {
        return new BandUpdatePresenterImpl(this);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_band_update;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        StatusBarUtil.setRootViewFitsSystemWindows(this,false);
        StatusBarUtil.setStatusBarDarkTheme(this,false);
        StatusBarUtil.setStatusBarColor(this,0x00000000);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        initTitle();
        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }
        DeviceInfo deviceInfo = (DeviceInfo) intent.getSerializableExtra(KEY_INFO);
          isFixBand = intent.getBooleanExtra(KEY_IS_FIX_BAND, false);
        String deviceMac = intent.getStringExtra(KEY_MAC);
        int upgradeid = intent.getIntExtra(KEY_UPGRADEID,-1);

        if (isFixBand) {
            getPresenter().requestFixDeviceUpdate(this, deviceInfo, deviceMac, upgradeid);
        } else {
            getPresenter().requestCheckVersionAndUpdate(this, deviceInfo, deviceMac);
        }

        progressDialog = new ProgressDialog(BandUpdateActivity.this);
        progressDialog.setMax(100);
        progressDialog.setProgress(0);
        progressDialog.setMessage(getString(R.string.content_update_ota));

    }


    private void initTitle() {
        tlTitle.setTitleShow(false);
    }

    @OnClick({R.id.ivBandUpdateBackIcon, R.id.tvBandUpdate})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivBandUpdateBackIcon:
                onBackPressed();
                break;
            case R.id.tvBandUpdate:
                getPresenter().requestStartOTA();
                break;
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        getPresenter().requestAbortOTA();
    }

    @Override
    public void onDialogLoading(String msg) {
        LoadingDialog.show(this, msg).setCancelable(true);
    }

    @Override
    public void onDialogDismiss() {
        LoadingDialog.dismiss();
    }

    @Override
    public void onDeviceVersion(int localVersion, int newVersion) {
        tvBandUpdateCurrentVersion.setText(ResUtil.format(getString(R.string.content_current_device_version), localVersion));
        boolean hasNewVersion = localVersion < newVersion;
        tvBandUpdateLastVersion.setText(hasNewVersion ? ResUtil.format(getString(R.string.content_last_device_version), newVersion) : getString(R.string.content_is_last_device_version));
        tvBandUpdate.setVisibility(hasNewVersion ? View.VISIBLE : View.GONE);

        if (hasNewVersion) {
            CommonDialog.create(this,
                    getString(R.string.content_new_device_version),
                    ResUtil.format(getString(R.string.content_update_new_device_version),localVersion,newVersion),
                    getString(R.string.content_cancel),
                    getString(R.string.content_confirm),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    },
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            getPresenter().requestStartOTA();
                        }
                    }
            ).show();
        }

    }

    @Override
    public void onFailed(boolean finish, String msg) {
        if (isFinished()) return;
        SNToast.toast(msg);
        if (finish) {
            setResult(Activity.RESULT_CANCELED);
            finish();
        }
    }

    @Override
    public void onOTAProgressChanged(int cur, int total) {
        if (isFinished()) return;
        progressDialog.setProgress(cur);
        progressDialog.setMax(total);
    }

    @Override
    public void onOTAStarted() {
        if (isFinished()) return;

        progressDialog.show();
    }

    @Override
    public void onOTAProcessing(String msg) {
        if (isFinished()) return;
        progressDialog.setTitle(msg);
    }

    @Override
    public void onOTAFailed(String msg) {
        if (isFinished()) return;
        progressDialog.dismiss();
        CommonDialog.createNoContent(this, msg, null, getString(R.string.content_confirm),
                null,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                        if(isFixBand) {
                            setResult(Activity.RESULT_CANCELED);
                        }else{
                           // PageJumpUtil.startScanningAndBindActivity(BandUpdateActivity.this);
                            setResult(Activity.RESULT_CANCELED);
                        }
                        finish();


                    }
                }
        ).show();

    }

    @Override
    public void onOTASuccessful() {
        if (isFinished()) return;
        progressDialog.dismiss();
        tvBandUpdate.setVisibility(View.GONE);
        CommonDialog.createNoContent(this, getString(R.string.content_congratulations_successful), null, getString(R.string.content_confirm),
                null,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        setResult(Activity.RESULT_OK);
                        finish();
                    }
                }
        ).show();
    }

    @Override
    protected void onDestroy() {
        progressDialog.dismiss();
        super.onDestroy();

        //重新开启自动重连
        //取消 [开发主动断开]  否则不会重连!
        SNBLEHelper.setIsUserDisconnected(false);
        //启用[自动重连]
        SNBLEHelper.setAutoReConnect(true);
        SNBLEHelper.connect(DeviceType.getDeviceMac());
    }
}
