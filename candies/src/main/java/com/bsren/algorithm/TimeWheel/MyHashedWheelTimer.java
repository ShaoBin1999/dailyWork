package com.bsren.algorithm.TimeWheel;

import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;
import io.netty.util.concurrent.ImmediateExecutor;
import io.netty.util.internal.PlatformDependent;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLong;

import static io.netty.util.internal.StringUtil.simpleClassName;

@Slf4j
public class MyHashedWheelTimer implements Timer {

    //这三个变量控制着timer的实例数
    private static final AtomicInteger INSTANCE_COUNTER = new AtomicInteger();
    private static final AtomicBoolean WARNED_TOO_MANY_INSTANCES = new AtomicBoolean();
    private static final int INSTANCE_COUNT_LIMIT = 64;
    private static final long MILLISECOND_NANOS = TimeUnit.MILLISECONDS.toNanos(1);

    private static final AtomicIntegerFieldUpdater<MyHashedWheelTimer> WORKER_STATE_UPDATER =
            AtomicIntegerFieldUpdater.newUpdater(MyHashedWheelTimer.class, "workerState");

    private final Worker worker = new Worker();
    private final Thread workerThread;

    public static final int WORKER_STATE_INIT = 0;
    public static final int WORKER_STATE_STARTED = 1;
    public static final int WORKER_STATE_SHUTDOWN = 2;

    private final long tickDuration;
    private final HashedWheelBucket[] wheel;
    private final int mask;
    private final CountDownLatch startTimeInitialized = new CountDownLatch(1);
    //TODO 了解mpscQueue
    private final Queue<HashedWheelTimeout> timeouts = PlatformDependent.newMpscQueue();
    private final Queue<HashedWheelTimeout> cancelledTimeouts = PlatformDependent.newMpscQueue();
    private final AtomicLong pendingTimeouts = new AtomicLong(0);
    private final long maxPendingTimeouts;
    private final Executor taskExecutor;

    private volatile int workerState;

    private volatile long startTime;

    public MyHashedWheelTimer() {
        this(Executors.defaultThreadFactory());
    }

    public MyHashedWheelTimer(long tickDuration, TimeUnit unit) {
        this(Executors.defaultThreadFactory(), tickDuration, unit);
    }

    public MyHashedWheelTimer(long tickDuration, TimeUnit unit, int ticksPerWheel) {
        this(Executors.defaultThreadFactory(), tickDuration, unit, ticksPerWheel);
    }

    public MyHashedWheelTimer(ThreadFactory threadFactory) {
        this(threadFactory, 100, TimeUnit.MILLISECONDS);
    }

    public MyHashedWheelTimer(ThreadFactory threadFactory, long tickDuration, TimeUnit unit) {
        this(threadFactory, tickDuration, unit, 512);
    }

    public MyHashedWheelTimer(ThreadFactory threadFactory, long tickDuration, TimeUnit unit, int ticksPerWheel) {
        this(threadFactory, tickDuration, unit, ticksPerWheel, -1);
    }

    public MyHashedWheelTimer(
            ThreadFactory threadFactory,
            long tickDuration, TimeUnit unit, int ticksPerWheel,
            long maxPendingTimeouts) {
        this(threadFactory, tickDuration, unit, ticksPerWheel, maxPendingTimeouts, ImmediateExecutor.INSTANCE);
    }

    /**
     * make sure params are valid
     *
     * @param threadFactory  用线程工厂来创建线程
     * @param tickDuration   tick的时间，默认为100ms
     * @param unit           tick的单位
     * @param ticksPerWheel  时间轮的大小，默认为512
     * @param maxPendingTimeouts 最大能承载的timeout数量
     * @param taskExecutor   任务的执行器
     */
    public MyHashedWheelTimer(
            ThreadFactory threadFactory,
            long tickDuration,
            TimeUnit unit,
            int ticksPerWheel,
            long maxPendingTimeouts,
            Executor taskExecutor) {
        this.taskExecutor = taskExecutor;
        wheel = createWheel(ticksPerWheel);
        mask = wheel.length-1;
        long duration = unit.toNanos(tickDuration);
        this.tickDuration = Math.max(duration, MILLISECOND_NANOS);
        workerThread = threadFactory.newThread(worker);
        this.maxPendingTimeouts = maxPendingTimeouts;
        if (INSTANCE_COUNTER.incrementAndGet() > INSTANCE_COUNT_LIMIT &&
                WARNED_TOO_MANY_INSTANCES.compareAndSet(false, true)) {
            reportTooManyInstances();
        }
    }


    //TODO
    @Override
    protected void finalize() throws Throwable {
        try {
            super.finalize();
        }finally {
            if (WORKER_STATE_UPDATER.getAndSet(this, WORKER_STATE_SHUTDOWN) != WORKER_STATE_SHUTDOWN) {
                INSTANCE_COUNTER.decrementAndGet();
            }
        }
    }

    private static void reportTooManyInstances() {
        String resourceType = simpleClassName(MyHashedWheelTimer.class);
        log.error("You are creating too many " + resourceType + " instances. " +
                resourceType + " is a shared resource that must be reused across the JVM, " +
                "so that only a few instances are created.");
    }

    private static HashedWheelBucket[] createWheel(int ticksPerWheel) {
        ticksPerWheel = normalizeTicksPerWheel(ticksPerWheel);
        HashedWheelBucket[] wheel = new HashedWheelBucket[ticksPerWheel];
        for (int i = 0; i < wheel.length; i ++) {
            wheel[i] = new HashedWheelBucket();
        }
        return wheel;
    }

    private static int normalizeTicksPerWheel(int ticksPerWheel) {
        int normalizedTicksPerWheel = 1;
        while (normalizedTicksPerWheel < ticksPerWheel) {
            normalizedTicksPerWheel <<= 1;
        }
        return normalizedTicksPerWheel;
    }

    public void start() {
        switch (WORKER_STATE_UPDATER.get(this)) {
            case WORKER_STATE_INIT:
                if (WORKER_STATE_UPDATER.compareAndSet(this, WORKER_STATE_INIT, WORKER_STATE_STARTED)) {
                    workerThread.start();
                }
                break;
            case WORKER_STATE_STARTED:
                break;
            case WORKER_STATE_SHUTDOWN:
                throw new IllegalStateException("cannot be started once stopped");
            default:
                throw new Error("Invalid WorkerState");
        }

        // Wait until the startTime is initialized by the worker.
        while (startTime == 0) {
            try {
                startTimeInitialized.await();
            } catch (InterruptedException ignore) {
                // Ignore - it will be ready very soon.
            }
        }
    }

    @Override
    public Timeout newTimeout(TimerTask task, long delay, TimeUnit unit) {
        long pendingTimeoutsCount = pendingTimeouts.incrementAndGet();

        if (maxPendingTimeouts > 0 && pendingTimeoutsCount > maxPendingTimeouts) {
            pendingTimeouts.decrementAndGet();
            throw new RejectedExecutionException("Number of pending timeouts ("
                    + pendingTimeoutsCount + ") is greater than or equal to maximum allowed pending "
                    + "timeouts (" + maxPendingTimeouts + ")");
        }

        start();

        // Add the timeout to the timeout queue which will be processed on the next tick.
        // During processing all the queued HashedWheelTimeouts will be added to the correct HashedWheelBucket.
        long deadline = System.nanoTime() + unit.toNanos(delay) - startTime;

        // Guard against overflow.
        if (delay > 0 && deadline < 0) {
            deadline = Long.MAX_VALUE;
        }
        HashedWheelTimeout timeout = new HashedWheelTimeout(this, task, deadline);
        timeouts.add(timeout);
        return timeout;
    }

    @Override
    public Set<Timeout> stop() {
        if (Thread.currentThread() == workerThread) {
            throw new IllegalStateException(
                    MyHashedWheelTimer.class.getSimpleName() +
                            ".stop() cannot be called from " +
                            TimerTask.class.getSimpleName());
        }

        if (!WORKER_STATE_UPDATER.compareAndSet(this, WORKER_STATE_STARTED, WORKER_STATE_SHUTDOWN)) {
            // workerState can be 0 or 2 at this moment - let it be always 2.
            if (WORKER_STATE_UPDATER.getAndSet(this, WORKER_STATE_SHUTDOWN) != WORKER_STATE_SHUTDOWN) {
                INSTANCE_COUNTER.decrementAndGet();
            }

            return Collections.emptySet();
        }

        try {
            boolean interrupted = false;
            while (workerThread.isAlive()) {
                workerThread.interrupt();
                try {
                    workerThread.join(100);
                } catch (InterruptedException ignored) {
                    interrupted = true;
                }
            }

            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        } finally {
            INSTANCE_COUNTER.decrementAndGet();
        }
        return worker.unprocessedTimeouts();
    }


    public long pendingTimeouts() {
        return pendingTimeouts.get();
    }


    private final class Worker implements Runnable{

        private final Set<Timeout> unprocessedTimeouts = new HashSet<>();

        private long tick;


        @Override
        public void run() {
            startTime = System.nanoTime();
            if(startTime==0){
                startTime = 1;
            }
            startTimeInitialized.countDown();  //保持timer和worker同时初始化
            do {
                final long deadline = waitForNextTick();
                if(deadline>0){
                    int idx = (int) (tick & mask);
                    processCancelledTasks();       //清除掉这一段时间中被取消的timeout
                    transferTimeoutsToBuckets();   //将这一段时期新添加的timeout加入到bucket中
                    HashedWheelBucket bucket = wheel[idx];
                    bucket.expireTimeouts(deadline);  //获取到tick所在的桶，处理该桶内的所有expire的timeout
                    tick++;   //一直加就完事儿，这里的mask是值得学习的
                }
            }while (WORKER_STATE_UPDATER.get(MyHashedWheelTimer.this)==WORKER_STATE_STARTED);

            //工作线程不再执行任务，被关闭，将所有未执行的任务添加到unprocessedTimeouts中
            for (HashedWheelBucket bucket: wheel){
                bucket.clearTimeouts(unprocessedTimeouts);
            }

            //将这一时段内添加到timeouts中的timeout进行处理
            for (;;){
                HashedWheelTimeout timeout = timeouts.poll();
                if(timeout==null){
                    break;
                }
                if(!timeout.isCancelled()){
                    unprocessedTimeouts.add(timeout);
                }
            }
            processCancelledTasks();
        }

        public Set<Timeout> unprocessedTimeouts() {
            return Collections.unmodifiableSet(unprocessedTimeouts);
        }

        private void transferTimeoutsToBuckets() {
            for (int i=0;i<100000;i++){
                HashedWheelTimeout timeout = timeouts.poll();
                if(timeout==null){
                    break;
                }
                if(timeout.state()==HashedWheelTimeout.ST_CANCELLED){
                    continue;
                }
                long calculated = timeout.deadline/tickDuration;
                timeout.remainingRounds = (calculated-tick)/wheel.length;
                final long ticks = Math.max(calculated,tick);
                int stop = (int) (ticks & mask);
                HashedWheelBucket bucket = wheel[stop];
                bucket.addTimeout(timeout);
            }
        }

        private void processCancelledTasks() {
            for (;;){
                HashedWheelTimeout timeout = cancelledTimeouts.poll();
                if(timeout==null){
                    break;
                }
                try {
                    timeout.remove();
                }catch (Throwable t){
                    log.warn("An exception was thrown while process a cancellation task", t);
                }
            }
        }

        private long waitForNextTick(){
            long deadline = tickDuration*(tick+1);
            for (;;){
                final long currentTime = System.nanoTime()-startTime;
                long sleepTimeMs = (deadline-currentTime+999999)/1000000;
                if(sleepTimeMs<=0){
                    if(currentTime==Long.MIN_VALUE){
                        return -Long.MAX_VALUE;
                    }else {
                        return currentTime;
                    }
                }
                if (PlatformDependent.isWindows()) {
                    sleepTimeMs = sleepTimeMs / 10 * 10;
                    if (sleepTimeMs == 0) {
                        sleepTimeMs = 1;
                    }
                }
                try {
                    Thread.sleep(sleepTimeMs);
                } catch (InterruptedException ignored) {
                    if (WORKER_STATE_UPDATER.get(MyHashedWheelTimer.this) == WORKER_STATE_SHUTDOWN) {
                        return Long.MIN_VALUE;
                    }
                }
            }
        }
    }

    private static final class HashedWheelTimeout implements Timeout,Runnable{

        private static final int ST_INIT = 0;
        private static final int ST_CANCELLED = 1;
        private static final int ST_EXPIRED = 2;

        private static final AtomicIntegerFieldUpdater<HashedWheelTimeout> STATE_UPDATER =
                AtomicIntegerFieldUpdater.newUpdater(HashedWheelTimeout.class, "state");

        private final MyHashedWheelTimer timer;
        private final TimerTask task;
        private final long deadline;

        private volatile int state = ST_INIT;

        long remainingRounds;

        HashedWheelTimeout prev;
        HashedWheelTimeout next;

        HashedWheelBucket bucket;

        HashedWheelTimeout(MyHashedWheelTimer timer,TimerTask task,long deadline){
            this.timer = timer;
            this.task = task;
            this.deadline = deadline;
        }

        public int state() {
            return state;
        }

        @Override
        public Timer timer() {
            return timer;
        }

        @Override
        public TimerTask task() {
            return task;
        }

        @Override
        public boolean isExpired() {
            return state==ST_EXPIRED;
        }

        @Override
        public boolean isCancelled() {
            return state==ST_CANCELLED;
        }

        @Override
        public boolean cancel() {
            if(!STATE_UPDATER.compareAndSet(this,ST_INIT, ST_CANCELLED)){
                return false;
            }
            timer.cancelledTimeouts.add(this);
            return true;
        }

        @Override
        public void run() {
            try {
                task.run(this);
            } catch (Throwable t) {
                log.info("An exception was thrown by " + TimerTask.class.getSimpleName() + '.', t);
            }
        }

        public void expire(){
            if(!STATE_UPDATER.compareAndSet(this,ST_INIT,ST_EXPIRED)){
                return;
            }
            try {
                timer.taskExecutor.execute(this);
            }catch (Throwable t){
                log.info("An exception was thrown while submit " + TimerTask.class.getSimpleName()
                        + " for execution.", t);
            }
        }

        void remove(){
            //bucket在添加timeout的时候进行赋值
            HashedWheelBucket bucket = this.bucket;
            if(bucket!=null){
                bucket.remove(this);
            }else {
                //在删除timeout的时候将该类下pending状态的任务减一，上面的remove也执行了该方法
                timer.pendingTimeouts.decrementAndGet();
            }
        }

        @Override
        public String toString() {
            final long currentTime = System.nanoTime();
            long remaining = deadline - currentTime + timer.startTime;

            StringBuilder buf = new StringBuilder(192)
                    .append(simpleClassName(this))
                    .append('(')
                    .append("deadline: ");
            if (remaining > 0) {
                buf.append(remaining)
                        .append(" ns later");
            } else if (remaining < 0) {
                buf.append(-remaining)
                        .append(" ns ago");
            } else {
                buf.append("now");
            }

            if (isCancelled()) {
                buf.append(", cancelled");
            }

            return buf.append(", task: ")
                    .append(task())
                    .append(')')
                    .toString();
        }
    }

    private static final class HashedWheelBucket {

        private HashedWheelTimeout head;
        private HashedWheelTimeout tail;

        public void addTimeout(HashedWheelTimeout timeout){
            assert timeout.bucket==null;
            timeout.bucket = this;
            if(head==null){
                head = tail = timeout;
            }else {
                tail.next = timeout;
                timeout.prev = tail;
                tail = timeout;
            }
        }

        public HashedWheelTimeout remove(HashedWheelTimeout timeout){
            HashedWheelTimeout next = timeout.next;
            if(timeout.prev!=null){
                timeout.prev.next = next;
            }
            if(timeout.next!=null){
                timeout.next.prev = timeout.prev;
            }
            if(timeout==head){
                if(timeout==tail){
                    head = null;
                    tail = null;
                }else{
                    head = next;
                }
            }else if(timeout == tail){
                tail = timeout.prev;
            }
            timeout.prev = null;
            timeout.next = null;
            timeout.bucket = null;
            timeout.timer.pendingTimeouts.decrementAndGet();
            return next;
        }

        private HashedWheelTimeout pollTimeout(){
            HashedWheelTimeout head = this.head;
            if(head==null){
                return null;
            }
            HashedWheelTimeout next = head.next;
            if(next==null){
                tail = this.head = null;
            }else {
                this.head = next;
                next.prev = null;
            }
            head.next = null;
            head.prev = null;
            head.bucket = null;
            return head;
        }

        /**
         * 将所有的非cancelled和非expired状态的timeout放到给定set中
         * 在worker的状态不再是work的时候调用该方法获取所有未处理的timeout
         */
        public void clearTimeouts(Set<Timeout> set){
            for (;;){
                HashedWheelTimeout pollTimeout = pollTimeout();
                if(pollTimeout==null){
                    return;
                }
                if(pollTimeout.isCancelled() ||  pollTimeout.isExpired()){
                    continue;
                }
                set.add(pollTimeout);
            }
        }

        public void expireTimeouts(long deadline){
            HashedWheelTimeout timeout = head;
            while (timeout!=null){
                HashedWheelTimeout next = timeout.next;
                if(timeout.remainingRounds<=0){
                    next = remove(timeout);
                    if(timeout.deadline <=deadline){
                        timeout.expire();
                    }else {
                        throw new IllegalStateException(String.format(
                                "timeout.deadline (%d) > deadline (%d)", timeout.deadline, deadline));
                    }
                }else if(timeout.isCancelled()){
                    next = remove(timeout);
                }else {
                    timeout.remainingRounds--;
                }
                timeout = next;
            }
        }
    }
}
