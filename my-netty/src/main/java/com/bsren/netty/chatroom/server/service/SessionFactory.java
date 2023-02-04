package com.bsren.netty.chatroom.server.service;

public  abstract class SessionFactory {
    private static final Session session = new SessionMemoryImpl();

    public static Session getSession(){
        return session;
    }
}
