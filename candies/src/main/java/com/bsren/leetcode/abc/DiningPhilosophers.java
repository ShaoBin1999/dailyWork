package com.bsren.leetcode.abc;

import java.util.Arrays;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class DiningPhilosophers {
    boolean[] fork = new boolean[5];
    ReentrantLock lock = new ReentrantLock();
    Condition c = lock.newCondition();

    public DiningPhilosophers() {
        Arrays.fill(fork,true);
    }

    // call the run() method of any runnable to execute its code
    public void wantsToEat(int philosopher,
                           Runnable pickLeftFork,
                           Runnable pickRightFork,
                           Runnable eat,
                           Runnable putLeftFork,
                           Runnable putRightFork) throws InterruptedException {
        lock.lock();
        try {
            if(philosopher==0){
                while (!(fork[4] && fork[0])){
                    c.await();
                }
            }else if(philosopher==4){
                while (!(fork[3] && fork[4])){
                    c.await();
                }
            }else {
                while (!(fork[philosopher-1] && fork[philosopher])){
                    c.await();
                }
            }
            pickLeftFork.run();
            pickRightFork.run();
            eat.run();
            putLeftFork.run();
            putRightFork.run();
            c.signalAll();
        }finally {
            lock.unlock();
        }
    }
}
