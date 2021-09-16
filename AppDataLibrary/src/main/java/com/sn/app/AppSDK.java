package com.sn.app;

import android.content.Context;

import com.sn.utils.storage.SNStorage;

/**
 * 作者:东芝(2018/1/19).
 * 功能:AppSDK
 */

public class AppSDK {
    private static Context context;

    public static Context getContext() {
        return context;
    }

    public static void init(Context context) {
        AppSDK.context = context.getApplicationContext();

        //SharedPreferences 工具
        SNStorage.init(context, String.format("%s.xml", BuildConfig.APP_UPDATE_APPNAME));
    }


}
