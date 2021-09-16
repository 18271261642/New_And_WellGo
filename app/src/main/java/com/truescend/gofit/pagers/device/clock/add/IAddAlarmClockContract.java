package com.truescend.gofit.pagers.device.clock.add;

import com.sn.app.db.data.clock.AlarmClockBean;

/**
 * Author:Created by 泽鑫 on 2018/1/18 14:28.
 */

public class IAddAlarmClockContract {
    interface IView {
        void updateAlarmClockBean(AlarmClockBean alarmClockBean);
    }

    interface IPresenter {
        void requestEditAlarmClock(int userId);
        void requestUpdateAlarmClock(AlarmClockBean alarmClockBean);
    }
}
