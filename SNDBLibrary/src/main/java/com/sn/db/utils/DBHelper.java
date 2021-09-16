package com.sn.db.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.util.List;

/**
 * 作者:东芝(2018/2/27).
 * 功能:数据库框架助手
 */
public class DBHelper extends OrmLiteSqliteOpenHelper {
    private static DBHelper instance;
    private List<Class<?>> tableClassSet;
    private OnUpgradeListener listener;


    private DBHelper(Context context, String databaseName, int databaseVersion, List<Class<?>> tableClassSet, OnUpgradeListener listener) {
        super(context, databaseName, null, databaseVersion);
        this.tableClassSet = tableClassSet;
        this.listener = listener;
    }

    public static  void init(Context context, String databaseName, int databaseVersion, List<Class<?>> tableClassSet,OnUpgradeListener listener) {
        if (instance == null) {
            instance = new DBHelper(context, databaseName, databaseVersion, tableClassSet, listener);
        }
    }

    public static DBHelper getInstance() {
        return instance;
    }

    /**
     * APP数据库首次使用
     *
     * @param sqLiteDatabase
     * @param connectionSource
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        for (Class<?> dataClass : tableClassSet) {
            try {
                TableUtils.createTableIfNotExists(connectionSource, dataClass);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * APP数据库升级
     *
     * @param db
     * @param cs
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource cs, int oldVersion, int newVersion) {
        if (listener == null) {
            return;
        }

        for (int version = oldVersion; version < newVersion; version++) {

            listener.onUpgrade(db, cs, oldVersion, newVersion, version);
        }

    }


    /**
     * APP数据库降级
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //TODO APP数据库降级 目前暂不处理,太繁琐,直接清空并重新创建
        //清空所有表
        for (Class<?> dataClass : tableClassSet) {
            try {
                TableUtils.dropTable(connectionSource, dataClass, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //重新创建空表
        onCreate(db);
    }

    public interface OnUpgradeListener {
        void onUpgrade(SQLiteDatabase db, ConnectionSource cs, int oldVersion, int newVersion, int version);
    }
}
