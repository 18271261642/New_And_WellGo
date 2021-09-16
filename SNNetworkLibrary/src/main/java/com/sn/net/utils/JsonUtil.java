package com.sn.net.utils;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * by东芝 2016/4/2.
 */
public class JsonUtil {
    public static String toJson(Object obj) {
        return getGson().toJson(obj);
    }

    public static <T> T toBean(String json, Class<T> classOfT) {
        return getGson().fromJson(json, classOfT);
    }

    @NonNull
    public static Gson getGson() {
        return new GsonBuilder()
                .registerTypeAdapter(int.class, new IntTypeAdapter())
                .registerTypeAdapter(Integer.class, new IntTypeAdapter()).create();
    }

    public static <T> List<T> toListBean(String json, Type listType) {
        try {
            return getGson().fromJson(json, listType);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> List<T> toListBean(String json, Class<T> t) {
        List<T> list = new ArrayList<>();
        JsonParser parser = new JsonParser();
        JsonArray jsonarray = parser.parse(json).getAsJsonArray();
        for (JsonElement element : jsonarray
                ) {
            list.add(getGson().fromJson(element, t));
        }
        return list;
    }

    /**
     * 别想为毛这么做 是为了适配IOS的数据而定下的
     * 纯数字的int数组 转成 String数字数组 [0,0,2]->["0","0","2"]
     *
     * @param jsonArrayString
     */
    public static JSONArray makeJsonStringIntArray(String jsonArrayString) {
        JSONArray newJsonArray = new JSONArray();
        try {
            JSONArray jsonArray = new JSONArray(jsonArrayString);
            for (int i = 0; i < jsonArray.length(); i++) {
                //转String 的int... 蛋疼的做法 不要怪我...
                newJsonArray.put(String.valueOf(jsonArray.getInt(i)));
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return newJsonArray;
    }

    /**
     * 倒序
     *
     * @param arr
     * @return
     * @throws JSONException
     */
    public static JSONArray reverse(JSONArray arr) throws JSONException {
        JSONArray sort = new JSONArray();
        for (int i = arr.length() - 1; i >= 0; i--) {
            sort.put(arr.get(i));
        }
        return sort;
    }

    public static List<JSONObject> toArrays(JSONArray array) throws JSONException {
        List<JSONObject> jsonObjects = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            jsonObjects.add(array.getJSONObject(i));
        }
        return jsonObjects;
    }
}
