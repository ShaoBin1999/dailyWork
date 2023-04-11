package com.bsren.disrupter;

public interface DataProvider<T>{
    T get(long sequence);
}
