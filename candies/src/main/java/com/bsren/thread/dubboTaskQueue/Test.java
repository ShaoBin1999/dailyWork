package com.bsren.thread.dubboTaskQueue;

import cn.hutool.core.thread.NamedThreadFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Test {

    /**
     * 因为只有一个coreThread,所有在提交一个任务后core就满掉了
     * 再提交任务的时候只能用非core线程来代替
     */
    @org.junit.jupiter.api.Test
    public void testEager() throws InterruptedException {
        TaskQueue<Runnable> taskQueue = new TaskQueue<>(5);
        EagerThreadPoolExecutor executor = new EagerThreadPoolExecutor(
                1,2,10, TimeUnit.SECONDS,taskQueue
                ,new NamedThreadFactory("my",true),
                new ThreadPoolExecutor.DiscardPolicy()
        );
        taskQueue.setExecutor(executor);
        executor.execute(new Task());
        executor.execute(new Task());
        while (true){
            System.out.println(executor.getActiveCount());
            Thread.sleep(200);
        }
    }

    /**
     * 一般的线程池会在coreThread满了后将任务放到队列中，
     * 等队列满了再创建非coreThread
     * @throws InterruptedException
     */
    @org.junit.jupiter.api.Test
    public void testJUC() throws InterruptedException {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                1,2,10,TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(5),new NamedThreadFactory("my",true),
                new ThreadPoolExecutor.DiscardPolicy()
        );
        executor.execute(new Task());
        executor.execute(new Task());
        while (true){
            System.out.println(executor.getActiveCount());
            Thread.sleep(200);
        }
    }

}

class Task implements Runnable{

    @Override
    public void run() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
