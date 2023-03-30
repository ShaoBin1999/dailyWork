package com.bsren.algorithm.TimeWheel.dubbo;

public interface TimerTask {

    void run(Timeout timeout) throws Exception;
}
