package com.bsren.disrupter.dsl;


import com.bsren.disrupter.Sequence;
import com.bsren.disrupter.SequenceBarrier;

import java.util.concurrent.Executor;

/**
 * 获取信息的接口，相比于直接给出信息的类更具有拓展性
 */
interface ConsumerInfo {

    Sequence[] getSequences();

    SequenceBarrier getBarrier();

    boolean isEndOfChain();

    void start(Executor executor);

    void halt();

    void markAsUsedInBarrier();

    boolean isRunning();
}
