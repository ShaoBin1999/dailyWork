package com.bsren.netty.chatroom.handler;

import com.bsren.netty.chatroom.message.GroupMembersRequestMessage;
import com.bsren.netty.chatroom.message.GroupMembersResponseMessage;
import com.bsren.netty.chatroom.server.service.GroupSessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Set;

@ChannelHandler.Sharable
public class GroupMembersRequestHandler extends SimpleChannelInboundHandler<GroupMembersRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupMembersRequestMessage msg) throws Exception {
        String groupName = msg.getGroupName();
        Set<String> members = GroupSessionFactory.getGroupSession().getMembers(groupName);
        String join = String.join(",", members);
        GroupMembersResponseMessage message = new GroupMembersResponseMessage(join);
        ctx.writeAndFlush(message);
    }
}
