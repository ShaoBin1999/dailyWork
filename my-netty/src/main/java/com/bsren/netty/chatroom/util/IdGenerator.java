package com.bsren.netty.chatroom.util;

import java.util.concurrent.atomic.AtomicInteger;



//TODO 不同的id-generator方式
public abstract class IdGenerator {
    private static final AtomicInteger id = new AtomicInteger();

    public static int nextId(){
        return id.getAndIncrement();
    }
}
