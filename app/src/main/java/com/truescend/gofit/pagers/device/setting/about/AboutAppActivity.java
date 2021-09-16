package com.truescend.gofit.pagers.device.setting.about;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.sn.app.db.data.config.DeviceConfigBean;
import com.sn.app.db.data.config.bean.UnitConfig;
import com.sn.blesdk.ble.DeviceType;
import com.sn.blesdk.net.bean.DeviceInfo;
import com.sn.utils.SNToast;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BaseActivity;
import com.truescend.gofit.pagers.common.bean.ItemBannerButton;
import com.truescend.gofit.pagers.common.dialog.CommonDialog;
import com.truescend.gofit.pagers.common.dialog.LoadingDialog;
import com.truescend.gofit.pagers.common.dialog.ProgressDialog;
import com.truescend.gofit.pagers.device.bean.ItemDeviceCommon;
import com.truescend.gofit.pagers.device.bean.ItemUnit;
import com.truescend.gofit.utils.AppVersionUpdateHelper;
import com.truescend.gofit.utils.StatusBarUtil;
import com.truescend.gofit.utils.UIRefresh;
import com.truescend.gofit.views.TitleLayout;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 功能：关于App界面
 * Author:Created by 泽鑫 on 2017/12/18 18:12.
 */

public class AboutAppActivity extends BaseActivity<AboutAppPresenterImpl, IAboutAppContract.IView> implements IAboutAppContract.IView {
    @BindView(R.id.tlTitle)
    TitleLayout tlTitle;
    @BindView(R.id.tvAboutAppCheckUpdate)
    TextView tvAboutAppCheckUpdate;
    @BindView(R.id.ivAboutAppBack)
    ImageView ivAboutAppBack;
    @BindView(R.id.tvAboutAppTitle)
    TextView tvAboutAppTitle;
    @BindView(R.id.ilAboutAppCurrentVersion)
    View ilAboutAppCurrentVersion;
    @BindView(R.id.ilAboutAppLastVersion)
    View ilAboutAppLastVersion;
    @BindView(R.id.ilAboutAppAutomaticSync)
    View ilAboutAppAutomaticSync;
    @BindView(R.id.ilAboutAppDistanceUnit)
    View ilAboutAppDistanceUnit;
    @BindView(R.id.ilAboutAppTemperatureUnit)
    View ilAboutAppTemperatureUnit;
    @BindView(R.id.ilAboutAppWeightUnit)
    View ilAboutAppWeightUnit;
    @BindView(R.id.ilAboutAppTimeUnit)
    View ilAboutAppTimeUnit;

    private ItemBannerButton currentVersionItem;
    private ItemBannerButton lastVersionItem;

    private ItemDeviceCommon automaticSyncItem;

    private ItemUnit distanceUnitItem;
    private ItemUnit temperatureUnitItem;
    private ItemUnit weightUnitItem;
    private ItemUnit timeUnitItem;

    private ProgressDialog mAppUpdateDialog;
    private DeviceConfigBean bean;

    @Override
    protected AboutAppPresenterImpl initPresenter() {
        return new AboutAppPresenterImpl(this);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_about_app;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        StatusBarUtil.setRootViewFitsSystemWindows(this,false);
        StatusBarUtil.setStatusBarDarkTheme(this,false);
        StatusBarUtil.setStatusBarColor(this,0x00000000);
        initTitle();
        initItem();
        getPresenter().requestCheckVersion();
        getPresenter().requestDeviceConfig();
    }

    private void initTitle() {
        tlTitle.setTitleShow(false);
    }

    private void initItem() {

        DeviceInfo currentDeviceInfo = DeviceType.getCurrentDeviceInfo();

        ////////////////////////////////////////////////////////////////////////////////////////
        //--------------------------------------其他Item-----------------------------------
        ////////////////////////////////////////////////////////////////////////////////////////
        currentVersionItem = new ItemBannerButton(ilAboutAppCurrentVersion);
        currentVersionItem.setTitle(R.string.content_app_current_version);
        currentVersionItem.setIconVisibility(View.GONE);


        lastVersionItem = new ItemBannerButton(ilAboutAppLastVersion);
        lastVersionItem.setTitle(R.string.content_app_last_version);
        lastVersionItem.setIconVisibility(View.GONE);


        automaticSyncItem = new ItemDeviceCommon(ilAboutAppAutomaticSync);
        automaticSyncItem.setTitle(R.string.content_app_auto_sync);
        automaticSyncItem.setSettingIconVisibility(View.INVISIBLE);
        automaticSyncItem.setIntervalTimeVisibility(View.GONE);
        automaticSyncItem.setSwitchOnCheckedChangeListener(onCheckedChangeListener);

        ////////////////////////////////////////////////////////////////////////////////////////
        //--------------------------------------单位item-----------------------------------
        ////////////////////////////////////////////////////////////////////////////////////////

        distanceUnitItem = new ItemUnit(ilAboutAppDistanceUnit);
        distanceUnitItem.setTitle(R.string.content_unit_distance);
        distanceUnitItem.setLeftText(R.string.unit_km);
        distanceUnitItem.setRightText(R.string.unit_mile);
        distanceUnitItem.setOnCheckedChangeListener(onItemUnitCheckedChangeListener);

        temperatureUnitItem = new ItemUnit(ilAboutAppTemperatureUnit);
        temperatureUnitItem.setTitle(R.string.content_unit_temperature);
        temperatureUnitItem.setLeftText(R.string.unit_C);
        temperatureUnitItem.setRightText(R.string.unit_F);
        temperatureUnitItem.setOnCheckedChangeListener(onItemUnitCheckedChangeListener);

        timeUnitItem = new ItemUnit(ilAboutAppTimeUnit);
        timeUnitItem.setTitle(R.string.content_unit_time);
        timeUnitItem.setLeftText("24");
        timeUnitItem.setRightText("12");
        timeUnitItem.setOnCheckedChangeListener(onItemUnitCheckedChangeListener);

        if(currentDeviceInfo!=null&&currentDeviceInfo.getExtra()!=null)
        {
            timeUnitItem.setVisibility(currentDeviceInfo.getExtra().isTimeFormatViewEnable()?View.VISIBLE:View.GONE);
        }

        weightUnitItem = new ItemUnit(ilAboutAppWeightUnit);
        weightUnitItem.setTitle(R.string.content_unit_weight);
        weightUnitItem.setLeftText(R.string.unit_kg);
        weightUnitItem.setRightText(R.string.unit_pounds);
        weightUnitItem.setOnCheckedChangeListener(onItemUnitCheckedChangeListener);
    }

    /**
     * 开关监听
     */
    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (bean == null) return;
            View view = (View) buttonView.getParent();
            switch (view.getId()) {
                case R.id.ilAboutAppAutomaticSync:
                    bean.setAutoSyncDeviceData(isChecked);
                    break;
            }
            //存储变化的数据
            getPresenter().requestChangeDeviceConfigData(bean);
        }
    };

    private ItemUnit.OnCheckedChangeListener onItemUnitCheckedChangeListener = new ItemUnit.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(ItemUnit item, int position) {
            if (bean == null) return;
            UnitConfig unitConfig = bean.getUnitConfig();
            if (unitConfig == null) return;

            if (item == distanceUnitItem) {
                unitConfig.setDistanceUnit(position == 0 ? UnitConfig.DISTANCE_KM : UnitConfig.DISTANCE_MILES);
                //设置等会UI需要刷新
                UIRefresh.setIsNeedRefreshTrackItem(true);
            } else if (item == temperatureUnitItem) {
                unitConfig.setTemperatureUnit(position == 0 ? UnitConfig.TEMPERATURE_C : UnitConfig.TEMPERATURE_F);
            } else if (item == timeUnitItem) {
                unitConfig.setTimeUnit(position == 0 ? UnitConfig.TIME_24 : UnitConfig.TIME_12);
            } else if (item == weightUnitItem) {
                unitConfig.setWeightUnit(position == 0 ? UnitConfig.WEIGHT_KG : UnitConfig.WEIGHT_LB);
            }
            getPresenter().requestChangeDeviceConfigData(bean);
        }
    };

    @Override
    public void onDialogLoading(String msg) {
        LoadingDialog.show(this, msg);
    }

    @Override
    public void onDialogDismiss() {
        LoadingDialog.dismiss();
    }

    @Override
    public void onUpdateStatusChange(boolean isNeedUpdate, String newVersion, String localVersion, String description) {
        currentVersionItem.setContent(getString(R.string.unit_version, localVersion));
        lastVersionItem.setContent(getString(R.string.unit_version, newVersion));
        if (isNeedUpdate) {
            CommonDialog.create(this,
                    getString(R.string.content_new_app_version),
                    description,
                    getString(R.string.content_cancel),
                    getString(R.string.content_update),
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
                            getPresenter().requestStartUpdateApp();
                            mAppUpdateDialog = new ProgressDialog(AboutAppActivity.this);
                            mAppUpdateDialog.setMax(100);
                            mAppUpdateDialog.setProgress(0);
                            mAppUpdateDialog.show();
                        }
                    }
            ).show();

        } else {
            SNToast.toast(getString(R.string.content_current_is_last));
        }
    }

    @Override
    public void onUpdateSuccessful(File file) {
        if (mAppUpdateDialog != null) {
            mAppUpdateDialog.dismiss();
            mAppUpdateDialog = null;
        }
        AppVersionUpdateHelper.startInstall(this, file);
    }

    @Override
    public void onUpdateProgress(final int progress) {

        if (mAppUpdateDialog != null) {
            mAppUpdateDialog.setProgress(progress);
        }
    }

    @Override
    public void onUpdateDeviceConfig(DeviceConfigBean bean) {
        this.bean = bean;

        automaticSyncItem.setSwitchCheck(bean.isAutoSyncDeviceData());

        UnitConfig unitConfig = bean.getUnitConfig();
        if (unitConfig == null) return;


        //刷新单位配置
        switch (unitConfig.distanceUnit) {
            case UnitConfig.DISTANCE_MILES:
                distanceUnitItem.setRightChecked(true);
                break;
            case UnitConfig.DISTANCE_KM:
                distanceUnitItem.setLeftChecked(true);
                break;
        }
        switch (unitConfig.temperatureUnit) {
            case UnitConfig.TEMPERATURE_F:
                temperatureUnitItem.setRightChecked(true);
                break;
            case UnitConfig.TEMPERATURE_C:
                temperatureUnitItem.setLeftChecked(true);
                break;
        }
        switch (unitConfig.timeUnit) {
            case UnitConfig.TIME_12:
                timeUnitItem.setRightChecked(true);
                break;
            case UnitConfig.TIME_24:
                timeUnitItem.setLeftChecked(true);
                break;
        }
        switch (unitConfig.weightUnit) {
            case UnitConfig.WEIGHT_LB:
                weightUnitItem.setRightChecked(true);
                break;
            case UnitConfig.WEIGHT_KG:
                weightUnitItem.setLeftChecked(true);
                break;
        }

    }


    @OnClick({R.id.ivAboutAppBack, R.id.tvAboutAppCheckUpdate})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivAboutAppBack:
                onBackPressed();
                break;
            case R.id.tvAboutAppCheckUpdate:
                getPresenter().requestCheckVersion();
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAppUpdateDialog != null) {
            mAppUpdateDialog.dismiss();
            mAppUpdateDialog = null;
        }
    }

}
