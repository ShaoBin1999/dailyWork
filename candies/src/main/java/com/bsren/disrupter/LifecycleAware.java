package com.bsren.disrupter;

public interface LifecycleAware {

    /**
     * Called once on thread start before first event is available.
     */
    void onStart();

    /**
     * <p>Called once just before the thread is shutdown.</p>
     * <p>
     * Sequence event processing will already have stopped before this method is called. No events will
     * be processed after this message.
     */
    void onShutdown();
}
