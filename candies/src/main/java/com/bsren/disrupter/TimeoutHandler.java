package com.bsren.disrupter;

/**
 * 用户实现
 */
public interface TimeoutHandler {

    void onTimeout(long sequence) throws Exception;
}
