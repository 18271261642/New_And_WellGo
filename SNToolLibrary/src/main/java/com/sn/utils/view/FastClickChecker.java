package com.sn.utils.view;

/**
 * 东芝
 * 快速点击检测,适用于耗时操作被用户狂点调用的情况
 */
public class FastClickChecker {
    private static long lastClickTime;

    public synchronized static boolean isFast() {
        return isFast(1000);
    }

    /**
     * @param interval 自定义时间长度
     * @return
     */
    public synchronized static boolean isFast(long interval) {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < interval) {
            return true;
        }
        lastClickTime = time;
        return false;
    }


}