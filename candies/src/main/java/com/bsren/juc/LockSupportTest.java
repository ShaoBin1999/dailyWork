package com.bsren.juc;

import com.lmax.disruptor.dsl.Disruptor;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;

public class LockSupportTest {



    public static Object u = new Object();

    public static class MyThread extends Thread{
        MyThread(String name){
            super(name);
        }

        @Override
        public void run() {
            synchronized (u){
                System.out.println("in "+getName());
                LockSupport.park();
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println("被中断了");
                }
                System.out.println("继续执行");
            }

        }
    }

    public static void main(String[] args) throws InterruptedException {
        MyThread myThread1 = new MyThread("t1");
        MyThread myThread2 = new MyThread("t2");
        myThread1.start();
        Thread.sleep(5000);
        myThread2.start();
        Thread.sleep(2000);
        LockSupport.unpark(myThread1);
//        LockSupport.unpark(myThread2);
        myThread1.join();
        myThread2.join();;
    }


}
