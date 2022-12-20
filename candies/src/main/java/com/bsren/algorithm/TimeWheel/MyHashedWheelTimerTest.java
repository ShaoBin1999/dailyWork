package com.bsren.algorithm.TimeWheel;

import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public class MyHashedWheelTimerTest {

    @Test
    void test1() throws InterruptedException {
        MyHashedWheelTimer myHashedWheelTimer = new MyHashedWheelTimer();
        System.out.println("start---------------");
        myHashedWheelTimer.newTimeout(new TimerTask() {
            @Override
            public void run(Timeout timeout) throws Exception {
                System.out.println("5s after");
            }
        },5000, TimeUnit.MILLISECONDS);
        myHashedWheelTimer.newTimeout(new TimerTask() {
            @Override
            public void run(Timeout timeout) throws Exception {
                System.out.println("8s after");
            }
        },8000,TimeUnit.MILLISECONDS);
        Thread.sleep(6000);
        Set<Timeout> stop = myHashedWheelTimer.stop();
        for (Timeout timeout : stop) {
            System.out.println(timeout);
        }
    }
}
