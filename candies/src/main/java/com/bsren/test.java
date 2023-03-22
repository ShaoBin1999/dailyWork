package com.bsren;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.text.ChoiceFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledExecutorService;

public class test {

    private Object object = new Object();

    @Test
    public void test0(){
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMinimumIntegerDigits(20);
        numberFormat.setMaximumFractionDigits(0);
        String format = numberFormat.format(11.11);
        System.out.println(format);
    }


    @Test
    public void test1() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        synchronized (object){
                            System.out.println(countDownLatch.getCount());
                            System.out.println("haha");
                        }
                        countDownLatch.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Random random = new Random();
                    try {
                        Thread.sleep(1000* random.nextInt(10));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        thread.start();
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    synchronized (object){
                        System.out.println(countDownLatch.getCount());
                        System.out.println("hihi");
                    }
                    countDownLatch.countDown();
                    Random random = new Random();
                    try {
                        Thread.sleep(1000* random.nextInt(10));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }}
        });
        thread1.start();
        Thread.sleep(100000);
    }
    public static void main(String[] args) throws InterruptedException {
        Thread t = new MyThread();
        t.start();
        Thread.sleep(1000);
        t.interrupt(); // 中断t线程
        t.join(); // 等待t线程结束
        System.out.println("end");
    }
}


class MyThread extends Thread {
    public void run() {
        Thread hello = new HelloThread();
        hello.start(); // 启动hello线程
        try {
            hello.join(); // 等待hello线程结束
        } catch (InterruptedException e) {
            System.out.println("interrupted!");
        }
        hello.interrupt();
    }
}

class HelloThread extends Thread {
    public void run() {
        int n = 0;
        while (!isInterrupted()) {
            n++;
            System.out.println(n + " hello!");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}