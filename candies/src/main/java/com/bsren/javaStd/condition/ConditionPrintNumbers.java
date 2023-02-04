package com.bsren.javaStd.condition;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.IntConsumer;

/**
 * 交替打印奇偶数，并在打印一个数后打印0
 */
public class ConditionPrintNumbers {

    @Test
    void test(){
        ReentZeroEvenOdd reentZeroEvenOdd = new ReentZeroEvenOdd(50);
        Thread threadZero = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    reentZeroEvenOdd.zero(System.out::println);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        Thread threadOdd = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    reentZeroEvenOdd.odd(System.out::println);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        Thread threadEven = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    reentZeroEvenOdd.even(System.out::println);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        threadZero.start();
        threadOdd.start();
        threadEven.start();
    }
}

class ReentZeroEvenOdd{
    private int n;
    private AtomicInteger cur = new AtomicInteger(1);
    AtomicInteger flag = new AtomicInteger(1);
    private ReentrantLock lock = new ReentrantLock();
    final Condition zeroCondition = lock.newCondition();
    final Condition evenCondition = lock.newCondition();
    final Condition oddCondition = lock.newCondition();

    public ReentZeroEvenOdd(int n){
        this.n = n;
    }

    public void zero(IntConsumer print) throws InterruptedException {
        lock.lock();
        try {
            while (cur.get()<=n){
                while (flag.get()!=0){
                    zeroCondition.await();
                }
                if(cur.get()>n){
                    break;
                }
                print.accept(0);
                if ((cur.get() & 1) == 1) {
                    flag.compareAndSet(0, 1);
                    oddCondition.signal();
                } else {
                    flag.compareAndSet(0, 2);
                    evenCondition.signal();
                }
            }
            //最后释放
            oddCondition.signal();
            evenCondition.signal();
        }finally {
            lock.unlock();
        }
    }

    public void odd(IntConsumer print) throws InterruptedException {
        lock.lock();
        try {
            while (cur.get()<=n){
                while (flag.get()!=1){
                    oddCondition.await();
                }
                if(cur.get()>n){
                    break;
                }
                print.accept(cur.getAndIncrement());
                flag.compareAndSet(1, 0);
                zeroCondition.signal();
            }
        }finally {
            lock.unlock();
        }
    }

    public void even(IntConsumer print) throws InterruptedException {
        lock.lock();
        try {
            while (cur.get()<=n){
                while (flag.get()!=2){
                    evenCondition.await();
                }
                if(cur.get()>n){
                    break;
                }
                print.accept(cur.getAndIncrement());
                flag.compareAndSet(2, 0);
                zeroCondition.signal();
            }
        }finally {
            lock.unlock();
        }
    }
}
