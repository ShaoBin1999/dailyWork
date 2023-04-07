package com.bsren.disrupter.test;

public class ContendedDemo {
//    @Contended
    public volatile long x;
    public volatile long y;

    public static void main(String[] args) throws InterruptedException {

        ContendedDemo cd = new ContendedDemo();

        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 1000000000L; i++) {
                cd.x = i;
            }
        });

        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 1000000000L; i++) {
                cd.y = i;
            }
        });

        long start = System.currentTimeMillis();
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
        System.out.println(System.currentTimeMillis() - start);

    }

}
