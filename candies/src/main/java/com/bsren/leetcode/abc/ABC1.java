package com.bsren.leetcode.abc;

import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ABC1 {

    ReentrantLock lock = new ReentrantLock();
    Condition condition1 = lock.newCondition();
    Condition condition2 = lock.newCondition();
    Condition condition3 = lock.newCondition();
    int num = 1;
    public static void main(String[] args) {

        ABC1 abc1 = new ABC1();
        Thread A = new Thread(new Runnable() {
            @Override
            public void run() {
                Random r = new Random();
                while (true){
                    try {
                        Thread.sleep(r.nextInt(10)*100);
                        abc1.first(new Runnable() {
                            @Override
                            public void run() {
                                System.out.println("A");
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        A.start();
        Thread B = new Thread(new Runnable() {
            @Override
            public void run() {
                Random r = new Random();
                while (true){
                    try {
                        Thread.sleep(r.nextInt(10)*100);
                        abc1.second(new Runnable() {
                            @Override
                            public void run() {
                                System.out.println("B");
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        B.start();
        Thread C = new Thread(new Runnable() {
            @Override
            public void run() {
                Random r = new Random();
                while (true){
                    try {
                        Thread.sleep(r.nextInt(10)*100);
                        abc1.third(new Runnable() {
                            @Override
                            public void run() {
                                System.out.println("C");
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        C.start();
    }

    public void first(Runnable printFirst) throws InterruptedException {
        lock.lock();
        try {
            while (num!=1){
                condition1.await();
            }
            printFirst.run();
            num = 2;
            condition2.signal();
        }finally {
            lock.unlock();
        }

    }

    public void second(Runnable printSecond) throws InterruptedException {
        lock.lock();
        try {
            while (num!=2){
                condition2.await();
            }
            printSecond.run();
            num = 3;
            condition3.signal();
        }finally {
            lock.unlock();
        }

    }

    public void third(Runnable printThird) throws InterruptedException {
        lock.lock();
        try {
            while (num!=3){
                condition3.await();
            }
            printThird.run();
            num = 1;
            condition1.signal();
        }finally {
            lock.unlock();
        }
    }
}
