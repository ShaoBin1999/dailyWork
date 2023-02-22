package com.bsren.leetcode.abc;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

public class ABC4 {


    private Semaphore s1 = new Semaphore(1);
    private Semaphore s2 = new Semaphore(0);
    private Semaphore s3 = new Semaphore(0);

    public static void main(String[] args) {

        ABC4 abc1 = new ABC4();
        Thread A = new Thread(new Runnable() {
            @Override
            public void run() {
                Random r = new Random();
                while (true){
                    try {
//                        Thread.sleep(r.nextInt(10)*100);
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
//                        Thread.sleep(r.nextInt(10)*100);
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
//                        Thread.sleep(r.nextInt(10)*100);
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
        s1.acquire();
        printFirst.run();
        s2.release();
    }

    public void second(Runnable printSecond) throws InterruptedException {
        s2.acquire();
        printSecond.run();
        s3.release();
    }

    public void third(Runnable printThird) throws InterruptedException {
        s3.acquire();
        printThird.run();
        s1.release();
    }
}
