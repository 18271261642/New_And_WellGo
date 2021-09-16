package com.sn.utils.tuple;

/**
 * 作者:东芝(2018/11/6).
 * 功能:四元组
 */
public class TupleFour<V1, V2, V3, V4> extends TupleThree<V1, V2, V3> {
    private final V4 v4;


    public TupleFour(V1 v1, V2 v2, V3 v3, V4 v4) {
        super(v1, v2, v3);
        this.v4 = v4;
    }

    public V4 getV4() {
        return v4;
    }

    public static <V1, V2, V3, V4> TupleFour<V1, V2, V3, V4> create(V1 v1, V2 v2, V3 v3, V4 v4) {
        return new TupleFour<>(v1, v2, v3, v4);
    }
}
