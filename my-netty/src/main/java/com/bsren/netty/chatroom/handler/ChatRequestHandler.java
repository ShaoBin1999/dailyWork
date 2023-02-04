package com.bsren.netty.chatroom.handler;

import com.bsren.netty.chatroom.message.ChatRequestMessage;
import com.bsren.netty.chatroom.message.ChatResponseMessage;
import com.bsren.netty.chatroom.server.service.SessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@ChannelHandler.Sharable
@Slf4j
public class ChatRequestHandler extends SimpleChannelInboundHandler<ChatRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatRequestMessage msg) throws Exception {
        String from = msg.getFrom();
        String to = msg.getTo();
        Set<Channel> channel = SessionFactory.getSession().getChannel(to);
        if(channel.size()==0){
            ctx.writeAndFlush(new ChatResponseMessage(false,"对方不在线"));
            return;
        }
        for (Channel ch : channel) {
            ch.writeAndFlush(new ChatResponseMessage(from,msg.getContent()));
        }
        log.info(from+" send "+channel+" to "+to);
    }
}
