package com.bsren.disrupter.util;

//import sun.misc.Unsafe;
//
//import java.lang.reflect.Field;
//import java.security.AccessController;
//import java.security.PrivilegedExceptionAction;
//
//public final class Util
//{
//    /**
//     * Calculate the next power of 2, greater than or equal to x.<p>
//     * From Hacker's Delight, Chapter 3, Harry S. Warren Jr.
//     *
//     * @param x Value to round up
//     * @return The next power of 2 from x inclusive
//     */
//    public static int ceilingNextPowerOfTwo(final int x)
//    {
//        return 1 << (32 - Integer.numberOfLeadingZeros(x - 1));
//    }
//
//    /**
//     * Get the minimum sequence from an array of {@link Sequence}s.
//     *
//     * @param sequences to compare.
//     * @return the minimum sequence found or Long.MAX_VALUE if the array is empty.
//     */
//    public static long getMinimumSequence(final Sequence[] sequences)
//    {
//        return getMinimumSequence(sequences, Long.MAX_VALUE);
//    }
//
//    /**
//     * Get the minimum sequence from an array of {@link Sequence}s.
//     *
//     * @param sequences to compare.
//     * @param minimum   an initial default minimum.  If the array is empty this value will be
//     *                  returned.
//     * @return the smaller of minimum sequence value found in {@code sequences} and {@code minimum};
//     * {@code minimum} if {@code sequences} is empty
//     */
//    public static long getMinimumSequence(final Sequence[] sequences, long minimum) {
//        for (Sequence sequence : sequences) {
//            long value = sequence.get();
//            minimum = Math.min(minimum, value);
//        }
//        return minimum;
//    }
//
//    /**
//     * Get an array of {@link Sequence}s for the passed {@link EventProcessor}s
//     *
//     * @param processors for which to get the sequences
//     * @return the array of {@link Sequence}s
//     */
//    public static Sequence[] getSequencesFor(final EventProcessor... processors) {
//        Sequence[] sequences = new Sequence[processors.length];
//        for (int i = 0; i < sequences.length; i++) {
//            sequences[i] = processors[i].getSequence();
//        }
//        return sequences;
//    }
//
//    private static final Unsafe THE_UNSAFE;
//
//    static {
//        try {
//            final PrivilegedExceptionAction<Unsafe> action = new PrivilegedExceptionAction<Unsafe>() {
//                public Unsafe run() throws Exception {
//                    Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
//                    theUnsafe.setAccessible(true);
//                    return (Unsafe) theUnsafe.get(null);
//                }
//            };
//
//            THE_UNSAFE = AccessController.doPrivileged(action);
//        }
//        catch (Exception e) {
//            throw new RuntimeException("Unable to load unsafe", e);
//        }
//    }
//
//    /**
//     * Get a handle on the Unsafe instance, used for accessing low-level concurrency
//     * and memory constructs.
//     *
//     * @return The Unsafe
//     */
//    public static Unsafe getUnsafe()
//    {
//        return THE_UNSAFE;
//    }
//
//    /**
//     * Calculate the log base 2 of the supplied integer, essentially reports the location
//     * of the highest bit.
//     *
//     * @param i Value to calculate log2 for.
//     * @return The log2 value
//     */
//    public static int log2(int i)
//    {
//        int r = 0;
//        while ((i >>= 1) != 0)
//        {
//            ++r;
//        }
//        return r;
//    }
//}