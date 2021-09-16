package com.dz.bleota.phy.sdk.firware;

/**
 * UpdateFirewareCallBack
 *
 * @author:zhoululu
 * @date:2018/7/14
 */

public interface UpdateFirewareCallBack {

    //发生错误
    public void onError(int code);
    //OTA进度%
    public void onProcess(float process);
    //OTA完成
    public void onUpdateComplete();

}
