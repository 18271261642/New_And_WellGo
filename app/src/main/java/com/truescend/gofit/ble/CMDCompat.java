package com.truescend.gofit.ble;

import com.sn.blesdk.cmd.SNCMD;
import com.sn.blesdk.cmd.XWCmd;
import com.sn.blesdk.interfaces.ICmd;

/**
 * 作者:东芝(2018/3/27).
 * 功能:应用层命令兼容
 * 不同与SNCMD.get()的是 这里与应用层数据交互
 */

public abstract class CMDCompat {

    public static CMDCompat get() {
        ICmd iCmd = SNCMD.get();
        if (iCmd instanceof XWCmd) {
            return new XWCMDCompat();
        }
//        else if (iCmd instanceof XWCmd) {
//            return new XWCMDCompat();
//        }
        throw new NullPointerException("错误!没有找到对应的公司的协议");
    }

    /**
     * 设置用户信息到设备里
     */
    public abstract boolean setUserInfo();

    /**
     * 设置日程信息
     *
     * @return
     */
    public abstract boolean setScheduleReminderInfo();

    /**
     * 设置闹钟信息
     *
     * @return
     */
    public abstract boolean setAlarmClockReminderInfo();

    /**
     * 设置喝水提醒
     *
     * @return
     */
    public abstract boolean setDrinkReminderInfo();

    /**
     * 设置久坐提醒
     *
     * @return
     */
    public abstract boolean setSedentaryReminderInfo();

    /**
     * 其他设置: 抬手亮屏,防丢,心率自动检测
     * @return
     */
    public abstract boolean setDeviceOtherInfo();

    /**
     * 夜间模式
     * @return
     */
    public abstract boolean setDeviceNightModeInfo();


}
