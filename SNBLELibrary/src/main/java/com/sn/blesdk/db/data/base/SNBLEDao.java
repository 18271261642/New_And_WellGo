package com.sn.blesdk.db.data.base;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.Where;
import com.sn.db.data.base.dao.SNBaseDao;
import com.sn.utils.DateUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 作者:东芝(2017/11/27).
 * 功能:增删改查 基类
 * 提供基本工具 具体特殊查询等 需要自己在子类写  这个类禁止编辑
 * 仅用于设备数据存储
 */
public class SNBLEDao<T extends SNBLEBaseBean, ID> extends SNBaseDao<T, ID> {

    public boolean delete(int user_id, String date) {
        try {
            Dao<T, ID> dao = getDao();
            DeleteBuilder<T, ID> tidDeleteBuilder = dao.deleteBuilder();
            tidDeleteBuilder.where().eq(T.COLUMN_USER_ID, user_id).and().eq(T.COLUMN_DATE, date);
            return tidDeleteBuilder.delete() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    /**
     * 查询未上传的
     * @param user_id
     * @return
     * @throws SQLException
     */
    public List<T> queryNotUpload(int user_id) throws SQLException {
        return  getDao()
                .queryBuilder()
                .where()
                .eq(T.COLUMN_USER_ID, user_id)
                .and()
                .eq(T.COLUMN_IS_UPLOADED, false)
                .query();
    }


    /**
     * 更新上传状态
     *
     * @param user_id
     * @param date       日期
     * @param isUploaded 数据是否已上传
     * @return
     * @throws SQLException
     */
    public boolean updateUploadStatus(int user_id, String date, boolean isUploaded) throws SQLException {
        Dao<T, ID> dao = getDao();

        //语法: update SportBean set isUploaded=1 where date='2018-01-21';
        int executeRawNoArgs = dao.executeRawNoArgs(String.format(Locale.ENGLISH, "update %s set %s=%d where %s='%s' and %s=%d;", dao.getTableName(), T.COLUMN_IS_UPLOADED, (isUploaded ? 1 : 0), T.COLUMN_DATE, date, T.COLUMN_USER_ID, user_id));
        return executeRawNoArgs > 0;
    }

    /**
     * 根据日期+mac共同条件插入数据
     *
     * @param user_id
     * @param data
     * @return
     */
    public boolean insertOrUpdate(int user_id, T data) throws SQLException {
        Where<T, ID> where = getDao().queryBuilder().where();
        where.eq(T.COLUMN_DATE, data.getDate()).and().eq(T.COLUMN_USER_ID, user_id);
        return insertOrUpdate(data, where);

    }

//    /**
//     * 查询全部 当天的所有数据
//     *
//     * @return
//     */
//    public T queryForToday() {
//        return queryForOneEq(T.COLUMN_DATE, DateUtil.getCurrentDate(DateUtil.YYYY_MM_DD));
//    }

    /**
     * 查询该设备下当天的所有数据
     *
     * @return
     * @throws SQLException
     */
    public List<T> queryForToday(int user_id) {
        return queryForDay(user_id, DateUtil.getCurrentDate(DateUtil.YYYY_MM_DD));
    }

    /**
     * 查询该设备此年的所有数据
     *
     * @return
     * @throws SQLException
     */
    public List<T> queryForYear(int user_id, int year) {
        try {
            return getDao()
                    .queryBuilder()
                    .where()
                    .like(T.COLUMN_DATE, year + "%")
                    .and()
                    .eq(T.COLUMN_USER_ID, user_id)
                    .query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public List<T> queryForMonth(int user_id, int year, int month) {
        try {
            return getDao()
                    .queryBuilder()
                    .where()
                    .like(T.COLUMN_DATE, String.format(Locale.ENGLISH, "%04d-%02d%%", year, month))
                    .and()
                    .eq(T.COLUMN_USER_ID, user_id)
                    .query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询某段时间
     *
     * @return
     * @throws SQLException
     */
    public List<T> queryForBetween(int user_id, String lowDate, String highDate) {
        try {
            return getDao()
                    .queryBuilder()
                    .where()
                    .between(T.COLUMN_DATE, lowDate, highDate)
                    .and()
                    .eq(T.COLUMN_USER_ID, user_id)
                    .query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 查询某天的所有数据
     *
     * @param date
     * @return
     * @throws SQLException
     */
    public List<T> queryForDay(int user_id, String date) {
        try {
            return getDao()
                    .queryBuilder()
                    .where()
                    .eq(T.COLUMN_DATE, date)
                    .and()
                    .eq(T.COLUMN_USER_ID, user_id)
                    .query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 查询某天的所有数据
     *
     * @param date
     * @return
     * @throws SQLException
     */
    public List<T> queryForDay(int user_id, String date, boolean orderByAscending) {
        try {
            return getDao()
                    .queryBuilder()
                    .orderBy(T.COLUMN_DATE, orderByAscending)
                    .where()
                    .eq(T.COLUMN_DATE, date)
                    .and()
                    .eq(T.COLUMN_USER_ID, user_id)
                    .query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 有数据?
     *
     * @param date
     * @return
     * @throws SQLException
     */
    public long queryCount(int user_id, String date) {
        try {
            return getDao().queryBuilder().where().eq(T.COLUMN_DATE, date).and().eq(T.COLUMN_USER_ID, user_id).countOf();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;

    }
    /**
     * 有数据?
     *
     * @return
     * @throws SQLException
     */
    public long queryAllCount(int user_id ) {
        try {
            return getDao().queryBuilder().where().eq(T.COLUMN_USER_ID, user_id).countOf();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;

    }
    public List<T> queryForAll(int user_id) {
        try {
            return getDao().queryBuilder().orderBy(T.COLUMN_DATE, true).where().eq(T.COLUMN_USER_ID, user_id).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
