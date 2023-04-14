package com.bsren.disrupter;


import com.bsren.disrupter.eventTranslator.EventTranslatorOneArg;
import com.bsren.disrupter.eventTranslator.EventTranslatorThreeArg;
import com.bsren.disrupter.eventTranslator.EventTranslatorTwoArg;
import com.bsren.disrupter.eventTranslator.EventTranslatorVararg;

public interface EventSink<E> {

    /**
     * Publishes an event to the ring buffer.  It handles
     * claiming the next sequence, getting the current (uninitialised)
     * event from the ring buffer and publishing the claimed sequence
     * after translation.
     *
     * @param translator The user specified translation for the event
     */
    void publishEvent(EventTranslator<E> translator);

    /**
     * Attempts to publish an event to the ring buffer.  It handles
     * claiming the next sequence, getting the current (uninitialised)
     * event from the ring buffer and publishing the claimed sequence
     * after translation.  Will return false if specified capacity
     * was not available.
     *
     * @param translator The user specified translation for the event
     * @return true if the value was published, false if there was insufficient
     * capacity.
     */
    boolean tryPublishEvent(EventTranslator<E> translator);


    <A> void publishEvent(EventTranslatorOneArg<E, A> translator, A arg0);

    <A> boolean tryPublishEvent(EventTranslatorOneArg<E, A> translator, A arg0);

    <A, B> void publishEvent(EventTranslatorTwoArg<E, A, B> translator, A arg0, B arg1);

    <A, B> boolean tryPublishEvent(EventTranslatorTwoArg<E, A, B> translator, A arg0, B arg1);

    <A, B, C> void publishEvent(EventTranslatorThreeArg<E, A, B, C> translator, A arg0, B arg1, C arg2);

    <A, B, C> boolean tryPublishEvent(EventTranslatorThreeArg<E, A, B, C> translator, A arg0, B arg1, C arg2);

    void publishEvent(EventTranslatorVararg<E> translator, Object... args);

    boolean tryPublishEvent(EventTranslatorVararg<E> translator, Object... args);

    void publishEvents(EventTranslator<E>[] translators);


    void publishEvents(EventTranslator<E>[] translators, int batchStartsAt, int batchSize);


    boolean tryPublishEvents(EventTranslator<E>[] translators);


    boolean tryPublishEvents(EventTranslator<E>[] translators, int batchStartsAt, int batchSize);

    <A> void publishEvents(EventTranslatorOneArg<E, A> translator, A[] arg0);


    <A> void publishEvents(EventTranslatorOneArg<E, A> translator, int batchStartsAt, int batchSize, A[] arg0);

    <A> boolean tryPublishEvents(EventTranslatorOneArg<E, A> translator, A[] arg0);

    <A> boolean tryPublishEvents(EventTranslatorOneArg<E, A> translator, int batchStartsAt, int batchSize, A[] arg0);

    <A, B> void publishEvents(EventTranslatorTwoArg<E, A, B> translator, A[] arg0, B[] arg1);

    <A, B> void publishEvents(
            EventTranslatorTwoArg<E, A, B> translator, int batchStartsAt, int batchSize, A[] arg0,
            B[] arg1);

    <A, B> boolean tryPublishEvents(EventTranslatorTwoArg<E, A, B> translator, A[] arg0, B[] arg1);


    <A, B> boolean tryPublishEvents(
            EventTranslatorTwoArg<E, A, B> translator, int batchStartsAt, int batchSize,
            A[] arg0, B[] arg1);

    <A, B, C> void publishEvents(EventTranslatorThreeArg<E, A, B, C> translator, A[] arg0, B[] arg1, C[] arg2);

    <A, B, C> void publishEvents(
            EventTranslatorThreeArg<E, A, B, C> translator, int batchStartsAt, int batchSize,
            A[] arg0, B[] arg1, C[] arg2);

    <A, B, C> boolean tryPublishEvents(EventTranslatorThreeArg<E, A, B, C> translator, A[] arg0, B[] arg1, C[] arg2);


    <A, B, C> boolean tryPublishEvents(
            EventTranslatorThreeArg<E, A, B, C> translator, int batchStartsAt,
            int batchSize, A[] arg0, B[] arg1, C[] arg2);


    void publishEvents(EventTranslatorVararg<E> translator, Object[]... args);


    void publishEvents(EventTranslatorVararg<E> translator, int batchStartsAt, int batchSize, Object[]... args);

    boolean tryPublishEvents(EventTranslatorVararg<E> translator, Object[]... args);

    boolean tryPublishEvents(EventTranslatorVararg<E> translator, int batchStartsAt, int batchSize, Object[]... args);

}
