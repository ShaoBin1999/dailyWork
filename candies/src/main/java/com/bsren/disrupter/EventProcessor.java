package com.bsren.disrupter;


public interface EventProcessor extends Runnable{

    Sequence getSequence();

    /**
     * Signal that this EventProcessor should stop when it has finished consuming at the next clean break.
     * It will call {@link SequenceBarrier#alert()} to notify the thread to check status.
     */
    void halt();

    boolean isRunning();

}
