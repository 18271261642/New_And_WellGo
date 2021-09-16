package com.truescend.gofit.pagers.device.health.reminder;

import com.sn.app.db.data.config.DeviceConfigDao;
import com.sn.app.db.data.config.bean.HealthReminderConfig;
import com.sn.app.utils.AppUserUtil;
import com.sn.blesdk.ble.SNBLEHelper;
import com.sn.blesdk.cmd.SNCMD;
import com.sn.blesdk.utils.DataAnalysisUtil;
import com.sn.utils.SNLog;
import com.sn.utils.task.SNAsyncTask;
import com.sn.utils.task.SNVTaskCallBack;
import com.truescend.gofit.pagers.base.BasePresenter;

/**
 * Author:Created by 泽鑫 on 2018/3/17 10:38.
 */

public class ReminderPresenterImpl extends BasePresenter<ReminderContract.IView> implements ReminderContract.IPresenter {
    private ReminderContract.IView view;
    private final int user_id;
    private final DeviceConfigDao deviceConfigDao;

    public ReminderPresenterImpl(ReminderContract.IView view) {
        this.view = view;
        deviceConfigDao = DeviceConfigDao.get(DeviceConfigDao.class);
        user_id = AppUserUtil.getUser().getUser_id();
    }

    @Override
    public void requestShowReminderData(final int type) {
        SNAsyncTask.execute(new SNVTaskCallBack() {
            private HealthReminderConfig.HealthReminder healthReminder;

            @Override
            public void run() throws Throwable {
                switch (type) {
                    case HealthReminderConfig.HealthReminder.TYPE_DRINK:
                        healthReminder = deviceConfigDao.queryHealthReminderConfig(user_id).getRemindDrink();
                        break;
                    case HealthReminderConfig.HealthReminder.TYPE_SEDENTARY:
                        healthReminder = deviceConfigDao.queryHealthReminderConfig(user_id).getRemindSedentary();
                        break;
                }
            }

            @Override
            public void done() {
                if (healthReminder != null) {
                    view.onShowReminderData(healthReminder);
                }
            }
        });
    }

    @Override
    public void requestUpdateReminderData(final HealthReminderConfig.HealthReminder healthReminder, final int type) {
        SNAsyncTask.execute(new SNVTaskCallBack() {
            @Override
            public void run() throws Throwable {
                HealthReminderConfig healthReminderConfig = deviceConfigDao.queryHealthReminderConfig(user_id);
                switch (type) {
                    case HealthReminderConfig.HealthReminder.TYPE_SEDENTARY:
                        healthReminderConfig.setRemindSedentary(healthReminder);
                        break;
                    case HealthReminderConfig.HealthReminder.TYPE_DRINK:
                        healthReminderConfig.setRemindDrink(healthReminder);
                        break;
                }
                sendCmd(healthReminder);
                //更新数据库
                deviceConfigDao.updateHealthReminderConfig(user_id, healthReminderConfig);
            }
        });
    }

    private void sendCmd(HealthReminderConfig.HealthReminder healthReminder) {
        int healthStatues = healthReminder.isOn() ? 1 : 0;
        int cycle = DataAnalysisUtil.getWeekCycle(healthReminder.getWeek());
        switch (healthReminder.getItemType()) {
            case HealthReminderConfig.HealthReminder.TYPE_DRINK: {
                byte[] data = SNCMD.get().setDrinkReminderInfo(
                        healthStatues,
                        cycle,
                        healthReminder.getItemStartTimeHour(),
                        healthReminder.getItemStartTimeMinute(),
                        healthReminder.getItemEndTimeHour(),
                        healthReminder.getItemEndTimeMinute(),
                        healthReminder.getItemIntervalTime());
                SNLog.e("SEND: %s", data);
                SNBLEHelper.sendCMD(data);
                break;
            }
            case HealthReminderConfig.HealthReminder.TYPE_SEDENTARY: {
                byte[] data = SNCMD.get().setSedentaryReminderInfo(
                        healthStatues,
                        cycle,
                        healthReminder.getItemStartTimeHour(),
                        healthReminder.getItemStartTimeMinute(),
                        healthReminder.getItemEndTimeHour(),
                        healthReminder.getItemEndTimeMinute(),
                        healthReminder.getItemIntervalTime());
                SNBLEHelper.sendCMD(data);
                break;
            }
        }

    }
}
