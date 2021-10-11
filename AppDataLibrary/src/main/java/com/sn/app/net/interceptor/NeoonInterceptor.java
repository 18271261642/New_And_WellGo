package com.sn.app.net.interceptor;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.sn.app.AppSDK;
import com.sn.app.net.AppUrl;
import com.sn.app.storage.UserStorage;
import com.sn.net.utils.NetUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.CacheControl;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

/**
 * 作者:东芝(2017/9/1).
 * 描述:拦截器
 */
public class NeoonInterceptor implements Interceptor {

    private static final String TAG = "NeoonInterceptor";

    private String version;

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        try {
            Log.e(TAG,"-----china="+(chain == null) +"\n"+(chain.request() == null)+"\n"+NetUtils.isConnected(AppSDK.getContext()));
            Request request = chain.request();
            if (!NetUtils.isConnected(AppSDK.getContext())) {
                //没网强制从缓存读取(必须得写，不然断网状态下，退出应用，或者等待一分钟后，就获取不到缓存）
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)
                        .build();
            } else {
                Request.Builder requestBuilder = request.newBuilder();

                Map<String, String> mGetParamsMap = new HashMap<>();
                Map<String, String> mPostParamsMap = new HashMap<>();


                Headers headers = request.headers();

                Headers.Builder newBuilder = request.headers().newBuilder();

                int size = headers.size();
                for (int i = 0; i < size; i++) {
                    String name = headers.name(i);
                    if (name.startsWith("@") && name.length() > 1) {
                        newBuilder.removeAll(name);
                        String key = name.substring(1, name.length());
                        addParams(request, requestBuilder, mGetParamsMap, mPostParamsMap, key, headers.value(i));
                    }
                }


                //重定义头 删除临时头参数
                requestBuilder.headers(newBuilder.build());
                try {
                    if (TextUtils.isEmpty(version)) {
                        Context context = AppSDK.getContext();
                        PackageManager pm = context.getPackageManager();
                        PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
                        version = String.format("%s/%s", pm.getApplicationLabel(packageInfo.applicationInfo), packageInfo.versionName);
                    }
                    requestBuilder.removeHeader("User-Agent");
                    requestBuilder.addHeader("User-Agent", version);
                } catch (Throwable ignored) {
                    ignored.printStackTrace();
                }

                //注入URL(GET或其他)参数
                request = injectUrlParamsRequest(request, requestBuilder, mGetParamsMap);
                //注入POST参数
                request = injectPostParamsRequest(request, requestBuilder, mPostParamsMap);
                Log.e(TAG,"----request="+(request == null));
            }
            return chain.proceed(request) ;
//            return proceedCache(chain, request) ;
        }catch (NullPointerException e){
            e.printStackTrace();
            return null;
        }

    }

    private void addParams(Request request, Request.Builder requestBuilder, Map<String, String> mGetParamsMap, Map<String, String> mPostParamsMap, String key, String value) {

        if (TextUtils.isEmpty(key) || TextUtils.isEmpty(value)) {
            return;
        }
        if ("url".equals(key) && !TextUtils.isEmpty(value)) {
            if (value.startsWith("http://") || value.startsWith("https://")) {
                request = setNewUrl(request, requestBuilder, value);
            }

            if ("neoon".equalsIgnoreCase(value)) {
                request = setNewUrl(request, requestBuilder, AppUrl.URL_NEEON);
            } else {//其他服务器

            }
        }
        //对立欧服务器进行动态请求头参数赋值
        if ("access_token".equals(key) && "neoon".equalsIgnoreCase(value)) {
            String token = UserStorage.getAccessToken();
            if (!TextUtils.isEmpty(token)) {
                value = token;
            }
        }

        if (isGET(request)) {
            mGetParamsMap.put(key, value);
        }
        if (isPOST(request)) {
            mPostParamsMap.put(key, value);
        }
    }

    private Request setNewUrl(Request request, Request.Builder requestBuilder, String url) {
        HttpUrl newBaseUrl = HttpUrl.parse(url);
        if (newBaseUrl != null) {
            //从request中获取原有的HttpUrl实例oldHttpUrl
            HttpUrl oldHttpUrl = request.url();
            //重建新的HttpUrl，修改需要修改的url部分
            HttpUrl newFullUrl = oldHttpUrl
                    .newBuilder()
                    .scheme(newBaseUrl.scheme())
                    .host(newBaseUrl.host())
                    .port(oldHttpUrl.port())
                    .build();
            request = requestBuilder.url(newFullUrl).build();

        }
        return request;
    }


    private Request injectPostParamsRequest(Request request, Request.Builder requestBuilder, Map<String, String> paramsMap) {
        // process post body inject
        if (paramsMap != null && paramsMap.size() > 0 && isPOST(request)) {
            if (request.body() instanceof FormBody) {
                FormBody.Builder newFormBodyBuilder = new FormBody.Builder();
                if (paramsMap.size() > 0) {
                    Iterator iterator = paramsMap.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry entry = (Map.Entry) iterator.next();
                        newFormBodyBuilder.add((String) entry.getKey(), (String) entry.getValue());
                    }
                }

                FormBody oldFormBody = (FormBody) request.body();
                int paramSize = oldFormBody.size();
                if (paramSize > 0) {
                    for (int i = 0; i < paramSize; i++) {
                        newFormBodyBuilder.add(oldFormBody.name(i), oldFormBody.value(i));
                    }
                }

                requestBuilder.post(newFormBodyBuilder.build());
                request = requestBuilder.build();
            } else if (request.body() instanceof MultipartBody) {
                MultipartBody.Builder multipartBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);

                Iterator iterator = paramsMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry entry = (Map.Entry) iterator.next();
                    multipartBuilder.addFormDataPart((String) entry.getKey(), (String) entry.getValue());
                }

                List<MultipartBody.Part> oldParts = ((MultipartBody) request.body()).parts();
                if (oldParts != null && oldParts.size() > 0) {
                    for (MultipartBody.Part part : oldParts) {
                        multipartBuilder.addPart(part);
                    }
                }

                requestBuilder.post(multipartBuilder.build());
                request = requestBuilder.build();
            } else {//无参时
                try {
                    if (request.body() != null && request.body().contentLength() == 0) {
                        FormBody.Builder newFormBodyBuilder = new FormBody.Builder();
                        Iterator iterator = paramsMap.entrySet().iterator();
                        while (iterator.hasNext()) {
                            Map.Entry entry = (Map.Entry) iterator.next();
                            newFormBodyBuilder.add((String) entry.getKey(), (String) entry.getValue());
                        }
                        requestBuilder.post(newFormBodyBuilder.build());
                        request = requestBuilder.build();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return request;
    }

    private boolean isPOST(Request request) {
        return "POST".equalsIgnoreCase(request.method());
    }

    private boolean isGET(Request request) {
        return "GET".equalsIgnoreCase(request.method());
    }

    private Request injectUrlParamsRequest(Request request, Request.Builder requestBuilder, Map<String, String> queryParamsMap) {
        if (queryParamsMap.size() > 0) {
            request = injectParamsIntoUrl(request.url().newBuilder(), requestBuilder, queryParamsMap);
        }
        return request;
    }

    private Response proceedCache(Chain chain, Request request) throws IOException {
        Response response =  chain.proceed(request);
        return response;
//        try {
//            response = chain == null || request == null ? null : chain.proceed(request);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        //Log.e("TAG","-response="+new Gson().toJson(response));
//        return response;
//        Response responseLatest = null;
//        if (NetUtils.isConnected(AppSDK.getContext())) {
//            int maxAge = 30; //有网失效10s
//            responseLatest = response.newBuilder()
//                    .removeHeader("Pragma")
//                    .removeHeader("Cache-Control")
//                    .header("Cache-Control", "public, max-age=" + maxAge)
//                    .build();
//        } else {
//            int maxStale = 60 * 60 * 6; // 没网失效6小时
//            responseLatest = response.newBuilder()
//                    .removeHeader("Pragma")
//                    .removeHeader("Cache-Control")
//                    .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
//                    .build();
//        }
//        return responseLatest;
    }

    private boolean canInjectIntoBody(Request request) {
        if (request == null) {
            return false;
        }
        if (!TextUtils.equals(request.method(), "POST")) {
            return false;
        }
        RequestBody body = request.body();
        if (body == null) {
            return false;
        }
        MediaType mediaType = body.contentType();
        if (mediaType == null) {
            return false;
        }
        return TextUtils.equals(mediaType.subtype(), "x-www-form-urlencoded");
    }

    // func to inject params into url
    private Request injectParamsIntoUrl(HttpUrl.Builder httpUrlBuilder, Request.Builder requestBuilder, Map<String, String> paramsMap) {
        if (paramsMap.size() > 0) {
            Iterator iterator = paramsMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                httpUrlBuilder.addQueryParameter((String) entry.getKey(), (String) entry.getValue());
            }
            requestBuilder.url(httpUrlBuilder.build());
            return requestBuilder.build();
        }

        return null;
    }

    private static String bodyToString(final RequestBody request) {
        try {
            final RequestBody copy = request;
            final Buffer buffer = new Buffer();
            if (copy != null)
                copy.writeTo(buffer);
            else
                return "";
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }


}