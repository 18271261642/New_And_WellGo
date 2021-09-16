package com.truescend.gofit.pagers.user.fit.google_fit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.sn.utils.SNLog;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BaseActivity;
import com.truescend.gofit.pagers.common.dialog.LoadingDialog;
import com.truescend.gofit.utils.GoogleFitTool;
import com.truescend.gofit.utils.UploadGoogleFitHelper;
import com.truescend.gofit.views.TitleLayout;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 由于该页面只提供关联和取消关联GoogleFit
 * Author Created by 泽鑫 on 2018/6/1.
 */
public class ConnectGoogleFitActivity extends BaseActivity<ConnectGoogleFitPresenterImpl, ConnectGoogleFitContract.IView> implements ConnectGoogleFitContract.IView {

    @BindView(R.id.btCancelConnected)
    Button btGoogleFitCancelConnected;
    @BindView(R.id.btUpload)
    Button btGoogleFitUpload;
    private UploadGoogleFitHelper helper;

    @Override
    protected ConnectGoogleFitPresenterImpl initPresenter() {
        return new ConnectGoogleFitPresenterImpl(this);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_googel_fit;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        initView();
        initGoogleFit();
    }

    @Override
    protected void onCreateTitle(TitleLayout titleLayout) {
        titleLayout.setTitle(R.string.title_associate_google_fit);
        titleLayout.setLeftIconFinishActivity(this);
    }

    private void initView() {
        helper = new UploadGoogleFitHelper(this);
        btGoogleFitCancelConnected.setEnabled(false);
        btGoogleFitUpload.setEnabled(false);
    }

    private void initGoogleFit() {
        if (!GoogleFitTool.hasPermission(this)) {
            SNLog.i("GoogleFit 检查权限: False");
            //Google 手动在Google设置取消关联，再次进去，默认请求，先关一遍
            GoogleFitTool.disconnectedToGoogleFit(this);
            GoogleFitTool.requestPermission(this);
        } else {
            SNLog.i("GoogleFit 检查权限: True");
            btGoogleFitCancelConnected.setEnabled(true);
            btGoogleFitUpload.setEnabled(true);
        }
    }

    @Override
    public void updateDisconnectSuccessful() {
        btGoogleFitCancelConnected.setEnabled(false);
        btGoogleFitUpload.setEnabled(false);
        SNLog.i("GoogleFit 取消关联: Successful!");
        LoadingDialog.dismiss();
    }

    @Override
    public void updateDisconnectFailure(String msg) {
        SNLog.i("GoogleFit 取消关联: Failure!" + msg);
        LoadingDialog.dismiss();
    }

    @OnClick({R.id.btCancelConnected, R.id.btUpload})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btCancelConnected:
                getPresenter().requestDisconnectGoogleFit(ConnectGoogleFitActivity.this);
                LoadingDialog.loading(this);
                break;
            case R.id.btUpload:
                helper.startAutoSync();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == GoogleFitTool.GOOGLE_FIT_PERMISSION_REQUEST_CODE) {
                SNLog.i("GoogleFit 请求权限: Successful!");
                btGoogleFitCancelConnected.setEnabled(true);
                btGoogleFitUpload.setEnabled(true);
            } else {
                SNLog.i("GoogleFit 请求权限: Failure!");
                btGoogleFitCancelConnected.setEnabled(false);
                btGoogleFitUpload.setEnabled(false);
            }
        }
    }

}
