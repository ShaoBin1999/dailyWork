package com.bsren.javaStd.condition;

import org.junit.jupiter.api.Test;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ConditionPrintABC {

    ReentrantLock lock = new ReentrantLock();
    Condition c1 = lock.newCondition();
    Condition c2 = lock.newCondition();
    Condition c3 = lock.newCondition();

    private int num = 1;  //初始状态，从A开始

    @Test
    void test(){
        Thread threadA = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i=0;i<10;i++){
                    try {
                        printA();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        },"A");
        Thread threadB = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i=0;i<10;i++){
                    try {
                        printB();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        },"B");
        Thread threadC = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i=0;i<10;i++){
                    try {
                        printC();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        },"C");
        threadA.start();
        threadB.start();
        threadC.start();
    }

    public void printA() throws InterruptedException {
        lock.lock();
        try {
            while (num!=1){
                c1.await();  //await的时候会解锁
            }
            System.out.println(Thread.currentThread().getName());
            num=2;
            c2.signal();
        }finally {
            lock.unlock();
        }
    }

    public void printB() throws InterruptedException {
        lock.lock();
        try {
            while (num!=2){
                c2.await();
            }
            System.out.println(Thread.currentThread().getName());
            num = 3;
            c3.signal();
        }finally {
            lock.unlock();
        }
    }

    public void printC() throws InterruptedException {
        lock.lock();
        try {
            while (num!=3){
                c3.await();
            }
            System.out.println(Thread.currentThread().getName());
            num = 1;
            c1.signal();
        }finally {
            lock.unlock();
        }
    }
}


