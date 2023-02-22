package com.bsren.leetcode.abc;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.function.IntConsumer;

public class ZeroEvenOdd5 {

    private final int n;
    BlockingQueue<Integer> b1 = new LinkedBlockingQueue<>(1);
    BlockingQueue<Integer> b2 = new LinkedBlockingQueue<>(1);
    BlockingQueue<Integer> b3 = new LinkedBlockingQueue<>(1);

    public ZeroEvenOdd5(int n) {
        this.n = n;
        b1.offer(1);
    }

    public static void main(String[] args) {
        ZeroEvenOdd5 zeroEvenOdd1 = new ZeroEvenOdd5(5);
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
            Integer take = b1.take();
            printNumber.accept(0);
            if(take==1){
                b2.offer(i);
            }else{
                b3.offer(i);
            }
        }

    }

    public void odd(IntConsumer printNumber) throws InterruptedException {
        for (int i=1;i<=n;i+=2){
            Integer take = b2.take();
            printNumber.accept(take);
            b1.offer(2);
        }
    }

    public void even(IntConsumer printNumber) throws InterruptedException {
        for (int i=2;i<=n;i+=2){
            Integer take = b3.take();
            printNumber.accept(take);
            b1.offer(1);
        }
    }
}
