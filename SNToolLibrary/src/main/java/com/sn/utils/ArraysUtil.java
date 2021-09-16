package com.sn.utils;

import java.util.Arrays;
import java.util.List;

/**
 * 作者:东芝(2017/12/16).
 * 功能:数组工具
 */

public class ArraysUtil {
    /**
     * 填充对象
     *
     * @param list
     * @param clazz
     * @param size
     * @param <T>
     */
    public static <T> void fillElement(List<T> list, Class<T> clazz, int size) {
        try {
            for (int i = 0; i < size; i++) {
                list.add(clazz.newInstance());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T> T[] asArrays(T... args) {
        return args;
    }

    public static <T> List<T> asList(T... args) {
        return Arrays.asList(args);
    }
}
