package com.bsren.job.core.trigger;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public class SimpleTrigger extends Trigger{

    private long delay;

    private long timeInterval;

    private int count;

    @Override
    public String toString() {
        return "SimpleTrigger{" +
                "delay=" + delay +
                ", timeInterval=" + timeInterval +
                ", count=" + count +
                '}';
    }
}
