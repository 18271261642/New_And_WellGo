package com.truescend.gofit.pagers.device.setting;

import com.sn.app.db.data.config.DeviceConfigBean;
import com.sn.app.db.data.config.DeviceConfigDao;
import com.sn.app.db.data.user.UserBean;
import com.sn.app.storage.UserStorage;
import com.sn.app.utils.AppUserUtil;
import com.sn.blesdk.ble.DeviceType;
import com.sn.blesdk.ble.SNBLEHelper;
import com.sn.blesdk.cmd.SNCMD;
import com.sn.blesdk.net.bean.DeviceInfo;
import com.sn.blesdk.storage.DeviceStorage;
import com.sn.utils.IF;
import com.sn.utils.SNLog;
import com.sn.utils.SNToast;
import com.sn.utils.task.SNAsyncTask;
import com.sn.utils.task.SNVTaskCallBack;
import com.truescend.gofit.pagers.base.BasePresenter;
import com.truescend.gofit.utils.WallpaperPickerUtil;

/**
 * Author:Created by 泽鑫 on 2017/12/16 16:54.
 */

public class DeviceSettingPresenterImpl extends BasePresenter<IDeviceSettingContract.IView> implements IDeviceSettingContract.IPresenter {
    private IDeviceSettingContract.IView view;

    public DeviceSettingPresenterImpl(IDeviceSettingContract.IView view) {
        this.view = view;
    }


    @Override
    public void requestResetBand() {
        SNBLEHelper.sendCMD(SNCMD.get().setDeviceReStart());
        WallpaperPickerUtil.deleteLastUserImage(DeviceType.getDeviceMac());
        UserStorage.setLastDeviceSyncTime(-1);
    }

    @Override
    public void requestClearCache() {

    }

    @Override
    public void requestUnblockBand() {

        view.onShowLoading(true);

        AppUserUtil.clearUserDevice(new AppUserUtil.OnOperationListener() {
            @Override
            public void success() {
                UserBean user = AppUserUtil.getUser();
                if (!IF.isEmpty(user.getMac())) {
//                    int user_id = user.getUser_id();
//                    String date = DateUtil.getCurrentDate(DateUtil.YYYY_MM_DD);
//                    SNBLEDao.get(SportDao.class).delete(user_id, date);
//                    SNBLEDao.get(SleepDao.class).delete(user_id, date);
//
//                    ////////////////////////////////////////////////////////////////////////////////////////
//                    //--------------------------------------心率 清除自动检测,保留手动检测的数据-----------------------------------
//                    ////////////////////////////////////////////////////////////////////////////////////////
//                    try {
//                        List<HeartRateBean> heartRateBeans = SNBLEDao.get(HeartRateDao.class).queryForDay(user_id, date);
//                        for (int i = 0; i < heartRateBeans.size(); i++) {
//                            HeartRateBean heartRateBean = heartRateBeans.get(i);
//                            ArrayList<HeartRateBean.HeartRateDetailsBean> details = heartRateBean.getHeartRateDetails();
//                            if (details != null) {
//                                for (int index = 0; index < details.size(); index++) {
//                                    if (details.get(index).getType() == 1) {
//                                        details.remove(index);
//                                        index--;
//                                    }
//                                }
//                            }
//                            int[] minAvgMaxTotal = HeartRateStorageHelper.getMinAvgMaxTotal(details);
//                            heartRateBean.setMin(minAvgMaxTotal[0]);
//                            heartRateBean.setAvg(minAvgMaxTotal[1]);
//                            heartRateBean.setMax(minAvgMaxTotal[2]);
//                            heartRateBean.setHeartRateDetails(details);
//                            SNBLEDao.get(HeartRateDao.class).insertOrUpdate(user_id, heartRateBean);
//                        }
//                    } catch (SQLException e) {
//                        e.printStackTrace();
//                    }
//                    ////////////////////////////////////////////////////////////////////////////////////////
//                    //--------------------------------------血氧 清除自动检测,保留手动检测的数据-----------------------------------
//                    ////////////////////////////////////////////////////////////////////////////////////////
//
//                    try {
//                        List<BloodOxygenBean> bloodOxygenBeans = SNBLEDao.get(BloodOxygenDao.class).queryForDay(user_id, date);
//                        for (int i = 0; i < bloodOxygenBeans.size(); i++) {
//                            BloodOxygenBean bloodOxygenBean = bloodOxygenBeans.get(i);
//                            ArrayList<BloodOxygenBean.BloodOxygenDetailsBean> details = bloodOxygenBean.getBloodOxygenDetails();
//                            if (details != null) {
//                                for (int index = 0; index < details.size(); index++) {
//                                    if (details.get(index).getType() == 1) {
//                                        details.remove(index);
//                                        index--;
//                                    }
//                                }
//                            }
//                            int[] minAvgMaxTotal = BloodOxygenStorageHelper.getMinAvgMaxTotal(details);
//                            bloodOxygenBean.setMin(minAvgMaxTotal[0]);
//                            bloodOxygenBean.setAvg(minAvgMaxTotal[1]);
//                            bloodOxygenBean.setMax(minAvgMaxTotal[2]);
//                            bloodOxygenBean.setBloodOxygenDetails(details);
//                            SNBLEDao.get(BloodOxygenDao.class).insertOrUpdate(user_id, bloodOxygenBean);
//                        }
//                    } catch (SQLException e) {
//                        e.printStackTrace();
//                    }
//                    ////////////////////////////////////////////////////////////////////////////////////////
//                    //--------------------------------------血压 清除自动检测,保留手动检测的数据-----------------------------------
//                    ////////////////////////////////////////////////////////////////////////////////////////
//                    try {
//                        List<BloodPressureBean> bloodPressureBeans = SNBLEDao.get(BloodPressureDao.class).queryForDay(user_id, date);
//                        for (int i = 0; i < bloodPressureBeans.size(); i++) {
//                            BloodPressureBean bloodPressureBean = bloodPressureBeans.get(i);
//                            ArrayList<BloodPressureBean.BloodPressureDetailsBean> details = bloodPressureBean.getBloodPressureDetails();
//                            if (details != null) {
//                                for (int index = 0; index < details.size(); index++) {
//                                    if (details.get(index).getType() == 1) {
//                                        details.remove(index);
//                                        index--;
//                                    }
//                                }
//                            }
//                            int[] bloodDiastolicMinAvgMaxTotal = BloodPressureStorageHelper.getBloodDiastolicMinAvgMaxTotal(details);
//                            int[] bloodSystolicMinAvgMaxTotal = BloodPressureStorageHelper.getBloodSystolicMinAvgMaxTotal(details);
//                            //TODO 这里好像只要平均值? 先这样写
//                            bloodPressureBean.setBloodDiastolic(bloodDiastolicMinAvgMaxTotal[1]);
//                            bloodPressureBean.setBloodSystolic(bloodSystolicMinAvgMaxTotal[1]);
//                            bloodPressureBean.setBloodPressureDetails(details);
//                            SNBLEDao.get(BloodPressureDao.class).insertOrUpdate(user_id, bloodPressureBean);
//                        }
//                    } catch (SQLException e) {
//                        e.printStackTrace();
//                    }
                    user.setMac(null);//删除mac记录
                    DeviceStorage.setDeviceMac(null);
                    //更新改变后的用户数据
                    AppUserUtil.setUser(user);
                }
                UserStorage.setLastDeviceSyncTime(-1);
                UserStorage.setHomeItemClose(null);
                UserStorage.setHomeItemOrder(null);
                if (SNBLEHelper.isConnected()) {
                    SNBLEHelper.disconnect();
                    SNBLEHelper.close();
                }
                view.onShowLoading(false);
                view.onUnblockBand(true);
            }

            @Override
            public void failed(String msg) {
                SNToast.toast(msg);
                view.onShowLoading(false);
                view.onUnblockBand(false);
            }
        });


    }

    @Override
    public void requestDeviceConfig() {

        SNAsyncTask.execute(new SNVTaskCallBack() {

            private DeviceConfigBean deviceConfigBean;

            @Override
            public void run() throws Throwable {
                DeviceConfigDao deviceConfigDao = DeviceConfigDao.get(DeviceConfigDao.class);
                deviceConfigBean = deviceConfigDao.queryForUser(AppUserUtil.getUser().getUser_id());
            }

            @Override
            public void done() {
                if (deviceConfigBean != null) {
                    view.onUpdateDeviceConfig(deviceConfigBean);
                }
            }
        });
    }

    @Override
    public void requestChangeDeviceConfigData(final DeviceConfigBean bean) {
        SNAsyncTask.execute(new SNVTaskCallBack() {
            @Override
            public void run() throws Throwable {
                DeviceConfigDao deviceConfigDao = DeviceConfigDao.get(DeviceConfigDao.class);
                boolean update = deviceConfigDao.update(bean);
                SNLog.i("设置数据变化存储:" + update);
                if (update) {
                    sendConfigToDevice(bean);
                }
            }

            @Override
            public void done() {
                super.done();
                //重新刷新界面
                if (bean != null) {
                    view.onUpdateDeviceConfig(bean);
                }
            }

            @Override
            public void error(Throwable e) {
                super.error(e);
                SNLog.i("设置数据变化存储失败:" + e);
            }
        });
    }

    private void sendConfigToDevice(DeviceConfigBean bean) {
        int power = 0;
        int light = bean.getLiftWristBrightScreenConfig().isOn() ? 1 : 0;
        boolean remindLostEnable;
        try {
            DeviceInfo.ExtraInfo extra = DeviceType.getCurrentDeviceInfo().getExtra();
            remindLostEnable = extra.isRemindLostEnable();
        } catch (Exception e) {
            remindLostEnable = true;
        }
        int antiLost = (bean.getRemindConfig().isRemindLost()&& remindLostEnable) ? 1 : 0;
        int healthAutoCheck = bean.getHeartRateAutoCheckConfig().isOn() ? 1 : 0;
        SNBLEHelper.sendCMD(SNCMD.get().setDeviceOtherInfo(power, light, antiLost, healthAutoCheck));
    }
}
