package com.sn.utils.tuple;

/**
 * 作者:东芝(2018/11/6).
 * 功能:三元组
 */
public class TupleThree<V1, V2, V3> extends TupleTwo<V1, V2> {
    private final V3 v3;


    public TupleThree(V1 v1, V2 v2, V3 v3) {
        super(v1, v2);
        this.v3 = v3;
    }

    public V3 getV3() {
        return v3;
    }

    public static <V1, V2, V3> TupleThree<V1, V2, V3> create(V1 v1, V2 v2, V3 v3) {
        return new TupleThree<>(v1, v2, v3);
    }
}
