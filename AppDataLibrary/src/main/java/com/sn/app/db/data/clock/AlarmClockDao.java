package com.sn.app.db.data.clock;

import com.j256.ormlite.stmt.Where;
import com.sn.app.db.data.base.AppDao;
import com.sn.app.utils.AppUserUtil;
import com.sn.utils.DateUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Author:Created by 泽鑫 on 2018/1/20 11:46.
 */

public class AlarmClockDao extends AppDao<AlarmClockBean, Integer> {

    /**
     * 查询要显示的闹钟
     */
    public List<AlarmClockBean> queryForUser() {
        List<AlarmClockBean> list = new ArrayList<>();
        int user_id = AppUserUtil.getUser().getUser_id();
        try {
            list = getDao().queryBuilder().where().eq(AlarmClockBean.COLUMN_USER_ID, user_id).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<AlarmClockBean> queryForSend(boolean containCurrentTime) {
        List<AlarmClockBean> list = new ArrayList<>();
        int user_id = AppUserUtil.getUser().getUser_id();
        try {
            if (containCurrentTime) {
                list = getDao()
                        .queryBuilder()
                        .where()
                        .eq(AlarmClockBean.COLUMN_USER_ID, user_id)
                        .and()
                        .ge(AlarmClockBean.COLUMN_DATE, DateUtil.getCurrentDate(DateUtil.YYYY_MM_DD_HH_MM))
                        .and()
                        .eq(AlarmClockBean.COLUMN_SWITCH, true)
                        .query();
            } else {
                list = getDao()
                        .queryBuilder()
                        .where()
                        .eq(AlarmClockBean.COLUMN_USER_ID, user_id)
                        .and()
                        .lt(AlarmClockBean.COLUMN_DATE, DateUtil.getCurrentDate(DateUtil.YYYY_MM_DD_HH_MM))
                        .and()
                        .eq(AlarmClockBean.COLUMN_SWITCH, true)
                        .query();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void queryForCloseOverdueClock() {
        List<AlarmClockBean> list = new ArrayList<>();
        int user_id = AppUserUtil.getUser().getUser_id();
        try {
            list = getDao()
                    .queryBuilder()
                    .where()
                    .eq(AlarmClockBean.COLUMN_USER_ID, user_id)
                    .and()
                    .le(AlarmClockBean.COLUMN_DATE, DateUtil.getCurrentDate(DateUtil.YYYY_MM_DD_HH_MM))
                    .query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for (AlarmClockBean clockBean : list) {
            updateClockStatue(clockBean);
        }
    }


    public AlarmClockBean queryForOne(int id) {
        int user_id = AppUserUtil.getUser().getUser_id();
        AlarmClockBean alarmClockBean = null;
        try {
            alarmClockBean = getDao().queryBuilder().where().eq(AlarmClockBean.COLUMN_ID, id).and().eq(AlarmClockBean.COLUMN_USER_ID, user_id).query().get(0);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return alarmClockBean;
    }

    public boolean insertOrUpdate(AlarmClockBean bean, int user_id) throws SQLException {
        Where<AlarmClockBean, Integer> where = getDao().queryBuilder().where();
        where.eq(AlarmClockBean.COLUMN_USER_ID, user_id).and().eq(AlarmClockBean.COLUMN_ID, bean.getId());
        return super.insertOrUpdate(bean, where);
    }

    private void updateClockStatue(AlarmClockBean clockBean) {
        int num = 0;
        boolean week[] = clockBean.getWeek();
        for (boolean aWeek : week) {
            if (aWeek) {
                num++;
            }
        }
        if (num == 0 && clockBean.isSwitchStatues()) {
            clockBean.setSwitchStatues(false);
            update(clockBean);
        }
    }

}
