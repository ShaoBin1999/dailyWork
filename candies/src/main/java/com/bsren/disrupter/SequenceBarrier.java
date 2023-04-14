package com.bsren.disrupter;

import com.lmax.disruptor.AlertException;
import com.lmax.disruptor.EventProcessor;
import com.lmax.disruptor.TimeoutException;

public interface SequenceBarrier {
    /**
     * Wait for the given sequence to be available for consumption.
     * @param sequence to wait for
     * @return the sequence up to which is available
     * @throws AlertException       if a status change has occurred for the Disruptor
     * @throws InterruptedException if the thread needs awaking on a condition variable.
     * @throws TimeoutException     if a timeout occurs while waiting for the supplied sequence.
     */
    long waitFor (long sequence) throws AlertException, InterruptedException, TimeoutException;

    /**
     * Get the current cursor value that can be read.
     *
     * @return value of the cursor for entries that have been published.
     */
    long getCursor ();

    /**
     * The current alert status for the barrier.
     * @return true if in alert otherwise false.
     */
    boolean isAlerted ();

    /**
     * Alert the {@link EventProcessor}s of a status change and stay in this status until cleared.
     */
    void alert ();

    /**
     * Clear the current alert status.
     */
    void clearAlert ();

    /**
     * Check if an alert has been raised and throw an {@link AlertException} if it has.
     *
     * @throws AlertException if alert has been raised.
     */
    void checkAlert () throws AlertException;
}
