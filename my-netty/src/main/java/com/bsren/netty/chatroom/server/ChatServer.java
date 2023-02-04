package com.bsren.netty.chatroom.server;

import com.bsren.netty.chatroom.handler.*;
import com.bsren.netty.chatroom.protocol.MessageCodec;
import com.bsren.netty.chatroom.protocol.ProtocolFrameDecoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChatServer {

    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        LoginRequestHandler LOGIN_HANDLER = new LoginRequestHandler();
        ChatRequestHandler CHAT_HANDLER = new ChatRequestHandler();
        GroupCreateRequestHandler GROUP_CREATE_HANDLER = new GroupCreateRequestHandler();
        GroupJoinRequestHandler GROUP_JOIN_HANDLER = new GroupJoinRequestHandler();
        GroupQuitRequestHandler GROUP_QUIT_HANDLER = new GroupQuitRequestHandler();
        GroupChatRequestHandler GROUP_CHAT_HANDLER = new GroupChatRequestHandler();
        GroupMembersRequestHandler GROUP_MEMBERS_HANDLER = new GroupMembersRequestHandler();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(boss, worker);
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
//                    ch.pipeline().addLast(LOGGING_HANDLER);
                    ch.pipeline().addLast(new ProtocolFrameDecoder());
                    ch.pipeline().addLast(new MessageCodec());
                    //5s内没有收到channel的数据，就会触发一个读空闲的事件
                    ch.pipeline().addLast(new IdleStateHandler(600, 0, 0));
                    ch.pipeline().addLast(new readIdleHandler());
                    ch.pipeline().addLast(LOGIN_HANDLER);
                    ch.pipeline().addLast(CHAT_HANDLER);
                    ch.pipeline().addLast(GROUP_CREATE_HANDLER);
                    ch.pipeline().addLast(GROUP_JOIN_HANDLER);
                    ch.pipeline().addLast(GROUP_QUIT_HANDLER);
                    ch.pipeline().addLast(GROUP_CHAT_HANDLER);
                    ch.pipeline().addLast(GROUP_MEMBERS_HANDLER);
                }
            });
            Channel channel = serverBootstrap.bind( 8080).sync().channel();
            channel.closeFuture().sync();
        }finally {
            worker.shutdownGracefully();
            boss.shutdownGracefully();
        }
    }

}
