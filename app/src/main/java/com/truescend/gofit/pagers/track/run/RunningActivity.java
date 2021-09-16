package com.truescend.gofit.pagers.track.run;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps.MapView;
import com.sn.map.bean.SNLocation;
import com.sn.map.impl.GpsLocationImpl;
import com.sn.map.utils.GPSUtil;
import com.sn.map.view.SNGaoDeMap;
import com.sn.map.view.SNGoogleMap;
import com.sn.map.view.SNMapHelper;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BaseActivity;
import com.truescend.gofit.pagers.base.dialog.BaseDialog;
import com.truescend.gofit.pagers.common.dialog.CommonDialog;
import com.truescend.gofit.pagers.common.dialog.LoadingDialog;
import com.truescend.gofit.utils.AppShareUtil;
import com.truescend.gofit.utils.MapType;
import com.truescend.gofit.utils.PageJumpUtil;
import com.truescend.gofit.utils.PermissionUtils;
import com.truescend.gofit.utils.RunningSportStartAnimationHelper;
import com.truescend.gofit.utils.StatusBarUtil;
import com.truescend.gofit.views.CircleRippleButton;
import com.truescend.gofit.views.TitleLayout;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 作者:东芝(2018/2/24).
 * 功能:运动轨迹界面/功能与动画
 */
public class RunningActivity extends BaseActivity<RunningPresenterImpl, IRunningContract.IView> implements IRunningContract.IView {

    public static final String KEY_MAP_TYPE = "mapType";
    @BindView(R.id.tlTitle)
    TitleLayout tlTitle;
    @BindView(R.id.ivRunningTitleBack)
    ImageView ivRunningTitleBack;
    @BindView(R.id.ivRunningTitleWeather)
    ImageView ivRunningTitleWeather;
    @BindView(R.id.tvRunningTitleTemperature)
    TextView tvRunningTitleTemperature;
    @BindView(R.id.rlRunningTitleWeather)
    View rlRunningTitleWeather;
    @BindView(R.id.tvRunningTitleAirQuality)
    TextView tvRunningTitleAirQuality;
    @BindView(R.id.tvRunningTitleQuality)
    TextView tvRunningTitleQuality;
    @BindView(R.id.ivRunningTitleSetting)
    ImageView ivRunningTitleSetting;
    @BindView(R.id.crbRunningReadyCountdown)
    ImageView crbRunningReadyCountdown;
    @BindView(R.id.ivRunningTitleShare)
    ImageView ivRunningTitleShare;
    @BindView(R.id.ivRunningGPS)
    ImageView ivRunningGPS;
    @BindView(R.id.ivRunningCurLocation)
    ImageView ivRunningCurLocation;
    @BindView(R.id.llRunningGPS)
    View llRunningGPS;
    @BindView(R.id.crbRunningReady)
    CircleRippleButton crbRunningReady;
    @BindView(R.id.tvRunningPace)
    TextView tvRunningPace;
    @BindView(R.id.ilRunningBottom)
    View ilRunningBottom;
    @BindView(R.id.mMapContent)
    FrameLayout mMapContent;
    @BindView(R.id.mMapControl)
    RelativeLayout mMapControl;
    @BindView(R.id.tvRunningDistances)
    TextView tvRunningDistances;
    @BindView(R.id.tvRunningCalories)
    TextView tvRunningCalories;
    @BindView(R.id.tvRunningHourSpeed)
    TextView tvRunningHourSpeed;
    @BindView(R.id.tvRunningSpendTime)
    TextView tvRunningSpendTime;
    @BindView(R.id.ilRunningTitle)
    View ilRunningTitle;

    private SNMapHelper mapHelper;
    private RunningSportStartAnimationHelper animationHelper = new RunningSportStartAnimationHelper();
    private BaseDialog mDialogCantSave;

    @Override
    protected RunningPresenterImpl initPresenter() {
        return new RunningPresenterImpl(this);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_running;
    }

    @Override
    protected void onCreateActivity(final Bundle savedInstanceState) {
        StatusBarUtil.setRootViewFitsSystemWindows(this,false);
        StatusBarUtil.setStatusBarDarkTheme(this,false);
        StatusBarUtil.setStatusBarColor(this,0x00000000);

        final MapType.TYPE mapType = (MapType.TYPE) getIntent().getSerializableExtra(KEY_MAP_TYPE);
        if (mapType == null||mapType== MapType.TYPE.UNKNOWN) {
            finish();
            return;
        }
        initTitle();
        PermissionUtils.requestPermissions(this,
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                },
                new PermissionUtils.OnPermissionGrantedListener() {
                    @Override
                    public void onGranted() {
                        initMap(savedInstanceState, mapType);
                        getPresenter().requestWeatherData();
                        getPresenter().requestMapFirstLocation();
                    }

                    @Override
                    public void onDenied() {
                        finish();
                    }
                });
        getPresenter().initDefaultValue();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionUtils.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }


    @Override
    protected void onResume() {
        super.onResume();
        getPresenter().requestSettingConfig();
    }

    /**
     * 初始化标题
     */
    private void initTitle() {
        tlTitle.setTitleShow(false);

    }

    @Override
    public void onUpdateSettingConfig(boolean isKeepScreenEnable, boolean isWeatherEnable) {
        if (isFinished()) return;

        Window window = getWindow();
        if (isKeepScreenEnable) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        rlRunningTitleWeather.setVisibility(isWeatherEnable ? View.VISIBLE : View.INVISIBLE);

    }

    @Override
    public void onUpdateWeatherData(int weatherType, String weatherTemperatureRange, String weatherQuality) {
        if (isFinished()) return;

        tvRunningTitleQuality.setText(weatherQuality);
        tvRunningTitleTemperature.setText(weatherTemperatureRange);
        switch (weatherType) {
            case 0x01://晴
                ivRunningTitleWeather.setImageResource(R.mipmap.icon_weather_sunny_day);
                break;
            case 0x02://多云
                ivRunningTitleWeather.setImageResource(R.mipmap.icon_weather_cloudy);
                break;
            case 0x14://雪
                ivRunningTitleWeather.setImageResource(R.mipmap.icon_weather_snow);
                break;
            case 0x03://阴天
                ivRunningTitleWeather.setImageResource(R.mipmap.icon_weather_cloudy_day);
                break;
            case 0x07://雨
                ivRunningTitleWeather.setImageResource(R.mipmap.icon_weather_rain);
                break;
            case 0x00://无
                ivRunningTitleWeather.setImageResource(R.mipmap.icon_weather_sunny_day);
                break;

        }
    }

    @Override
    public void onUpdateMapFirstLocation(double mLatitude, double mLongitude) {
        if (isFinished()) return;
        if (mapHelper == null) return;
        //初始化地图时 显示默认的经纬度,如果没有 那就默认北京
        mapHelper.setMarkerAndMoveCamera(new SNLocation(mLatitude, mLongitude));
    }

    private void exitAlertDialog() {
        CommonDialog.create(this,
                getString(R.string.content_are_you_sure),
                getString(R.string.content_exit_run),
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
                        LoadingDialog.show(RunningActivity.this, R.string.content_save_data);
                        getPresenter().requestStopSport();
                    }
                }
        ).show();
    }

    private void gpsPrmAlertDialog() {
        CommonDialog.create(this,
                getString(R.string.content_want_exit),
                getString(R.string.content_need_open_gps),
                getString(R.string.content_leave_running),
                getString(R.string.content_open_now),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        finish();
                    }
                },
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        GPSUtil.openGpsSetting(RunningActivity.this);
                        dialogInterface.dismiss();
                    }
                }
        ).show();
    }


    /**
     * 初始化地图
     */
    private void initMap(Bundle savedInstanceState, MapType.TYPE mapType) {
        GpsLocationImpl iLocation = new GpsLocationImpl(this, 1000, 10);

        View mMapView;
        switch (mapType) {
            case A_MAP:  //在国内 使用高德
                mMapView = new MapView(this);
                mapHelper = new SNGaoDeMap(this, savedInstanceState, iLocation, (MapView) mMapView);
                break;
            case GOOGLE_MAP: //在国外使用谷歌地图
                mMapView = new com.google.android.gms.maps.MapView(this);
                mapHelper = new SNGoogleMap(this, savedInstanceState, iLocation, (com.google.android.gms.maps.MapView) mMapView);
                break;
            default:
                finish();
                return;
        }
        mMapContent.addView(mMapView, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        getPresenter().initMapListener(mapHelper);


    }

    @OnClick({R.id.ivRunningTitleBack, R.id.ivRunningTitleSetting, R.id.ivRunningTitleShare, R.id.crbRunningReady, R.id.ivRunningCurLocation})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivRunningTitleBack:
                onBackPressed();
                break;
            case R.id.ivRunningTitleSetting:
                PageJumpUtil.startRunSettingActivity(this);
                break;
            case R.id.ivRunningTitleShare:
                AppShareUtil.shareCaptureMap(RunningActivity.this, mapHelper, mMapControl);
                break;
            case R.id.crbRunningReady:
                getPresenter().requestStartSport();
                break;
            case R.id.ivRunningCurLocation:
                getPresenter().requestMapFirstLocation();
                break;
        }
    }


    @Override
    public void onUpdateGpsSignal(int signal) {
        if (isFinished()) {
            return;
        }
        switch (signal) {
            case RunningPresenterImpl.SIGNAL_WEAK:
                ivRunningGPS.setImageResource(R.mipmap.icon_gps_1);
                llRunningGPS.setBackground(getResources().getDrawable(R.drawable.item_gps_weak_bg));
                break;
            case RunningPresenterImpl.SIGNAL_MIDDLE:
                ivRunningGPS.setImageResource(R.mipmap.icon_gps_2);
                llRunningGPS.setBackground(getResources().getDrawable(R.drawable.item_gps_middle_bg));
                break;
            case RunningPresenterImpl.SIGNAL_STRONG:
                ivRunningGPS.setImageResource(R.mipmap.icon_gps_3);
                llRunningGPS.setBackground(getResources().getDrawable(R.drawable.item_gps_strong_bg));
                break;
            case RunningPresenterImpl.SIGNAL_STRONG_MAX:
                ivRunningGPS.setImageResource(R.mipmap.icon_gps_4);
                llRunningGPS.setBackground(getResources().getDrawable(R.drawable.item_gps_strong_bg));
                break;
            case RunningPresenterImpl.SIGNAL_GPS_OFF:
                gpsPrmAlertDialog();
                break;
        }
    }

    @Override
    public void onUpdateSportData(String distances, String calories, String hourSpeed, String pace) {
        tvRunningDistances.setText(distances);
        tvRunningCalories.setText(calories);
        tvRunningHourSpeed.setText(hourSpeed);
        tvRunningPace.setText(pace);

    }

    @Override
    public void onUpdateSportData(String spendTime) {
        tvRunningSpendTime.setText(spendTime);
    }

    @Override
    public void onSaveSportDataStatusChange(int code) {
        LoadingDialog.dismiss();
        switch (code) {
            case RunningPresenterImpl.CODE_COUNT_LITTLE:
                finish();
                break;
            case RunningPresenterImpl.CODE_ERROR:
            case RunningPresenterImpl.CODE_TIME_OUT:
                if(mDialogCantSave!=null&&mDialogCantSave.isShowing()){
                    mDialogCantSave.dismiss();
                }
                mDialogCantSave = CommonDialog.create(this,
                        null,
                        getString(R.string.content_err_save_sport_track_data_network_fail),
                        getString(R.string.content_donot_save), getString(R.string.content_retry),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                finish();
                            }
                        },
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                LoadingDialog.show(RunningActivity.this, R.string.content_save_data);
                                getPresenter().requestRetrySaveSportData();
                            }
                        }
                );
                mDialogCantSave.show();
                break;
            case RunningPresenterImpl.CODE_SUCCESS:
                setResult(Activity.RESULT_OK);
                finish();
                break;
        }
    }


    @Override
    public void onSportStartAnimationEnable(boolean enable) {
        if (enable) {
            animationHelper.start(crbRunningReady, (View) crbRunningReady.getParent(), crbRunningReadyCountdown, ilRunningBottom);
        }
    }

    @Override
    public void onBackPressed() {
        //没开始跑步时 用户直接点返回键 直接退出 不提示弹窗 不然很烦人
        if (mapHelper != null && !mapHelper.isStarted()) {
            finish();
        } else {
            if (mapHelper != null) {
                mapHelper.animateCameraToScreenBounds();
            }
            exitAlertDialog();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CommonDialog.dismiss();
        if (mapHelper != null) {
            mapHelper.stopSport();
        }
    }

}
