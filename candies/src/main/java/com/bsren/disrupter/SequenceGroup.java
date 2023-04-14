package com.bsren.disrupter;

import com.bsren.disrupter.util.Util;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public final class SequenceGroup extends Sequence {
    private static final AtomicReferenceFieldUpdater<SequenceGroup, Sequence[]> SEQUENCE_UPDATER =
            AtomicReferenceFieldUpdater.newUpdater(SequenceGroup.class, Sequence[].class, "sequences");
    private volatile Sequence[] sequences = new Sequence[0];

    public SequenceGroup() {
        super(-1);
    }

    @Override
    public long get() {
        return Util.getMinimumSequence(sequences);
    }

    @Override
    public void set(final long value) {
        final Sequence[] sequences = this.sequences;
        for (Sequence sequence : sequences) {
            sequence.set(value);
        }
    }

    public void add(final Sequence sequence) {
        Sequence[] oldSequences;
        Sequence[] newSequences;
        do {
            oldSequences = sequences;
            final int oldSize = oldSequences.length;
            newSequences = new Sequence[oldSize + 1];
            System.arraycopy(oldSequences, 0, newSequences, 0, oldSize);
            newSequences[oldSize] = sequence;
        }
        while (!SEQUENCE_UPDATER.compareAndSet(this, oldSequences, newSequences));
    }

    /**
     * Remove the first occurrence of the {@link com.lmax.disruptor.Sequence} from this aggregate.
     *
     * @param sequence to be removed from this aggregate.
     * @return true if the sequence was removed otherwise false.
     */
    public boolean remove(final Sequence sequence)
    {
        return SequenceGroups.removeSequence(this, SEQUENCE_UPDATER, sequence);
    }

    /**
     * Get the size of the group.
     *
     * @return the size of the group.
     */
    public int size() {
        return sequences.length;
    }

    /**
     * Adds a sequence to the sequence group after threads have started to publish to
     * the Disruptor.  It will set the sequences to cursor value of the ringBuffer
     * just after adding them.  This should prevent any nasty rewind/wrapping effects.
     *
     * @param cursored The data structure that the owner of this sequence group will
     *                 be pulling it's events from.
     * @param sequence The sequence to add.
     */
    public void addWhileRunning(Cursored cursored, Sequence sequence)
    {
        SequenceGroups.addSequences(this, SEQUENCE_UPDATER, cursored, sequence);
    }
}