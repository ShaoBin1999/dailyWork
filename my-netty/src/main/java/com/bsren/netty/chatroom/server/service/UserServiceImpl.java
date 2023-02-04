package com.bsren.netty.chatroom.server.service;

import java.util.HashMap;
import java.util.Map;

public class UserServiceImpl implements UserService{

    private static final Map<String,String> users = new HashMap<>();

    static {
        users.put("rsb","123");
        users.put("kr","123");
        users.put("wrq","123");
    }

    @Override
    public boolean login(String userName, String password) {
        String s = users.get(userName);
        return s != null && s.equals(password);
    }
}
