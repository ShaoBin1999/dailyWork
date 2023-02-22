package com.bsren.leetcode.abc;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.IntConsumer;

public class ZeroEvenOdd2 {

    ReentrantLock lock = new ReentrantLock();
    Condition c = lock.newCondition();

    private final int n;
    int state = 0;

    public ZeroEvenOdd2(int n) {
        this.n = n;
    }

    public static void main(String[] args) {
        ZeroEvenOdd2 zeroEvenOdd1 = new ZeroEvenOdd2(5);
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
            lock.lock();
            try {
                while (state>=1){
                    c.await();
                }
                printNumber.accept(0);
                if(state==0){
                    state = 1;
                }else{
                    state = 2;
                }
                c.signalAll();
            }finally {
                lock.unlock();
            }
        }
    }

    public void odd(IntConsumer printNumber) throws InterruptedException {
        for (int i=1;i<=(1+n)/2;i++){
            lock.lock();
            try {
                while (state!=1){
                    c.await();
                }
                printNumber.accept(i*2-1);
                state = -1;
                c.signalAll();
            }finally {
                lock.unlock();
            }
        }
    }

    public void even(IntConsumer printNumber) throws InterruptedException {
        for (int i=1;i<=n/2;i++){
            lock.lock();
            try {
                while (state != 2) {
                    c.await();
                }
                printNumber.accept(i*2);
                state = 0;
                c.signalAll();
            } finally {
                lock.unlock();
            }
        }
    }
}
