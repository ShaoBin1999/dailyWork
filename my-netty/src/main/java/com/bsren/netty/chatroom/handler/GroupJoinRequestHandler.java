package com.bsren.netty.chatroom.handler;

import com.bsren.netty.chatroom.message.GroupJoinRequestMessage;
import com.bsren.netty.chatroom.message.GroupJoinResponseMessage;
import com.bsren.netty.chatroom.server.service.GroupSessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@ChannelHandler.Sharable
public class GroupJoinRequestHandler extends SimpleChannelInboundHandler<GroupJoinRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupJoinRequestMessage msg) throws Exception {
        String groupName = msg.getGroupName();
        boolean add = GroupSessionFactory.getGroupSession().add(groupName, msg.getUserName());
        GroupJoinResponseMessage message;
        if(add){
            message = new GroupJoinResponseMessage(true,"您已加入群聊"+groupName);
        }else {
            message = new GroupJoinResponseMessage(false,"加入群聊"+groupName+"失败");
        }
        ctx.writeAndFlush(message);
    }
}
