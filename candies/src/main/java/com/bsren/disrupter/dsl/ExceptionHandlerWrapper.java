package com.bsren.disrupter.dsl;


import com.bsren.disrupter.ExceptionHandler;
import com.bsren.disrupter.exceptionHandler.FatalExceptionHandler;

public class ExceptionHandlerWrapper<T> implements ExceptionHandler<T> {
    private ExceptionHandler<? super T> delegate = new FatalExceptionHandler();

    public void switchTo(final ExceptionHandler<? super T> exceptionHandler)
    {
        this.delegate = exceptionHandler;
    }

    @Override
    public void handleEventException(final Throwable ex, final long sequence, final T event)
    {
        delegate.handleEventException(ex, sequence, event);
    }

    @Override
    public void handleOnStartException(final Throwable ex)
    {
        delegate.handleOnStartException(ex);
    }

    @Override
    public void handleOnShutdownException(final Throwable ex)
    {
        delegate.handleOnShutdownException(ex);
    }
}
