package com.bsren.netty.chatroom.handler;

import com.bsren.netty.chatroom.message.GroupChatRequestMessage;
import com.bsren.netty.chatroom.message.GroupChatResponseMessage;
import com.bsren.netty.chatroom.server.service.GroupSessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Map;
import java.util.Set;

@ChannelHandler.Sharable
public class GroupChatRequestHandler extends SimpleChannelInboundHandler<GroupChatRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupChatRequestMessage msg) throws Exception {
        String groupName = msg.getGroupName();
        Map<String, Set<Channel>> channels = GroupSessionFactory.getGroupSession().getMemberChannels(groupName);
        GroupChatResponseMessage message = new GroupChatResponseMessage(msg.getFrom(),msg.getContent());
        for (Map.Entry<String, Set<Channel>> entry : channels.entrySet()) {
            for (Channel channel : entry.getValue()) {
                channel.writeAndFlush(message);
            }
        }
    }
}
