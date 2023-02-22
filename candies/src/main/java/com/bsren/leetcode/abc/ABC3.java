package com.bsren.leetcode.abc;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ABC3 {

    private BlockingQueue<Integer> b1 = new LinkedBlockingQueue<>(1);
    private BlockingQueue<Integer> b2 = new LinkedBlockingQueue<>(1);
    private BlockingQueue<Integer> b3 = new LinkedBlockingQueue<>(1);

    ABC3(){
        b1.offer(1);
    }
    public static void main(String[] args) {

        ABC3 abc1 = new ABC3();
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
        b1.take();
        printFirst.run();
        b2.offer(1);
    }

    public void second(Runnable printSecond) throws InterruptedException {
        b2.take();
        printSecond.run();
        b3.offer(1);
    }

    public void third(Runnable printThird) throws InterruptedException {
        b3.take();
        printThird.run();
        b1.offer(1);
    }
}
