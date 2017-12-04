package com.xxm.toolbox.clock;

public class TwoTuple<T, K> extends Tuple<T> {
    public K _2;

    public TwoTuple(T _1, K _2) {
        super(_1);
        this._2 = _2;
    }
}
