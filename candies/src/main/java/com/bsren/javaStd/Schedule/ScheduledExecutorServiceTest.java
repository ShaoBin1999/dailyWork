package com.bsren.javaStd.Schedule;

import org.junit.jupiter.api.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ScheduledExecutorServiceTest {

    @Test
    public void test1() throws Exception {
        ScheduledExecutorService service = Executors.newScheduledThreadPool(2);
        ScheduledFuture future = service.schedule(() -> {
            try {
                Thread.sleep(3000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("task finish time: " + System.currentTimeMillis());

        }, 1000, TimeUnit.MILLISECONDS);
        System.out.println("schedule finish time: " + System.currentTimeMillis());

        System.out.println("Runnable future's result is: " + future.get() + ", and time is: " + System.currentTimeMillis());
    }
}
