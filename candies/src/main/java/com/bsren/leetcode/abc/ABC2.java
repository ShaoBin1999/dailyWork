package com.bsren.leetcode.abc;

import java.util.Random;

public class ABC2 {

    volatile int num = 1;
    private Object object = new Object();
    public static void main(String[] args) {

        ABC2 abc1 = new ABC2();
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
        synchronized (object){
            while (num!=1){
                System.out.println("-------------A get lock but num is "+num);
                object.wait();
            }
            System.out.println("-------------A get lock and num is "+num);
            printFirst.run();
            num = 2;
            object.notifyAll();
        }

    }

    public void second(Runnable printSecond) throws InterruptedException {
        synchronized (object){
            while (num!=2){
                System.out.println("-------------B get lock but num is "+num);
                object.wait();
            }
            System.out.println("-------------B get lock and num is "+num);
            printSecond.run();
            num = 3;
            object.notifyAll();
        }

    }

    public void third(Runnable printThird) throws InterruptedException {
        synchronized (object){
            while (num!=3){
                System.out.println("-------------C get lock but num is "+num);
                object.wait();
            }
            System.out.println("-------------C get lock and num is "+num);
            printThird.run();
            num = 1;
            object.notifyAll();
        }
    }
}