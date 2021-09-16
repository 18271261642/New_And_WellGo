package com.truescend.gofit.pagers.device.health;

import com.sn.app.db.data.config.DeviceConfigDao;
import com.sn.app.db.data.config.bean.HealthReminderConfig;
import com.sn.app.utils.AppUserUtil;
import com.sn.utils.task.SNAsyncTask;
import com.sn.utils.task.SNVTaskCallBack;
import com.truescend.gofit.ble.CMDCompat;
import com.truescend.gofit.pagers.base.BasePresenter;

/**
 * Author:Created by 泽鑫 on 2017/11/24 11:09.
 */

public class HealthReminderPresenterImpl extends BasePresenter<IHealthReminderContract.IView> implements IHealthReminderContract.IPresenter {
    private final int user_id;
    private final DeviceConfigDao deviceConfigDao;
    private IHealthReminderContract.IView view;

    public HealthReminderPresenterImpl(IHealthReminderContract.IView view) {
        this.view = view;
        deviceConfigDao = DeviceConfigDao.get(DeviceConfigDao.class);
        user_id = AppUserUtil.getUser().getUser_id();
    }


    @Override
    public void requestChangeReminderData(final boolean isOn, final int type) {
        SNAsyncTask.execute(new SNVTaskCallBack() {
            @Override
            public void run() throws Throwable {
                HealthReminderConfig healthReminderConfig = deviceConfigDao.queryHealthReminderConfig(user_id);
                switch (type) {
                    case HealthReminderConfig.HealthReminder.TYPE_SEDENTARY: {
                        HealthReminderConfig.HealthReminder healthReminder = healthReminderConfig.getRemindSedentary();
                        if (healthReminder != null) {
                            healthReminder.setOn(isOn);
                        }
                    }
                    break;
                    case HealthReminderConfig.HealthReminder.TYPE_DRINK: {
                        HealthReminderConfig.HealthReminder healthReminder = healthReminderConfig.getRemindDrink();
                        if (healthReminder != null) {
                            healthReminder.setOn(isOn);
                        }
                    }
                    break;
                }
                //更新数据库
                deviceConfigDao.updateHealthReminderConfig(user_id, healthReminderConfig);

                sendConfigToDevice(type);
            }
        });
    }


    @Override
    public void requestLoadReminderItemData() {
        SNAsyncTask.execute(new SNVTaskCallBack() {

            private HealthReminderConfig healthReminderConfig;

            @Override
            public void run() throws Throwable {
                healthReminderConfig = deviceConfigDao.queryHealthReminderConfig(user_id);
            }

            @Override
            public void done() {
                if (healthReminderConfig != null) {
                    view.onUpdateReminderItemData(healthReminderConfig.getRemindSedentary().isOn(), healthReminderConfig.getRemindDrink().isOn());
                    view.onUpdateRemindSedentaryData(
                            healthReminderConfig.getRemindSedentary().getItemStartTimeHour(),
                            healthReminderConfig.getRemindSedentary().getItemStartTimeMinute(),
                            healthReminderConfig.getRemindSedentary().getItemEndTimeHour(),
                            healthReminderConfig.getRemindSedentary().getItemEndTimeMinute(),
                            healthReminderConfig.getRemindSedentary().getItemIntervalTime()
                    );
                    view.onUpdateRemindDrinkData(
                            healthReminderConfig.getRemindDrink().getItemStartTimeHour(),
                            healthReminderConfig.getRemindDrink().getItemStartTimeMinute(),
                            healthReminderConfig.getRemindDrink().getItemEndTimeHour(),
                            healthReminderConfig.getRemindDrink().getItemEndTimeMinute(),
                            healthReminderConfig.getRemindDrink().getItemIntervalTime()
                    );

                }
            }
        });
    }

//    @Override
//    public void requestShowReminderDialog(final int type) {
//        SNAsyncTask.execute(new SNVTaskCallBack() {
//
//            private HealthReminderConfig.HealthReminder healthReminder;
//
//            @Override
//            public void run() throws Throwable {
//                switch (type) {
//                    case HealthReminderConfig.HealthReminder.TYPE_SEDENTARY:
//                        healthReminder = deviceConfigDao.queryHealthReminderConfig(user_id).getRemindSedentary();
//                        break;
//                    case HealthReminderConfig.HealthReminder.TYPE_DRINK:
//                        healthReminder = deviceConfigDao.queryHealthReminderConfig(user_id).getRemindDrink();
//                        break;
//                }
//            }
//
//            @Override
//            public void done()  {
//                if (healthReminder != null) {
//                    view.onShowReminderDialog(healthReminder, type);
//                }
//            }
//        });
//    }


    private void sendConfigToDevice(int type) {
        switch (type) {
            case HealthReminderConfig.HealthReminder.TYPE_DRINK: {
                CMDCompat.get().setDrinkReminderInfo();
                break;
            }
            case HealthReminderConfig.HealthReminder.TYPE_SEDENTARY: {
                CMDCompat.get().setSedentaryReminderInfo();
                break;
            }
        }
    }
}
