/*
 * Copyright 2011 LMAX Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bsren.disrupter;



import com.bsren.disrupter.eventProcessor.WorkProcessor;
import com.bsren.disrupter.util.Util;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * WorkerPool contains a pool of {@link WorkProcessor}s that will consume sequences
 * so jobs can be farmed out across a pool of workers.
 * Each of the {@link WorkProcessor}s manage and calls a {@link com.lmax.disruptor.WorkHandler} to process the events.
 * @param <T> event to be processed by a pool of workers
 */
public final class WorkerPool<T> {
    private final AtomicBoolean started = new AtomicBoolean(false);

    // 与workProcessor的sequence不同，它用来协调workProcessors的工作用的，
    // 它总是大于workProcessor的sequence,
    // 当workProcessor竞争workPool的sequence成功时，
    // 其他的workProcessors可以消费下一个sequence序号了
    private final Sequence workSequence = new Sequence(Sequencer.INITIAL_CURSOR_VALUE);  // 所有消费者公有的序列器
    private final RingBuffer<T> ringBuffer;
    // WorkProcessors are created to wrap each of the provided WorkHandlers
    private final WorkProcessor<?>[] workProcessors;  //事件处理器，每个workProcessor都需要一个单独的执行线程

    /**
     * Create a worker pool to enable an array of {@link com.lmax.disruptor.WorkHandler}s to consume published sequences.
     * <p>
     * This option requires a pre-configured {@link com.lmax.disruptor.RingBuffer} which must have {@link com.lmax.disruptor.RingBuffer#addGatingSequences(com.lmax.disruptor.Sequence...)}
     * called before the work pool is started.
     *
     * @param ringBuffer       of events to be consumed.
     * @param sequenceBarrier  on which the workers will depend.
     * @param exceptionHandler to callback when an error occurs which is not handled by the {@link com.lmax.disruptor.WorkHandler}s.
     * @param workHandlers     to distribute the work load across.
     */
    @SafeVarargs
    public WorkerPool(
        final RingBuffer<T> ringBuffer,
        final SequenceBarrier sequenceBarrier,
        final ExceptionHandler<? super T> exceptionHandler,
        final WorkHandler<? super T>... workHandlers) {
        this.ringBuffer = ringBuffer;
        final int numWorkers = workHandlers.length;
        workProcessors = new WorkProcessor[numWorkers];

        for (int i = 0; i < numWorkers; i++) {
            workProcessors[i] = new WorkProcessor<>(
                ringBuffer,
                sequenceBarrier,
                workHandlers[i],
                exceptionHandler,
                workSequence);
        }
    }

    /**
     * Construct a work pool with an internal {@link com.lmax.disruptor.RingBuffer} for convenience.
     * <p>
     * This option does not require {@link com.lmax.disruptor.RingBuffer#addGatingSequences(com.lmax.disruptor.Sequence...)} to be called before the work pool is started.
     *
     * @param eventFactory     for filling the {@link com.lmax.disruptor.RingBuffer}
     * @param exceptionHandler to callback when an error occurs which is not handled by the {@link com.lmax.disruptor.WorkHandler}s.
     * @param workHandlers     to distribute the work load across.
     */
    @SafeVarargs
    public WorkerPool(
        final EventFactory<T> eventFactory,
        final ExceptionHandler<? super T> exceptionHandler,
        final WorkHandler<? super T>... workHandlers
    ) {
        ringBuffer = RingBuffer.createMultiProducer(eventFactory, 1024, new BlockingWaitStrategy());
        final SequenceBarrier barrier = ringBuffer.newBarrier();
        final int numWorkers = workHandlers.length;
        workProcessors = new WorkProcessor[numWorkers];

        for (int i = 0; i < numWorkers; i++) {
            workProcessors[i] = new WorkProcessor<>(
                ringBuffer,
                barrier,
                workHandlers[i],
                exceptionHandler,
                workSequence);
        }

        ringBuffer.addGatingSequences(getWorkerSequences());
    }


    /**
     * 获取每个worker的sequence，末尾添加workSequence
     */
    public Sequence[] getWorkerSequences() {
        final Sequence[] sequences = new Sequence[workProcessors.length + 1];
        for (int i = 0, size = workProcessors.length; i < size; i++) {
            sequences[i] = workProcessors[i].getSequence();
        }
        sequences[sequences.length - 1] = workSequence;
        return sequences;
    }

    /**
     * Start the worker pool processing events in sequence.
     *
     * @param executor providing threads for running the workers.
     * @return the {@link com.lmax.disruptor.RingBuffer} used for the work queue.
     * @throws IllegalStateException if the pool has already been started and not halted yet
     */
    public RingBuffer<T> start(final Executor executor) {
        if (!started.compareAndSet(false, true)) {
            throw new IllegalStateException("WorkerPool has already been started and cannot be restarted until halted.");
        }

        final long cursor = ringBuffer.getCursor();
        workSequence.set(cursor);

        for (WorkProcessor<?> processor : workProcessors) {
            processor.getSequence().set(cursor);
            executor.execute(processor);
        }
        return ringBuffer;
    }

    /**
     * Wait for the {@link RingBuffer} to drain of published events then halt the workers.
     */
    public void drainAndHalt() {
        Sequence[] workerSequences = getWorkerSequences();
        while (ringBuffer.getCursor() > Util.getMinimumSequence(workerSequences)) {
            Thread.yield();
        }

        for (WorkProcessor<?> processor : workProcessors) {
            processor.halt();
        }

        started.set(false);
    }

    /**
     * Halt all workers immediately at the end of their current cycle.
     */
    public void halt() {
        for (WorkProcessor<?> processor : workProcessors) {
            processor.halt();
        }
        started.set(false);
    }

    public boolean isRunning()
    {
        return started.get();
    }
}
