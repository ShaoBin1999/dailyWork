package com.bsren.leetcode;

public class S31 {

    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(1);
                }
            }
        });
        thread.setDaemon(true);
        thread.start();

        Thread.sleep(2000);
    }


    public void nextPermutation(int[] nums) {

    }


}
