package com.bsren.disrupter;


import com.bsren.disrupter.util.Util;
import com.lmax.disruptor.InsufficientCapacityException;
import com.lmax.disruptor.Sequencer;

import java.util.concurrent.locks.LockSupport;

abstract class SingleProducerSequencerPad extends AbstractSequencer {
    protected long p1, p2, p3, p4, p5, p6, p7;

    SingleProducerSequencerPad(int bufferSize, WaitStrategy waitStrategy)
    {
        super(bufferSize, waitStrategy);
    }
}

abstract class SingleProducerSequencerFields extends SingleProducerSequencerPad {
    SingleProducerSequencerFields(int bufferSize, WaitStrategy waitStrategy)
    {
        super(bufferSize, waitStrategy);
    }

    /**
     * Set to -1 as sequence starting point
     */
    long nextValue = Sequence.INITIAL_VALUE;
    long cachedValue = Sequence.INITIAL_VALUE;
}


public final class SingleProducerSequencer extends SingleProducerSequencerFields {
    protected long p1, p2, p3, p4, p5, p6, p7;


    public SingleProducerSequencer(int bufferSize, WaitStrategy waitStrategy) {
        super(bufferSize, waitStrategy);
    }

    @Override
    public boolean hasAvailableCapacity(int requiredCapacity) {
        return hasAvailableCapacity(requiredCapacity, false);
    }

    private boolean hasAvailableCapacity(int requiredCapacity, boolean doStore) {
        long nextValue = this.nextValue;

        // 下一位置加上所需容量减去整个bufferSize，如果为正数，那证明至少转了一圈，
        // 则需要检查gatingSequences（由消费者更新里面的Sequence值）以保证不覆盖还未被消费的
        // 由于最多只能生产不大于整个bufferSize的Events。所以减去一个bufferSize与最小sequence相比较即可
        long wrapPoint = (nextValue + requiredCapacity) - bufferSize;
        //缓存
        long cachedGatingSequence = this.cachedValue;
        //缓存失效条件
        if (wrapPoint > cachedGatingSequence || cachedGatingSequence > nextValue) {
            if (doStore) {
                cursor.setVolatile(nextValue);  // StoreLoad fence
            }

            long minSequence = Util.getMinimumSequence(gatingSequences, nextValue);
            this.cachedValue = minSequence;

            if (wrapPoint > minSequence) {
                return false;
            }
        }
        return true;
    }

    @Override
    public long next()
    {
        return next(1);
    }

    @Override
    public long next(int n) {
        if (n < 1) {
            throw new IllegalArgumentException("n must be > 0");
        }

        //表示生产者最后一次投递的位置，在方法结束的时候，会向前走一位，追上nextSequence
        long nextValue = this.nextValue;

        //表示生产者这次要投递的位置
        long nextSequence = nextValue + n;

        //表示包裹点，当生产者还没有生产完一圈的时候，其值为负，
        //没有和生产者中最慢的位置比较的意义，因为就算消费者一个都没消费，
        //其最慢的消费位置为-1；就算wrapPoint为-1了，
        //表示生产者要投递第一圈的最后一个位置了，也还没追上消费者，无需阻塞；
        long wrapPoint = nextSequence - bufferSize;

        // 表示上一次投递，消费者最慢的位置，
        // 这个位置在最新一次投递时，有可能已经落后了，
        // 不过没关系，其作为第一道门槛还是有意义的：
        // 如果生产者这次的投递位置还没到上次最慢的消费者的位置，
        // 那么无需担心生产者阻塞的问题，
        // 直接投递，不会覆盖尚未消费的Event的；
        long cachedGatingSequence = this.cachedValue;

        // cachedGatingSequence > nextValue
        // 上次生产者投递的时候，最慢的消费者的位置都比生产者最后投递成功的位置大，消费者领先于生产者
        // wrapPoint > cachedGatingSequence
        // 生产者已经追上上次最慢的额消费者
        if (wrapPoint > cachedGatingSequence || cachedGatingSequence > nextValue) {
            cursor.setVolatile(nextValue);  // StoreLoad fence
            //表示最慢的消费者的消费位置，
            //如果消费者一个都没消费，其值为-1；
            //minSequence和wrapPoint的关系是：
            //如果生产者这次要投递的位置已经超过最慢的消费者的位置，
            //那么生产者这次要投递的位置上有待消费的Event，生产者会阻塞 1 纳秒，直到生产者这次要投递的位置的Event被消费掉；
            long minSequence;
            // 生产者的速度太快了，追上消费者了
            while (wrapPoint > (minSequence = Util.getMinimumSequence(gatingSequences, nextValue))) {
                LockSupport.parkNanos(1L); // TODO: Use waitStrategy to spin?
            }
            //生产者的速度没那么快，记录下这次最慢的消费者；
            //cachedValue 存的应该是消费者中最慢的
            this.cachedValue = minSequence;
        }
        this.nextValue = nextSequence;
        return nextSequence;
    }

    @Override
    public long tryNext() throws InsufficientCapacityException {
        return tryNext(1);
    }

    @Override
    public long tryNext(int n) throws InsufficientCapacityException {
        if (n < 1) {
            throw new IllegalArgumentException("n must be > 0");
        }

        if (!hasAvailableCapacity(n, true)) {
            throw InsufficientCapacityException.INSTANCE;
        }
        long nextSequence = this.nextValue += n;
        return nextSequence;
    }


    @Override
    public long remainingCapacity() {
        long nextValue = this.nextValue;
        long consumed = Util.getMinimumSequence(gatingSequences, nextValue);
        return getBufferSize() - (nextValue - consumed);
    }

    /**
     * @see com.lmax.disruptor.Sequencer#claim(long)
     */
    @Override
    public void claim(long sequence)
    {
        this.nextValue = sequence;
    }

    /**
     * @see com.lmax.disruptor.Sequencer#publish(long)
     */
    @Override
    public void publish(long sequence) {
        cursor.set(sequence);
        waitStrategy.signalAllWhenBlocking();
    }

    /**
     * @see com.lmax.disruptor.Sequencer#publish(long, long)
     */
    @Override
    public void publish(long lo, long hi)
    {
        publish(hi);
    }

    /**
     * @see Sequencer#isAvailable(long)
     */
    @Override
    public boolean isAvailable(long sequence)
    {
        return sequence <= cursor.get();
    }

    @Override
    public long getHighestPublishedSequence(long lowerBound, long availableSequence)
    {
        return availableSequence;
    }

}
