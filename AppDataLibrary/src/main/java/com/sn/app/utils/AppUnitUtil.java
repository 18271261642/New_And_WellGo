package com.sn.app.utils;

import com.sn.app.db.data.config.DeviceConfigDao;
import com.sn.app.db.data.config.bean.UnitConfig;

/**
 * 作者:东芝(2018/3/16).
 * 功能:app全局单位获取 工具
 */

public class AppUnitUtil {
    private static UnitConfig bean;

    public static synchronized boolean setUnitConfig(final UnitConfig config) {
        if(config==null)return true;
        DeviceConfigDao deviceConfigDao = DeviceConfigDao.get(DeviceConfigDao.class);
        return deviceConfigDao.updateUnitConfig(AppUserUtil.getUser().getUser_id(), config);
    }

    public static synchronized UnitConfig getUnitConfig() {

        DeviceConfigDao deviceConfigDao = DeviceConfigDao.get(DeviceConfigDao.class);
        bean = deviceConfigDao.queryUnitConfig(AppUserUtil.getUser().getUser_id());
        return bean;
    }

}
