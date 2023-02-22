package com.bsren.leetcode.abc;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.IntConsumer;

public class FizzBuzz {

    private int n;

    private ReentrantLock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();
    private static AtomicInteger a = new AtomicInteger(1);

    public FizzBuzz(int n) {
        this.n = n;
    }

    public static void main(String[] args) {
        FizzBuzz f = new FizzBuzz(15);
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    f.fizz(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("fizz");
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    f.buzz(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("buzz");
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        Thread t3 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    f.fizzbuzz(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("fuzzbuzz");
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        Thread t4 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    f.number(new IntConsumer() {
                        @Override
                        public void accept(int value) {
                            System.out.println(value);
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t1.start();
        t2.start();
        t3.start();
        t4.start();
    }

    // printFizz.run() outputs "fizz".
    public void fizz(Runnable printFizz) throws InterruptedException {
        while (a.get()<=n){
            lock.lock();
            try {
                while ((a.get() % 3 != 0 || a.get() % 15 == 0) && a.get()<=n) {
                    condition.await();
                }
                if(a.get()>n){
                    break;
                }
                printFizz.run();
                a.getAndIncrement();
                condition.signalAll();
            }finally {
                lock.unlock();
            }
        }
    }

    // printBuzz.run() outputs "buzz".
    public void buzz(Runnable printBuzz) throws InterruptedException {
        while (a.get()<=n){
            lock.lock();
            try {
                while ((a.get() % 5 != 0 || a.get() % 15 == 0) && a.get()<=n){
                    condition.await();
                }
                if(a.get()>n){
                    break;
                }
                printBuzz.run();
                a.getAndIncrement();
                condition.signalAll();
            }finally {
                lock.unlock();
            }
        }
    }

    // printFizzBuzz.run() outputs "fizzbuzz".
    public void fizzbuzz(Runnable printFizzBuzz) throws InterruptedException {
        while (a.get()<=n){
            lock.lock();
            try {
                while ((a.get() % 15 != 0) && a.get()<=n){
                    condition.await();
                }
                if(a.get()>n){
                    break;
                }
                printFizzBuzz.run();
                a.getAndIncrement();
                condition.signalAll();
            }finally {
                lock.unlock();
            }
        }
    }

    // printNumber.accept(x) outputs "x", where x is an integer.
    public void number(IntConsumer printNumber) throws InterruptedException {
        while (a.get()<=n){
            lock.lock();
            try {
                while ((a.get()%3==0 || a.get()%5==0) && a.get()<=n){
                    condition.await();
                }
                if(a.get()>n){
                    break;
                }
                printNumber.accept(a.getAndIncrement());
                condition.signalAll();
            }finally {
                lock.unlock();
            }
        }
    }

}
