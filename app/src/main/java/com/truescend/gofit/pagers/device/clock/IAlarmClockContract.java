package com.truescend.gofit.pagers.device.clock;

import com.sn.app.db.data.clock.AlarmClockBean;

import java.util.List;

/**
 * Author:Created by 泽鑫 on 2017/11/21 16:32.
 */

public class IAlarmClockContract {

    interface IView {
        void updateAlarmClockItemList(List<AlarmClockBean> list);
    }

    interface IPresenter {
        void requestAlarmClockItemList();
        void requestRemoveAlarmClockItem(int id);
        void requestChangeAlarmClockStatues(int id, boolean statues);
    }
}
