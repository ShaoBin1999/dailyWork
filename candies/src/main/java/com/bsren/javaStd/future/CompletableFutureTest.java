package com.bsren.javaStd.future;


import org.junit.jupiter.api.Test;

import java.util.concurrent.*;
import java.util.function.*;

public class CompletableFutureTest {

    ExecutorService service = Executors.newFixedThreadPool(5);

    /**
     * 创建一个completableFuture
     */
    @Test
    public void test1() throws ExecutionException, InterruptedException {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return "haha";
            }
        });
        String s = future.get();
        System.out.println(s);
    }

    /**
     * 处理异常
     */
    @Test
    public void test2() throws ExecutionException, InterruptedException {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                int i = 1 / 0;
                return "haha";
            }
        });
        CompletableFuture<String> exceptionally = future.exceptionally(new Function<Throwable, String>() {
            @Override
            public String apply(Throwable throwable) {
                return "异常";
            }
        });
        System.out.println(exceptionally.join());
    }

    /**
     * 任务执行后执行的回调方法
     * whenComplete无返回值
     * handle有返回值
     */
    @Test
    public void test3(){
        CompletableFuture<String> future = CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return "haha";
            }
        });
        future.whenComplete(new BiConsumer<String, Throwable>() {
            @Override
            public void accept(String s, Throwable throwable) {
                System.out.println("任务完成，结果是"+s);
            }
        });
        CompletableFuture<Object> handle = future.handle(new BiFunction<String, Throwable, Object>() {
            @Override
            public Object apply(String s, Throwable throwable) {
                return "返回结果是" + s;
            }
        });
        System.out.println(future.join());
        System.out.println(handle.join());
    }

    /**
     * 任务有先后顺序，下一个任务无返回值，无传参
     * thenRun与thenRunAsync的区别在于前者的第二个任务使用的是第一个任务的线程池，后者使用的是默认线程池forkjoin
     */
    @Test
    public void test4(){
        CompletableFuture<String> future = CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                try {
                    Thread.sleep(1000);
                    System.out.println("第一个任务执行完毕");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return "haha";
            }
        });
        future.thenRun(new Runnable() {
            @Override
            public void run() {
                System.out.println("开始第二个任务,执行半秒");
                try {
                    Thread.sleep(500);
                    System.out.println("第二个任务结束");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });
        CompletableFuture<Void> s = future.thenRunAsync(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("执行第三个任务1s");
                    Thread.sleep(1000);
                    System.out.println("第三个任务结束");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        s.join();
    }

    /**
     * 任务有先后顺序，有传参，无返回值
     */
    @Test
    public void test5(){
        CompletableFuture<String> future = CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                try {
                    Thread.sleep(1000);
                    System.out.println("任务1结束");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return "haha";
            }
        });
        CompletableFuture<Void> future1 = future.thenAccept(new Consumer<String>() {
            @Override
            public void accept(String s) {
                if ("haha".equals(s)) {
                    System.out.println("第一个任务输出正确");
                }
            }
        });
        CompletableFuture<Void> future2 = future.thenAcceptAsync(new Consumer<String>() {
            @Override
            public void accept(String s) {
                if ("haha".equals(s)) {
                    System.out.println("第一个任务输出正确");
                }
            }
        });
        future1.join();
        future2.join();
    }

    /**
     * 任务有先后顺序，有传参，有返回值
     */
    @Test
    public void test6(){
        CompletableFuture<String> future = CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                try {
                    Thread.sleep(1000);
                    System.out.println("第一个任务执行完毕");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return "haha";
            }
        });
        CompletableFuture<Object> future1 = future.thenApply(new Function<String, Object>() {
            @Override
            public Object apply(String s) {
                System.out.println(s + "666");
                return "rsb";
            }
        });
        Object join = future1.join();
        System.out.println(join);
    }

    /**
     * 多个任务组合
     */
    @Test
    public void test7(){
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                try {
                    Thread.sleep(1000);
                    System.out.println("第一个任务执行完毕");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return "任务1";
            }
        });
        CompletableFuture<String> future2= CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                try {
                    Thread.sleep(1500);
                    System.out.println("第二个任务执行完毕");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return "任务2";
            }
        });
        CompletableFuture<Void> future3 = future1.thenRun(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("任务3完成");
            }
        });
        CompletableFuture<Void> future4 = future2.thenRun(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("任务4完成");
            }
        });
        CompletableFuture<Object> future5 = future1.thenCombine(future2, new BiFunction<String, String, Object>() {
            @Override
            public Object apply(String s, String s2) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return "任务5完成" + s + s2;
            }
        });
        CompletableFuture<Void> allOf = CompletableFuture.allOf(future3, future4, future5);
        CompletableFuture<Object> future = allOf.thenApply(new Function<Void, Object>() {
            @Override
            public Object apply(Void unused) {
                Object join = future5.join();
                System.out.println("获取到任务5的结果"+join);
                return "hhhh" + "所有任务结束咧";
            }
        });
        System.out.println(future.join());
    }

    /**
     * thenCompose
     * thenCompose方法会在某个任务执行完成后，将该任务的执行结果,作为方法入参,去执行指定的方法。
     * 该方法会返回一个新的CompletableFuture实例
     * 如果该CompletableFuture实例的result不为null，则返回一个基于该result新的CompletableFuture实例；
     * 如果该CompletableFuture实例为null，然后就执行这个新任务
     */
    @Test
    public void test8(){
        CompletableFuture<String> f = CompletableFuture.completedFuture("第一个任务");
        //第二个异步任务
        ExecutorService executor = Executors.newSingleThreadExecutor();
        CompletableFuture<String> future = CompletableFuture
                .supplyAsync(() -> "第二个任务", executor)
                .thenComposeAsync(data -> {
                    System.out.println(data);
                    return f; //使用第一个任务作为返回
                }, executor);
        System.out.println(future.join());
        executor.shutdown();
    }
}
