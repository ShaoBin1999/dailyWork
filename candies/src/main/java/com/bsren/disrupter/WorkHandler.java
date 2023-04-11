package com.bsren.disrupter;

/**
 * 用户实现
 */
public interface WorkHandler<T> {
    void onEvent(T event) throws Exception;
}
