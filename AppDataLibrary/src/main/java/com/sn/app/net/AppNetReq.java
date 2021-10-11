package com.sn.app.net;

import android.content.Context;
import android.os.Environment;

import com.sn.app.AppSDK;
import com.sn.app.net.data.app.AppApiService;
import com.sn.app.net.interceptor.NeoonInterceptor;
import com.sn.net.comm.factory.MyGsonConverterFactory;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * 作者:东芝(2018/1/26).
 * 功能:APP接口请求
 */

public class AppNetReq {
    private static Retrofit retrofit;

    public static AppApiService getApi() {
        if(retrofit==null){
            //获取Retrofit对象，设置地址
            OkHttpClient okHttpClient = createOkHttpClient(AppSDK.getContext());
            retrofit = new Retrofit.Builder()
                    .client(okHttpClient)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(MyGsonConverterFactory.create())
                    .baseUrl(AppUrl.URL_NEEON)
                    .build();
        }
        return retrofit.create(AppApiService.class);
    }


    private static OkHttpClient createOkHttpClient(Context context) {
        return new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .cache(provideOkHttpCache(context))
                .addInterceptor(new NeoonInterceptor())
                .addNetworkInterceptor(new NeoonInterceptor())
                .build();
    }

    private static Cache provideOkHttpCache(Context context) {
        int cacheSize = 100 * 1024 * 1024; // 10 MiB
        File externalCacheDir = context.getExternalCacheDir();
        if (externalCacheDir == null) {
            externalCacheDir = new File(Environment.getExternalStorageDirectory(), "/Android/" + context.getPackageName() + "/cache/");
        }
        if (!externalCacheDir.exists()) {
            boolean mkdirs = externalCacheDir.mkdirs();
        }
        return new Cache(externalCacheDir, cacheSize);
    }

}
