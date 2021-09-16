package com.sn.app.db.data.base;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.Where;
import com.sn.db.data.base.dao.SNBaseDao;
import com.sn.db.utils.DBHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;

/**
 * 作者:东芝(2018/1/5).
 * 功能:增删改查 基类
 * 提供基本工具 具体特殊查询等 需要自己在子类写
 * 仅用于APP数据存储
 */

public class AppDao<T extends AppBean, ID> extends SNBaseDao<T, ID> {
    /**
     * 有数据?
     *
     * @return
     * @throws SQLException
     */
    public long queryCount(int user_id) {
        try {
            return getDao().queryBuilder().where().eq(T.COLUMN_USER_ID, user_id).countOf();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;

    }

    public boolean delete(int user_id, String date) {
        try {
            Dao<T, ID> dao = getDao();
            DeleteBuilder<T, ID> tidDeleteBuilder = dao.deleteBuilder();
            tidDeleteBuilder.where().eq(T.COLUMN_USER_ID, user_id);
            return tidDeleteBuilder.delete() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
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
        where.eq(T.COLUMN_USER_ID, user_id);
        return insertOrUpdate(data, where);
    }


    public T queryForUser(int user_id) {
        try {
            return queryForEq(T.COLUMN_USER_ID, user_id).get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    /**
     * 优化性能,只更新单个列
     *
     * @param columnName
     * @param user_id
     * @return
     */
    protected boolean updateColumnObject(String columnName, int user_id, Object obj) {
        int update = 0;
        try {
            //使用原生语法:
            byte blob[] = null;
            ByteArrayOutputStream byteArrayOutputStream = null;
            ObjectOutputStream objectOutputStream = null;
            try {
                byteArrayOutputStream = new ByteArrayOutputStream();
                objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                objectOutputStream.writeObject(obj);
                objectOutputStream.flush();
                blob = byteArrayOutputStream.toByteArray();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (objectOutputStream != null) {
                        objectOutputStream.close();
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                try {
                    if (byteArrayOutputStream != null) {
                        byteArrayOutputStream.close();
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if(blob==null){
                return false;
            }

            SQLiteDatabase db = DBHelper.getInstance().getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(columnName,blob);
            update = db.update(getDao().getTableName(), values,   T.COLUMN_USER_ID + "=?", new String[]{String.valueOf(user_id)});
//            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return update>0;
    }

    /**
     * 优化性能,只查询单个列
     *
     * @param columnName
     * @param user_id
     * @return
     */
    protected Object queryColumnObject(String columnName, int user_id) {
        //使用原生语法:
        Object o = null;
        try {
            SQLiteDatabase db = DBHelper.getInstance().getReadableDatabase();
            Cursor cursor = db.query(getDao().getTableName(), new String[]{columnName}, T.COLUMN_USER_ID + "=?", new String[]{String.valueOf(user_id)}, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    byte[] blob = cursor.getBlob(cursor.getColumnIndex(columnName));
                    ByteArrayInputStream byteArrayInputStream = null;
                    ObjectInputStream objectInputStream = null;
                    try {
                        byteArrayInputStream = new ByteArrayInputStream(blob);
                        objectInputStream = new ObjectInputStream(byteArrayInputStream);
                        o = objectInputStream.readObject();

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (objectInputStream != null) {
                                objectInputStream.close();
                            }
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        try {
                            if (byteArrayInputStream != null) {
                                byteArrayInputStream.close();
                            }
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
                cursor.close();
            }
//            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return o;
    }

}
