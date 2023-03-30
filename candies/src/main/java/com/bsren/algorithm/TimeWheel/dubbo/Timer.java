package com.bsren.algorithm.TimeWheel.dubbo;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public interface Timer {

    Timeout newTimeout(TimerTask task, long delay, TimeUnit timeUnit);

    Set<Timeout> stop();

    boolean isStop();
}
