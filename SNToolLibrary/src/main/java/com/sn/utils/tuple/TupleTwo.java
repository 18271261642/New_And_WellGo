package com.sn.utils.tuple;

/**
 * 作者:东芝(2018/11/6).
 * 功能:二元组
 */
public class TupleTwo<V1, V2>{
    private final V1 v1;
    private final V2 v2;


    public TupleTwo(V1 v1, V2 v2) {
        this.v1 = v1;
        this.v2 = v2;
    }

    public V1 getV1() {
        return v1;
    }

    public V2 getV2() {
        return v2;
    }

    public static <V1, V2> TupleTwo<V1, V2> create(V1 v1, V2 v2) {
        return new TupleTwo<V1, V2>(v1, v2);
    }
}
