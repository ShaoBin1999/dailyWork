package com.bsren.thread.Ttl;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.threadpool.TtlExecutors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Test {

    static TransmittableThreadLocal<String> transmittableThreadLocal = new TransmittableThreadLocal<>();// 使用TransmittableThreadLocal


    public static void main(String[] args) throws InterruptedException {

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService = TtlExecutors.getTtlExecutorService(executorService); // 用TtlExecutors装饰线程池

        transmittableThreadLocal.set("i am a transmittable parent");
        executorService.execute(new Runnable() {
            @Override
            public void run() {

                System.out.println(transmittableThreadLocal.get());
                transmittableThreadLocal.set("i am a old transmittable parent");// 子线程设置新的值

            }
        });
        System.out.println(transmittableThreadLocal.get());

        TimeUnit.SECONDS.sleep(1);
        transmittableThreadLocal.set("i am a new transmittable parent");// 主线程设置新的值

        executorService.execute(new Runnable() {
            @Override
            public void run() {

                System.out.println(transmittableThreadLocal.get());
            }
        });
    }

}
