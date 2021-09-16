package com.truescend.gofit.pagers.device.health.reminder;

import com.sn.app.db.data.config.bean.HealthReminderConfig;

/**
 * Author:Created by 泽鑫 on 2018/3/17 10:36.
 */

public class ReminderContract {
    interface IView{
        void onShowReminderData(HealthReminderConfig.HealthReminder healthReminder);
    }

    interface IPresenter{
        void requestShowReminderData(@HealthReminderConfig.HealthReminder.Type int type);
        void requestUpdateReminderData(HealthReminderConfig.HealthReminder healthReminder, @HealthReminderConfig.HealthReminder.Type int type);
    }
}
