package com.truescend.gofit.pagers.device.schedule.history;

import com.sn.app.db.data.schedule.ScheduleBean;
import com.sn.app.db.data.schedule.ScheduleDao;
import com.sn.app.utils.AppUserUtil;
import com.sn.utils.IF;
import com.sn.utils.task.SNAsyncTask;
import com.sn.utils.task.SNVTaskCallBack;
import com.truescend.gofit.pagers.base.BasePresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * Author:Created by 泽鑫 on 2018/2/6 14:38.
 */

public class HistorySchedulePresenterImpl extends BasePresenter<IHistoryScheduleContract.IView> implements IHistoryScheduleContract.IPresenter{
    private IHistoryScheduleContract.IView view;
    private List<ScheduleBean> scheduleBeans = new ArrayList<>();
    private ScheduleDao scheduleDao = ScheduleDao.get(ScheduleDao.class);

    public HistorySchedulePresenterImpl(IHistoryScheduleContract.IView view){
        this.view = view;
    }

    @Override
    public void requestHistoryScheduleItemList() {
        SNAsyncTask.execute(new SNVTaskCallBack() {
            @Override
            public void run() throws Throwable {
                int userId = AppUserUtil.getUser().getUser_id();
                scheduleBeans.clear();
                List<ScheduleBean> list = scheduleDao.queryForHistory();
                if (!IF.isEmpty(list)) {
                    for (ScheduleBean bean : list){
                        if (!bean.getRead()) {
                            bean.setRead(true);
                            scheduleDao.insertOrUpdate(bean, userId);
                        }
                    }
                    scheduleBeans.addAll(list);
                }
            }

            @Override
            public void done() {
                view.updateHistoryScheduleItemList(scheduleBeans);
            }
        });
    }

    @Override
    public void requestRemoveHistoryScheduleItem(int id) {
        ScheduleBean scheduleBean = scheduleDao.queryForOne(id);
        scheduleDao.delete(scheduleBean);
    }
}
