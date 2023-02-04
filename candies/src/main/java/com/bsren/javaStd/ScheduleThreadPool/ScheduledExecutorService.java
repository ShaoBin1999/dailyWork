package com.bsren.javaStd.ScheduleThreadPool;

import com.bsren.javaStd.threadFactory.ExecutorService;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public interface ScheduledExecutorService extends ExecutorService {


    ScheduledFuture<?> schedule(Runnable command,
                                long delay, TimeUnit unit);

    <V> ScheduledFuture<V> schedule(Callable<V> callable,
                                    long delay, TimeUnit unit);

    ScheduledFuture<?> scheduleAtFixedRate(Runnable command,
                                           long initialDelay,
                                           long period,
                                           TimeUnit unit);

    ScheduledFuture<?> scheduleWithFixedDelay(Runnable command,
                                              long initialDelay,
                                              long delay,
                                              TimeUnit unit);
}
