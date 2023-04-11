package com.bsren.disrupter;

import com.bsren.disrupter.util.Util;
import com.lmax.disruptor.InsufficientCapacityException;
import com.lmax.disruptor.SingleProducerSequencer;
import com.lmax.disruptor.WaitStrategy;
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
        //该电脑上为4
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

        return null;
    }


    @Override
    public long getCursor() {
        return 0;
    }

    @Override
    public E get(long sequence) {
        return null;
    }

    @Override
    public int getBufferSize() {
        return 0;
    }

    @Override
    public boolean hasAvailableCapacity(int requiredCapacity) {
        return false;
    }

    @Override
    public long remainingCapacity() {
        return 0;
    }

    @Override
    public long next() {
        return 0;
    }

    @Override
    public long next(int n) {
        return 0;
    }

    @Override
    public long tryNext() throws InsufficientCapacityException {
        return 0;
    }

    @Override
    public long tryNext(int n) throws InsufficientCapacityException {
        return 0;
    }

    @Override
    public void publish(long sequence) {

    }

    @Override
    public void publish(long lo, long hi) {

    }
}
