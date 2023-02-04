package com.bsren.netty.chatroom.handler;

import com.bsren.netty.chatroom.message.GroupCreateRequestMessage;
import com.bsren.netty.chatroom.message.GroupCreateResponseMessage;
import com.bsren.netty.chatroom.server.service.GroupSessionFactory;
import com.bsren.netty.chatroom.server.service.SessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Set;

@ChannelHandler.Sharable
public class GroupCreateRequestHandler extends SimpleChannelInboundHandler<GroupCreateRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupCreateRequestMessage msg) throws Exception {
        String groupName = msg.getName();
        Set<String> members = msg.getGroupMembers();
        boolean b = GroupSessionFactory.getGroupSession().create(groupName, members);
        GroupCreateResponseMessage message;
        if(b){
            message = new GroupCreateResponseMessage(true,"群"+groupName+"创建成功");
        }else {
            message = new GroupCreateResponseMessage(false,"群"+groupName+"创建失败");
        }
        Set<Channel> creator = SessionFactory.getSession().getChannel(msg.getCreator());
        for (Channel ch : creator) {
            ch.writeAndFlush(message);
        }
        for (String member : members) {
            Set<Channel> channel = SessionFactory.getSession().getChannel(member);
            for (Channel ch : channel) {
                ch.writeAndFlush("您已加入群聊"+groupName);
            }
        }
    }
}
