package com.bsren.disrupter;

import com.bsren.disrupter.dsl.ProducerType;
import com.bsren.disrupter.eventTranslator.EventTranslatorOneArg;
import com.bsren.disrupter.eventTranslator.EventTranslatorThreeArg;
import com.bsren.disrupter.eventTranslator.EventTranslatorTwoArg;
import com.bsren.disrupter.eventTranslator.EventTranslatorVararg;
import com.bsren.disrupter.util.Util;
import com.lmax.disruptor.InsufficientCapacityException;
import sun.misc.Unsafe;

abstract class RingBufferPad {
    protected long p1, p2, p3, p4, p5, p6, p7;
}

abstract class RingBufferFields<E> extends RingBufferPad {
    private static final int BUFFER_PAD;   //32
    private static final long REF_ARRAY_BASE;  //144 数组在内存中的偏移量，也就是第一个元素的内存偏移量。
    private static final int REF_ELEMENT_SHIFT;  //2  entries数组中元素偏移量位移位数。如果scale为4，则该值为2，表示左移2位
    private static final Unsafe UNSAFE = Util.getUnsafe();

    static {
        //该电脑上为4，虽然是64位操作系统，但是开启了指针压缩，一个指针是32位
        final int scale = UNSAFE.arrayIndexScale(Object[].class);
        if (4 == scale) {
            REF_ELEMENT_SHIFT = 2;
        }
        else if (8 == scale) {
            REF_ELEMENT_SHIFT = 3;
        }
        else {
            throw new IllegalStateException("Unknown pointer size");
        }
        //pad = 32
        BUFFER_PAD = 128 / scale;
        // Including the buffer pad in the array base offset
        //UNSAFE.arrayBaseOffset(Object[].class)是16
        //UNSAFE.arrayBaseOffset(int[].class)，UNSAFE.arrayBaseOffset(long[].class)都是16
        //由于entries数组被填充了32个对象引用，占用了128个字节，所以entries中第一个真实的元素在内存中的偏移量要加128
        REF_ARRAY_BASE = UNSAFE.arrayBaseOffset(Object[].class) + (BUFFER_PAD << REF_ELEMENT_SHIFT);
    }


    private final long indexMask;
    private final Object[] entries;
    protected final int bufferSize;
    protected final Sequencer sequencer;

    RingBufferFields(EventFactory<E> eventFactory, Sequencer sequencer) {
        this.sequencer = sequencer;
        this.bufferSize = sequencer.getBufferSize();

        if (bufferSize < 1) {
            throw new IllegalArgumentException("bufferSize must not be less than 1");
        }
        if (Integer.bitCount(bufferSize) != 1) {
            throw new IllegalArgumentException("bufferSize must be a power of 2");
        }

        this.indexMask = bufferSize - 1;
        this.entries = new Object[sequencer.getBufferSize() + 2 * BUFFER_PAD];
        fill(eventFactory);
    }

    /**
     * 在entries数组前后填充32或16个对象引用，
     * 保证前后各占用128个字节的内存，这样对于缓存行大小为64或128字节的系统来说，
     * 可以避免其它变量与entries数组元素在一个缓存行内，避免伪共享问题。
     *
     * 数组预填充：避免了垃圾回收代来的系统开销
     */
    private void fill(EventFactory<E> eventFactory) {
        for (int i = 0; i < bufferSize; i++) {
            entries[BUFFER_PAD + i] = eventFactory.newInstance();
        }
    }

    @SuppressWarnings("unchecked")
    protected final E elementAt(long sequence) {
        //144+  (sequence & indexMask)*2
        return (E) UNSAFE.getObject(entries, REF_ARRAY_BASE + ((sequence & indexMask) << REF_ELEMENT_SHIFT));
    }
}

public final class RingBuffer<E>extends RingBufferFields<E> implements Cursored, EventSequencer<E>, EventSink<E> {

    public static final long INITIAL_CURSOR_VALUE = Sequence.INITIAL_VALUE;
    protected long p1, p2, p3, p4, p5, p6, p7;

    RingBuffer(EventFactory<E> eventFactory, Sequencer sequencer) {
        super(eventFactory, sequencer);
    }

    public static <E> RingBuffer<E> createSingleProducer(
            EventFactory<E> factory,
            int bufferSize,
            WaitStrategy waitStrategy) {
        SingleProducerSequencer sequencer = new SingleProducerSequencer(bufferSize, waitStrategy);

        return new RingBuffer<E>(factory, sequencer);
    }

    public static <E> RingBuffer<E> createSingleProducer(EventFactory<E> factory, int bufferSize) {
        return createSingleProducer(factory, bufferSize, new BlockingWaitStrategy());
    }


    public static <E> RingBuffer<E> createMultiProducer(EventFactory<E> factory, int bufferSize) {
        return createMultiProducer(factory, bufferSize, new BlockingWaitStrategy());
    }

    public static <E> RingBuffer<E> createMultiProducer(
            EventFactory<E> factory,
            int bufferSize,
            WaitStrategy waitStrategy) {
        MultiProducerSequencer sequencer = new MultiProducerSequencer(bufferSize, waitStrategy);

        return new RingBuffer<E>(factory, sequencer);
    }


    public static <E> RingBuffer<E> create(
            ProducerType producerType,
           EventFactory<E> factory,
            int bufferSize,
            WaitStrategy waitStrategy
    ) {
        switch (producerType) {
            case SINGLE:
                return createSingleProducer(factory, bufferSize, waitStrategy);
            case MULTI:
                return createMultiProducer(factory, bufferSize, waitStrategy);
            default:
                throw new IllegalStateException(producerType.toString());
        }
    }

    /**
     * Get the event for a given sequence in the RingBuffer.
     * This call has 2 uses.
     * Firstly use this call when publishing to a ring buffer.
     * After calling next() use this call to get hold of the preallocated event to fill with data before calling publish(long).
     * Secondly use this call when consuming data from the ring buffer.
     * After calling SequenceBarrier.waitFor(long) call this method
     * with any value greater than that your current consumer sequence
     * and less than or equal to the value returned from the SequenceBarrier.waitFor(long) method.
     */
    @Override
    public E get(long sequence) {
        return elementAt(sequence);
    }

    @Override
    public long next()
    {
        return sequencer.next();
    }

    @Override
    public long next(int n)
    {
        return sequencer.next(n);
    }

    @Override
    public long tryNext() throws InsufficientCapacityException {
        return sequencer.tryNext();
    }

    @Override
    public long tryNext(int n) throws InsufficientCapacityException {
        return sequencer.tryNext(n);
    }

    /**
     * Sets the cursor to a specific sequence
     * and returns the preallocated entry that is stored there.
     * This can cause a data race and should only be done in controlled circumstances, e.g. during initialisation.
     */
    public E claimAndGetPreallocated(long sequence) {
        sequencer.claim(sequence);
        return get(sequence);
    }

    /**
     * Add the specified gating sequences to this instance of the Disruptor.
     * They will safely and atomically added to the list of gating sequences.
     */

    public void addGatingSequences(Sequence... gatingSequences) {
        sequencer.addGatingSequences(gatingSequences);
    }


    public long getMinimumGatingSequence() {
        return sequencer.getMinimumSequence();
    }

    public boolean removeGatingSequence(Sequence sequence) {
        return sequencer.removeGatingSequence(sequence);
    }

    public SequenceBarrier newBarrier(Sequence... sequencesToTrack) {
        return sequencer.newBarrier(sequencesToTrack);
    }

    /**
     * Creates an event poller for this ring buffer gated on the supplied sequences.
     */
    public EventPoller<E> newPoller(Sequence... gatingSequences) {
        return sequencer.newPoller(this, gatingSequences);
    }

    @Override
    public long getCursor()
    {
        return sequencer.getCursor();
    }

    /**
     * The size of the buffer.
     */
    public int getBufferSize()
    {
        return bufferSize;
    }

    public boolean hasAvailableCapacity(int requiredCapacity) {
        return sequencer.hasAvailableCapacity(requiredCapacity);
    }

    @Override
    public void publishEvent(EventTranslator<E> translator) {
        final long sequence = sequencer.next();
        translateAndPublish(translator, sequence);
    }

    @Override
    public boolean tryPublishEvent(EventTranslator<E> translator) {
        try {
            final long sequence = sequencer.tryNext();
            translateAndPublish(translator, sequence);
            return true;
        }
        catch (InsufficientCapacityException e) {
            return false;
        }
    }

    @Override
    public <A> void publishEvent(EventTranslatorOneArg<E, A> translator, A arg0) {
        final long sequence = sequencer.next();
        translateAndPublish(translator, sequence, arg0);
    }



    @Override
    public <A> boolean tryPublishEvent(EventTranslatorOneArg<E, A> translator, A arg0) {
        try {
            final long sequence = sequencer.tryNext();
            translateAndPublish(translator, sequence, arg0);
            return true;
        }
        catch (InsufficientCapacityException e) {
            return false;
        }
    }

    @Override
    public <A, B> void publishEvent(EventTranslatorTwoArg<E, A, B> translator, A arg0, B arg1) {

    }

    @Override
    public <A, B> boolean tryPublishEvent(EventTranslatorTwoArg<E, A, B> translator, A arg0, B arg1) {
        try {
            final long sequence = sequencer.tryNext();
            translateAndPublish(translator, sequence, arg0, arg1);
            return true;
        }
        catch (InsufficientCapacityException e) {
            return false;
        }
    }

    @Override
    public <A, B, C> void publishEvent(EventTranslatorThreeArg<E, A, B, C> translator, A arg0, B arg1, C arg2) {
        final long sequence = sequencer.next();
        translateAndPublish(translator, sequence, arg0, arg1, arg2);
    }


    @Override
    public <A, B, C> boolean tryPublishEvent(EventTranslatorThreeArg<E, A, B, C> translator, A arg0, B arg1, C arg2) {
        try {
            final long sequence = sequencer.tryNext();
            translateAndPublish(translator, sequence, arg0, arg1, arg2);
            return true;
        }
        catch (InsufficientCapacityException e) {
            return false;
        }
    }



    @Override
    public void publishEvent(EventTranslatorVararg<E> translator, Object... args) {
        final long sequence = sequencer.next();
        translateAndPublish(translator, sequence, args);
    }

    @Override
    public boolean tryPublishEvent(EventTranslatorVararg<E> translator, Object... args) {
        try {
            final long sequence = sequencer.tryNext();
            translateAndPublish(translator, sequence, args);
            return true;
        }
        catch (InsufficientCapacityException e) {
            return false;
        }
    }

    @Override
    public void publishEvents(EventTranslator<E>[] translators) {
        publishEvents(translators, 0, translators.length);
    }

    @Override
    public void publishEvents(EventTranslator<E>[] translators, int batchStartsAt, int batchSize) {
        checkBounds(translators, batchStartsAt, batchSize);
        final long finalSequence = sequencer.next(batchSize);
        translateAndPublishBatch(translators, batchStartsAt, batchSize, finalSequence);
    }

    @Override
    public boolean tryPublishEvents(EventTranslator<E>[] translators) {
        return tryPublishEvents(translators, 0, translators.length);
    }

    @Override
    public boolean tryPublishEvents(EventTranslator<E>[] translators, int batchStartsAt, int batchSize)
    {
        checkBounds(translators, batchStartsAt, batchSize);
        try
        {
            final long finalSequence = sequencer.tryNext(batchSize);
            translateAndPublishBatch(translators, batchStartsAt, batchSize, finalSequence);
            return true;
        }
        catch (InsufficientCapacityException e)
        {
            return false;
        }
    }

    @Override
    public <A> void publishEvents(EventTranslatorOneArg<E, A> translator, A[] arg0)
    {
        publishEvents(translator, 0, arg0.length, arg0);
    }

    @Override
    public <A> void publishEvents(EventTranslatorOneArg<E, A> translator, int batchStartsAt, int batchSize, A[] arg0)
    {
        checkBounds(arg0, batchStartsAt, batchSize);
        final long finalSequence = sequencer.next(batchSize);
        translateAndPublishBatch(translator, arg0, batchStartsAt, batchSize, finalSequence);
    }



    @Override
    public <A> boolean tryPublishEvents(EventTranslatorOneArg<E, A> translator, A[] arg0)
    {
        return tryPublishEvents(translator, 0, arg0.length, arg0);
    }

    /**
     * @see com.lmax.disruptor.EventSink#tryPublishEvents(com.lmax.disruptor.EventTranslatorOneArg, int, int, Object[])
     * com.lmax.disruptor.EventSink#tryPublishEvents(com.lmax.disruptor.EventTranslatorOneArg, int, int, A[])
     */
    @Override
    public <A> boolean tryPublishEvents(
            EventTranslatorOneArg<E, A> translator, int batchStartsAt, int batchSize, A[] arg0)
    {
        checkBounds(arg0, batchStartsAt, batchSize);
        try
        {
            final long finalSequence = sequencer.tryNext(batchSize);
            translateAndPublishBatch(translator, arg0, batchStartsAt, batchSize, finalSequence);
            return true;
        }
        catch (InsufficientCapacityException e)
        {
            return false;
        }
    }


    @Override
    public <A, B> void publishEvents(EventTranslatorTwoArg<E, A, B> translator, A[] arg0, B[] arg1)
    {
        publishEvents(translator, 0, arg0.length, arg0, arg1);
    }

    /**
     * @see com.lmax.disruptor.EventSink#publishEvents(com.lmax.disruptor.EventTranslatorTwoArg, int, int, Object[], Object[])
     * com.lmax.disruptor.EventSink#publishEvents(com.lmax.disruptor.EventTranslatorTwoArg, int, int, A[], B[])
     */
    @Override
    public <A, B> void publishEvents(
            EventTranslatorTwoArg<E, A, B> translator, int batchStartsAt, int batchSize, A[] arg0, B[] arg1)
    {
        checkBounds(arg0, arg1, batchStartsAt, batchSize);
        final long finalSequence = sequencer.next(batchSize);
        translateAndPublishBatch(translator, arg0, arg1, batchStartsAt, batchSize, finalSequence);
    }

    /**
     * @see com.lmax.disruptor.EventSink#tryPublishEvents(com.lmax.disruptor.EventTranslatorTwoArg, Object[], Object[])
     * com.lmax.disruptor.EventSink#tryPublishEvents(com.lmax.disruptor.EventTranslatorTwoArg, A[], B[])
     */
    @Override
    public <A, B> boolean tryPublishEvents(EventTranslatorTwoArg<E, A, B> translator, A[] arg0, B[] arg1)
    {
        return tryPublishEvents(translator, 0, arg0.length, arg0, arg1);
    }

    /**
     * @see com.lmax.disruptor.EventSink#tryPublishEvents(com.lmax.disruptor.EventTranslatorTwoArg, int, int, Object[], Object[])
     * com.lmax.disruptor.EventSink#tryPublishEvents(com.lmax.disruptor.EventTranslatorTwoArg, int, int, A[], B[])
     */
    @Override
    public <A, B> boolean tryPublishEvents(
            EventTranslatorTwoArg<E, A, B> translator, int batchStartsAt, int batchSize, A[] arg0, B[] arg1)
    {
        checkBounds(arg0, arg1, batchStartsAt, batchSize);
        try
        {
            final long finalSequence = sequencer.tryNext(batchSize);
            translateAndPublishBatch(translator, arg0, arg1, batchStartsAt, batchSize, finalSequence);
            return true;
        }
        catch (InsufficientCapacityException e)
        {
            return false;
        }
    }

    @Override
    public <A, B, C> void publishEvents(EventTranslatorThreeArg<E, A, B, C> translator, A[] arg0, B[] arg1, C[] arg2)
    {
        publishEvents(translator, 0, arg0.length, arg0, arg1, arg2);
    }

    /**
     * @see com.lmax.disruptor.EventSink#publishEvents(com.lmax.disruptor.EventTranslatorThreeArg, int, int, Object[], Object[], Object[])
     * com.lmax.disruptor.EventSink#publishEvents(com.lmax.disruptor.EventTranslatorThreeArg, int, int, A[], B[], C[])
     */
    @Override
    public <A, B, C> void publishEvents(
            EventTranslatorThreeArg<E, A, B, C> translator, int batchStartsAt, int batchSize, A[] arg0, B[] arg1, C[] arg2)
    {
        checkBounds(arg0, arg1, arg2, batchStartsAt, batchSize);
        final long finalSequence = sequencer.next(batchSize);
        translateAndPublishBatch(translator, arg0, arg1, arg2, batchStartsAt, batchSize, finalSequence);
    }

    /**
     * @see com.lmax.disruptor.EventSink#tryPublishEvents(com.lmax.disruptor.EventTranslatorThreeArg, Object[], Object[], Object[])
     * com.lmax.disruptor.EventSink#tryPublishEvents(com.lmax.disruptor.EventTranslatorThreeArg, A[], B[], C[])
     */
    @Override
    public <A, B, C> boolean tryPublishEvents(
            EventTranslatorThreeArg<E, A, B, C> translator, A[] arg0, B[] arg1, C[] arg2)
    {
        return tryPublishEvents(translator, 0, arg0.length, arg0, arg1, arg2);
    }

    /**
     * @see com.lmax.disruptor.EventSink#tryPublishEvents(com.lmax.disruptor.EventTranslatorThreeArg, int, int, Object[], Object[], Object[])
     * com.lmax.disruptor.EventSink#tryPublishEvents(com.lmax.disruptor.EventTranslatorThreeArg, int, int, A[], B[], C[])
     */
    @Override
    public <A, B, C> boolean tryPublishEvents(
            EventTranslatorThreeArg<E, A, B, C> translator, int batchStartsAt, int batchSize, A[] arg0, B[] arg1, C[] arg2)
    {
        checkBounds(arg0, arg1, arg2, batchStartsAt, batchSize);
        try
        {
            final long finalSequence = sequencer.tryNext(batchSize);
            translateAndPublishBatch(translator, arg0, arg1, arg2, batchStartsAt, batchSize, finalSequence);
            return true;
        }
        catch (InsufficientCapacityException e)
        {
            return false;
        }
    }

    /**
     * @see com.lmax.disruptor.EventSink#publishEvents(com.lmax.disruptor.EventTranslatorVararg, java.lang.Object[][])
     */
    @Override
    public void publishEvents(EventTranslatorVararg<E> translator, Object[]... args)
    {
        publishEvents(translator, 0, args.length, args);
    }

    /**
     * @see com.lmax.disruptor.EventSink#publishEvents(com.lmax.disruptor.EventTranslatorVararg, int, int, java.lang.Object[][])
     */
    @Override
    public void publishEvents(EventTranslatorVararg<E> translator, int batchStartsAt, int batchSize, Object[]... args)
    {
        checkBounds(batchStartsAt, batchSize, args);
        final long finalSequence = sequencer.next(batchSize);
        translateAndPublishBatch(translator, batchStartsAt, batchSize, finalSequence, args);
    }

    @Override
    public boolean tryPublishEvents(EventTranslatorVararg<E> translator, Object[]... args)
    {
        return tryPublishEvents(translator, 0, args.length, args);
    }

    @Override
    public boolean tryPublishEvents(
            EventTranslatorVararg<E> translator, int batchStartsAt, int batchSize, Object[]... args) {
        checkBounds(args, batchStartsAt, batchSize);
        try
        {
            final long finalSequence = sequencer.tryNext(batchSize);
            translateAndPublishBatch(translator, batchStartsAt, batchSize, finalSequence, args);
            return true;
        }
        catch (InsufficientCapacityException e)
        {
            return false;
        }
    }

    /**
     * Publish the specified sequence.  This action marks this particular
     * message as being available to be read.
     *
     * @param sequence the sequence to publish.
     */
    @Override
    public void publish(long sequence) {
        sequencer.publish(sequence);
    }

    /**
     * Publish the specified sequences.  This action marks these particular
     * messages as being available to be read.
     *
     * @param lo the lowest sequence number to be published
     * @param hi the highest sequence number to be published
     * @see com.lmax.disruptor.Sequencer#next(int)
     */
    @Override
    public void publish(long lo, long hi)
    {
        sequencer.publish(lo, hi);
    }

    /**
     * Get the remaining capacity for this ringBuffer.
     *
     * @return The number of slots remaining.
     */
    public long remainingCapacity()
    {
        return sequencer.remainingCapacity();
    }

    private void checkBounds(final EventTranslator<E>[] translators, final int batchStartsAt, final int batchSize)
    {
        checkBatchSizing(batchStartsAt, batchSize);
        batchOverRuns(translators, batchStartsAt, batchSize);
    }

    private void checkBatchSizing(int batchStartsAt, int batchSize) {
        if (batchStartsAt < 0 || batchSize < 0)
        {
            throw new IllegalArgumentException("Both batchStartsAt and batchSize must be positive but got: batchStartsAt " + batchStartsAt + " and batchSize " + batchSize);
        }
        else if (batchSize > bufferSize)
        {
            throw new IllegalArgumentException("The ring buffer cannot accommodate " + batchSize + " it only has space for " + bufferSize + " entities.");
        }
    }

    private <A> void checkBounds(final A[] arg0, final int batchStartsAt, final int batchSize)
    {
        checkBatchSizing(batchStartsAt, batchSize);
        batchOverRuns(arg0, batchStartsAt, batchSize);
    }

    private <A, B> void checkBounds(final A[] arg0, final B[] arg1, final int batchStartsAt, final int batchSize) {
        checkBatchSizing(batchStartsAt, batchSize);
        batchOverRuns(arg0, batchStartsAt, batchSize);
        batchOverRuns(arg1, batchStartsAt, batchSize);
    }

    private <A, B, C> void checkBounds(
            final A[] arg0, final B[] arg1, final C[] arg2, final int batchStartsAt, final int batchSize) {
        checkBatchSizing(batchStartsAt, batchSize);
        batchOverRuns(arg0, batchStartsAt, batchSize);
        batchOverRuns(arg1, batchStartsAt, batchSize);
        batchOverRuns(arg2, batchStartsAt, batchSize);
    }

    private void checkBounds(final int batchStartsAt, final int batchSize, final Object[][] args) {
        checkBatchSizing(batchStartsAt, batchSize);
        batchOverRuns(args, batchStartsAt, batchSize);
    }

    private <A> void batchOverRuns(final A[] arg0, final int batchStartsAt, final int batchSize) {
        if (batchStartsAt + batchSize > arg0.length)
        {
            throw new IllegalArgumentException(
                    "A batchSize of: " + batchSize +
                            " with batchStatsAt of: " + batchStartsAt +
                            " will overrun the available number of arguments: " + (arg0.length - batchStartsAt));
        }
    }

    private void translateAndPublish(EventTranslator<E> translator, long sequence) {
        try {
            translator.translateTo(get(sequence), sequence);
        }
        finally {
            sequencer.publish(sequence);
        }
    }

    private <A> void translateAndPublish(EventTranslatorOneArg<E, A> translator, long sequence, A arg0) {
        try {
            translator.translateTo(get(sequence), sequence, arg0);
        }
        finally {
            sequencer.publish(sequence);
        }
    }

    private <A, B> void translateAndPublish(EventTranslatorTwoArg<E, A, B> translator, long sequence, A arg0, B arg1) {
        try {
            translator.translateTo(get(sequence), sequence, arg0, arg1);
        }
        finally {
            sequencer.publish(sequence);
        }
    }

    private <A, B, C> void translateAndPublish(
            EventTranslatorThreeArg<E, A, B, C> translator, long sequence,
            A arg0, B arg1, C arg2) {
        try
        {
            translator.translateTo(get(sequence), sequence, arg0, arg1, arg2);
        }
        finally
        {
            sequencer.publish(sequence);
        }
    }

    private void translateAndPublish(EventTranslatorVararg<E> translator, long sequence, Object... args) {
        try {
            translator.translateTo(get(sequence), sequence, args);
        }
        finally {
            sequencer.publish(sequence);
        }
    }

    private void translateAndPublishBatch(
            final EventTranslator<E>[] translators, int batchStartsAt,
            final int batchSize, final long finalSequence) {
        final long initialSequence = finalSequence - (batchSize - 1);
        try {
            long sequence = initialSequence;
            final int batchEndsAt = batchStartsAt + batchSize;
            for (int i = batchStartsAt; i < batchEndsAt; i++)
            {
                final EventTranslator<E> translator = translators[i];
                translator.translateTo(get(sequence), sequence++);
            }
        }
        finally
        {
            sequencer.publish(initialSequence, finalSequence);
        }
    }

    private <A> void translateAndPublishBatch(
            final EventTranslatorOneArg<E, A> translator, final A[] arg0,
            int batchStartsAt, final int batchSize, final long finalSequence)
    {
        final long initialSequence = finalSequence - (batchSize - 1);
        try
        {
            long sequence = initialSequence;
            final int batchEndsAt = batchStartsAt + batchSize;
            for (int i = batchStartsAt; i < batchEndsAt; i++)
            {
                translator.translateTo(get(sequence), sequence++, arg0[i]);
            }
        }
        finally
        {
            sequencer.publish(initialSequence, finalSequence);
        }
    }

    private <A, B> void translateAndPublishBatch(
            final EventTranslatorTwoArg<E, A, B> translator, final A[] arg0,
            final B[] arg1, int batchStartsAt, int batchSize,
            final long finalSequence) {
        final long initialSequence = finalSequence - (batchSize - 1);
        try
        {
            long sequence = initialSequence;
            final int batchEndsAt = batchStartsAt + batchSize;
            for (int i = batchStartsAt; i < batchEndsAt; i++)
            {
                translator.translateTo(get(sequence), sequence++, arg0[i], arg1[i]);
            }
        }
        finally
        {
            sequencer.publish(initialSequence, finalSequence);
        }
    }

    private <A, B, C> void translateAndPublishBatch(
            final EventTranslatorThreeArg<E, A, B, C> translator,
            final A[] arg0, final B[] arg1, final C[] arg2, int batchStartsAt,
            final int batchSize, final long finalSequence) {
        final long initialSequence = finalSequence - (batchSize - 1);
        try
        {
            long sequence = initialSequence;
            final int batchEndsAt = batchStartsAt + batchSize;
            for (int i = batchStartsAt; i < batchEndsAt; i++)
            {
                translator.translateTo(get(sequence), sequence++, arg0[i], arg1[i], arg2[i]);
            }
        }
        finally
        {
            sequencer.publish(initialSequence, finalSequence);
        }
    }

    private void translateAndPublishBatch(
            final EventTranslatorVararg<E> translator, int batchStartsAt,
            final int batchSize, final long finalSequence, final Object[][] args)
    {
        final long initialSequence = finalSequence - (batchSize - 1);
        try
        {
            long sequence = initialSequence;
            final int batchEndsAt = batchStartsAt + batchSize;
            for (int i = batchStartsAt; i < batchEndsAt; i++)
            {
                translator.translateTo(get(sequence), sequence++, args[i]);
            }
        }
        finally
        {
            sequencer.publish(initialSequence, finalSequence);
        }
    }

    @Override
    public String toString() {
        return "RingBuffer{" +
                "bufferSize=" + bufferSize +
                ", sequencer=" + sequencer +
                "}";
    }
}
