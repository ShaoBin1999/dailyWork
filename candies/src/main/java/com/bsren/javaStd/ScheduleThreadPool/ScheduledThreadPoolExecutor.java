package com.bsren.javaStd.ScheduleThreadPool;

import com.bsren.javaStd.threadFactory.ThreadPoolExecutor;
import io.netty.channel.EventLoopGroup;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

//public class ScheduledThreadPoolExecutor extends ThreadPoolExecutor implements ScheduledExecutorService {
//
//
//    private volatile boolean continueExistingPeriodicTasksAfterShutdown;
//
//    private volatile boolean executeExistingDelayedTasksAfterShutdown = true;
//
//    private volatile boolean removeOnCancel = false;
//
//    private static final AtomicLong sequencer = new AtomicLong();
//
//    /**
//     * Returns current nanosecond time.
//     */
//    final long now() {
//        return System.nanoTime();
//    }
//
//    private class ScheduledFutureTask<V>
//            extends FutureTask<V> implements RunnableScheduledFuture<V> {
//
//        /** Sequence number to break ties FIFO */
//        private final long sequenceNumber;
//
//        /** The time the task is enabled to execute in nanoTime units */
//        private long time;
//
//        /**
//         * Period in nanoseconds for repeating tasks.  A positive
//         * value indicates fixed-rate execution.  A negative value
//         * indicates fixed-delay execution.  A value of 0 indicates a
//         * non-repeating task.
//         */
//        private final long period;
//
//        /** The actual task to be re-enqueued by reExecutePeriodic */
//        RunnableScheduledFuture<V> outerTask = this;
//
//        /**
//         * Index into delay queue, to support faster cancellation.
//         */
//        int heapIndex;
//
//        /**
//         * Creates a one-shot action with given nanoTime-based trigger time.
//         */
//        ScheduledFutureTask(Runnable r, V result, long ns) {
//            super(r, result);
//            this.time = ns;
//            this.period = 0;
//            this.sequenceNumber = sequencer.getAndIncrement();
//        }
//
//        /**
//         * Creates a periodic action with given nano time and period.
//         */
//        ScheduledFutureTask(Runnable r, V result, long ns, long period) {
//            super(r, result);
//            this.time = ns;
//            this.period = period;
//            this.sequenceNumber = sequencer.getAndIncrement();
//        }
//
//        /**
//         * Creates a one-shot action with given nanoTime-based trigger time.
//         */
//        ScheduledFutureTask(Callable<V> callable, long ns) {
//            super(callable);
//            this.time = ns;
//            this.period = 0;
//            this.sequenceNumber = sequencer.getAndIncrement();
//        }
//
//        public long getDelay(TimeUnit unit) {
//            return unit.convert(time - now(), NANOSECONDS);
//        }
//
//        public int compareTo(Delayed other) {
//            if (other == this) // compare zero if same object
//                return 0;
//            if (other instanceof java.util.concurrent.ScheduledThreadPoolExecutor.ScheduledFutureTask) {
//                java.util.concurrent.ScheduledThreadPoolExecutor.ScheduledFutureTask<?> x = (java.util.concurrent.ScheduledThreadPoolExecutor.ScheduledFutureTask<?>)other;
//                long diff = time - x.time;
//                if (diff < 0)
//                    return -1;
//                else if (diff > 0)
//                    return 1;
//                else if (sequenceNumber < x.sequenceNumber)
//                    return -1;
//                else
//                    return 1;
//            }
//            long diff = getDelay(NANOSECONDS) - other.getDelay(NANOSECONDS);
//            return (diff < 0) ? -1 : (diff > 0) ? 1 : 0;
//        }
//
//        /**
//         * Returns {@code true} if this is a periodic (not a one-shot) action.
//         *
//         * @return {@code true} if periodic
//         */
//        public boolean isPeriodic() {
//            return period != 0;
//        }
//
//        /**
//         * Sets the next time to run for a periodic task.
//         */
//        private void setNextRunTime() {
//            long p = period;
//            if (p > 0)
//                time += p;
//            else
//                time = triggerTime(-p);
//        }
//
//        public boolean cancel(boolean mayInterruptIfRunning) {
//            boolean cancelled = super.cancel(mayInterruptIfRunning);
//            if (cancelled && removeOnCancel && heapIndex >= 0)
//                remove(this);
//            return cancelled;
//        }
//
//        /**
//         * Overrides FutureTask version so as to reset/requeue if periodic.
//         */
//        public void run() {
//            boolean periodic = isPeriodic();
//            if (!canRunInCurrentRunState(periodic))
//                cancel(false);
//            else if (!periodic)
//                java.util.concurrent.ScheduledThreadPoolExecutor.ScheduledFutureTask.super.run();
//            else if (java.util.concurrent.ScheduledThreadPoolExecutor.ScheduledFutureTask.super.runAndReset()) {
//                setNextRunTime();
//                reExecutePeriodic(outerTask);
//            }
//        }
//    }
//
//    @Override
//    public void execute(Runnable command) {
//
//    }
//
//    @Override
//    public void shutdown() {
//
//    }
//
//    @Override
//    public List<Runnable> shutdownNow() {
//        return null;
//    }
//
//    @Override
//    public boolean isShutdown() {
//        return false;
//    }
//
//    @Override
//    public boolean isTerminated() {
//        return false;
//    }
//
//    @Override
//    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
//        return false;
//    }
//
//    @Override
//    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
//        return null;
//    }
//
//    @Override
//    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
//        return null;
//    }
//
//    @Override
//    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
//        return null;
//    }
//
//    @Override
//    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
//        return null;
//    }
//}
