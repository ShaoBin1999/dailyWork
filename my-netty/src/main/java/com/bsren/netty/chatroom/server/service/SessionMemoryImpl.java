package com.bsren.netty.chatroom.server.service;

import io.netty.channel.Channel;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SessionMemoryImpl implements Session{

    Map<String,Set<Channel>> userChannel = new ConcurrentHashMap<>();

    Map<Channel,String> channelUser = new ConcurrentHashMap<>();

    @Override
    public void bind(Channel channel, String userName) {
        if(userChannel.containsKey(userName)){
            userChannel.get(userName).add(channel);
        }else {
            Set<Channel> channels = new HashSet<>();
            channels.add(channel);
            userChannel.put(userName,channels);
        }
        channelUser.put(channel,userName);
    }

    @Override
    public void unbind(Channel channel) {
        channelUser.remove(channel);
        for (Map.Entry<String, Set<Channel>> entry : userChannel.entrySet()) {
            entry.getValue().remove(channel);
        }
    }

    @Override
    public Set<Channel> getChannel(String userName) {
        return userChannel.get(userName);
    }
}
