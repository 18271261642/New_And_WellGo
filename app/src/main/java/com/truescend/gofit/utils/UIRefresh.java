package com.truescend.gofit.utils;

/**
 * 作者:东芝(2018/3/17).
 * 功能:UI刷新
 * 考虑到 app 多重 跳转修改某些值后 回到最初的界面 需要刷新
 * 当时有考虑利用事件总线刷新或者callback刷新或者onActivityResult刷新 或者 每次onResume刷新
 * 都实际上有问题, 比如
 * 1.有些界面是不能实时刷新的 需要见到界面才刷新 此时不能用 callback
 * 2.有些界面是多重跳转的 不能用onActivityResult (你想说可以onActivityResult后 再setResult 递归传递到后一层 但这样岂不更麻烦?)
 * 3.每次onResume刷新 这样的确可以界面 但是这样的话 每次都刷新 导致内存消耗很大 一旦内存泄露 内存越来越大 直到app崩溃
 *
 * 因此得出一个最简单的办法! 用静态常量记录 需要刷新的界面或功能, 在onResume 或Fragment可见时 判断一下需不需要刷新 即可!
 */

public class UIRefresh {
    private static boolean isNeedRefreshTrackItem =true;

    /**
     * 需要刷新运动轨迹列表项的记录
     * @return
     */
    public static boolean isIsNeedRefreshTrackItem() {
        return isNeedRefreshTrackItem;
    }
    /**
     * 需要刷新运动轨迹列表项的记录
     */
    public static void setIsNeedRefreshTrackItem(boolean isNeedRefreshTrackItem) {
        UIRefresh.isNeedRefreshTrackItem = isNeedRefreshTrackItem;
    }
}
