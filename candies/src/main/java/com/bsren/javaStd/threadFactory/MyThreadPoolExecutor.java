package com.bsren.javaStd.threadFactory;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MyThreadPoolExecutor extends AbstractExecutorService{

    private final AtomicInteger ctl = new AtomicInteger(ctlOf(RUNNING, 0));
    private static final int COUNT_BITS = Integer.SIZE - 3;
    private static final int CAPACITY   = (1 << COUNT_BITS) - 1;

    private static final int RUNNING    = -1 << COUNT_BITS;
    private static final int SHUTDOWN   =  0 << COUNT_BITS;  //不再接受新任务，但是处理队列中的任务
    private static final int STOP       =  1 << COUNT_BITS;  //不再接受新任务或者处理队列中的任务，并会中断正在执行的任务
    private static final int TIDYING    =  2 << COUNT_BITS;  //所有的任务已经没了，工作线程数为0，准备terminate
    private static final int TERMINATED =  3 << COUNT_BITS;  //terminated()方法已经执行完毕

    private static int runStateOf(int c)     { return c & ~CAPACITY; }
    private static int workerCountOf(int c)  { return c & CAPACITY; }
    private static int ctlOf(int rs, int wc) { return rs | wc; }

    private static boolean runStateLessThan(int c, int s) {
        return c < s;
    }

    private static boolean runStateAtLeast(int c, int s) {
        return c >= s;
    }

    private static boolean isRunning(int c) {
        return c < SHUTDOWN;
    }

    private boolean compareAndIncrementWorkerCount(int expect) {
        return ctl.compareAndSet(expect, expect + 1);
    }

    private boolean compareAndDecrementWorkerCount(int expect) {
        return ctl.compareAndSet(expect, expect - 1);
    }

    private void decrementWorkerCount() {
        do {} while (! compareAndDecrementWorkerCount(ctl.get()));
    }

    private volatile ThreadFactory threadFactory;

//    private final BlockingQueue<Runnable> workQueue;

    private final ReentrantLock mainLock = new ReentrantLock();

    private final HashSet<Worker> workers = new HashSet<Worker>();

    private final Condition termination = mainLock.newCondition();

    private int largestPoolSize;

    private long completedTaskCount;

    private volatile RejectedExecutionHandler handler;

    private volatile long keepAlive;

    private volatile boolean allowCoreThreadTimeOut;

    private volatile int corePoolSize;

    private static final RejectedExecutionHandler defaultHandler = new ThreadPoolExecutor.AbortPolicy();

    private final class Worker extends AbstractQueuedSynchronizer implements Runnable{
        
        final Thread thread;

        Runnable firstTask;

        volatile long completedTasks;

        Worker(Runnable firstTask){
            setState(-1);
            this.firstTask = firstTask;
            this.thread = getThreadFactory().newThread(this);
        }

        protected boolean tryAcquire(int unused){
            if(compareAndSetState(0,1)){
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }

        protected boolean isHeldExclusively(){
            return getState()!=0;
        }

        protected boolean tryRelease(int unused){
            setExclusiveOwnerThread(null);
            setState(0);
            return true;
        }

        @Override
        public void run() {
//            runWorker(this);
        }

        public void lock() {
            acquire(1);
        }

        public boolean tryLock() {
            return tryAcquire(1);
        }

        public void unlock() {
            release(1);
        }

        public boolean isLocked() {
            return isHeldExclusively();
        }

        void interruptIfStarted(){
            Thread t;
            if(getState()>=0 && (t = thread)!=null && !t.isInterrupted()){
                try{
                    t.interrupt();
                }catch (Exception ignored){}
            }
        }

    }




    public void setThreadFactory(ThreadFactory threadFactory){
        if(threadFactory==null){
            throw new NullPointerException();
        }
        this.threadFactory = threadFactory;
    }

    public ThreadFactory getThreadFactory(){
        return threadFactory;
    }

    public void setRejectedExecutionHandler(RejectedExecutionHandler handler) {
        if (handler == null)
            throw new NullPointerException();
        this.handler = handler;
    }

    public RejectedExecutionHandler getRejectedExecutionHandler() {
        return handler;
    }







    @Override
    public void execute(Runnable command) {

    }

    @Override
    public void shutdown() {

    }

    @Override
    public List<Runnable> shutdownNow() {
        return null;
    }

    @Override
    public boolean isShutdown() {
        return false;
    }

    @Override
    public boolean isTerminated() {
        return false;
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return false;
    }
}
