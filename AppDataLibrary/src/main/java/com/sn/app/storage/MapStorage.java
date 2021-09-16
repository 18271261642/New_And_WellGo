package com.sn.app.storage;

import com.sn.utils.storage.SNStorage;

/**
 * 作者:东芝(2018/4/11).
 * 功能:地图轻量级存储
 */

public class MapStorage extends SNStorage {

    private final static String LAST_MAP_TYPE = "last_map_type";
    private final static String SELECT_MAP_TYPE = "select_map_type";


    public static void setLastSmartMapType(int mapType) {
        setValue(LAST_MAP_TYPE, mapType);
    }

    public static int getLastSmartMapType() {
        return getValue(LAST_MAP_TYPE, -1);
    }

    public static void setSelectMapType(int selectType) {
        setValue(SELECT_MAP_TYPE, selectType);
    }

    public static int getSelectMapType() {
        return getValue(SELECT_MAP_TYPE, 0);
    }
}
