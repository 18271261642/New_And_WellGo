package com.truescend.gofit.service;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;
import androidx.annotation.Nullable;
import android.view.KeyEvent;

import com.sn.app.net.AppNetReq;
import com.sn.app.net.callback.OnResponseListener;
import com.sn.app.net.data.app.bean.WeatherBean;
import com.sn.app.net.data.app.bean.WeatherListBean;
import com.sn.app.storage.UserStorage;
import com.sn.app.storage.WeatherStorage;
import com.sn.blesdk.ble.SNBLEHelper;
import com.sn.blesdk.cmd.SNCMD;
import com.sn.blesdk.utils.eventbus.SNBLEEvent;
import com.sn.utils.DateUtil;
import com.sn.utils.IF;
import com.sn.utils.SNLog;
import com.sn.utils.eventbus.SNEvent;
import com.sn.utils.eventbus.SNEventBus;
import com.truescend.gofit.R;
import com.truescend.gofit.service.sync.SyncDataHelper;
import com.truescend.gofit.utils.AppDataNotifyUtil;
import com.truescend.gofit.utils.AppEvent;
import com.truescend.gofit.utils.BandCallPhoneNotifyUtil;
import com.truescend.gofit.utils.ContactsUtil;
import com.truescend.gofit.utils.CountTimer;
import com.truescend.gofit.utils.GoogleFitTool;
import com.truescend.gofit.utils.MusicControlUtil;
import com.truescend.gofit.utils.UnreadMessages;
import com.truescend.gofit.utils.UploadGoogleFitHelper;
import com.truescend.gofit.utils.UploadTodayDataToMyFriendsSeeHelper;
import com.truescend.gofit.utils.VolumeControlUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import retrofit2.Call;

/**
 * 功能：主服务
 * 常驻，负责处理app中各种耗时杂事
 * Author:Created by 泽鑫 on 2018/2/24 11:44.
 * 作者:东芝
 * 功能:位置定位服务,事件接收器
 */

public class MainService extends Service implements CountTimer.OnCountTimerListener, LocationServiceHelper.OnLocationListener {
    private LocationServiceHelper mLSHelper;
    private CountTimer countTimer = new CountTimer(60 * 1000L, this);
    private Call<WeatherListBean> weatherListBeanCall;
    private UploadGoogleFitHelper mGLFitHelper;
    private long minutes;

    private String date;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mLSHelper = new LocationServiceHelper(this);
        mGLFitHelper = new UploadGoogleFitHelper(this);
        mLSHelper.setOnLocationListener(this);

        SNEventBus.register(this);
        countTimer.start();
        //刚开始定位一次并拿到天气信息的原因是:
        //1.等会自动连接设备后能第一时间给设备发送天气数据,而不用重新等待天气请求成功
        //2.防止用户一打开app马上进去地图轨迹界面,无法即时拿到天气的尴尬
        startLocation();

        //8.0以上要求在5秒内运行这个
        AppDataNotifyUtil.updateNotificationTitle(this, getString(R.string.app_name), "--");

        minutes = 0;
    }


    /**
     * 一分钟回调一次
     *
     * @param millisecond
     */
    @Override
    public void onCountTimerChanged(long millisecond) {

        date = DateUtil.getDate(DateUtil.HH_MM, System.currentTimeMillis());

        long lastDeviceSyncTime = UserStorage.getLastDeviceSyncTime();
        long nextDeviceSyncTime = lastDeviceSyncTime + SyncDataHelper.SYNC_TIME_INTERVAL * 60 * 1000;
        long nextMinutes = SyncDataHelper.SYNC_TIME_INTERVAL - (nextDeviceSyncTime - System.currentTimeMillis()) / 1000 / 60;
        if (System.currentTimeMillis() > nextDeviceSyncTime) {

        } else if (this.minutes == 0) {
            //minutes修正
            this.minutes = nextMinutes;
            SNLog.i("定时器:上次同步时间:%s,30分钟后将会同步", DateUtil.getDate(DateUtil.YYYY_MM_DD_HH_MM_SS,lastDeviceSyncTime));
        }

        //每半小时设备数据同步
        if (this.minutes == 0 || this.minutes % SyncDataHelper.SYNC_TIME_INTERVAL == 0) {
            SNLog.i("定时器:设备数据同步");
            SNEventBus.sendEvent(AppEvent.EVENT_USER_REQUEST_SYNC_DEVICE_DATA, true);

        }

        //每6小时 同步一次天气
        if (this.minutes == 0 || this.minutes % 360 == 0) {
            SNLog.i("定时器:定位同步天气");
            //重置上次请求时间 防止这个值导致无法即时准点更新天气
            SNEventBus.sendEvent(AppEvent.EVENT_USER_REQUEST_SYNC_WEATHER_DATA);
        }


        //每小时刷新一次 好友消息
        if (this.minutes == 0 || this.minutes % 60 == 0) {
            SNLog.i("定时器:好友消息");
            SNEventBus.sendEvent(AppEvent.EVENT_USER_UNREAD_MESSAGE_NUMBER, true);
        }

        //每4小时定时GoogleFit同步
        switch (date) {
            case "00:00":
            case "04:00":
            case "08:00":
            case "12:00":
            case "16:00":
            case "20:00":
                final boolean hasPermission = GoogleFitTool.hasPermission(this);
                SNLog.i("定时器:GoogleFit同步");
                if (hasPermission && mGLFitHelper != null) {
                    mGLFitHelper.startAutoSync();
                }
                break;
        }


        this.minutes++;
    }

    private void startLocation() {
        //重复定位 大于1小时的,重新定位
        if (System.currentTimeMillis() - WeatherStorage.getLastWeatherTime() > 60 * 60 * 1000) {
            if (mLSHelper != null) {
                mLSHelper.startLocation();
            }
        } else {
            //小于1小时的 不请求天气,直接使用上一次的天气 减少天气API压力
            WeatherListBean weatherListBean = WeatherStorage.getWeatherListBean();
            if (weatherListBean != null) {
                sendWeatherToDevices(weatherListBean);
                return;
            }
            //无天气数据则重新定位并获取
            if (mLSHelper != null) {
                mLSHelper.startLocation();
            }
        }
    }


    @Override
    public void onLocationChanged(final String city, double latitude, double longitude) {
        if (mLSHelper != null) {
            mLSHelper.pause();
        }
        if (city == null) return;

        WeatherStorage.setCity(city);
        WeatherStorage.setLatitude(latitude);
        WeatherStorage.setLongitude(longitude);

        WeatherListBean weatherListBean = WeatherStorage.getWeatherListBean();
        //重复定位 大于1小时的,重新定位,  没有历史数据的第一次 也重新定位
        if (weatherListBean == null || (System.currentTimeMillis() - WeatherStorage.getLastWeatherTime() > 60 * 60 * 1000)) {
            SNLog.i("天气服务:获取["+city+"]的天气...");
            weatherListBeanCall = AppNetReq.getApi().requestWeatherMulti(latitude, longitude, city);
            weatherListBeanCall.enqueue(new OnResponseListener<WeatherListBean>() {
                int errRetry = 0;

                @Override
                public void onResponse(WeatherListBean body) throws Throwable {
                    WeatherStorage.setLastWeatherTime(System.currentTimeMillis());
                    WeatherStorage.setWeatherListBean(body);
                    SNEventBus.sendEvent(AppEvent.EVENT_SYNC_WEATHER_DATA_SUCCESS);
                    sendWeatherToDevices(body);
                    SNLog.i("天气服务:获取天气成功:"+body);
                }

                @Override
                public void onFailure(int ret, String msg) {
                    errRetry++;
                    //错误 重试2次
                    if (errRetry <= 2) {
                        if (!weatherListBeanCall.isExecuted()) {
                            weatherListBeanCall.enqueue(this);
                        }
                    } else {
                        errRetry = 0;
                        //实在拿不到天气,则提交上次本地存储的天气数据
                        sendWeatherToDevices(WeatherStorage.getWeatherListBean());
                    }
                }
            });
        } else {
            sendWeatherToDevices(weatherListBean);
        }
    }

    private void sendWeatherToDevices(WeatherListBean weatherListBean) {
        if (weatherListBean != null) {
            List<WeatherBean.DataBean> datas = weatherListBean.getData();
            if (!IF.isEmpty(datas)) {
                if (SNBLEHelper.isConnected()) {
                    //PS: 为了兼容老设备 倒序遍历
                    for (int dayOffset = datas.size() - 1; dayOffset >= 0; dayOffset--) {
                        WeatherBean.DataBean data = datas.get(dayOffset);
                        if (data != null) {
                            SNBLEHelper.sendCMD(SNCMD.get().setWeatherInfo(data.getCond_code_type(), data.getTmp(), data.getTmp_max(), data.getTmp_min(), dayOffset));
                        }
                    }
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventReceived(SNEvent event) {
        switch (event.getCode()) {
            case SNBLEEvent.EVENT_BLE_HANG_UP_THE_PHONE:
                ContactsUtil.rejectCall(this);
                break;
            case SNBLEEvent.EVENT_BLE_MUTE_CALLS:
                ContactsUtil.modifyingVolume(this, true);
                break;
            case SNBLEEvent.EVENT_BLE_BAND_CALL_PHONE:
                BandCallPhoneNotifyUtil.startNotification(this);
                break;
            case AppEvent.EVENT_UPDATED_NOTIFICATION_TITLE:
                AppDataNotifyUtil.updateNotificationTitle(this, getString(R.string.app_name), (String) event.getData());
                break;
            case AppEvent.EVENT_SYNC_DEVICE_ALL_DATA_SUCCESS:
                startLocation();
                uploadDataToMyFriendsSee();
                break;
            case SNBLEEvent.EVENT_DEVICE_MUSIC_START_OR_PAUSE:
                MusicControlUtil.sendKeyEvents(this, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
                break;
            case SNBLEEvent.EVENT_DEVICE_MUSIC_NEXT:
                MusicControlUtil.sendKeyEvents(this, KeyEvent.KEYCODE_MEDIA_NEXT);
                break;

            case SNBLEEvent.EVENT_DEVICE_MUSIC_PREVIOUS:
                MusicControlUtil.sendKeyEvents(this, KeyEvent.KEYCODE_MEDIA_PREVIOUS);
                break;
            case SNBLEEvent.EVENT_DEVICE_MUSIC_VOLUME_UP:
                VolumeControlUtil.setVolumeUp(this);
                break;
            case SNBLEEvent.EVENT_DEVICE_MUSIC_VOLUME_DOWN:
                VolumeControlUtil.setVolumeDown(this);
                break;
            case AppEvent.EVENT_USER_UNREAD_MESSAGE_NUMBER:
                UnreadMessages.refreshUnreadNumber();
                break;
            case AppEvent.EVENT_USER_REQUEST_SYNC_WEATHER_DATA:
                WeatherStorage.setLastWeatherTime(0L);
                startLocation();
                break;

        }
    }

    /**
     * 上传数据给我的朋友看
     */
    private void uploadDataToMyFriendsSee() {
        UploadTodayDataToMyFriendsSeeHelper.upload();
    }

    /**
     * 系统配置改变监听 这里监听的是系统语言变化
     *
     * @param newConfig
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mLSHelper != null) {
            mLSHelper.refreshLocationType();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLSHelper != null) {
            mLSHelper.pause();
            mLSHelper.close();
        }

        SNEventBus.unregister(this);
        countTimer.stop();
        if (weatherListBeanCall != null && !weatherListBeanCall.isCanceled()) {
            weatherListBeanCall.cancel();
        }
    }


}
