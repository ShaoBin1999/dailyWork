package com.bsren.leetcode.abc;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class H2O {
    Semaphore s1 = new Semaphore(2);
    Semaphore s2 = new Semaphore(1);
    private CyclicBarrier c = new CyclicBarrier(3, new Runnable() {
        @Override
        public void run() {
            s1.release(2);
            s2.release();
        }
    });

    public H2O() {
    }

    public void hydrogen(Runnable releaseHydrogen) throws InterruptedException {
        s1.acquire();
        try {
            c.await();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
        releaseHydrogen.run();
    }

    public void oxygen(Runnable releaseOxygen) throws InterruptedException {
        s2.acquire();
        try {
            c.await();
        }catch (BrokenBarrierException e){
            e.printStackTrace();
        }
        releaseOxygen.run();
    }
}
