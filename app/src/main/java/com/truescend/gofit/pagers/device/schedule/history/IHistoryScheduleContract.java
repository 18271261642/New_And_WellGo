package com.truescend.gofit.pagers.device.schedule.history;

import com.sn.app.db.data.schedule.ScheduleBean;

import java.util.List;

/**
 * Author:Created by 泽鑫 on 2018/2/6 14:38.
 */

public class IHistoryScheduleContract {
    interface IView {
        void updateHistoryScheduleItemList(List<ScheduleBean> list);
    }

    interface IPresenter {
        void requestHistoryScheduleItemList();
        void requestRemoveHistoryScheduleItem(int id);
    }
}
