package com.sn.utils.tuple;

/**
 * 作者:东芝(2018/11/6).
 * 功能:六元组
 */
public class TupleSix<V1, V2, V3, V4, V5, V6> extends TupleFive<V1, V2, V3, V4, V5> {
    private final V6 v6;


    public TupleSix(V1 v1, V2 v2, V3 v3, V4 v4, V5 v5, V6 v6) {
        super(v1, v2, v3, v4, v5);
        this.v6 = v6;
    }

    public V6 getV6() {
        return v6;
    }

    public static <V1, V2, V3, V4, V5, V6> TupleSix<V1, V2, V3, V4, V5, V6> create(V1 v1, V2 v2, V3 v3, V4 v4, V5 v5, V6 v6) {
        return new TupleSix<>(v1, v2, v3, v4, v5, v6);
    }
}
