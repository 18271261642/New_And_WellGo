package com.truescend.gofit.pagers.health;

import com.sn.blesdk.ble.SNBLEHelper;
import com.sn.blesdk.cmd.SNCMD;
import com.sn.blesdk.utils.eventbus.SNBLEEvent;
import com.sn.utils.eventbus.SNEvent;
import com.sn.utils.eventbus.SNEventBus;
import com.truescend.gofit.pagers.base.BasePresenter;
import com.truescend.gofit.utils.CountTimer;
import com.truescend.gofit.utils.ResUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者:东芝(2018/1/31).
 * 功能:我的体检
 */
public class HealthCheckPresenterImpl extends BasePresenter<IHealthCheckContract.IView> implements IHealthCheckContract.IPresenter, CountTimer.OnCountTimerListener {
    private IHealthCheckContract.IView view;
    public static final int TYPE_HEART_RATE = 0;
    public static final int TYPE_BLOOD_PRESSURE = 1;
    public static final int TYPE_BLOOD_OXYGEN = 2;
    private List<Integer> diastolicList = new ArrayList<>();
    private List<Integer> systolicList = new ArrayList<>();
    private List<Integer> bloodOxygenList = new ArrayList<>();
    private List<Integer> heartRateList = new ArrayList<>();
    private int curr_type;

    public HealthCheckPresenterImpl(IHealthCheckContract.IView view) {
        this.view = view;
    }


    /**
     * 计时器, 延时60秒执行
     */
    private CountTimer timer = new CountTimer(60 * 1000, 0, this);

    @Override
    public void onCountTimerChanged(long millisecond) {
        timer.stop();//执行一次马上停止
        switch (curr_type) {
            case TYPE_HEART_RATE:
                requestStartHealthCheck(TYPE_HEART_RATE, false, true);
                break;
            case TYPE_BLOOD_OXYGEN:
                requestStartHealthCheck(TYPE_BLOOD_OXYGEN, false, true);
                break;
            case TYPE_BLOOD_PRESSURE:
                requestStartHealthCheck(TYPE_BLOOD_PRESSURE, false, true);
                break;
        }

    }

    /**
     * @param type    体检类型 这个则是 TabLayout的 position, 第0  为心率, 1为血压 2为血氧
     * @param on      体检开关
     * @param isTimer 是正常结束还是人为结束
     */
    @Override
    public void requestStartHealthCheck(int type, boolean on, boolean isTimer) {
        //TODO 这里的状态  不应该交给app开发者来确定1和0 是开 还是关,因为这是协议了, 所以后面要统一改接口setXXXStatus
        //TODO 以后这些要抽成 一个类,应用层不应该这样发送蓝牙值,然后设置监听来回调
        curr_type = type;
        switch (type) {
            case TYPE_HEART_RATE:
                if (on) {
                    heartRateList.clear();
                    SNBLEHelper.sendCMD(SNCMD.get().setHeartRateStatus(1));
                } else {
                    SNBLEHelper.sendCMD(SNCMD.get().setHeartRateStatus(0));
                }
                break;
            case TYPE_BLOOD_OXYGEN:
                if (on) {
                    bloodOxygenList.clear();
                    SNBLEHelper.sendCMD(SNCMD.get().setBloodOxygenStatus(1));
                } else {
                    SNBLEHelper.sendCMD(SNCMD.get().setBloodOxygenStatus(0));
                }
                break;
            case TYPE_BLOOD_PRESSURE:
                if (on) {
                    diastolicList.clear();
                    systolicList.clear();
                    SNBLEHelper.sendCMD(SNCMD.get().setBloodPressureStatus(1));
                } else {
                    SNBLEHelper.sendCMD(SNCMD.get().setBloodPressureStatus(0));
                }
                break;
        }
        if (on) {
            timer.start();
            view.onHealthCheckStarted();
        } else {
            timer.stop();
            if (isTimer) {
                view.onHealthCheckStopped(false);
            } else {
                view.onHealthCheckStopped(true);
            }
        }
    }

    @Override
    public void requestGetHealthCheckLastValue(int type) {

        switch (type) {
            case TYPE_HEART_RATE:
                view.onUpdateHealthCheckHeartRate(formatHeartRate(getListAvgValue(heartRateList)));
                break;
            case TYPE_BLOOD_OXYGEN:
                view.onUpdateHealthCheckBloodOxygen(formatBloodOxygen(getListAvgValue(bloodOxygenList)));
                break;
            case TYPE_BLOOD_PRESSURE:
                view.onUpdateHealthCheckBloodPressure(formatBloodPressure(getListAvgValue(diastolicList), getListAvgValue(systolicList)));
                break;
        }
    }


    /**
     * 取得List所有int的平均值
     *
     * @param list
     * @return
     */
    private int getListAvgValue(List<Integer> list) {
        int all = 0;
        for (Integer val : list) {
            all += val;
        }
        if (all == 0) {
            return 0;
        }
        return Math.round(all * 1.0f / list.size());
    }


    private String formatBloodOxygen(int bloodOxygen) {
        return bloodOxygen == 0 ? "--" : String.valueOf(bloodOxygen);
    }


    private String formatHeartRate(int heartRate) {
        return heartRate == 0 ? "--" : String.valueOf(heartRate);
    }

    private String formatBloodPressure(int diastolic, int systolic) {
        return (diastolic == 0 && systolic == 0) ? "--" : ResUtil.format("%02d/%02d", diastolic, systolic);
    }


    @Override
    protected void onCreate() {
        super.onCreate();
        SNEventBus.register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SNEventBus.unregister(this);
    }

    //TODO 以后这些要抽成 一个类, 应用层不应该这样接收蓝牙值 而是接口callback
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventReceived(SNEvent event) {
        switch (event.getCode()) {
            case SNBLEEvent.EVENT_BLE_HEALTH_CHECK_HEART_RATE:
                int heartRate = (int) event.getData();
                heartRateList.add(heartRate);
                view.onUpdateHealthCheckHeartRate(formatHeartRate(heartRate));
                break;

            case SNBLEEvent.EVENT_BLE_HEALTH_CHECK_BLOOD_OXYGEN:
                int bloodOxygen = (int) event.getData();
                bloodOxygenList.add(bloodOxygen);
                view.onUpdateHealthCheckBloodOxygen(formatHeartRate(bloodOxygen));
                break;

            case SNBLEEvent.EVENT_BLE_HEALTH_CHECK_BLOOD_PRESSURE:
                int[] data = (int[]) event.getData();
                int diastolic = data[0];
                int systolic = data[1];

                diastolicList.add(diastolic);
                systolicList.add(systolic);
                view.onUpdateHealthCheckBloodPressure(formatBloodPressure(diastolic, systolic));
                timer.stop();
                view.onHealthCheckStopped(false);
                SNBLEHelper.sendCMD(SNCMD.get().setBloodPressureStatus(0));
                break;

        }

    }


}
