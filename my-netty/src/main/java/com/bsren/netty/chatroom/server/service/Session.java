package com.bsren.netty.chatroom.server.service;

import io.netty.channel.Channel;

import java.util.Set;

public interface Session {

    void bind(Channel channel,String userName);

    void unbind(Channel channel);

    Set<Channel> getChannel(String userName);

}
