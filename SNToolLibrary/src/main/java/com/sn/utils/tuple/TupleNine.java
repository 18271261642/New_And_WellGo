package com.sn.utils.tuple;

/**
 * 作者:东芝(2018/11/6).
 * 功能:九元组
 */
public class TupleNine<V1, V2, V3, V4, V5, V6, V7, V8, V9> extends TupleEight<V1, V2, V3, V4, V5, V6, V7, V8> {
    private final V9 v9;


    public TupleNine(V1 v1, V2 v2, V3 v3, V4 v4, V5 v5, V6 v6, V7 v7, V8 v8, V9 v9) {
        super(v1, v2, v3, v4, v5, v6, v7, v8);
        this.v9 = v9;
    }

    public V9 getV9() {
        return v9;
    }

    public static <V1, V2, V3, V4, V5, V6, V7, V8, V9> TupleNine<V1, V2, V3, V4, V5, V6, V7, V8, V9> create(V1 v1, V2 v2, V3 v3, V4 v4, V5 v5, V6 v6, V7 v7, V8 v8, V9 v9) {
        return new TupleNine<>(v1, v2, v3, v4, v5, v6, v7, v8, v9);
    }
}
