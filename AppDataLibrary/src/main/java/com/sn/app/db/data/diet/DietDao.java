package com.sn.app.db.data.diet;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.Where;
import com.sn.app.db.data.base.AppDao;

import java.sql.SQLException;
import java.util.List;

/**
 * 作者:东芝(2018/11/28).
 * 功能:食谱数据表 操作
 */

public class DietDao extends AppDao<DietBean, Integer> {

    /**
     * 根据日期 插入数据
     *
     * @param user_id
     * @param data
     * @return
     */
    public boolean insertOrUpdate(int user_id, DietBean data) throws SQLException {
        Where<DietBean, Integer> where = getDao().queryBuilder().where();
        where.eq(DietBean.COLUMN_DATE, data.getDate()).and().eq(DietBean.COLUMN_USER_ID, user_id);
        return insertOrUpdate(data, where);

    }



    public DietBean queryForLast(int user_id) throws Exception {
        try {
            Dao<DietBean, Integer> dao = getDao();
            if (dao != null) {
                List<DietBean> all = dao.queryForEq(DietBean.COLUMN_USER_ID, user_id);//这里可以倒序一次再queryForFirst 这样就不用全部查查出来  但是先这样,有时间再改
                return all.get(all.size() - 1);
            }
        } catch (Exception e) {
            throw e;
        }
        return null;
    }
    public DietBean queryForDate(int user_id,String date) throws Exception {
        return getDao()
                .queryBuilder()
                .where()
                .eq(DietBean.COLUMN_USER_ID,user_id)
                .and()
                .eq(DietBean.COLUMN_DATE,date)
                .queryForFirst();
    }

//
//    public boolean updateForDate(int user_id,String date) throws Exception {
//        UpdateBuilder<DietBean, Integer> updateBuilder = getDao().updateBuilder();
//        updateBuilder.where()
//                .eq(DietBean.COLUMN_USER_ID,user_id)
//                .and()
//                .eq(DietBean.COLUMN_DATE,date);
//        return updateBuilder.update() > 0;
//    }
}
