package com.bsren.netty.chatroom.handler;

import com.bsren.netty.chatroom.message.GroupQuitRequestMessage;
import com.bsren.netty.chatroom.message.GroupQuitResponseMessage;
import com.bsren.netty.chatroom.server.service.GroupSessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@ChannelHandler.Sharable
public class GroupQuitRequestHandler extends SimpleChannelInboundHandler<GroupQuitRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupQuitRequestMessage msg) throws Exception {
        boolean quit = GroupSessionFactory.getGroupSession().quit(msg.getGroupName(), msg.getUserName());
        GroupQuitResponseMessage message;
        if(quit){
            message = new GroupQuitResponseMessage(true,"您已退出群聊"+msg.getGroupName());
        }else {
            message = new GroupQuitResponseMessage(false,"退出群聊失败");
        }
        ctx.writeAndFlush(message);
    }
}
