package com.bsren.javaStd.threadFactory;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

public interface ExecutorService extends Executor{

    void shutdown();

    List<Runnable> shutdownNow();

    boolean isShutdown();

    boolean isTerminated();

    boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException;

    <T> Future<T> submit(Callable<T> task);

    <T> Future<T> submit(Runnable task, T result);

    Future<?> submit(Runnable task);

//    <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks)
//            throws InterruptedException;
//
//    <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks,
//                                  long timeout, TimeUnit unit)
//            throws InterruptedException;
//
//    <T> T invokeAny(Collection<? extends Callable<T>> tasks)
//            throws InterruptedException, ExecutionException;
//
//    <T> T invokeAny(Collection<? extends Callable<T>> tasks,
//                    long timeout, TimeUnit unit)
//            throws InterruptedException, ExecutionException, TimeoutException;
}
