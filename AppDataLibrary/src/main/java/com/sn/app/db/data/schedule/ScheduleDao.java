package com.sn.app.db.data.schedule;

import com.j256.ormlite.stmt.Where;
import com.sn.app.db.data.base.AppDao;
import com.sn.app.utils.AppUserUtil;
import com.sn.utils.DateUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Author:Created by 泽鑫 on 2018/2/2 15:53.
 */

public class ScheduleDao extends AppDao<ScheduleBean, Integer>{

    /**
     * 查询要显示的闹钟（过期不显示）
     */
    public List<ScheduleBean> queryForFuture() {
        List<ScheduleBean> list = new ArrayList<>();
        int user_id = AppUserUtil.getUser().getUser_id();
        try {
            list = getDao()
                    .queryBuilder()
                    .where()
                    .eq(ScheduleBean.COLUMN_USER_ID, user_id)
                    .and()
                    .gt(ScheduleBean.COLUMN_DATE, DateUtil.getCurrentDate(DateUtil.YYYY_MM_DD_HH_MM))
                    .query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<ScheduleBean> queryForHistory() {
        List<ScheduleBean> list = new ArrayList<>();
        int user_id = AppUserUtil.getUser().getUser_id();
        try {
            list = getDao()
                    .queryBuilder()
                    .where()
                    .eq(ScheduleBean.COLUMN_USER_ID, user_id)
                    .and()
                    .le(ScheduleBean.COLUMN_DATE, DateUtil.getCurrentDate(DateUtil.YYYY_MM_DD_HH_MM))
                    .query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }


    public List<ScheduleBean> queryForUnread(){
        List<ScheduleBean> list = new ArrayList<>();
        int user_id = AppUserUtil.getUser().getUser_id();
        try {
            list = getDao()
                    .queryBuilder()
                    .where()
                    .eq(ScheduleBean.COLUMN_USER_ID, user_id)
                    .and()
                    .le(ScheduleBean.COLUMN_DATE, DateUtil.getCurrentDate(DateUtil.YYYY_MM_DD_HH_MM))
                    .and()
                    .eq(ScheduleBean.COLUMN_READ, false)
                    .query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }


    public ScheduleBean queryForOne(int id) {
        int user_id = AppUserUtil.getUser().getUser_id();
        ScheduleBean scheduleBean = new ScheduleBean();
        try {
            scheduleBean = getDao()
                    .queryBuilder()
                    .where()
                    .eq(ScheduleBean.COLUMN_ID, id)
                    .and()
                    .eq(ScheduleBean.COLUMN_USER_ID, user_id)
                    .query()
                    .get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return scheduleBean;
    }

    public boolean insertOrUpdate(ScheduleBean bean, int user_id){
        Where<ScheduleBean, Integer> where = getDao().queryBuilder().where();
        try {
            where.eq(ScheduleBean.COLUMN_USER_ID, user_id).and().eq(ScheduleBean.COLUMN_ID, bean.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return super.insertOrUpdate(bean, where);
    }
}
