package com.dz.blesdk.interfaces;

/**
 * 作者:东芝(2017/11/18).
 * 功能:连接监听接口
 */

public interface ConnectListener {
    /**
     * 连接成功
     */
    void onConnected();

    /**
     * 通知开启
     */
    void onNotifyEnable();

    /**
     * 连接已断开
     */
    void onDisconnected();

    /**
     * 连接错误
     * @param errorType 错误类型
     */
    void onFailed(int errorType);
}
