package com.sn.app.db.data.user;

import com.j256.ormlite.stmt.Where;
import com.sn.app.db.data.base.AppDao;

import java.sql.SQLException;

/**
 * 作者:东芝(2018/01/24).
 * 功能:用户信息表操作
 */
public class UserDao extends AppDao<UserBean,Integer> {

    /**
     * 更新表 条件:当userId=xxx时
     * @param bean
     * @param user_id
     * @return
     * @throws SQLException
     */
    public boolean insertOrUpdate(UserBean bean, int user_id) throws SQLException {
        Where<UserBean,Integer> where = getDao().queryBuilder().where();
        where.eq(UserBean.COLUMN_USER_ID,user_id);
        return super.insertOrUpdate(bean, where);
    }


}
