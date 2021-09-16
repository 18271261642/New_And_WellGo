package com.sn.utils.tuple;

/**
 * 作者:东芝(2018/11/6).
 * 功能:八元组
 */
public class TupleEight<V1, V2, V3, V4, V5, V6, V7, V8> extends TupleSeven<V1, V2, V3, V4, V5, V6, V7> {
    private final V8 v8;


    public TupleEight(V1 v1, V2 v2, V3 v3, V4 v4, V5 v5, V6 v6, V7 v7, V8 v8) {
        super(v1, v2, v3, v4, v5, v6, v7);
        this.v8 = v8;
    }

    public V8 getV8() {
        return v8;
    }

    public static <V1, V2, V3, V4, V5, V6, V7, V8> TupleEight<V1, V2, V3, V4, V5, V6, V7, V8> create(V1 v1, V2 v2, V3 v3, V4 v4, V5 v5, V6 v6, V7 v7, V8 v8) {
        return new TupleEight<>(v1, v2, v3, v4, v5, v6, v7, v8);
    }
}
