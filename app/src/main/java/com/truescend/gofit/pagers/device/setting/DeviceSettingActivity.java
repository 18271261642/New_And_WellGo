package com.truescend.gofit.pagers.device.setting;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;

import com.sn.app.db.data.config.DeviceConfigBean;
import com.sn.app.db.data.config.bean.RemindConfig;
import com.sn.app.db.data.config.bean.TimeCycleSwitch;
import com.sn.blesdk.ble.DeviceType;
import com.sn.blesdk.ble.SNBLEHelper;
import com.sn.blesdk.net.bean.DeviceInfo;
import com.sn.blesdk.storage.DeviceStorage;
import com.sn.utils.SNToast;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BaseActivity;
import com.truescend.gofit.pagers.common.bean.ItemBannerButton;
import com.truescend.gofit.pagers.common.dialog.CommonDialog;
import com.truescend.gofit.pagers.common.dialog.LoadingDialog;
import com.truescend.gofit.pagers.common.dialog.TimeCyclePickerDialog;
import com.truescend.gofit.pagers.device.bean.ItemDeviceCommon;
import com.truescend.gofit.utils.PageJumpUtil;
import com.truescend.gofit.views.TitleLayout;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 功能：设备设置页面
 * Author:Created by 泽鑫 on 2017/12/16 16:53.
 */

public class DeviceSettingActivity extends BaseActivity<DeviceSettingPresenterImpl, IDeviceSettingContract.IView> implements IDeviceSettingContract.IView {
    @BindView(R.id.tlTitle)
    TitleLayout tlTitle;
    @BindView(R.id.ilDeviceSettingBandUpdate)
    View ilDeviceSettingBandUpdate;
    @BindView(R.id.ilDeviceSettingAboutApp)
    View ilDeviceSettingAboutApp;

    @BindView(R.id.ilDeviceSettingHeartDetection)
    View ilDeviceSettingHeartDetection;
    @BindView(R.id.ilDeviceSettingLightScreen)
    View ilDeviceSettingLightScreen;
    @BindView(R.id.ilDeviceSettingLostReminder)
    View ilDeviceSettingLostReminder;
    @BindView(R.id.ilDeviceSettingReset)
    View ilDeviceSettingReset;
    @BindView(R.id.ilDeviceSettingClearCache)
    View ilDeviceSettingClearCache;
    @BindView(R.id.ilDeviceSettingUnblockDevice)
    View ilDeviceSettingUnblockDevice;

    private ItemBannerButton bandUpdateItem;
    private ItemBannerButton aboutAppItem;


    private ItemDeviceCommon heartDetectionItem;
    private ItemDeviceCommon lightScreenItem;
    private ItemDeviceCommon lostReminderItem;


    private ItemBannerButton resetItem;
    private ItemBannerButton clearCacheItem;
    private ItemBannerButton unblockDeviceItem;

    private DeviceConfigBean bean;

    @Override
    protected DeviceSettingPresenterImpl initPresenter() {
        return new DeviceSettingPresenterImpl(this);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_device_setting;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        initTitle();
        initItem();
        getPresenter().requestDeviceConfig();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 初始化标题
     */
    private void initTitle() {
        tlTitle.setTitle(getString(R.string.title_device_setting));
    }

    /**
     * 初始化Item布局
     */
    private void initItem() {
        bandUpdateItem = new ItemBannerButton(ilDeviceSettingBandUpdate);
        bandUpdateItem.setTitle(R.string.content_band_update);

        aboutAppItem = new ItemBannerButton(ilDeviceSettingAboutApp);
        aboutAppItem.setTitle(R.string.content_about_app);

        heartDetectionItem = new ItemDeviceCommon(ilDeviceSettingHeartDetection);
        heartDetectionItem.setTitle(R.string.content_heart_detection);
        heartDetectionItem.setIntervalTime(R.string.content_heart_detection_tips);
        heartDetectionItem.setSettingIconVisibility(View.INVISIBLE);//暂无功能，隐藏
        heartDetectionItem.setSettingIconOnClickListener(mItemConfigClickListener);
        heartDetectionItem.setSwitchOnCheckedChangeListener(onCheckedChangeListener);
        //TODO 目前手环没有这个功能 先隐藏
        heartDetectionItem.setTimeVisibility(View.GONE);
        heartDetectionItem.setSettingIconVisibility(View.INVISIBLE);

        lightScreenItem = new ItemDeviceCommon(ilDeviceSettingLightScreen);
        lightScreenItem.setTitle(R.string.content_light_screen);
        lightScreenItem.setSettingIconOnClickListener(mItemConfigClickListener);
        lightScreenItem.setSwitchOnCheckedChangeListener(onCheckedChangeListener);
        //TODO 目前手环没有这个功能 先隐藏
        lightScreenItem.setTimeVisibility(View.GONE);

        DeviceInfo currentDeviceInfo = DeviceType.getCurrentDeviceInfo();
        if (currentDeviceInfo != null && currentDeviceInfo.isSupportBandSelfSetting()) {
            lightScreenItem.setIntervalTime(R.string.content_app_unsupport_tips);
            lightScreenItem.setIntervalTimeVisibility(View.VISIBLE);
            lightScreenItem.setItemClickable(false);
        } else {
            lightScreenItem.setIntervalTimeVisibility(View.GONE);
            lightScreenItem.setItemClickable(true);
        }
        lightScreenItem.setSettingIconVisibility(View.INVISIBLE);

        lostReminderItem = new ItemDeviceCommon(ilDeviceSettingLostReminder);
        lostReminderItem.setTitle(R.string.content_lost_reminder);
        lostReminderItem.setSettingIconVisibility(View.INVISIBLE);
        lostReminderItem.setSwitchOnCheckedChangeListener(onCheckedChangeListener);
        if(currentDeviceInfo!=null&&currentDeviceInfo.getExtra()!=null){
            ilDeviceSettingLostReminder.setVisibility(currentDeviceInfo.getExtra().isRemindLostEnable()?View.VISIBLE:View.GONE);
        }


        resetItem = new ItemBannerButton(ilDeviceSettingReset);
        resetItem.setTitle(R.string.content_reset);
        resetItem.setIconVisibility(View.INVISIBLE);

        clearCacheItem = new ItemBannerButton(ilDeviceSettingClearCache);
        clearCacheItem.setTitle(R.string.content_clean_cache);
        clearCacheItem.setIconVisibility(View.INVISIBLE);

        unblockDeviceItem = new ItemBannerButton(ilDeviceSettingUnblockDevice);
        unblockDeviceItem.setTitle(R.string.content_unblock_device);
        unblockDeviceItem.setIconVisibility(View.INVISIBLE);

    }

    @Override
    public void onUpdateDeviceConfig(DeviceConfigBean bean) {
        this.bean = bean;

        //防丢提醒
        RemindConfig remindConfig = bean.getRemindConfig();
        lostReminderItem.setSwitchCheck(remindConfig.isRemindLost());
        //心率自动检测
        TimeCycleSwitch heartRateAutoCheckConfig = bean.getHeartRateAutoCheckConfig();
        heartDetectionItem.setSwitchCheck(heartRateAutoCheckConfig.isOn());
        //TODO 设备暂不支持此功能
//        heartDetectionItem.setTime(String.format("%s-%s", heartRateAutoCheckConfig.getStartTime(), heartRateAutoCheckConfig.getEndTime()));
        //抬腕亮屏
        TimeCycleSwitch liftWristBrightScreenConfig = bean.getLiftWristBrightScreenConfig();
        lightScreenItem.setSwitchCheck(liftWristBrightScreenConfig.isOn());
        lightScreenItem.setTime(String.format("%s-%s", liftWristBrightScreenConfig.getStartTime(), liftWristBrightScreenConfig.getEndTime()));
    }

    @Override
    public void onShowLoading(boolean show) {
        if (show) {
            LoadingDialog.show(this, R.string.loading);
        } else {
            LoadingDialog.dismiss();
        }
    }

    @Override
    public void onUnblockBand(boolean success) {
        if (success) {
            finish();
        }
    }

    @OnClick({R.id.ilDeviceSettingBandUpdate,
            R.id.ilDeviceSettingAboutApp,
            R.id.ilDeviceSettingReset,
            R.id.ilDeviceSettingClearCache,
            R.id.ilDeviceSettingUnblockDevice})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ilDeviceSettingBandUpdate:
                if(!SNBLEHelper.isConnected()){
                    SNToast.toast(R.string.content_disconnect_band);
                    PageJumpUtil.startScanningAndBindActivity(this);
                    return;
                }
                if(DeviceStorage.getDeviceLastElectric()[0]<50){
                    SNToast.toast(R.string.toast_electric_low_ota_tips);
                    return;
                }
                PageJumpUtil.startBandUpdateActivity(this, DeviceType.getCurrentDeviceInfo(), DeviceType.getDeviceMac(), false);
                break;
            case R.id.ilDeviceSettingAboutApp:
                PageJumpUtil.startAboutAppActivity(this);
                break;

            case R.id.ilDeviceSettingReset:
                if(!SNBLEHelper.isConnected()){
                    SNToast.toast(R.string.content_disconnect_band);
                    return;
                }
                resetDialog();
                break;
            case R.id.ilDeviceSettingClearCache:
                if(!SNBLEHelper.isConnected()){
                    SNToast.toast(R.string.content_disconnect_band);
                    return;
                }
                clearCacheDialog();
                break;
            case R.id.ilDeviceSettingUnblockDevice:
                unblockBandDialog();
                break;
        }
    }

    /**
     * 设置监听
     */
    private View.OnClickListener mItemConfigClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            View view = (View) v.getParent();
            switch (view.getId()) {
                //心率自动检测
                case R.id.ilDeviceSettingHeartDetection: {
                    final TimeCycleSwitch heartRateAutoCheckConfig = bean.getHeartRateAutoCheckConfig();
                    TimeCyclePickerDialog timeCyclePickerDialog = new TimeCyclePickerDialog(DeviceSettingActivity.this, heartRateAutoCheckConfig.getStartTime(), heartRateAutoCheckConfig.getEndTime());
                    timeCyclePickerDialog.setOnSettingListener(new TimeCyclePickerDialog.OnSettingListener() {
                        @Override
                        public void onTimeChanged(String startTime, String endTime) {
                            heartRateAutoCheckConfig.setStartTime(startTime);
                            heartRateAutoCheckConfig.setEndTime(endTime);
                            //存储变化的数据
                            getPresenter().requestChangeDeviceConfigData(bean);
                        }
                    });
                    timeCyclePickerDialog.show();
                }
                break;
                //抬腕亮屏
                case R.id.ilDeviceSettingLightScreen: {
                    final TimeCycleSwitch liftWristBrightScreenConfig = bean.getLiftWristBrightScreenConfig();
                    TimeCyclePickerDialog timeCyclePickerDialog = new TimeCyclePickerDialog(DeviceSettingActivity.this, liftWristBrightScreenConfig.getStartTime(), liftWristBrightScreenConfig.getEndTime());
                    timeCyclePickerDialog.setOnSettingListener(new TimeCyclePickerDialog.OnSettingListener() {
                        @Override
                        public void onTimeChanged(String startTime, String endTime) {
                            liftWristBrightScreenConfig.setStartTime(startTime);
                            liftWristBrightScreenConfig.setEndTime(endTime);
                            //存储变化的数据
                            getPresenter().requestChangeDeviceConfigData(bean);
                        }
                    });
                    timeCyclePickerDialog.show();
                }
                break;
            }
        }
    };

    /**
     * 开关监听
     */
    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (bean == null) return;
            View view = (View) buttonView.getParent();
            switch (view.getId()) {
                //心率自动检测
                case R.id.ilDeviceSettingHeartDetection:
                    bean.getHeartRateAutoCheckConfig().setOn(isChecked);
                    break;
                //抬腕亮屏
                case R.id.ilDeviceSettingLightScreen:
                    bean.getLiftWristBrightScreenConfig().setOn(isChecked);
                    break;
                //防丢提醒
                case R.id.ilDeviceSettingLostReminder:
                    bean.getRemindConfig().setRemindLost(isChecked);
                    break;
            }
            //存储变化的数据
            getPresenter().requestChangeDeviceConfigData(bean);
        }
    };

    /**
     * 恢复出厂设置弹框
     */
    private void resetDialog() {
        CommonDialog.create(this,
                getString(R.string.content_are_you_sure),
                getString(R.string.content_want_reset),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                },
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getPresenter().requestResetBand();
                        dialogInterface.dismiss();
                    }
                }
        ).show();
    }


    /**
     * 清除缓存弹框
     */
    private void clearCacheDialog() {
        CommonDialog.create(this,
                getString(R.string.content_are_you_sure),
                getString(R.string.content_want_clear),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                },
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getPresenter().requestClearCache();
                        dialogInterface.dismiss();
                    }
                }
        ).show();
    }

    /**
     * 解绑设备弹框
     */
    private void unblockBandDialog() {
        CommonDialog.create(this,
                getString(R.string.content_are_you_sure),
                getString(R.string.content_want_unbind),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                },
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getPresenter().requestUnblockBand();
                        dialogInterface.dismiss();
                    }
                }
        ).show();
    }


}
