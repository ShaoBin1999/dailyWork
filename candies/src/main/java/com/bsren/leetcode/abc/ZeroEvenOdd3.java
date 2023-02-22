package com.bsren.leetcode.abc;

import java.util.function.IntConsumer;

public class ZeroEvenOdd3 {

    private final int n;
    int state = 0;

    public ZeroEvenOdd3(int n) {
        this.n = n;
    }

    public static void main(String[] args) {
        ZeroEvenOdd3 zeroEvenOdd1 = new ZeroEvenOdd3(5);
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
        for (int i=0;i<n;i++){
            synchronized (this){
                while (state%2!=0){
                    wait();
                }
                printNumber.accept(0);
                state++;
                notifyAll();
            }
        }
    }

    public void odd(IntConsumer printNumber) throws InterruptedException {
        for (int i=1;i<=n;i+=2){
            synchronized (this){
                while (state!=1){
                    wait();
                }
                printNumber.accept(i);
                state++;
                notifyAll();
            }
        }
    }

    public void even(IntConsumer printNumber) throws InterruptedException {
        for (int i=2;i<=n;i+=2){
            synchronized (this){
                while (state!=3){
                    wait();
                }
                printNumber.accept(i);
                state=0;
                notifyAll();
            }
        }
    }
}
