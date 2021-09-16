package com.sn.map.interfaces;

/**
 * 作者:东芝(2017/12/27).
 * 功能:获取地址回调
 */

public interface OnMapLocationAddressListener {
    void onLocationAddress(String address);
    void onLocationAddressFailed(int code);
}
