package com.bsren.netty.chatroom.server.service;

public abstract class UserServiceFactory {

    private static final UserService userService = new UserServiceImpl();


    public static UserService getUserService(){
        return userService;
    }
}
