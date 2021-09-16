package com.sn.db.sdk;

import android.content.Context;

import com.sn.db.utils.DBHelper;

import java.util.List;

/**
 * 作者:东芝(2017/11/27).
 * 功能:数据库
 */

public class SNDBSDK {

    /**
     * 初始化我 之前 必须 tables 中已经添加好全部的表
     * @param context
     * @param listener
     */
    public static void init(Context context, String databaseName, int databaseVersion, List<Class<?>> tableClassSet, DBHelper.OnUpgradeListener listener) {
        Context mBaseContext = context.getApplicationContext();
        DBHelper.init(mBaseContext,  databaseName, databaseVersion,tableClassSet,listener);
    }

    public static void close() {
        DBHelper.getInstance().close();
    }
}
