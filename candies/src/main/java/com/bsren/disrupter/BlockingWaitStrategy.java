package com.bsren.disrupter;


import com.bsren.disrupter.util.ThreadHints;
import com.lmax.disruptor.AlertException;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class BlockingWaitStrategy implements WaitStrategy {

    private final Lock lock = new ReentrantLock();
    private final Condition processorNotifyCondition = lock.newCondition();

    /**
     * 第一重判断：
     * 消费者消费的序号不能超过当前生产者消费当前生产的序号，
     * 否则消费者就阻塞等待；
     * 当然，这里因为是BlockingWaitStrategy等待策略的实现，
     * 如果是其他策略，比如BusySpinWaitStrategy和YieldingWaitStrategy的话，
     * 这里消费者是不会阻塞等待的，
     * 而是自旋，因此这也是其无锁化的实现了，但就是很耗CPU而已；
     *
     * 第二重判断：
     * 消费者消费的序号不能超过其前面依赖的消费消费的序号，
     * 否则其自旋等待。因为这里是消费者等消费者，
     * 按理说前面消费者应该会很快处理完，所以不用阻塞等待；
     * 但是消费者等待生产者的话，如果生产者没生产数据的话，
     * 消费者还是自旋等待的话会比较浪费CPU，
     * 所以对于BlockingWaitStrategy策略，是阻塞等待了。
     */
    @Override
    public long waitFor(long sequence, Sequence cursorSequence, Sequence dependentSequence, SequenceBarrier barrier)
            throws AlertException, InterruptedException {
        long availableSequence;
        // cursorSequence:生产者的序号
        if (cursorSequence.get() < sequence) {
            lock.lock();
            try {
                while (cursorSequence.get() < sequence) {
                    barrier.checkAlert();
                    processorNotifyCondition.await();
                }
            } finally {
                lock.unlock();
            }
        }

        // 第二重条件判断：自旋等待
        // 即当前消费者线程要消费的下一个sequence大于其前面执行链路（若有依赖关系）的任何一个消费者最小sequence（dependentSequence.get()），
        // 那么这个消费者要自旋等待，
        // 直到前面执行链路（若有依赖关系）的任何一个消费者最小sequence（dependentSequence.get()）
        // 已经大于等于当前消费者的sequence时，说明前面执行链路的消费者已经消费完了
        while ((availableSequence = dependentSequence.get()) < sequence) {
            barrier.checkAlert();
            ThreadHints.onSpinWait();
        }

        return availableSequence;
    }

    @Override
    public void signalAllWhenBlocking() {
        lock.lock();
        try {
            processorNotifyCondition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String toString() {
        return "BlockingWaitStrategy{" +
                "processorNotifyCondition=" + processorNotifyCondition +
                '}';
    }

}
