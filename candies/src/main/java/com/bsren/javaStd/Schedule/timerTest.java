package com.bsren.javaStd.Schedule;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class timerTest {
        public static String getCurrentTime() {
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return sdf.format(date);
        }

        public static void main(String[] args) throws InterruptedException {
            System.out.println("main start:"+getCurrentTime());
            Timer timer = startTimer();
            Thread.sleep(1000*5); //休眠5秒
            System.out.println("main  end:"+getCurrentTime());
            timer.cancel();
        }



        public static Timer startTimer(){
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    System.out.println("task  run:"+getCurrentTime());
                }
            };
            Timer timer = new Timer();
            timer.schedule(task, 0);
            return timer;
        }
}
