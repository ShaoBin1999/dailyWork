package com.bsren.juc;

import java.util.concurrent.locks.LockSupport;

public class SuspendTest {
    public static Object u = new Object();

    public static class MyThread extends Thread{
        MyThread(String name){
            super(name);
        }

        @Override
        public void run() {
            synchronized (u){
                System.out.println("in "+getName());
                Thread.currentThread().suspend();
            }

        }
    }

    static LockSupportTest.MyThread myThread1 = new LockSupportTest.MyThread("t1");
    static LockSupportTest.MyThread myThread2 = new LockSupportTest.MyThread("t2");


    public static void main(String[] args) throws InterruptedException {
        myThread1.start();
        myThread2.start();
        Thread.sleep(1000);
        myThread1.resume();
        Thread.sleep(1000);
        myThread2.resume();
        myThread1.join();
        myThread2.join();
    }

}
