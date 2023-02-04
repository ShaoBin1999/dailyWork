package com.bsren.netty.chatroom.server.service;

import io.netty.channel.Channel;

import java.util.Map;
import java.util.Set;

public interface GroupSession {

    boolean add(String groupName,String username);

    boolean quit(String groupName,String username);

    boolean create(String groupName,Set<String> usernames);

    Set<String> getMembers(String groupName);

    Map<String,Set<Channel>> getMemberChannels(String groupName);
}
