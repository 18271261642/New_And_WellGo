package com.truescend.gofit.pagers.device.clock.add;

import com.sn.app.db.data.clock.AlarmClockBean;
import com.sn.app.db.data.clock.AlarmClockDao;
import com.sn.app.utils.AppUserUtil;
import com.sn.blesdk.ble.SNBLEHelper;
import com.sn.blesdk.cmd.SNCMD;
import com.sn.blesdk.utils.DataAnalysisUtil;
import com.sn.utils.DateUtil;
import com.truescend.gofit.pagers.base.BasePresenter;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

/**
 * Author:Created by 泽鑫 on 2018/1/18 14:29.
 */

public class AddAlarmClockPresenterImpl extends BasePresenter<IAddAlarmClockContract.IView> implements IAddAlarmClockContract.IPresenter {
    private IAddAlarmClockContract.IView view;
    private AlarmClockDao alarmClockDao = AlarmClockDao.get(AlarmClockDao.class);


    public AddAlarmClockPresenterImpl(IAddAlarmClockContract.IView view) {
        this.view = view;
    }



    @Override
    public void requestEditAlarmClock(int id) {
        AlarmClockBean clockBean = alarmClockDao.queryForOne(id);
        view.updateAlarmClockBean(clockBean);
    }

    @Override
    public void requestUpdateAlarmClock(AlarmClockBean alarmClockBean) {
        int userId = AppUserUtil.getUser().getUser_id();
        try {
            alarmClockDao.insertOrUpdate(alarmClockBean, userId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        changeAlarmClock();
    }

    /**
     * 修改闹钟的状态
     */
    private void changeAlarmClock(){
        int i = 0;
        List<AlarmClockBean> list = alarmClockDao.queryForSend(true);
        for (AlarmClockBean alarmClockBean : list) {
            int cycle = DataAnalysisUtil.getWeekCycle(alarmClockBean.getWeek());
            int statue = alarmClockBean.isSwitchStatues() ? 1 : 0;
            int hour = 0;
            int minute = 0;
            try {
                hour = DateUtil.getHour(DateUtil.convertStringToLong(DateUtil.YYYY_MM_DD_HH_MM, alarmClockBean.getDate()));
                minute = DateUtil.getMinute(DateUtil.convertStringToLong(DateUtil.YYYY_MM_DD_HH_MM, alarmClockBean.getDate()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            SNBLEHelper.sendCMD(SNCMD.get().setAlarmReminderInfo(i, statue, cycle, hour, minute, 1));
            i++;
        }
        for (int j = 0; j < 5 - list.size(); j++){
            SNBLEHelper.sendCMD(SNCMD.get().setAlarmReminderInfo(i, 0, 128, 0, 0, 1));
            i++;
        }

    }

}
