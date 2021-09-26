package com.truescend.gofit.pagers.device;

import android.Manifest;
import android.content.Intent;
import android.view.View;

import com.sn.utils.SNToast;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BaseFragment;
import com.truescend.gofit.pagers.base.BasePresenter;
import com.truescend.gofit.pagers.device.camera.viewer.ImageViewerPresenterImpl;
import com.truescend.gofit.utils.PermissionUtils;
import com.truescend.gofit.wifi.MainActivity;

/**
 * Created by Admin
 * Date 2021/9/8
 */
public class WifiFragment extends BaseFragment<WifiPresenterIml,IWifiContract.IView> implements  IWifiContract.IView{


    public static WifiFragment getInstance(){
        return new WifiFragment();
    }

    @Override
    protected WifiPresenterIml initPresenter() {
        return new WifiPresenterIml(this);
    }

    @Override
    protected int initLayout() {
        return R.layout.fragment_wifi_home_layout;
    }

    @Override
    protected void onCreate() {
        assert getView() != null;
        getView().findViewById(R.id.wifiBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              startToWifi();
            }
        });

    }

    @Override
    public void menuClick() {

    }


    private void startToWifi(){
        PermissionUtils.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, new PermissionUtils.OnPermissionGrantedListener() {
            @Override
            public void onGranted() {
                startActivity(new Intent(getActivity(), MainActivity.class));
            }

            @Override
            public void onDenied() {
                SNToast.init(getActivity());
                SNToast.toast("请打开存储权限!");
            }
        });
    }
}
