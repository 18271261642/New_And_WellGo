package com.sn.utils.tuple;

/**
 * 作者:东芝(2018/11/6).
 * 功能:五元组
 */
public class TupleFive<V1, V2, V3, V4, V5> extends TupleFour<V1, V2, V3, V4> {
    private final V5 v5;


    public TupleFive(V1 v1, V2 v2, V3 v3, V4 v4, V5 v5) {
        super(v1, v2, v3, v4);
        this.v5 = v5;
    }

    public V5 getV5() {
        return v5;
    }

    public static <V1, V2, V3, V4, V5> TupleFive<V1, V2, V3, V4, V5> create(V1 v1, V2 v2, V3 v3, V4 v4, V5 v5) {
        return new TupleFive<>(v1, v2, v3, v4, v5);
    }
}
