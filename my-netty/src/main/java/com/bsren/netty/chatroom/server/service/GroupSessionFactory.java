package com.bsren.netty.chatroom.server.service;

public abstract class GroupSessionFactory {

    private static final GroupSession groupSession = new GroupSessionImpl();

    public static GroupSession getGroupSession(){
        return groupSession;
    }
}
