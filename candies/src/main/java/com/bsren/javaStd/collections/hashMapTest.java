package com.bsren.javaStd.collections;

import java.util.concurrent.ConcurrentSkipListMap;

public class hashMapTest {
    public static void main(String[] args) {
        ConcurrentSkipListMap<Object, Object> map = new ConcurrentSkipListMap<>();
    }


    static final int MAXIMUM_CAPACITY = 1 << 30;

    static final int tableSizeFor(int cap) {
        int n = cap - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }

}
