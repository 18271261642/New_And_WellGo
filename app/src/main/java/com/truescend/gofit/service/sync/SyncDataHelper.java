package com.truescend.gofit.service.sync;

import android.content.Context;

import com.dz.blesdk.utils.BLELog;
import com.sn.app.storage.UserStorage;
import com.sn.app.utils.AppUserUtil;
import com.sn.blesdk.ble.DeviceType;
import com.sn.blesdk.ble.SNBLEHelper;
import com.sn.blesdk.cmd.SNCMD;
import com.sn.blesdk.interfaces.ICmd;
import com.sn.blesdk.net.bean.DeviceInfo;
import com.sn.blesdk.storage.DeviceStorage;
import com.sn.blesdk.utils.DataAnalysisUtil;
import com.sn.blesdk.utils.eventbus.SNBLEEvent;
import com.sn.utils.eventbus.SNEventBus;
import com.sn.utils.task.SNAsyncTask;
import com.sn.utils.task.SNVTaskCallBack;
import com.truescend.gofit.ble.CMDCompat;
import com.truescend.gofit.utils.AppEvent;

/**
 * 作者:东芝(2017/11/21).
 * 功能:数据同步
 */

public class SyncDataHelper {
    private boolean isSynced = false;//是否已同步
    private boolean isSyncing = false;//是否同步中
    private boolean isStop;//是否已停止
    private ICmd iCmd = SNCMD.get();
    private boolean isSyncTime;
    private boolean isSyncUserInfo;
    private boolean isSyncRealTimeStep;
    private boolean isSyncStep;
    private boolean isSyncSleep;
    private boolean isSyncBandInfo;
    private boolean isSyncHeart;
    private boolean isSyncOtherInfo2;
    private boolean isSyncOtherInfo1;
    private boolean isSyncSportModel;
    private long startTime;
    public static final int SYNC_TIME_INTERVAL = 30;//同步时间间隔
    private boolean isSupportSportModeHistory;

    public SyncDataHelper(Context context) {

    }

    public boolean isSyncing() {
        return isSyncing;
    }

    public void startSyncTime() {
        isSyncTime = true;
        //1a.同步时间
        SNBLEHelper.sendCMD(iCmd.setDeviceTime());
    }
    /**
     * 开始同步
     */
    public void startSync() {
        if (SNBLEHelper.isConnected()) {   //请求使用高速的连接间隔进行传输
            SNBLEHelper.requestConnectionPriorityHigh();
        } else {
            BLELog.d("同步:失败,未连接");
            return;
        }
        isSynced = false;
        isStop = false;
        isSyncTime = false;
        isSyncUserInfo = false;
        isSyncRealTimeStep = false;
        isSyncStep = false;
        isSyncSleep = false;
        isSyncBandInfo = false;
        isSyncHeart = false;
        isSyncOtherInfo2 = false;
        isSyncOtherInfo1 = false;
        isSyncSportModel = false;
        isSupportSportModeHistory = false;
        isSyncing = true;
        //1a.同步时间
        SNBLEHelper.sendCMD(iCmd.setDeviceTime());
        BLELog.d("同步:时间同步:开始");
        startTime = System.currentTimeMillis();
    }


    /**
     * 同步所有数据
     *
     * @param buffer
     */
    public void syncAllData(byte[] buffer) {

        if (isSynced)//如果已经同步 就不需要再同步 但是如果用户切换设备会设置isSynced=false,重新重新同步,具体看start();
        {
            return;
        }
        if (isStop)//停止了同步
        {
            return;
        }
        //TODO 以后这里出现类似 安卓某种大数据丢包,而ios不丢包 的问题 ,原因是 同步顺序的问题 比如 x1plus 同步实时步数->大数据步数 时 大数据步数会丢几条前面的数据

        //1b.同步时间-完成
        if (!isSyncTime&&SNBLEHelper.startWith(buffer, "05020101")) {
            isSyncTime = true;

            if (!isSyncUserInfo) {
                BLELog.d("同步:用户信息-开始");
                //2a.同步用户信息
                CMDCompat.get().setUserInfo();
            }
        }
        //2b.同步用户信息-完成
        if (SNBLEHelper.startWith(buffer, "05020201")) {
            isSyncUserInfo = true;

            if (!isSyncRealTimeStep) {
                BLELog.d("同步:实时步数-开始");
                //3a.实时步数同步
                SNBLEHelper.sendCMD(iCmd.syncRealTimeSportData());
            }
        }
        //3b.实时步数-完成
        if (SNBLEHelper.startWith(buffer, "05070100")) {
            isSyncRealTimeStep = true;

            if (!isSyncSleep) {
                BLELog.d("同步:睡眠大数据-开始");
                //5a.开始睡眠同步
                SNBLEHelper.sendCMD(iCmd.syncHistorySleepData());
            }

        }
        //5b.睡眠同步:完成
        if (SNBLEHelper.startWith(buffer, "0507FE01") || SNBLEHelper.startWith(buffer, "07FE01")) {
            isSyncSleep = true;
            if (!isSyncStep) {
                BLELog.d("同步:步数大数据-开始");
                //4a.步数同步
                SNBLEHelper.sendCMD(iCmd.syncHistorySportData());
            }
        }

        //4b.步数大数据-完成
        if (SNBLEHelper.startWith(buffer, "0507FF01")) {
            isSyncStep = true;
            if (!isSyncBandInfo) {
                BLELog.d("同步:获取手环兼容信息-开始");
                //获取手环兼容信息
                SNBLEHelper.sendCMD(iCmd.getDeviceInfoCmd0());
            }

        }

        //解析手环兼容信息
        if (SNBLEHelper.startWith(buffer, "050102")) {
            if (!isSyncStep) {   //如果未同步步数 可能是手环事先给我发了手环兼容信息,导致同步混乱,所以此处应该 return
                return;
            }

            isSyncBandInfo = true;
            int mDeviceID = SNBLEHelper.subBytesToInt(buffer, 2, 3, 4);
            int mDeviceVersion = buffer[5] & 0xFF;
            int mDeviceBatteryStatus = buffer[6] & 0xFF;
            int mDeviceBatteryLevel = buffer[7] & 0xFF;
            int mCustomerID = SNBLEHelper.subBytesToInt(buffer, 2, 8, 9);
            //设备支持的功能定义位
            int mDeviceFeatures = buffer[10] & 0xFF;
            int mDeviceBatteryLevelStep = buffer[11] & 0xFF;
            BLELog.d("mDeviceID=" + mDeviceID);
            BLELog.d("mDeviceVersion=" + mDeviceVersion);
            BLELog.d("mDeviceBatteryStatus=" + mDeviceBatteryStatus);
            BLELog.d("mDeviceBatteryLevel=" + mDeviceBatteryLevel);
            BLELog.d("mCustomerID=" + mCustomerID);
            BLELog.d("mDeviceFeatures=" + mDeviceFeatures);
            boolean isSupportHeartRateHistory = DataAnalysisUtil.get1Bit(mDeviceFeatures, 0) == 1;
            boolean isSupportBloodOxygenHistory = DataAnalysisUtil.get1Bit(mDeviceFeatures, 1) == 1;
            boolean isSupportBloodPressureHistory = DataAnalysisUtil.get1Bit(mDeviceFeatures, 2) == 1;
            isSupportSportModeHistory = DataAnalysisUtil.get1Bit(mDeviceFeatures, 3) == 1;
            BLELog.d("心率大数据支持=" + isSupportHeartRateHistory);
            BLELog.d("血氧大数据支持=" + isSupportBloodOxygenHistory);
            BLELog.d("血压大数据支持=" + isSupportBloodPressureHistory);
            BLELog.d("运动模式支持=" + isSupportSportModeHistory);

            DeviceStorage.setDeviceCustomerId(mCustomerID);
            DeviceStorage.setDeviceVersion(mDeviceVersion);
            DeviceStorage.setDeviceUpgradeId(mDeviceID);

            SNEventBus.sendEvent(SNBLEEvent.EVENT_DEVICE_INFO_0, new int[]{mDeviceID, mDeviceVersion, mDeviceBatteryStatus, mDeviceBatteryLevel, mCustomerID, mDeviceBatteryLevelStep});
            //是否支持心率大数据?
            if (isSupportHeartRateHistory) {
                if (!isSyncHeart) {
                    BLELog.d("同步:心率大数据-开始");
                    //6a.体检-心率大数据同步
                    SNBLEHelper.sendCMD(iCmd.syncHistoryHeartRateData());
                }
            } else {
                if (!isSyncOtherInfo1) {
                    //其他设置: 抬手亮屏,防丢,心率自动检测
                    CMDCompat.get().setDeviceOtherInfo();
                }
            }
            syncNetworkUserDevice();


        }
        //6b.体检-心率大数据同步 完成
        if (SNBLEHelper.startWith(buffer, "0507FD01")) {

            isSyncHeart = true;
            if (!isSyncOtherInfo1) {
                //其他设置: 抬手亮屏,防丢,心率自动检测
                CMDCompat.get().setDeviceOtherInfo();
            }
        }
        //其他设置: 抬手亮屏,防丢,心率自动检测 成功
        if (SNBLEHelper.startWith(buffer, "05020A01")) {
            isSyncOtherInfo1 = true;

            if (isSupportSportModeHistory) {

                //同步运动模式-大数据
                if (!isSyncSportModel) {
                    BLELog.d("同步:运动模式大数据-开始");
                    SNBLEHelper.sendCMD(iCmd.syncHistorySportModelData());
                }
            } else {
                //通知UI刷新
                SNEventBus.sendEvent(AppEvent.EVENT_SYNC_DEVICE_UI_DATA_SUCCESS);
                syncOtherInfo2();
            }
        }
        //同步运动模式-大数据完成
        if (SNBLEHelper.startWith(buffer, "0507F901")) {
            isSyncSportModel = true;
            //通知UI刷新
            SNEventBus.sendEvent(AppEvent.EVENT_SYNC_DEVICE_UI_DATA_SUCCESS);
            syncOtherInfo2();
        }

    }

    private void syncNetworkUserDevice() {
        //提交上次连接的手环
        DeviceInfo deviceInfo = DeviceType.getCurrentDeviceInfo();
        if (deviceInfo != null) {
            AppUserUtil.uploadUserDevice(DeviceType.getDeviceMac(), deviceInfo.getDevice_name(), deviceInfo.getFunction(), deviceInfo.getAdv_id(),deviceInfo.getCustomid(), DeviceStorage.getDeviceUpgradeId(),
            null);
        }
    }

    /**
     * 同步各类提醒类型的设置
     */
    private void syncOtherInfo2() {
        if (!isSyncOtherInfo2) {
            //马上重置标志位,表示只允许进一次,避免下面的线程较慢导致下一次误进来时又重新发
            isSyncOtherInfo2 = true;

            SNAsyncTask.execute(new SNVTaskCallBack() {

                @Override
                public void run() throws Throwable {
                    //这里就没做监听是否成功了, 因为....有n个日程 和n个闹钟啊... 监听的话  会同一个请求回调n次,
                    //而且我的框架有[死也要发送成功]的操作,不会发送失败的,除非手环收到了却没处理 那就是手环问题了!
                    //先这样,有问题再说
                    BLELog.d("同步:日程同步:开始");
                    //7a.日程同步
                    CMDCompat.get().setScheduleReminderInfo();
                    sleep(100);
                    BLELog.d("同步:闹钟同步:开始");
                    //8a.闹钟同步
                    CMDCompat.get().setAlarmClockReminderInfo();
                    sleep(100);
                    BLELog.d("同步:喝水提醒-开始");
                    //9a.喝水提醒
                    CMDCompat.get().setDrinkReminderInfo();
                    sleep(100);
                    BLELog.d("同步:久坐提醒-开始");
                    //10a.久坐提醒
                    CMDCompat.get().setSedentaryReminderInfo();
                    sleep(100);
                    BLELog.d("同步:夜间模式设置-开始");
                    //11a.夜间模式设置
                    CMDCompat.get().setDeviceNightModeInfo();

                    //通知同步完成
                    SNEventBus.sendEvent(AppEvent.EVENT_SYNC_DEVICE_ALL_DATA_SUCCESS);
                    int offset = (int) ((System.currentTimeMillis() - startTime) / 1000);
                    BLELog.d("同步:恭喜! 全部同步操作完成,耗时: %d秒", offset);
                    //请求使用高速的连接间隔进行传输
                    if (SNBLEHelper.isConnected()) {
                        SNBLEHelper.requestConnectionPriorityBalanced();
                    }
                    isSynced = true;
                    isSyncing = false;
                    UserStorage.setLastDeviceSyncTime(System.currentTimeMillis());
                }
            });
        }
    }


    /**
     * 终止同步
     */
    public void stopSync() {
        isStop = true;
        isSyncing = false;
    }

}
