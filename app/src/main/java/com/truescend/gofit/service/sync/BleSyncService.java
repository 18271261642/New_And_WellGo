package com.truescend.gofit.service.sync;

import com.sn.app.storage.UserStorage;
import com.sn.blesdk.service.SNBLEService;
import com.sn.utils.eventbus.SNEvent;
import com.sn.utils.eventbus.SNEventBus;
import com.truescend.gofit.BuildConfig;
import com.truescend.gofit.utils.AppEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 作者:东芝(2018/1/21).
 * 功能:蓝牙数据同步服务
 * app数据->设备
 * 2019-05-20 先微开会 为了手环省电 改成见到界面不自动刷新, 只有一种逻辑 那就是后台30分钟才刷新一次
 * 数据传输逻辑会议总结：
 * 1.自动同步大数据取消；
 * 2.回连时，距离上次完整同步完大数据时间半个小时内不再同步：
 * 手动同步例外
 * 3.app从后台进入前台时，距离上次完整同步完大数据时间半个小时内不再同步：
 * 手动同步例外
 * 4.ios每5分钟同步大数据替换为每5分钟同步实时步数：
 * 5.只有app进入日常界面时，才启动快速传输实时步数模式，其他界面包括后台不均不启动；app进入后台时，手环每2分钟主数据（实时记步数据）：
 * 6.手环处于断开状态时，不发送任何运动数据
 */

public class BleSyncService extends SNBLEService {

    private SyncDataHelper mSyncDataHelper;
    private long lastSyncTime;


    @Override
    public void onCreate() {
        super.onCreate();

        SNEventBus.register(this);
        mSyncDataHelper = new SyncDataHelper(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SNEventBus.unregister(this);
        mSyncDataHelper.stopSync();
        System.gc();
    }

    @Override
    protected void startSyncIfNeed() {
        long localLastSyncTime = UserStorage.getLastDeviceSyncTime();
        //重复同步数据 30分钟只能拉取一次
        boolean case1 = localLastSyncTime > 0 && System.currentTimeMillis() - localLastSyncTime < (SyncDataHelper.SYNC_TIME_INTERVAL * (60 * 1000));
        //如果正在同步中 就勿请求同步
        boolean case2 = mSyncDataHelper.isSyncing();
        if (case1 || case2) {
            //直接返回成功
            mSyncDataHelper.startSyncTime();
            SNEventBus.sendEvent(AppEvent.EVENT_SYNC_DEVICE_UI_DATA_SUCCESS);
            return;
        }
        mSyncDataHelper.startSync();
    }

    @Override
    protected void startSync() {
        //重复同步数据 3分钟只能拉取一次
        boolean case1 = lastSyncTime > 0 && System.currentTimeMillis() - lastSyncTime < (3 * (60 * 1000));
        if (BuildConfig.DEBUG) {
            case1 = false;
        }
        //如果正在同步中 就勿请求同步
        boolean case2 = mSyncDataHelper.isSyncing();
        if (case1 || case2) {
            //直接返回成功
            SNEventBus.sendEvent(AppEvent.EVENT_SYNC_DEVICE_UI_DATA_SUCCESS);
            return;
        }

        lastSyncTime = System.currentTimeMillis();
        mSyncDataHelper.startSync();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventReceived(SNEvent event) {
        switch (event.getCode()) {
            case AppEvent.EVENT_USER_REQUEST_SYNC_DEVICE_DATA:
                boolean start = (boolean) event.getData();
                if (start) {
                    startSync();
                } else {
                    stopSync();
                }
                break;
        }
    }

    @Override
    protected void processSync(byte[] buffer) {
        mSyncDataHelper.syncAllData(buffer);
    }

    @Override
    protected void stopSync() {
        mSyncDataHelper.stopSync();
    }

}
