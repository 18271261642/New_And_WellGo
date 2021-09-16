package com.truescend.gofit.pagers.device;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.sn.app.db.data.schedule.ScheduleBean;
import com.sn.app.db.data.schedule.ScheduleDao;
import com.sn.app.utils.AppUserUtil;
import com.sn.blesdk.ble.DeviceType;
import com.sn.blesdk.ble.SNBLEControl;
import com.sn.blesdk.ble.SNBLEHelper;
import com.sn.blesdk.cmd.SNCMD;
import com.sn.blesdk.storage.DeviceStorage;
import com.sn.blesdk.utils.eventbus.SNBLEEvent;
import com.sn.utils.IF;
import com.sn.utils.eventbus.SNEvent;
import com.sn.utils.eventbus.SNEventBus;
import com.sn.utils.task.SNAsyncTask;
import com.sn.utils.task.SNVTaskCallBack;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BasePresenter;
import com.truescend.gofit.utils.ResUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * 功能:设备页面数据交互处理
 * Author:Created by 泽鑫 on 2017/11/20 09:40.
 */
public class DevicePresenterImpl extends BasePresenter<IDeviceContract.IView> implements IDeviceContract.IPresenter {
    private IDeviceContract.IView view;
    private int num;
    private boolean isUnbind;
    private boolean connected;

    public DevicePresenterImpl(IDeviceContract.IView view) {
        this.view = view;
    }


    @Override
    public void requestGetDeviceInfo() {
        if(isUIEnable()) {
            if (view != null) {
                view.updateDeviceInfo(DeviceType.getCurrentDeviceInfo());
            }
        }
    }

    @Override
    public void requestGetBandName() {
        boolean connected = SNBLEHelper.isConnected();
        String deviceName = null;
        try {
            deviceName = SNBLEHelper.getDeviceName();
        } catch (NullPointerException ignored) {
        }
        if (IF.isEmpty(deviceName)) {
            deviceName = DeviceType.getDeviceName();
        }
        if (connected) {
            deviceName=ResUtil.getString(R.string.title_connected)+" "+deviceName;
        } else {
            deviceName=deviceName+" "+ResUtil.getString(R.string.content_scanning);
        }
        if(isUIEnable()) {
            if (view != null) {
                view.updateBandName(deviceName);
            }
        }


    }

    @Override
    public void requestGetMacAddress() {

        if (SNBLEHelper.isConnected()) {
            if(isUIEnable()) {
                if (view != null) {
                    view.updateMacAddress(SNBLEHelper.getDeviceMacAddress());
                }
            }
        } else {
            if(isUIEnable()) {
                if (view != null) {
                    view.updateMacAddress(DeviceType.getDeviceMac());
                }
            }
        }
    }

    @Override
    public void requestGetElectric() {
        if (SNBLEHelper.isConnected()) {
            SNBLEHelper.sendCMD(SNCMD.get().getDeviceInfoCmd0());
        }
        int[] electric = DeviceStorage.getDeviceLastElectric();
        if(isUIEnable()) {
            if (view != null) {
                view.updateElectric(electric[0], electric[1], electric[2]);
            }
        }
    }

    @Override
    public void requestUnreadSchedule() {
        SNAsyncTask.execute(new SNVTaskCallBack() {
            @Override
            public void run() throws Throwable {
                ScheduleDao scheduleDao = ScheduleDao.get(ScheduleDao.class);
                List<ScheduleBean> list = scheduleDao.queryForUnread();
                if (!IF.isEmpty(list)) {
                    num = list.size();
                } else {
                    num = 0;
                }
            }

            @Override
            public void done() {
                if(isUIEnable()) {
                    if (view != null) {
                        view.updateUnreadMessages(num);
                    }
                }
            }
        });
    }


    @Override
    protected void onCreate() {
        super.onCreate();
        SNEventBus.register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(0);
        SNEventBus.unregister(this);
    }

    @Override
    protected void onVisible() {
        super.onVisible();
        delayChangeStatus(true);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventReceived(SNEvent event) {
        if (!isUIEnable()) {
            return;
        }
        switch (event.getCode()) {
            case SNBLEEvent.EVENT_BLE_BAND_ELECTRIC:
                byte[] buffer = (byte[]) event.getData();
                int electric = SNBLEHelper.subBytesToInt(buffer, 1, 7, 7);
                int mDeviceBatteryLevelStep = buffer[11] & 0xFF;
                int maxLevelStep = (mDeviceBatteryLevelStep >> 4) & 0xFF;
                int curLevelStep = mDeviceBatteryLevelStep & 0x0F;
                DeviceStorage.setDeviceLastElectric(electric, curLevelStep, maxLevelStep);
                if (view != null) {
                    view.updateElectric(electric, curLevelStep, maxLevelStep);
                }
                break;
            case SNBLEEvent.EVENT_BLE_STATUS_BLUETOOTH_OFF:
            case SNBLEEvent.EVENT_BLE_STATUS_DISCONNECTED:
            case SNBLEEvent.EVENT_BLE_STATUS_CONNECT_FAILED:
            case SNBLEEvent.EVENT_BLE_STATUS_CONNECTED:
                delayChangeStatus(false);
                break;
        }
    }

    private void delayChangeStatus(boolean fastShow) {
        connected = SNBLEHelper.isConnected();
        isUnbind = IF.isEmpty(AppUserUtil.getUser().getMac());
        boolean enable = SNBLEControl.isBluetoothEnable();
        if ((fastShow && connected) || isUnbind || !enable) {
            mHandler.removeMessages(0);
            mHandler.sendEmptyMessageDelayed(0, 0);
        } else {
            mHandler.removeMessages(0);
            mHandler.sendEmptyMessageDelayed(0, 0/*貌似可以去掉了,界面都改了1000*/);
        }
    }

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (!isUIEnable()) return;
            switch (msg.what) {
                case 0:
                    if (view != null) {
                        view.updateDeviceStatus(connected, isUnbind);
                    }
                    break;
            }
        }
    };

}
