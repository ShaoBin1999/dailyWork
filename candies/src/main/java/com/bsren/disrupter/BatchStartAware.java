package com.bsren.disrupter;

public interface BatchStartAware {
    void onBatchStart(long batchSize);
}
