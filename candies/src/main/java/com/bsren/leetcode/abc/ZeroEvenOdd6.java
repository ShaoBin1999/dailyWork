package com.bsren.leetcode.abc;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntConsumer;

public class ZeroEvenOdd6 {

    volatile AtomicInteger a = new AtomicInteger(1);
    private final int n;

    public ZeroEvenOdd6(int n) {
        this.n = n;
    }

    public static void main(String[] args) {
        ZeroEvenOdd6 zeroEvenOdd1 = new ZeroEvenOdd6(5);
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    zeroEvenOdd1.zero(new IntConsumer() {
                        @Override
                        public void accept(int value) {
                            System.out.println(value);
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"t1");
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    zeroEvenOdd1.odd(new IntConsumer() {
                        @Override
                        public void accept(int value) {
                            System.out.println(value);
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"t2");
        Thread thread3 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    zeroEvenOdd1.even(new IntConsumer() {
                        @Override
                        public void accept(int value) {
                            System.out.println(value);
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"t3");
        thread1.start();
        thread2.start();
        thread3.start();
    }

    // printNumber.accept(x) outputs "x", where x is an integer.
    public void zero(IntConsumer printNumber) throws InterruptedException {
        for (int i=1;i<=n;i++){
            while (a.get()%2==0){
                Thread.yield();
            }
            printNumber.accept(0);
            a.getAndIncrement();
        }
    }

    public void odd(IntConsumer printNumber) throws InterruptedException {
        for (int i=1;i<=n;i+=2){
            while (a.get()%4!=2){
                Thread.yield();
            }
            printNumber.accept(i);
            a.getAndIncrement();
        }
    }

    public void even(IntConsumer printNumber) throws InterruptedException {
        for (int i=2;i<=n;i+=2){
            while (a.get()%4!=0){
                Thread.yield();
            }
            printNumber.accept(i);
            a.getAndIncrement();
        }
    }
}
