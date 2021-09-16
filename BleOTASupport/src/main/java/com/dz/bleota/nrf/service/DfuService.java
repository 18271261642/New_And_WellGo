package com.dz.bleota.nrf.service;

import android.app.Activity;

import com.dz.bleota.BuildConfig;

import java.lang.reflect.Field;

import no.nordicsemi.android.dfu.DfuBaseService;

/**
 * 作者:东芝(2018/3/7).
 * 功能:Dfu服务
 */

public class DfuService extends DfuBaseService {
    @Override
    protected Class<? extends Activity> getNotificationTarget() {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //反射 dfu库, 设置debug模式 跟随本工程
        try {
            Field debug = DfuBaseService.class.getDeclaredField("DEBUG");
            debug.setAccessible(true);
            debug.set(null, BuildConfig.DEBUG);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
