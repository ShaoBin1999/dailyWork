//package com.bsren.disrupter;
//
//import com.bsren.disrupter.util.Util;
//import sun.misc.Unsafe;
//
//
//abstract class RingBufferPad{
//    protected long p1,p2,p3,p4,p5,p6,p7;
//}
//
//abstract class RingBufferFields<E> extends RingBufferPad{
//    private static final int BUFFER_PAD;
//    private static final long REF_ARRAY_BASE;
//    private static final int REF_ELEMENT_SHIFT;
//    private static final Unsafe UNSAFE = Util.getUnsafe();
//
//    static {
//        //该电脑上为4，虽然是64位操作系统，但是开启了指针压缩，一个指针是32位，占4个字节
//        final int scale = UNSAFE.arrayIndexScale(Object[].class);
//        if (4 == scale) {
//            REF_ELEMENT_SHIFT = 2;
//        }
//        else if (8 == scale) {
//            REF_ELEMENT_SHIFT = 3;
//        }
//        else {
//            throw new IllegalStateException("Unknown pointer size");
//        }
//        //pad = 32
//        BUFFER_PAD = 128 / scale;
//        //由于entries数组被填充了32个对象引用，占用了128个字节，所以entries中第一个真实的元素在内存中的偏移量要加128
//        REF_ARRAY_BASE = UNSAFE.arrayBaseOffset(Object[].class) + (BUFFER_PAD << REF_ELEMENT_SHIFT);
//    }
//
//    private final long indexMask;
//    private final Object[] entries;
//    protected final int bufferSize;
//    protected final Sequencer sequencer;
//}
//
//public class RingBuffer {
//}
