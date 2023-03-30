package com.bsren.algorithm.TimeWheel.dubbo;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.ClassUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 这是一个轮盘，指针不断从一个位置移到下一个位置
 * 如果该Bucket上面的remainTime大于0的话就减一，如果等于0的话就将该任务取出并执行
 * 有几个关键的点
 * 1.给定任务的过期时间，确定任务在轮盘中的位置和剩余圈数
 * 2.在任务过期的时候将任务从bucket中移出并执行
 * 3.worker线程应不断循环如下操作
 *   1. 处理一个时间片内被取消的任务
 *   2. 将该时间片内的任务添加到bucket中
 *   3. 处理该指针指向bucket的任务，过期则执行，否则圈数-1
 *   4. 指针移动到下一个位置
 *   5. 休眠一定的时间
 */
@Slf4j
public class HashedWheelTimer implements Timer{

    private static final AtomicInteger INSTANCE_COUNTER = new AtomicInteger();
    private static final AtomicBoolean WARNED_TOO_MANY_INSTANCES = new AtomicBoolean();
    private static final int INSTANCE_COUNT_LIMIT = 64;
    private static final AtomicIntegerFieldUpdater<HashedWheelTimer> WORKER_STATE_UPDATER =
            AtomicIntegerFieldUpdater.newUpdater(HashedWheelTimer.class, "workerState");
    private volatile int workerState;

    private static final int WORKER_STATE_INIT = 0;
    private static final int WORKER_STATE_STARTED = 1;
    private static final int WORKER_STATE_SHUTDOWN = 2;


    private final Thread workerThread;
    private final Worker worker = new Worker();


    //一个tick的时长
    private final long tickDuration;

    //wheel轮
    private final HashedWheelBucket[] wheel;
    private final int mask;

    //系统开始的时间
    private volatile long startTime;

    //被取消的任务，任务会首先被修改状态，然后来到这里，但是并不会从bucket中清除
    private final Queue<HashedWheelTimeout> cancelledTimeouts = new LinkedBlockingQueue<>();
    //提交后还未执行的任务数，统计量
    private final AtomicLong pendingTimeouts = new AtomicLong(0);

    //在worker启动后timer才启动
    private final CountDownLatch startTimeInitialized = new CountDownLatch(1);

    //任务首先会被添加到这里
    private final Queue<HashedWheelTimeout> timeouts = new LinkedBlockingQueue<>();

    private final long maxPendingTimeouts;

    public HashedWheelTimer() {
        this(Executors.defaultThreadFactory());
    }

    public HashedWheelTimer(long tickDuration, TimeUnit unit) {
        this(Executors.defaultThreadFactory(), tickDuration, unit);
    }

    public HashedWheelTimer(long tickDuration, TimeUnit unit, int ticksPerWheel) {
        this(Executors.defaultThreadFactory(), tickDuration, unit, ticksPerWheel);
    }

    public HashedWheelTimer(ThreadFactory threadFactory) {
        this(threadFactory, 100, TimeUnit.MILLISECONDS);
    }

    public HashedWheelTimer(
            ThreadFactory threadFactory, long tickDuration, TimeUnit unit) {
        this(threadFactory, tickDuration, unit, 512);
    }

    public HashedWheelTimer(
            ThreadFactory threadFactory,
            long tickDuration, TimeUnit unit, int ticksPerWheel) {
        this(threadFactory, tickDuration, unit, ticksPerWheel, -1);
    }

    public HashedWheelTimer(
            ThreadFactory threadFactory,
            long tickDuration, TimeUnit unit, int ticksPerWheel,
            long maxPendingTimeouts) {

        if (threadFactory == null) {
            throw new NullPointerException("threadFactory");
        }
        if (unit == null) {
            throw new NullPointerException("unit");
        }
        if (tickDuration <= 0) {
            throw new IllegalArgumentException("tickDuration must be greater than 0: " + tickDuration);
        }
        if (ticksPerWheel <= 0) {
            throw new IllegalArgumentException("ticksPerWheel must be greater than 0: " + ticksPerWheel);
        }

        // Normalize ticksPerWheel to power of two and initialize the wheel.
        wheel = createWheel(ticksPerWheel);
        mask = wheel.length - 1;

        // Convert tickDuration to nanos.
        this.tickDuration = unit.toNanos(tickDuration);

        // Prevent overflow.
        if (this.tickDuration >= Long.MAX_VALUE / wheel.length) {
            throw new IllegalArgumentException(String.format(
                    "tickDuration: %d (expected: 0 < tickDuration in nanos < %d",
                    tickDuration, Long.MAX_VALUE / wheel.length));
        }
        workerThread = threadFactory.newThread(worker);

        this.maxPendingTimeouts = maxPendingTimeouts;

        if (INSTANCE_COUNTER.incrementAndGet() > INSTANCE_COUNT_LIMIT &&
                WARNED_TOO_MANY_INSTANCES.compareAndSet(false, true)) {
            reportTooManyInstances();
        }
    }

    private static HashedWheelTimer.HashedWheelBucket[] createWheel(int ticksPerWheel) {
        if (ticksPerWheel <= 0) {
            throw new IllegalArgumentException(
                    "ticksPerWheel must be greater than 0: " + ticksPerWheel);
        }
        if (ticksPerWheel > 1073741824) {
            throw new IllegalArgumentException(
                    "ticksPerWheel may not be greater than 2^30: " + ticksPerWheel);
        }

        ticksPerWheel = normalizeTicksPerWheel(ticksPerWheel);
        HashedWheelBucket[] wheel = new HashedWheelBucket[ticksPerWheel];
        for (int i = 0; i < wheel.length; i++) {
            wheel[i] = new HashedWheelBucket();
        }
        return wheel;
    }

    private static int normalizeTicksPerWheel(int ticksPerWheel) {
        int normalizedTicksPerWheel = ticksPerWheel - 1;
        normalizedTicksPerWheel |= normalizedTicksPerWheel >>> 1;
        normalizedTicksPerWheel |= normalizedTicksPerWheel >>> 2;
        normalizedTicksPerWheel |= normalizedTicksPerWheel >>> 4;
        normalizedTicksPerWheel |= normalizedTicksPerWheel >>> 8;
        normalizedTicksPerWheel |= normalizedTicksPerWheel >>> 16;
        return normalizedTicksPerWheel + 1;
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
        if (task == null) {
            throw new NullPointerException("task");
        }
        if (unit == null) {
            throw new NullPointerException("unit");
        }

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
                    HashedWheelTimer.class.getSimpleName() +
                            ".stop() cannot be called from " +
                            TimerTask.class.getSimpleName());
        }

        if (!WORKER_STATE_UPDATER.compareAndSet(this, WORKER_STATE_STARTED, WORKER_STATE_SHUTDOWN)) {
            // workerState can be 0 or 2 at this moment - let it always be 2.
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

            //如果workerThread被中断，则该线程也向上抛出异常
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        } finally {
            INSTANCE_COUNTER.decrementAndGet();
        }
        return worker.unprocessedTimeouts();
    }

    @Override
    public boolean isStop() {
        return WORKER_STATE_SHUTDOWN == WORKER_STATE_UPDATER.get(this);
    }

    private static void reportTooManyInstances() {
        String resourceType = ClassUtils.simpleClassName(org.apache.dubbo.common.timer.HashedWheelTimer.class);
        log.error("You are creating too many " + resourceType + " instances. " +
                resourceType + " is a shared resource that must be reused across the JVM," +
                "so that only a few instances are created.");
    }


    private final class Worker implements Runnable{
        private final Set<Timeout> unprocessedTimeouts = new HashSet<Timeout>();

        private long tick;


        @Override
        public void run() {
            startTime = Math.max(System.nanoTime(),1);

            startTimeInitialized.countDown();

            do {
                final long deadline = waitForNextTick();
                if(deadline>0){
                    int idx = (int) (tick & mask);
                    processCancelledTasks();
                    HashedWheelBucket bucket = wheel[idx];
                    transferTimeoutsToBuckets();
                    bucket.expireTimeouts(deadline);
                    tick++;  //迈向下一个bucket
                }
            }while (WORKER_STATE_UPDATER.get(HashedWheelTimer.this)==WORKER_STATE_STARTED);

            //工作结束，将wheel中未处理的timeout放到unprocessedTimeout中
            for (HashedWheelBucket bucket:wheel){
                bucket.clearTimeouts(unprocessedTimeouts);
            }
            //工作结束，将timeouts中未处理的timeout放到unprocessedTimeout中
            for (;;){
                HashedWheelTimeout timeout = timeouts.poll();
                if(timeout==null){
                    break;
                }
                if(!timeout.isCancelled()){
                    unprocessedTimeouts.add(timeout);
                }
            }
            //处理这些任务
            processCancelledTasks();
        }

        /**
         * 从timeout中获取到所有的timeout,计算剩余的轮数，计算在wheel中的位置，并放进去
         */
        private void transferTimeoutsToBuckets(){
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
                // Ensure we don't schedule for past.
                final long ticks = Math.max(calculated,tick);
                int stopIndex = (int) (ticks & mask);
                HashedWheelBucket bucket = wheel[stopIndex];
                bucket.addTimeout(timeout);
            }
        }

        /**
         * 从 cancelledTimeouts获取到timeout,将它从bucket移除（如果在bucket中）
         * 同时将pendingTimeouts计数减一
         */
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
                long sleepTimeMs = (deadline - currentTime + 999999) / 1000000;
                if(sleepTimeMs<=0){
                    if(currentTime==Long.MAX_VALUE){
                        return -Long.MAX_VALUE;
                    }else {
                        return currentTime;
                    }
                }
                try {
                    Thread.sleep(sleepTimeMs);
                } catch (InterruptedException e) {
                    if (WORKER_STATE_UPDATER.get(HashedWheelTimer.this) == WORKER_STATE_SHUTDOWN) {
                        return Long.MIN_VALUE;
                    }
                }

            }
        }

        Set<Timeout> unprocessedTimeouts() {
            return Collections.unmodifiableSet(unprocessedTimeouts);
        }
    }

    private static final class HashedWheelTimeout implements Timeout{

        //初始状态，取消状态，过期状态
        private static final int ST_INIT = 0;
        private static final int ST_CANCELLED = 1;
        private static final int ST_EXPIRED = 2;

        private static final AtomicIntegerFieldUpdater<HashedWheelTimeout> STATE_UPDATER =
                AtomicIntegerFieldUpdater.newUpdater(HashedWheelTimeout.class, "state");

        private final HashedWheelTimer timer;
        private final TimerTask task;
        private final long deadline;

        private volatile int state = ST_INIT;

        long remainingRounds;

        HashedWheelTimeout next;
        HashedWheelTimeout prev;

        HashedWheelBucket bucket;

        HashedWheelTimeout(HashedWheelTimer timer,TimerTask task,long deadline){
            this.timer = timer;
            this.task = task;
            this.deadline = deadline;
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
        public boolean cancel() {
            if(!compareAndSetState(ST_INIT,ST_CANCELLED)){
                return false;
            }
            timer.cancelledTimeouts.add(this);
            return true;
        }

        public boolean compareAndSetState(int expected, int state) {
            return STATE_UPDATER.compareAndSet(this, expected, state);
        }

        void remove(){
            HashedWheelBucket bucket = this.bucket;
            if(bucket!=null){
                bucket.remove(this);
            }else {
                timer.pendingTimeouts.decrementAndGet();
            }
        }

        public int state(){
            return state();
        }

        @Override
        public boolean isCancelled() {
            return state()==ST_CANCELLED;
        }

        @Override
        public boolean isExpired() {
            return state()==ST_EXPIRED;
        }

        public void expire(){
            if(!compareAndSetState(ST_INIT,ST_EXPIRED)){
                return;
            }
            try {
                task.run(this);
            } catch (Throwable t) {
                log.warn("An exception was thrown by " + TimerTask.class.getSimpleName() + '.', t);
            }
        }

        @Override
        public String toString() {
            final long currentTime = System.nanoTime();
            long remaining = deadline - currentTime + timer.startTime;
            String simpleClassName = ClassUtils.simpleClassName(this.getClass());

            StringBuilder sb = new StringBuilder(192);
            sb.append(simpleClassName)
                    .append('(')
                    .append("deadline: ");
            if(remaining>0){
                sb.append(remaining).append(" ns later");
            }else if(remaining<0){
                sb.append(-remaining).append(" ns ago");
            }else {
                sb.append("now");
            }
            if(isCancelled()){
                sb.append(", cancelled");
            }
            return sb.append(", task")
                    .append(task())
                    .append(')')
                    .toString();
        }


    }

    private static final class HashedWheelBucket {

        private HashedWheelTimeout head;
        private HashedWheelTimeout tail;

        void addTimeout(HashedWheelTimeout timeout){
            assert timeout.bucket == null;
            timeout.bucket = this;
            if(head==null){
                head = tail = timeout;
            }else {
                tail.next = timeout;
                timeout.prev = tail;
                tail = timeout;
            }
        }

        void expireTimeouts(long deadline){
            HashedWheelTimeout timeout = head;
            while (timeout!=null){
                HashedWheelTimeout next = timeout.next;
                if(timeout.remainingRounds<=0){
                    next = remove(timeout);
                    if(timeout.deadline<=deadline){
                        timeout.expire();
                    }else {
                        // The timeout was placed into a wrong slot. This should never happen.
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
                    head = tail = null;
                }else {
                    head = next;
                }
            }else if(timeout==tail){
                tail = timeout.prev;
            }
            timeout.prev = null;
            timeout.next = null;
            timeout.bucket = null;
            timeout.timer.pendingTimeouts.decrementAndGet();
            return next;
        }

        void clearTimeouts(Set<Timeout> set){
            for (;;){
                HashedWheelTimeout timeout = pollTimeout();
                if(timeout==null){
                    return;
                }
                if(timeout.isCancelled() || timeout.isExpired()){
                    continue;
                }
                set.add(timeout);
            }
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
            head.prev = null;
            head.next = null;
            head.bucket = null;
            return head;
        }

    }




}
