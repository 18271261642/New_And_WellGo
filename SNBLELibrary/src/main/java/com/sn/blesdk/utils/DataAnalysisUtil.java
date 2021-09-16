package com.sn.blesdk.utils;

import com.sn.utils.SNLog;

/**
 * 作者:东芝(2018/1/26).
 * 功能:数据解析工具
 */

public class DataAnalysisUtil {

    /**
     * 获取睡眠状态
     * @param src16Bit
     * @return
     */
    public static int getSleepStatus(int src16Bit) {
        return (src16Bit >> 14);
    }

    /**
     * 获取睡眠分钟
     * @param src16Bit
     * @return
     */
    public static int getSleepMinutes(int src16Bit) {
        return src16Bit & 0x3FFF;
    }

    /**
     * 取得某一位
     * @param val
     * @param bitNum
     * @return
     */
    public static int get1Bit(int val, int bitNum) {
        return (val >> bitNum) & 1;
    }



    /**
     * 计算重复周期
     */
    public static int getWeekCycle(boolean[] week) {
        int num = 0;
        for (int i = 0; i < week.length ; i++){
            if (week[i]){
                num |= (1 << i);
            }
        }
        SNLog.e("cycle: %s", num == 0 ? 128 : num);
        return num == 0 ? 128 : num;
    }

    /**
     * 合并两个byte  则16位
     *
     * @param num1
     * @param num2
     * @return
     */
    public static int convertTo16Bit(byte num1, byte num2) {
        int i = ((num1 & 0xff) * 256 + (num2 & 0xff)) & 0xffff;//合并;
        return i;
    }
}
