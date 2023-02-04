package com.bsren.netty.chatroom.handler;

import com.bsren.netty.chatroom.message.LoginRequestMessage;
import com.bsren.netty.chatroom.message.LoginResponseMessage;
import com.bsren.netty.chatroom.server.service.SessionFactory;
import com.bsren.netty.chatroom.server.service.UserServiceFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@ChannelHandler.Sharable
public class LoginRequestHandler extends SimpleChannelInboundHandler<LoginRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginRequestMessage msg) throws Exception {
        String userName = msg.getName();
        String password = msg.getPassword();
        boolean login = UserServiceFactory.getUserService().login(userName, password);
        LoginResponseMessage message;
        if(login){
            SessionFactory.getSession().bind(ctx.channel(),userName);
            message = new LoginResponseMessage(true,"登录成功");
        }else {
            message = new LoginResponseMessage(false,"用户密码不正确");
        }
        ctx.writeAndFlush(message);

    }
}
