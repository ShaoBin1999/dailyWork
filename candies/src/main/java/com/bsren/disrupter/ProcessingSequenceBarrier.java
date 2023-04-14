package com.bsren.disrupter;


import com.lmax.disruptor.AlertException;
import com.lmax.disruptor.TimeoutException;

/**
 * ProcessingSequenceBarrier实例作用：序号屏障，
 * 通过追踪生产者的cursorSequence和每个消费者（ EventProcessor）的sequence的方式来协调生产者和消费者之间的数据交换进度
 */
public class ProcessingSequenceBarrier  implements SequenceBarrier {

    private final WaitStrategy waitStrategy;
    //第一个消费者即前面不依赖任何消费者的消费者，dependentSequence就是生产者游标；
    //有依赖其他消费者的消费者，dependentSequence就是依赖的消费者的sequence
    private final Sequence dependentSequence;
    private volatile boolean alerted = false;
    private final Sequence cursorSequence;
    private final Sequencer sequencer;

    ProcessingSequenceBarrier(
            final Sequencer sequencer,
            final WaitStrategy waitStrategy,
            final Sequence cursorSequence,
            final Sequence[] dependentSequences) {
        this.sequencer = sequencer;
        this.waitStrategy = waitStrategy;
        this.cursorSequence = cursorSequence;
        if (0 == dependentSequences.length) {
            dependentSequence = cursorSequence;
        } else {
            dependentSequence = new FixedSequenceGroup(dependentSequences);
        }
    }

    @Override
    public long waitFor(final long sequence)
            throws AlertException, InterruptedException, TimeoutException {
        //检查是否alerted
        checkAlert();
        //通过等待策略获取下一个可消费的sequence
        //需要大于cursorSequence和dependentSequence，我们可以通过dependentSequence实现先后消费

        // 获取生产者生产后可用的序号
        long availableSequence = waitStrategy.waitFor(
                sequence,  //消费者要消费的下一个序号
                cursorSequence,  //生产者生产数据时的当前序号
                dependentSequence,
                this);
        //等待可能被中断，所以检查下availableSequence是否小于sequence
        if (availableSequence < sequence) {
            return availableSequence;
        }
        //如果不小于，返回所有sequence（可能多生产者）和availableSequence中最大的
        return sequencer.getHighestPublishedSequence(sequence, availableSequence);
    }

    @Override
    public long getCursor() {
        return dependentSequence.get();
    }

    @Override
    public boolean isAlerted() {
        return alerted;
    }

    @Override
    public void alert() {
        alerted = true;
        waitStrategy.signalAllWhenBlocking();
    }

    @Override
    public void clearAlert() {
        alerted = false;
    }

    @Override
    public void checkAlert() throws AlertException {
        if (alerted) {
            throw AlertException.INSTANCE;
        }
    }
}
