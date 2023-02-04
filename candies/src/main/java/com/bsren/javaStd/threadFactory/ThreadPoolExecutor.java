package com.bsren.javaStd.threadFactory;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadPoolExecutor extends AbstractExecutorService {



    //状态和workers数的合体，对它的操作是原子的
    private final AtomicInteger ctl = new AtomicInteger(ctlOf(RUNNING, 0));
    private static final int COUNT_BITS = Integer.SIZE - 3;
    private static final int CAPACITY   = (1 << COUNT_BITS) - 1;
    // runState is stored in the high-order bits
    /**
     * 状态转移
     * running -> shutdown: invocation of shutdown()
     * running or shutdown -> stop: invocation of shutdownNow()
     * shutdown -> tidying: 队列和工作线程都清零了
     * stop -> tidying: 工作线程清零
     * tidying -> terminated: terminated()方式执行完毕，执行awaitTermination方法的线程会返回
     * 检测从SHUTDOWN到TIDYING的转换不像您想的那么简单，因为队列可能在非空状态后变为空，反之亦然，
     * 但我们只能在看到它为空后才终止，workerCount为0(有时需要重新检查——参见下文)
     */
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

    /**
     * 减少ctl的workerCount字段。循环尝试cas直到成功位置
     * 这只在线程突然终止时调用(参见processWorkerExit)。在getTask中执行其他递减操作。
     */
    private void decrementWorkerCount() {
        do {} while (! compareAndDecrementWorkerCount(ctl.get()));
    }

    private volatile ThreadFactory threadFactory;

    private final BlockingQueue<Runnable> workQueue;

    private final ReentrantLock mainLock = new ReentrantLock();

    private final HashSet<Worker> workers = new HashSet<>();

    private final Condition termination = mainLock.newCondition();

    private int largestPoolSize;

    private long completedTaskCount;

    private volatile RejectedExecutionHandler handler;

    /**
     * 等待工作的空闲线程的超时时间(纳秒)。
     * 线程在corePoolSize大于或allowCoreThreadTimeOut时使用此超时。否则他们会永远等待新的工作。
     */
    private volatile long keepAliveTime;

    private volatile boolean allowCoreThreadTimeOut;

    private volatile int corePoolSize;

    private volatile int maximumPoolSize;

    private static final RejectedExecutionHandler defaultHandler = new AbortPolicy();

    /**
     * 将线程包装为工作线程，提供加锁和解锁，以及中断
     * 在创建worker可携带task
     * 同时记录了该线程完成的任务数量
     *
     * 该类继承了AQS,可以中断正在等待的线程，而不能中断正在运行的线程
     * 实现了一个简单的不可重入的互斥锁，而不使用可重入锁，
     * 因为不希望在setCorePoolSize等线程池的控制方法中重新获取锁
     * 简单的方法就实现了功能(state，AQS的一个成员变量，0,1表示解锁和加锁状态)没必要用可重入锁
     * 在初始状态下锁为负值，避免此时被中断，并且在启动时将其清除
     * 在 interruptIdleWorkers()方法中，会tryLock，如果线程正在运行，则返回false
     * 表示线程正在执行任务，不能被中断
     */
    private final class Worker extends AbstractQueuedSynchronizer implements Runnable {

        final Thread thread;

        Runnable firstTask;

        volatile long completedTasks;

        Worker(Runnable firstTask) {
            setState(-1);    //初始状态
            this.firstTask = firstTask;
            this.thread = getThreadFactory().newThread(this);
        }

        //0和1两个状态，0表示unlock，1表示lock
        protected boolean tryAcquire(int unused) {
            if (compareAndSetState(0, 1)) {
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }

        protected boolean isHeldExclusively() {
            return getState() != 0;
        }

        protected boolean tryRelease(int unused) {
            setExclusiveOwnerThread(null);
            setState(0);
            return true;
        }


        @Override
        public void run() {
            runWorker(this);
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

        //中断线程
        void interruptIfStarted() {
            Thread t;
            if (getState() >= 0 && (t = thread) != null && !t.isInterrupted()) {
                try {
                    t.interrupt();
                } catch (Exception ignore) {
                }
            }
        }
    }

    private void interruptWorkers() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            for (ThreadPoolExecutor.Worker w : workers)
                w.interruptIfStarted();
        } finally {
            mainLock.unlock();
        }
    }

    final void runWorker(Worker w){
        Thread t = Thread.currentThread();
        Runnable task = w.firstTask;
        w.firstTask = null;
        w.unlock();
        boolean completedAbruptly = true;
        try {
            //首次任务，以及从workQueue中拿的数据
            while (task!=null || (task=getTask())!=null){
                w.lock();
                // If pool is stopping, ensure thread is interrupted;
                // if not, ensure thread is not interrupted.  This
                // requires a recheck in second case to deal with
                // shutdownNow race while clearing interrupt
                // if成立的必要条件-> 线程是非中断的
                // runStateAtLeast(ctl.get(),STOP)->如果状态为stop,则工作线程不能执行任务了
                // Thread.interrupted() 当前线程是否处于中断状态，并清除掉该状态，这时候再调用t.isInterrupted会返回false
                // todo 这里感觉省代码了，没必要这样写判断条件
                if((runStateAtLeast(ctl.get(),STOP) || (Thread.interrupted() && runStateAtLeast(ctl.get(),STOP)))
                && !t.isInterrupted()){
                    t.interrupt();
                }
                try {
                    beforeExecute(t,task);
                    Throwable thrown = null;
                    try {
                        task.run();
                    }catch (RuntimeException x){
                        thrown = x;
                        throw x;
                    }catch (Error x){
                        thrown = x;
                        throw x;
                    }catch (Throwable x){
                        thrown = x;
                        throw new Error(x);
                    }finally {
                        afterExecute(task,thrown);
                    }
                }finally {
                    task = null;
                    //completedTasks是volatile的意义：
                    //因为后面有统计的功能，我猜是为了多线程操作下的顺序性
                    w.completedTasks++;
                    w.unlock();
                }
            }
            completedAbruptly = false;
        }finally {
            processWorkerExit(w, completedAbruptly);
        }
    }

    /**
     * 如果是因为异常退出的，则需要将worker数目减一
     * 计算总的task完成数，将worker从workers中move
     * 然后尝试terminate，因为线程池可能被关闭了或者发生了异常情况
     * 如果并没有关闭线程池，说明线程池是运行良好的，那么可能需要替换掉该废弃的线程
     * 如果线程池状态是ok的，该线程因为某些异常退出，则检查线程池线程数量，在满足条件的情况下进行添加
     */
    private void processWorkerExit(Worker w, boolean completedAbruptly) {
        if (completedAbruptly) // If abrupt, then workerCount wasn't adjusted
            decrementWorkerCount();

        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            completedTaskCount += w.completedTasks;
            workers.remove(w);
        } finally {
            mainLock.unlock();
        }

        tryTerminate();

        int c = ctl.get();
        if (runStateLessThan(c, STOP)) {
            if (!completedAbruptly) {
                int min = allowCoreThreadTimeOut ? 0 : corePoolSize;
                if (min == 0 && ! workQueue.isEmpty())
                    min = 1;
                if (workerCountOf(c) >= min)
                    return; // replacement not needed
            }
            addWorker(null, false);
        }
    }

    private void afterExecute(Runnable task, Throwable thrown) {
    }

    private void beforeExecute(Thread t, Runnable task) {
    }


    /**
     * @param targetState     SHUTDOWN OR STOP
     */
    private void advanceRunState(int targetState) {
        for (;;) {
            int c = ctl.get();
            if (runStateAtLeast(c, targetState) || ctl.compareAndSet(c, ctlOf(targetState, workerCountOf(c))))
                break;
        }
    }

    /**
     * 如果(SHUTDOWN且池和队列为空)或(STOP且池为空)，
     * 则转换到TERMINATED状态。
     * 如果符合终止条件但workerCount非零，
     * 则中断一个空闲的worker以确保关闭信号传播。
     * 必须在任何可能导致终止的操作之后调用此方法——在关闭期间减少工作人员数量或从队列中删除任务。
     * 该方法是非私有的，允许ScheduledThreadPoolExecutor访问。
     */
    final void tryTerminate() {
        for (;;) {
            int c = ctl.get();

            // case1: 运行中的状态running是不会到terminated的
            // case2: 如果已经tidying了，则它会自动到terminated阶段，无需额外操作
            // case3: 如果是shutdown状态，则必须等到任务队列全部清零才行
            if (isRunning(c) ||
                    runStateAtLeast(c, TIDYING) ||
                    (runStateOf(c) == SHUTDOWN && ! workQueue.isEmpty()))
                return;
            //如果是stop状态，或者shutdown状态但是任务队列已经清零
            //这两个状态是无法直接转到terminated状态，必须先走到tidying状态
            //也就是说其中的工作线程数量还没有清零，那么这里会尝试关闭一个空闲的工作线程并直接返回
            //对于一个线程，如果没有任务执行的话，就可能自动关闭，执行tryTerminate方法
            //所以只关闭一个线程也是可以把整个pool给停掉的，前提是允许核心线程超时退出，否则核心线程就会一直循环等待
            if (workerCountOf(c) != 0) { // Eligible to terminate
                interruptIdleWorkers(ONLY_ONE);
                return;
            }

            final ReentrantLock mainLock = this.mainLock;
            mainLock.lock();
            try {
                if (ctl.compareAndSet(c, ctlOf(TIDYING, 0))) {
                    try {
                        terminated();
                    } finally {
                        ctl.set(ctlOf(TERMINATED, 0));
                        termination.signalAll();
                    }
                    return;
                }
            } finally {
                mainLock.unlock();
            }
            // else retry on failed CAS
        }
    }

    private void terminated() {
    }

    /**
     * 中断可能正在等待任务的线程（没有持有锁）
     *
     * @param onlyOne
     */
    private void interruptIdleWorkers(boolean onlyOne) {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            for (ThreadPoolExecutor.Worker w : workers) {
                Thread t = w.thread;
                if (!t.isInterrupted() && w.tryLock()) {
                    try {
                        t.interrupt();
                    } catch (SecurityException ignore) {
                    } finally {
                        w.unlock();
                    }
                }
                if (onlyOne)
                    break;
            }
        } finally {
            mainLock.unlock();
        }
    }

    private void interruptIdleWorkers() {
        interruptIdleWorkers(false);
    }

    private static final boolean ONLY_ONE = true;

    final void reject(Runnable command) {
        handler.rejectedExecution(command, this);
    }

    void onShutdown() {
    }

    final boolean isRunningOrShutdown(boolean shutdownOK) {
        int rs = runStateOf(ctl.get());
        return rs == RUNNING || (rs == SHUTDOWN && shutdownOK);
    }

    /**
     * 将任务队列排入一个新列表，通常使用drainTo。
     * 但如果队列是一个DelayQueue或任何其他类型的队列，轮询或drainTo可能无法删除某些元素，
     * 则会逐个删除它们。
     */
    private List<Runnable> drainQueue() {
        BlockingQueue<Runnable> q = workQueue;
        ArrayList<Runnable> taskList = new ArrayList<Runnable>();
        q.drainTo(taskList);
        if (!q.isEmpty()) {
            for (Runnable r : q.toArray(new Runnable[0])) {
                if (q.remove(r))
                    taskList.add(r);
            }
        }
        return taskList;
    }

    /**
     * -----------------------------
     * addWorker的条件：
     * 1. shutdown状态虽然不能添加新的任务，但是依然可以添加新的工作线程
     *    但是必须要求不能携带任务，并且workQueue不能是空了的，因为这样就不需要再添加新的工作线程处理任务了
     * 2. 如果是核心线程，则不能超过核心线程数
     * 3. 如果是工作线程，则不能超过max线程数
     * 然后尝试将工作线程数加一，成功则跳出retry，否则重新进入循环
     * -----------------------------
     *
     *
     * @param firstTask
     * @param core
     * @return
     */
    private boolean addWorker(Runnable firstTask, boolean core) {
        retry:
        for (;;) {
            int c = ctl.get();
            int rs = runStateOf(c);
            if (rs >= SHUTDOWN &&
                     (rs != SHUTDOWN || firstTask != null || workQueue.isEmpty()))
                return false;

            for (;;) {
                int wc = workerCountOf(c);
                if (wc >= CAPACITY ||
                        wc >= (core ? corePoolSize : maximumPoolSize))
                    return false;
                if (compareAndIncrementWorkerCount(c))
                    break retry;
                c = ctl.get();  // Re-read ctl
                if (runStateOf(c) != rs)
                    continue retry;
                // else CAS failed due to workerCount change; retry inner loop
            }
        }

        boolean workerStarted = false;
        boolean workerAdded = false;
        Worker w = null;
        try {
            w = new Worker(firstTask);
            final Thread t = w.thread;
            if (t != null) {
                final ReentrantLock mainLock = this.mainLock;
                mainLock.lock();
                try {
                    // Recheck while holding lock.
                    // Back out on ThreadFactory failure or if
                    // shut down before lock acquired.
                    int rs = runStateOf(ctl.get());
                    // 再次检查
                    if (rs < SHUTDOWN || (rs == SHUTDOWN && firstTask == null)) {
                        if (t.isAlive()) // precheck that t is startable
                            throw new IllegalThreadStateException();
                        workers.add(w);
                        int s = workers.size();
                        if (s > largestPoolSize)
                            largestPoolSize = s;
                        workerAdded = true;
                    }
                } finally {
                    mainLock.unlock();
                }
                if (workerAdded) {
                    t.start();
                    workerStarted = true;
                }
            }
        } finally {
            if (!workerStarted)
                addWorkerFailed(w);
        }
        return workerStarted;
    }

    private void addWorkerFailed(Worker w) {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            if (w != null)   //其实这里workers也不一定会有w
                workers.remove(w);
            decrementWorkerCount();
            tryTerminate();  //添加工作线程失败了，哪里除了问题，尝试terminated
        } finally {
            mainLock.unlock();
        }
    }

    /**
     * 如果状态大于stop则不能接受任务
     * 如果状态是shutdown而且任务队列是空的，也不能接受任务
     * @return
     */
    private Runnable getTask() {
        boolean timedOut = false; // Did the last poll() time out?

        for (;;) {
            int c = ctl.get();
            int rs = runStateOf(c);

            // Check if queue empty only if necessary.
            if (rs >= SHUTDOWN && (rs >= STOP || workQueue.isEmpty())) {
                decrementWorkerCount();
                return null;
            }

            int wc = workerCountOf(c);

            // Are workers subject to culling?
            boolean timed = allowCoreThreadTimeOut || wc > corePoolSize;

            // if成立的前提条件：工作线程至少为2或者workQueue是空的
            // 在这个前提下，有两种可能，
            // 1是当前线程大于了最大线程数，这时候一定会减少线程数量
            // 如果当前线程数量并没有超过最大线程数，并且发生了超时等待
            // 那么看看有没有可以减少工作线程的可能:
            // 确实有，如果允许核心线程超时，或者存在非核心线程
            // 那么就可以减少核心线程（允许超时的情况下发生了超时）
            // 或者减少非核心线程（已经超时等待了，那你这个线程还有什么用）
            if ((wc > maximumPoolSize || (timed && timedOut)) && (wc > 1 || workQueue.isEmpty())) {
                if (compareAndDecrementWorkerCount(c))  //todo  关闭线程和设置线程数不统一
                    return null;
                continue;
            }

            try {
                //如果获取超时了，则设置timeout为true，这时的timed应该也是true
                //因为take是一个NotNull返回
                Runnable r = timed ?
                        workQueue.poll(keepAliveTime, TimeUnit.NANOSECONDS) :
                        workQueue.take();
                if (r != null)
                    return r;
                timedOut = true;
            } catch (InterruptedException retry) {
                timedOut = false;
            }
        }
    }

    /**
     * make sure valid params
     *
     * @param corePoolSize
     * @param maximumPoolSize
     * @param keepAliveTime
     * @param unit
     * @param workQueue
     * @param threadFactory
     * @param handler
     */
    public ThreadPoolExecutor(int corePoolSize,
                              int maximumPoolSize,
                              long keepAliveTime,
                              TimeUnit unit,
                              BlockingQueue<Runnable> workQueue,
                              ThreadFactory threadFactory,
                              RejectedExecutionHandler handler){
        this.corePoolSize = corePoolSize;
        this.maximumPoolSize = maximumPoolSize;
        this.workQueue = workQueue;
        this.keepAliveTime = unit.toNanos(keepAliveTime);
        this.threadFactory = threadFactory;
        this.handler = handler;
    }

    /**
     * 1. 如果当前线程数小于核心线程数，则尝试添加新线程，并将该任务附到该线程上
     * 2. 如果是运行状态，并且能够正常的将任务添加到队列中，这时double check一下线程池的状态
     * 3. 如果没有办法添加到队列，说明队列已满，但此时非核心线程也许未满，则尝试添加新的线程，并将该线程附在该线程上
     * 4. 拒绝该任务
     */
    @Override
    public void execute(Runnable command) {
        if(command==null){
            throw new NullPointerException();
        }
        int c = ctl.get();
        //如果当前工作线程数小于核心线程数，则需要添加核心线程，初始任务为该command
        if(workerCountOf(c)<corePoolSize){
            if(addWorker(command,true)){  //如果添加成功则直接返回
                return;
            }
            c = ctl.get();   //重新获取一下ctl
        }
        //走到这里的条件是上方添加核心线程并没有成功
        //如果一个task被成功地添加到队列中，那么我们依然要double check，是否应该添加新的线程
        //因为在上一次check之后，线程数为0了，或者线程池被shutdown了
        //如果线程被shutdown了，就去把该task拒绝掉
        //如果线程数为0了，则尝试添加新的线程，这时候并不需要拒绝掉该task
        if(isRunning(c) && workQueue.offer(command)){
            int recheck = ctl.get();
            if(!isRunning(recheck) && remove(command)){
                reject(command);
            }
            else if(workerCountOf(recheck)==0){
                addWorker(null,false);
            }
        }
        //如果添加到任务队列也失败，则说明队列已经饱和
        //这个时候尝试将该任务直接放到一个新线程里添加到线程池
        //如果失败了就拒绝掉该task
        else if(!addWorker(command,false)){
            reject(command);
        }
    }

    @Override
    public void shutdown() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            advanceRunState(SHUTDOWN);
            interruptIdleWorkers();
            onShutdown();  // hook for ScheduledThreadPoolExecutor
        }finally {
            mainLock.unlock();
        }
        tryTerminate();
    }

    @Override
    public List<Runnable> shutdownNow() {
        List<Runnable> tasks;
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();;
        try {
            advanceRunState(STOP);
            interruptWorkers();
            tasks = drainQueue();
        }finally {
            mainLock.unlock();
        }
        tryTerminate();
        return tasks;
    }

    @Override
    public boolean isShutdown() {
        return !isRunning(ctl.get());
    }

    public boolean isTerminating(){
        int c = ctl.get();
        return !isRunning(c) && runStateLessThan(c,TERMINATED);
    }

    /**
     * 如果该执行程序在shutdown或shutdownNow后正在终止，但尚未完全终止，则返回true。
     * @return true if terminating but not yet terminated
     */
    @Override
    public boolean isTerminated() {
        return runStateAtLeast(ctl.get(), TERMINATED);
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        long nanos = unit.toNanos(timeout);
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            for (;;){
                if(runStateAtLeast(ctl.get(),TERMINATED)){
                    return true;
                }
                if(nanos<=0){
                    return false;
                }
                //TODO condition.awaitNanos的用法
                nanos = termination.awaitNanos(nanos);
            }
        }finally {
            mainLock.unlock();
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

    /**
     * 如果新值小于当前值，多余的现有线程将在下一次空闲时终止。
     * 如果线程更大，如果需要，将启动新线程来执行任何队列任务。
     */
    public void setCorePoolSize(int corePoolSize){
        if(corePoolSize<0){
            throw new IllegalStateException();
        }
        int delta = corePoolSize-this.corePoolSize;
        this.corePoolSize = corePoolSize;
        if(workerCountOf(ctl.get())>corePoolSize){
            interruptIdleWorkers();
        }
        else if(delta>0){
            //取多余以及任务队列的最小值，添加这么多数量的核心线程
            //如果在判断中workerQueue为空，则break掉
            int k = Math.min(delta,workQueue.size());
            while (k-->0 && addWorker(null,true)){
                if(workQueue.isEmpty()){
                    break;
                }
            }
        }
    }

    public int getCorePoolSize(){
        return corePoolSize;
    }

    /**
     * 添加一个核心线程
     * @return true if a thread was started
     */
    public boolean preStartCoreThread(){
        return workerCountOf(ctl.get())<corePoolSize && addWorker(null,true);
    }

    /**
     * 如果当前工作线程数小于corePoolSize，则添加一个核心工作线程
     * 如果corePoolSize=0,则添加一个工作线程，保证至少有一个线程
     */
    void ensurePreStart(){
        int wc = workerCountOf(ctl.get());
        if(wc<corePoolSize){
            addWorker(null,true);
        }else if(wc==0){
            addWorker(null,false);
        }
    }

    /**
     * start 所有的核心工作线程，返回添加的数量
     */
    public int preStartAllCoreThreads(){
        int n=0;
        while (addWorker(null,true)){
            n++;
        }
        return n;
    }

    public boolean allowsCoreThreadTimeOut() {
        return allowCoreThreadTimeOut;
    }

    public void allowCoreThreadTimeOut(boolean value) {
        if (value && keepAliveTime <= 0)
            throw new IllegalArgumentException("Core threads must have nonzero keep alive times");
        if (value != allowCoreThreadTimeOut) {
            allowCoreThreadTimeOut = value;
            if (value)
                interruptIdleWorkers();
        }
    }


    /**
     * set maximumPoolSize，如果当前工作线程数多于maximum，则interrupt
     */
    public void setMaximumPoolSize(int maximumPoolSize) {
        if (maximumPoolSize <= 0 || maximumPoolSize < corePoolSize)
            throw new IllegalArgumentException();
        this.maximumPoolSize = maximumPoolSize;
        if (workerCountOf(ctl.get()) > maximumPoolSize)
            interruptIdleWorkers();
    }

    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    /**
     * 设置线程在终止前可以保持空闲的时间
     * 如果该时间变小，则需要执行 interruptIdleWorkers()
     */
    public void setKeepAliveTime(long time,TimeUnit timeUnit){
        if(time<0){
            throw new IllegalStateException();
        }
        if(time==0 && allowsCoreThreadTimeOut()){
            throw new IllegalStateException("Core threads must have nonzero keep alive times");
        }
        long keepAliveTime = timeUnit.toNanos(time);
        long delta = keepAliveTime-this.keepAliveTime;
        this.keepAliveTime = keepAliveTime;
        if(delta<0){
            interruptIdleWorkers();
        }
    }

    public long getKeepAliveTime(TimeUnit unit) {
        return unit.convert(keepAliveTime, TimeUnit.NANOSECONDS);
    }

    public BlockingQueue<Runnable> getQueue() {
        return workQueue;
    }

    public boolean remove(Runnable task) {
        boolean removed = workQueue.remove(task);
        tryTerminate(); // In case SHUTDOWN and now empty
        return removed;
    }

    /**
     * purge  清洗
     */
    public void purge() {
        final BlockingQueue<Runnable> q = workQueue;
        try {
            q.removeIf(r -> r instanceof Future<?> && ((Future<?>) r).isCancelled());
        } catch (ConcurrentModificationException fallThrough) {
            // Take slow path if we encounter interference during traversal.
            // Make copy for traversal and call remove for cancelled entries.
            // The slow path is more likely to be O(N*N).
            for (Object r : q.toArray())
                if (r instanceof Future<?> && ((Future<?>)r).isCancelled())
                    q.remove(r);
        }

        tryTerminate(); // In case SHUTDOWN and now empty
    }



    public int getPoolSize() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            // Remove rare and surprising possibility of
            // isTerminated() && getPoolSize() > 0
            return runStateAtLeast(ctl.get(), TIDYING) ? 0
                    : workers.size();
        } finally {
            mainLock.unlock();
        }
    }

    /**
     * 返回正在执行任务的线程数
     */
    public int getActiveCount() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            int n = 0;
            for (Worker w : workers)
                if (w.isLocked())
                    ++n;
            return n;
        } finally {
            mainLock.unlock();
        }
    }

    public int getLargestPoolSize() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            return largestPoolSize;
        } finally {
            mainLock.unlock();
        }
    }

    /**
     * 返回线程池中总的task数量，为估计值
     * return 工作线程已经完成的task数量+工作线程正在处理的数量+workQueue中的task数量
     */
    public long getTaskCount() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            long n = completedTaskCount;
            for (Worker w : workers) {
                n += w.completedTasks;
                if (w.isLocked())
                    ++n;
            }
            return n + workQueue.size();
        } finally {
            mainLock.unlock();
        }
    }

    /**
     * 返回线程池已经完成的task数量
     */
    public long getCompletedTaskCount() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            long n = completedTaskCount;
            for (ThreadPoolExecutor.Worker w : workers)
                n += w.completedTasks;
            return n;
        } finally {
            mainLock.unlock();
        }
    }

    public String toString(){
        long completed;
        int workersNum,activeNum;
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            completed = completedTaskCount;   //只包含已经退出的线程执行的任务数
            activeNum = 0;
            workersNum = this.workers.size();
            for (Worker worker : this.workers) {
                completed += worker.completedTasks;
                if(worker.isLocked()){
                    activeNum++;
                }
            }
        }finally {
            mainLock.unlock();
        }
        int c = ctl.get();
        String s = (runStateLessThan(c,SHUTDOWN)? "Running":
                (runStateAtLeast(c,TERMINATED)?"Terminated":"Shutting down"));
        return super.toString()+
                "[" + s +
                ", pool size = " + workersNum +
                ", active threads = " + activeNum+
                ", queued tasks = " + workQueue.size() +
                ", completed tasks = " + completed +
                "]";
    }

    //由调度任务的线程执行任务
    public static class CallerRunsPolicy implements RejectedExecutionHandler {

        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            if (!e.isShutdown()) {
                r.run();
            }
        }
    }

    //直接拒绝，抛出异常
    public static class AbortPolicy implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            throw new RuntimeException("Task " + r.toString() +
                    " rejected from " +
                    executor.toString());
        }
    }

    //丢弃任务，但是不抛出异常
    public static class DiscardPolicy implements RejectedExecutionHandler {

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {

        }
    }

    //丢弃队伍头的任务，执行任务
    public static class DiscardOldestPolicy implements RejectedExecutionHandler {

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            if (!executor.isShutdown()) {
                executor.getQueue().poll();
                executor.execute(r);
            }
        }
    }
}
