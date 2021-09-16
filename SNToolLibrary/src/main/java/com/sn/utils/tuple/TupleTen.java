package com.sn.utils.tuple;

/**
 * 作者:东芝(2018/11/6).
 * 功能:十元组
 */
public class TupleTen<V1, V2, V3, V4, V5, V6, V7, V8, V9, V10> extends TupleNine<V1, V2, V3, V4, V5, V6, V7, V8, V9> {
    private final V10 v10;


    public TupleTen(V1 v1, V2 v2, V3 v3, V4 v4, V5 v5, V6 v6, V7 v7, V8 v8, V9 v9, V10 v10) {
        super(v1, v2, v3, v4, v5, v6, v7, v8, v9);
        this.v10 = v10;
    }

    public V10 getV10() {
        return v10;
    }

    public static <V1, V2, V3, V4, V5, V6, V7, V8, V9, V10> TupleTen<V1, V2, V3, V4, V5, V6, V7, V8, V9, V10> create(V1 v1, V2 v2, V3 v3, V4 v4, V5 v5, V6 v6, V7 v7, V8 v8, V9 v9, V10 v10) {
        return new TupleTen<>(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10);
    }
}
