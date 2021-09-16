package com.sn.blesdk.cmd;

import com.sn.blesdk.interfaces.ICmd;

/**
 * 作者:东芝(2017/11/21).
 * 功能:命令工具
 */

public class SNCMD {
    public static ICmd get(){
        return new XWCmd();
    }
}
