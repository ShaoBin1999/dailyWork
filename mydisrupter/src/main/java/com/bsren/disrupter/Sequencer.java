package com.bsren.disrupter;

public interface Sequencer extends Cursored,Sequence{

    long INITIAL_CURSOR_VALUE = -1L;

    void claim(long sequence);
}
