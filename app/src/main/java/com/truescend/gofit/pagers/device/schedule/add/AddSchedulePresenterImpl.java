package com.truescend.gofit.pagers.device.schedule.add;

import android.database.SQLException;

import com.sn.app.db.data.schedule.ScheduleBean;
import com.sn.app.db.data.schedule.ScheduleDao;
import com.sn.app.utils.AppUserUtil;
import com.truescend.gofit.ble.CMDCompat;
import com.truescend.gofit.pagers.base.BasePresenter;

/**
 * Author:Created by 泽鑫 on 2018/2/1 18:07.
 */

public class AddSchedulePresenterImpl extends BasePresenter<IAddScheduleContract.IView> implements IAddScheduleContract.IPresenter{
    private IAddScheduleContract.IView view;
    private ScheduleDao scheduleDao = ScheduleDao.get(ScheduleDao.class);

    public AddSchedulePresenterImpl(IAddScheduleContract.IView view){
        this.view = view;
    }

    @Override
    public void requestEditSchedule(int userId) {
        ScheduleBean scheduleBean = scheduleDao.queryForOne(userId);
        view.updateScheduleBean(scheduleBean);
    }

    @Override
    public void requestUpdateSchedule(ScheduleBean bean) {
        int userId = AppUserUtil.getUser().getUser_id();
        try {
            scheduleDao.insertOrUpdate(bean, userId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        sendConfigToDevice();
    }

    /**
     * 修改日程的状态
     */
    private void sendConfigToDevice(){
        CMDCompat.get().setScheduleReminderInfo();
    }
}
