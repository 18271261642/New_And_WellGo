package com.truescend.gofit.pagers.device.schedule.add;

import com.sn.app.db.data.schedule.ScheduleBean;

/**
 * Author:Created by 泽鑫 on 2018/2/1 18:07.
 */

public class IAddScheduleContract {
    interface IView{
        void updateScheduleBean(ScheduleBean bean);
    }

    interface IPresenter{
        void requestEditSchedule(int userId);
        void requestUpdateSchedule(ScheduleBean bean);
    }
}
