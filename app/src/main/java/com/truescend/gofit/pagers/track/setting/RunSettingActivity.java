package com.truescend.gofit.pagers.track.setting;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;

import com.sn.app.storage.MapSettingStorage;
import com.sn.app.storage.MapStorage;
import com.truescend.gofit.utils.MapType;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BaseActivity;
import com.truescend.gofit.pagers.track.bean.ItemMapSetting;
import com.truescend.gofit.pagers.track.bean.ItemRunSetting;
import com.truescend.gofit.views.CompoundGroup;
import com.truescend.gofit.views.TitleLayout;

import butterknife.BindView;

/**
 * 功能：运动设置界面
 * Author:Created by 泽鑫 on 2017/12/13 14:14.
 */

public class RunSettingActivity extends BaseActivity<RunSettingPresenterImpl, IRunSettingContract.IView> implements IRunSettingContract.IView {
    @BindView(R.id.tlTitle)
    TitleLayout tlTitle;
    @BindView(R.id.ilRunSettingScreenAlwaysOn)
    View ilRunSettingScreenAlwaysOn;
    @BindView(R.id.ilRunSettingShakeBandStart)
    View ilRunSettingShakeBandStart;
    @BindView(R.id.ilRunSettingShakeBandEnd)
    View ilRunSettingShakeBandEnd;
    @BindView(R.id.ilRunSettingShowWeatherCurrentCity)
    View ilRunSettingShowWeatherCurrentCity;

    @BindView(R.id.ilMapSettingAuto)
    View ilMapSettingAuto;
    @BindView(R.id.ilMapSettingGaodeMap)
    View ilMapSettingGaodeMap;
    @BindView(R.id.ilMapSettingGoogleMap)
    View ilMapSettingGoogleMap;

    @BindView(R.id.cgMapTypeSelector)
    CompoundGroup cgMapTypeSelector;

    private ItemRunSetting itemScreenAlwaysOn;
    private ItemRunSetting itemShakeBandStart;
    private ItemRunSetting itemShakeBandEnd;
    private ItemRunSetting itemShowWeatherCurrentCity;
    private ItemMapSetting itemMapSettingAuto;
    private ItemMapSetting itemMapSettingGaodeMap;
    private ItemMapSetting itemMapSettingGoogleMap;
    private int selectMapType;

    @Override
    protected RunSettingPresenterImpl initPresenter() {
        return new RunSettingPresenterImpl(this);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_run_setting;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        initTitle();
        initItem();
        initMapTypeSelector();
    }

    private void initMapTypeSelector() {
        itemMapSettingAuto = new ItemMapSetting(ilMapSettingAuto);
        itemMapSettingAuto.setTitle(getString(R.string.content_map_type_automatic_smart_select));

        itemMapSettingGaodeMap = new ItemMapSetting(ilMapSettingGaodeMap);
        itemMapSettingGaodeMap.setTitle(getString(R.string.content_map_type_gaode));

        itemMapSettingGoogleMap = new ItemMapSetting(ilMapSettingGoogleMap);
        itemMapSettingGoogleMap.setTitle(getString(R.string.content_map_type_google));

        selectMapType = MapStorage.getSelectMapType();
        cgMapTypeSelector.setChecked(selectMapType, true);
        cgMapTypeSelector.setOnCheckedChangeListener(new CompoundGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundGroup group, int checkedPosition) {
                if (checkedPosition == 2) {
                    boolean available = MapType.isGooglePlayServicesAvailable(RunSettingActivity.this);
                    if (!available) {
                        cgMapTypeSelector.setChecked(selectMapType, true);
                        Dialog errorDialog = MapType.getGooglePlayServicesErrorDialog(RunSettingActivity.this);
                        errorDialog.show();
                        return;
                    }
                }
                selectMapType = checkedPosition;
                MapStorage.setSelectMapType(checkedPosition);
            }
        });
    }


    /**
     * 初始化标题
     */
    private void initTitle() {
        tlTitle.setTitle(getString(R.string.title_track_setting));
    }

    /**
     * 初始化item布局
     */
    private void initItem() {
        itemScreenAlwaysOn = new ItemRunSetting(ilRunSettingScreenAlwaysOn);
        itemScreenAlwaysOn.setTitle(R.string.content_keep_screen_light);
        itemScreenAlwaysOn.setOnCheckedChangeListener(onCheckedChangeListener);


        itemShakeBandStart = new ItemRunSetting(ilRunSettingShakeBandStart);
        itemShakeBandStart.setTitle(R.string.content_shake_band_start);
        itemShakeBandStart.setOnCheckedChangeListener(onCheckedChangeListener);


        itemShakeBandEnd = new ItemRunSetting(ilRunSettingShakeBandEnd);
        itemShakeBandEnd.setTitle(R.string.content_shake_band_end);
        itemShakeBandEnd.setOnCheckedChangeListener(onCheckedChangeListener);


        itemShowWeatherCurrentCity = new ItemRunSetting(ilRunSettingShowWeatherCurrentCity);
        itemShowWeatherCurrentCity.setTitle(R.string.content_show_weather);
        itemShowWeatherCurrentCity.setOnCheckedChangeListener(onCheckedChangeListener);

        refreshItemCheckStatus();
    }

    private void refreshItemCheckStatus() {
        itemScreenAlwaysOn.setChecked(MapSettingStorage.isKeepScreenEnable());
        itemShakeBandStart.setChecked(MapSettingStorage.isBeginVibrationEnable());
        itemShakeBandEnd.setChecked(MapSettingStorage.isEndVibrationEnable());
        itemShowWeatherCurrentCity.setChecked(MapSettingStorage.isWeatherEnable());


    }

    /**
     * 开关监听
     */
    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            View view = (View) buttonView.getParent();
            switch (view.getId()) {
                case R.id.ilRunSettingScreenAlwaysOn:
                    MapSettingStorage.setKeepScreenEnable(isChecked);
                    break;
                case R.id.ilRunSettingShakeBandStart:
                    MapSettingStorage.setBeginVibrationEnable(isChecked);
                    break;
                case R.id.ilRunSettingShakeBandEnd:
                    MapSettingStorage.setEndVibrationEnable(isChecked);
                    break;
                case R.id.ilRunSettingShowWeatherCurrentCity:
                    MapSettingStorage.setWeatherEnable(isChecked);
                    break;
            }
            refreshItemCheckStatus();
        }
    };

}
