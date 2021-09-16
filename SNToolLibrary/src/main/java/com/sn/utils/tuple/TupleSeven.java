package com.sn.utils.tuple;

/**
 * 作者:东芝(2018/11/6).
 * 功能:七元组
 */
public class TupleSeven<V1, V2, V3, V4, V5, V6, V7> extends TupleSix<V1, V2, V3, V4, V5, V6> {
    private final V7 v7;


    public TupleSeven(V1 v1, V2 v2, V3 v3, V4 v4, V5 v5, V6 v6, V7 v7) {
        super(v1, v2, v3, v4, v5, v6);
        this.v7 = v7;
    }

    public V7 getV7() {
        return v7;
    }

    public static <V1, V2, V3, V4, V5, V6, V7> TupleSeven<V1, V2, V3, V4, V5, V6, V7> create(V1 v1, V2 v2, V3 v3, V4 v4, V5 v5, V6 v6, V7 v7) {
        return new TupleSeven<>(v1, v2, v3, v4, v5, v6, v7);
    }
}
