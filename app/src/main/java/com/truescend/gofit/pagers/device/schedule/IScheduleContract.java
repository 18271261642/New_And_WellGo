package com.truescend.gofit.pagers.device.schedule;

import com.sn.app.db.data.schedule.ScheduleBean;

import java.util.List;

/**
 * Author:Created by 泽鑫 on 2018/2/1 18:07.
 */

public class IScheduleContract {
    interface IView {
        void updateScheduleItemList(List<ScheduleBean> list);
    }

    interface IPresenter {
        void requestScheduleItemList();
        void requestRemoveScheduleItem(int id);
    }
}
