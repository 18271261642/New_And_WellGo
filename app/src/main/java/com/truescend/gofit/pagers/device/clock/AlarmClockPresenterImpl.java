package com.truescend.gofit.pagers.device.clock;

import com.sn.app.db.data.clock.AlarmClockBean;
import com.sn.app.db.data.clock.AlarmClockDao;
import com.sn.utils.DateUtil;
import com.sn.utils.IF;
import com.sn.utils.task.SNAsyncTask;
import com.sn.utils.task.SNVTaskCallBack;
import com.truescend.gofit.ble.CMDCompat;
import com.truescend.gofit.pagers.base.BasePresenter;
import com.truescend.gofit.utils.ResUtil;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Author:Created by 泽鑫 on 2017/11/21 16:35.
 */
public class AlarmClockPresenterImpl extends BasePresenter<IAlarmClockContract.IView> implements IAlarmClockContract.IPresenter {
    private IAlarmClockContract.IView view;
    private List<AlarmClockBean> clockBeans = new ArrayList<>();
    private AlarmClockDao alarmClockDao = AlarmClockDao.get(AlarmClockDao.class);

    public AlarmClockPresenterImpl(IAlarmClockContract.IView view) {
        this.view = view;
    }

    @Override
    public void requestAlarmClockItemList() {
        SNAsyncTask.execute(new SNVTaskCallBack() {
            @Override
            public void run() throws Throwable {
                alarmClockDao.queryForCloseOverdueClock();
                clockBeans.clear();
                List<AlarmClockBean> alarmClockBeans = alarmClockDao.queryForUser();
                if (!IF.isEmpty(alarmClockBeans)) {
                    clockBeans.addAll(alarmClockBeans);
                }
            }

            @Override
            public void done() {
                view.updateAlarmClockItemList(clockBeans);
            }
        });

    }

    @Override
    public void requestRemoveAlarmClockItem(int id) {
        AlarmClockBean alarmClockBean = alarmClockDao.queryForOne(id);
        alarmClockDao.delete(alarmClockBean);
    }

    @Override
    public void requestChangeAlarmClockStatues(int id, boolean statues) {
        AlarmClockBean alarmClockBean = alarmClockDao.queryForOne(id);
        if (statues) {
            updateClock(alarmClockBean);
        }
        alarmClockBean.setSwitchStatues(statues);
        alarmClockDao.update(alarmClockBean);
        sendConfigToDevice();
        requestAlarmClockItemList();
    }

    /**
     * 更改过期的单次闹钟的状态
     * @param clockBean
     */
    private void updateClock(AlarmClockBean clockBean){
        int num = 0;
        boolean week[] = clockBean.getWeek();
        for (boolean aWeek : week) {
            if (aWeek) {
                num++;
            }
        }
        long clockTime = 0;
        try {
            clockTime = DateUtil.convertStringToLong(DateUtil.YYYY_MM_DD_HH_MM, clockBean.getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (num == 0 && clockTime <= DateUtil.getCurrentDate()){
            String date = calcDate(clockBean.getDate());
            clockBean.setDate(date);
        }
    }

    /**
     * 拼接日期
     * @param date
     * @return
     */
    private String calcDate(String date) {
        int hour = 0;
        int minute = 0;
        try {
            hour = DateUtil.getHour(DateUtil.convertStringToLong(DateUtil.YYYY_MM_DD_HH_MM, date));
            minute = DateUtil.getMinute(DateUtil.convertStringToLong(DateUtil.YYYY_MM_DD_HH_MM, date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String yMD = DateUtil.getCurrentDate(DateUtil.YYYY_MM_DD);
        int current = hour *60 + minute;
        int sys = DateUtil.getHour(new Date()) * 60 + DateUtil.getMinute(new Date());
        if (current <= sys){
            yMD = DateUtil.getWhichDate(DateUtil.YYYY_MM_DD, 1);
        }
        return ResUtil.format("%s %02d:%02d", yMD, hour, minute);
    }

    /**
     * 修改闹钟的状态
     */
    private void sendConfigToDevice(){
        CMDCompat.get().setAlarmClockReminderInfo();
    }

}
