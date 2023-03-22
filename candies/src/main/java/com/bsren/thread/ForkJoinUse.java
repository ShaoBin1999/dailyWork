package com.bsren.thread;

import org.junit.jupiter.api.Test;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class ForkJoinUse {

    /**
     * 执行有返回结果的
     */
    @Test
    public  void t1() {
        ForkJoinPool pool = new ForkJoinPool();
        ForkJoinTask<Integer> forkJoinTask = pool.submit(new CalculatedRecursiveTask(0, 10));
        try {
            //根据返回类型获取返回值
            Integer result = forkJoinTask.get();
            System.out.println("结果为：" + result);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private static class CalculatedRecursiveTask extends RecursiveTask<Integer> {
        private int start;
        private int end;
        public CalculatedRecursiveTask(int start, int end) {
            this.start = start;
            this.end = end;
        }
        @Override
        protected Integer compute() {
            //判断计算范围，如果小于等于5，那么一个线程计算就够了，否则进行分割
            if ((end-start)<=5) {
                return IntStream.rangeClosed(start, end).sum();
            }else {
                //任务分割
                int middle = (end+start)/2;
                CalculatedRecursiveTask task1 = new CalculatedRecursiveTask(start,middle);
                CalculatedRecursiveTask task2 = new CalculatedRecursiveTask(middle+1,end);
                //执行
                task1.fork();
                task2.fork();
                //等待返回结果
                return task1.join()+task2.join();
            }
        }
    }


    @Test
    public void t2() throws InterruptedException {
        ForkJoinPool pool = new ForkJoinPool(2);
        pool.submit(new CalculatedRecursiveTaskR(0, 10));
        //等待3秒后输出结果，因为计算需要时间
        pool.awaitTermination(3, TimeUnit.SECONDS);
        System.out.println("结果为："+SUM);
    }


    private static final int MAX_THRESHOLD = 5;
    private static final AtomicInteger SUM = new AtomicInteger(0);

    private static class CalculatedRecursiveTaskR extends RecursiveAction {
        private int start;
        private int end;

        public CalculatedRecursiveTaskR(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        protected void compute() {
            //判断计算范围，如果小于等于5，那么一个线程计算就够了，否则进行分割
            if ((end - start) <= 5) {
                //因为没有返回值，所有这里如果我们要获取结果，需要存入公共的变量中
                SUM.addAndGet(IntStream.rangeClosed(start, end).sum());
            } else {
                //任务分割
                int middle = (end + start) / 2;
                CalculatedRecursiveTaskR task1 = new CalculatedRecursiveTaskR(start, middle);
                CalculatedRecursiveTaskR task2 = new CalculatedRecursiveTaskR(middle + 1, end);
                //执行
                task1.fork();
                task2.fork();
            }
        }
    }
}
