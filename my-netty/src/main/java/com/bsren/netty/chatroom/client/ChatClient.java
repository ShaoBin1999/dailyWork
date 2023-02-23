package com.bsren.netty.chatroom.client;

import com.bsren.netty.chatroom.handler.LoginRequestHandler;
import com.bsren.netty.chatroom.message.*;
import com.bsren.netty.chatroom.protocol.MessageCodec;
import com.bsren.netty.chatroom.protocol.ProtocolFrameDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;

@Slf4j
public class ChatClient {

    public static void main(String[] args) throws InterruptedException {
        ChatClient chatClient = new ChatClient();
        chatClient.start();
    }

    AtomicBoolean LOGIN = new AtomicBoolean(false);
    AtomicBoolean EXIT = new AtomicBoolean(false);
    Scanner scanner = new Scanner(System.in);
    CountDownLatch WAIT_FOR_LOGIN = new CountDownLatch(1);

    public void start() throws InterruptedException {
        NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(nioEventLoopGroup);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ProtocolFrameDecoder());
                    ch.pipeline().addLast(new MessageCodec());
                    ch.pipeline().addLast(new IdleStateHandler(0,300,0));
                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                        @Override
                        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                            IdleStateEvent idleStateEvent = (IdleStateEvent)evt;
                            if(idleStateEvent.state()== IdleState.WRITER_IDLE){
                                ctx.writeAndFlush("写空闲");
//                                System.out.println("写空闲");
                            }
                        }
                    });
                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            log.info("msg: {}", msg);
                            if (msg instanceof LoginResponseMessage) {
                                LoginResponseMessage message = (LoginResponseMessage) msg;
                                if (message.isSuccess()) {
                                    LOGIN.set(true);
                                }
                                WAIT_FOR_LOGIN.countDown();
                            }
                        }

                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            Thread thread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    System.out.println("请输入用户名");
                                    String username = scanner.nextLine();
                                    System.out.println("请输入密码");
                                    String password = scanner.nextLine();
                                    LoginRequestMessage login = new LoginRequestMessage(username, password);
                                    ctx.writeAndFlush(login);
                                    System.out.println("----------等待登录-----------");
                                    try {
                                        WAIT_FOR_LOGIN.await();
                                    } catch (Exception e) {
                                        log.info("客户端异常关闭");
                                    }
                                    if (!LOGIN.get()) {
                                        ctx.channel().close();
                                        return;
                                    }
                                    for (; ; ) {
                                        System.out.println("==================================");
                                        System.out.println("send [username] [content]");
                                        System.out.println("gsend [group name] [content]");
                                        System.out.println("gcreate [group name] [m1,m2,m3...]");
                                        System.out.println("gmembers [group name]");
                                        System.out.println("gjoin [group name]");
                                        System.out.println("gquit [group name]");
                                        System.out.println("quit");
                                        System.out.println("==================================");
                                        String command = scanner.nextLine();
                                        if (EXIT.get()) {
                                            return;
                                        }
                                        sendMessageFromCommand(username, command, ctx);
                                    }
                                }
                            });
                            thread.start();
                        }
                    });
                }
            });
            Channel channel = bootstrap.connect("localhost", 8080).sync().channel();
            channel.closeFuture().sync();
        }finally {
            nioEventLoopGroup.shutdownGracefully();
        }
    }

    private void sendMessageFromCommand(String from,String command,ChannelHandlerContext ctx) {
        String[] strings = command.split(" ");
        switch (strings[0]){
            case "send":
                ctx.writeAndFlush(new ChatRequestMessage(strings[2],from,strings[1]));
                break;
            case "gsend":
                ctx.writeAndFlush(new GroupChatRequestMessage(from,strings[1],strings[2]));
                break;
            case "gcreate":
                String[] members = strings[2].split(",");
                HashSet<String> set = new HashSet<>(Arrays.asList(members));
                set.add(from);
                ctx.writeAndFlush(new GroupCreateRequestMessage(strings[1],set,from));
                break;
            case "gmembers":
                ctx.writeAndFlush(new GroupMembersRequestMessage(strings[1]));
                break;
            case "gjoin":
                ctx.writeAndFlush(new GroupJoinRequestMessage(strings[1],from));
                break;
            case "gquit":
                ctx.writeAndFlush(new GroupQuitRequestMessage(strings[1],from));
                break;
            case "quit":
                ctx.channel().close();
                break;
        }
    }

}
