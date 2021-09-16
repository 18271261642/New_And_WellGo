package com.dz.blesdk.utils;

import android.os.Build;

/**
 * 作者:东芝(2018/4/25).
 * 功能:7.0扫描限制兼容
 * 源码:GattService.java
 * bug反映:App xxx is scanning too frequently
 * 原因:安卓7.0以后,30秒内不能扫描超过5次
 */
public class BLEScanLimitUtil {

    private static int scanCount;
    private static long mFirstScanTime;
    private final static int SEC = 30 * 1000;
    private static long remainingTime;

    public static long getRemainingTime() {
        return remainingTime;
    }

    public static boolean isScanningTooFrequently(){
        //低于安卓N没有这个问题
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return false;
        }
        scanCount++;
        if(scanCount==1)
        {
            mFirstScanTime = System.currentTimeMillis();
        }
        long timeOffset = System.currentTimeMillis() - mFirstScanTime;
        boolean isMaxCount = scanCount >= 4/*4次更保守一点*/;
        if(isMaxCount)
        {

            if(timeOffset < SEC){
                remainingTime = SEC - timeOffset;
                return true;
            }else{
                mFirstScanTime = 0;
                scanCount = 0;
                return false;
            }
        }else{
            return false;
        }
    }
}
