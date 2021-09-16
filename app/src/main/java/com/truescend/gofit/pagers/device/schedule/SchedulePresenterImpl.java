package com.truescend.gofit.pagers.device.schedule;

import com.sn.app.db.data.schedule.ScheduleBean;
import com.sn.app.db.data.schedule.ScheduleDao;
import com.sn.utils.IF;
import com.sn.utils.task.SNAsyncTask;
import com.sn.utils.task.SNVTaskCallBack;
import com.truescend.gofit.ble.CMDCompat;
import com.truescend.gofit.pagers.base.BasePresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * Author:Created by 泽鑫 on 2018/2/1 18:07.
 */

public class SchedulePresenterImpl extends BasePresenter<IScheduleContract.IView> implements IScheduleContract.IPresenter {
    private IScheduleContract.IView view;
    private List<ScheduleBean> scheduleBeans = new ArrayList<>();
    private ScheduleDao scheduleDao;

    public SchedulePresenterImpl(IScheduleContract.IView view) {
        this.view = view;
        scheduleDao = ScheduleDao.get(ScheduleDao.class);
    }


    @Override
    public void requestScheduleItemList() {
        SNAsyncTask.execute(new SNVTaskCallBack() {
            @Override
            public void run() throws Throwable {
                scheduleBeans.clear();
                List<ScheduleBean> list = scheduleDao.queryForFuture();
                if (!IF.isEmpty(list)) {
                    scheduleBeans.addAll(list);
                }
            }

            @Override
            public void done() {
                view.updateScheduleItemList(scheduleBeans);
            }
        });
    }

    @Override
    public void requestRemoveScheduleItem(int id) {
        ScheduleBean scheduleBean = scheduleDao.queryForOne(id);
        scheduleDao.delete(scheduleBean);
        sendConfigToDevice();
    }

    /**
     * 修改日程的状态
     */
    private void sendConfigToDevice() {
        CMDCompat.get().setScheduleReminderInfo();
    }

}
