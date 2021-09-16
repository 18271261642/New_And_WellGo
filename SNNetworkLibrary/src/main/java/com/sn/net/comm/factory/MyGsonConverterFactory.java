package com.sn.net.comm.factory;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.sn.net.BuildConfig;
import com.sn.net.utils.JsonUtil;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * 作者:东芝(2017/8/7).
 * 描述:GSON转换器 适配php工程师(董睿)的后台
 */
public class MyGsonConverterFactory extends Converter.Factory {
    /**
     * Create an instance using a default {@link Gson} instance for conversion. Encoding to JSON and
     * decoding from JSON (when no charset is specified by a header) will use UTF-8.
     */
    public static MyGsonConverterFactory create() {

        return new MyGsonConverterFactory(JsonUtil.getGson());
    }


    private final Gson gson;

    private MyGsonConverterFactory(Gson gson) {
        this.gson = gson;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
                                                            Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new MyGsonResponseBodyConverter(gson, adapter);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type,
                                                          Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new MyGsonResponseBodyConverter(gson, adapter);
    }
}

final class MyGsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final Gson gson;
    private final TypeAdapter<T> adapter;

    MyGsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        String data = value.string();
        if (data.contains("\"data\":[]") || data.contains("\"data\":{}")) {//修复董睿的后台json 成功和错误data字段数据类型不统一的bug
            try {
                JSONObject object = new JSONObject(data);
                object.remove("data");//如果数据为空  data移除掉 不解析 避免Gson无法解析
                data = object.toString();
            } catch (Exception ignored) {
            }
        }

        try {
            //取消严格数据类型审查   gson.newJsonReader()换成 new JsonReader() 的方式适配ios  的非严格字符串数字
            // 比如 ios json 说只能实现 ["1","2","3"] 而安卓可以[1,2,3]和["1","2","3"] 但为了兼容ios的数据
            JsonReader jsonReader = new JsonReader(new InputStreamReader(new ByteArrayInputStream(data.getBytes())));
            T read = adapter.read(jsonReader);
            return read;
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                System.err.println("json= "+data);
                e.printStackTrace();
            }
            throw e;
        } finally {
            value.close();
        }

    }
}
