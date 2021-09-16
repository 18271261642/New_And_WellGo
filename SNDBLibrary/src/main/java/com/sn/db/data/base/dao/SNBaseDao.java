package com.sn.db.data.base.dao;

import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.table.TableInfo;
import com.sn.db.utils.DBHelper;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作者:东芝(2017/11/27).
 * 功能:增删改查 基类
 * 提供基本工具 具体特殊查询等 需要自己在子类写  这个类禁止编辑
 */

public abstract class SNBaseDao<T , ID> {
    private Dao<T, ID> dao;


    public Dao<T, ID> getDao() {
        return dao;
    }

    protected SNBaseDao() {

        try {
            Class type = (Class) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            dao = DBHelper.getInstance().getDao(type);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public SQLiteDatabase getReadableDatabase() {
        synchronized (this) {
            return  DBHelper.getInstance().getReadableDatabase();
        }
    }

    public SQLiteDatabase getWritableDatabase() {
        synchronized (this) {
            return  DBHelper.getInstance().getWritableDatabase();
        }
    }

    private static Map<Class<? extends SNBaseDao>, Object> INSTANCES_MAP = new HashMap<>();


    public synchronized static <E extends SNBaseDao> E get(Class<E> instanceClass) {
        if (INSTANCES_MAP.containsKey(instanceClass)) {
            return (E) INSTANCES_MAP.get(instanceClass);
        } else {
            E instance = null;
            try {
                instance = instanceClass.newInstance();

                INSTANCES_MAP.put(instanceClass, instance);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return instance;
        }
    }


    // 插入
    public boolean insert(T data)   {
        if (dao != null) {
            try {
                return dao.create(data) > 0;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
  
    /**
     * 插入或更新
     * 用法注意 需要传入要对比的值 一般是日期时间,用户名,用户账号,等唯一列 数据标识
     * 举例: insertOrUpdate(new User("张三"),"username","张三")  意思是更新或新增User数据 条件是where username='张三'
     * 该方法如果更新的columnName有多条 那么会替换全部 如果你不是这样的需求 则别用
     *
     * @param data  新的新数据/要更新的新数据
     * @param where  where 判断语句
     * @return
     */
    public boolean insertOrUpdate(T data,Where<T, ID> where)  {
        if (dao != null) {
            try {
                List<T> query = null;
                try {
                    query = where.query();
                } catch (Throwable ignored) {
                }
                if (query == null || query.isEmpty()) {
                    return insert(data);
                } else {
                    int c = 0;
                    try {
                        for (T t : query) {
                            ID id = dao.extractId(t);
                            BaseDaoImpl baseDao = (BaseDaoImpl) this.dao;
                            TableInfo tableInfo = baseDao.getTableInfo();
                            FieldType idField = tableInfo.getIdField();
                            Field field = idField.getField();
                            field.setAccessible(true);
                            field.set(data, id);//设置对方一个id  这样ORM才能更新 这是ORMLite最严重的bug
                            try {
                                c+=baseDao.update(data);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return c > 0;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 更新数据  注意data这个参数的对象需要设置一个数据库对应的id 才能更新 不然数据库不知道更新谁
     *
     * @param data
     * @return
     */
    public boolean update(T data)   {
        if (dao != null) {

            try {
                ID id = dao.extractId(data);
                if (id == null) {
                    throw new Exception("你没设置id,我都不知道更新谁");
                }
                return dao.update(data) > 0;
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return false;
    }


    // 删
    public boolean delete(T data)   {
        if (dao != null) {
            try {
                ID id = dao.extractId(data);
                if (id == null) {
                    throw new Exception("你没设置id,我都不知道删除谁");
                }
                return dao.delete(data) > 0;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    // 查
    public List<T> queryForAll()   {
        if (dao != null) {
            try {
                return dao.queryForAll();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>();
    }

    // 查
    public T queryForFirst()   {
        if (dao != null) {
            try {
                return dao.queryBuilder().queryForFirst();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    // 查
    public T queryForLast() throws Exception {
        try {
            if (dao != null) {
                List<T> all = queryForAll();//这里可以倒序一次再queryForFirst 这样就不用全部查查出来  但是先这样,有时间再改
                return all.get(all.size() - 1);
            }
        } catch (Exception e) {
            throw e;
        }
        return null;
    }

    // 查
    public T queryForId(ID id)  {
        if (dao != null) {
            try {
                return (T) dao.queryForId(id);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    // 查
    public List<T> queryForEq(String column, Object value)   {
        if (dao != null) {

            try {
                return dao.queryForEq(column, value);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>();
    }


    // 查
    public T queryForOneEq(String column, Object value)   {
        if (dao != null) {
           return queryForEq(column,value).get(0);
        }
        return null;
    }

}
