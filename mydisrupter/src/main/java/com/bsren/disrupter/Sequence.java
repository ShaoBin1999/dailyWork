package com.bsren.disrupter;

import com.bsren.disrupter.exception.InsufficientCapacityException;

public interface Sequence {

    int getBufferSize();

    boolean hasAvailableCapacity(int requiredCapacity);

    long remainingCapacity();

    long next();

    long next(int n);

    long tryNext() throws InsufficientCapacityException;

    long tryNext(int n) throws InsufficientCapacityException;
}
