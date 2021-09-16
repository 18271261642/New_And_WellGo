package com.sn.blesdk.ble;

import com.dz.blesdk.ble.BaseUUID;

import java.util.UUID;

/**
 * 作者:东芝(2017/11/20).
 * 功能:UUID初始化,处理多厂商定制的协议方案
 */

class SNUUID {

    static void init() {
        initXianWeiUUID();
    }

    private static void initXianWeiUUID() {
        BaseUUID.SERVICE = UUID.fromString("8f400001-cfb4-14a3-f1ba-f61f35cddbaf");
        BaseUUID.WRITE = UUID.fromString("8f400002-cfb4-14a3-f1ba-f61f35cddbaf");
        BaseUUID.NOTIFY = UUID.fromString("8f400003-cfb4-14a3-f1ba-f61f35cddbaf");
        BaseUUID.WRITE_WALLPAPER = UUID.fromString("8f400004-cfb4-14a3-f1ba-f61f35cddbaf");
        BaseUUID.NOTIFY_WALLPAPER = UUID.fromString("8f400005-cfb4-14a3-f1ba-f61f35cddbaf");
    }

    private static void initI6UUID() {
        BaseUUID.SERVICE = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
        BaseUUID.WRITE = UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb");
        BaseUUID.NOTIFY = UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb");
    }

}
