package com.bsren.netty.case5;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Scanner;

@Slf4j
public class EventLoopClient {

    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup eventExecutors = new NioEventLoopGroup();
        Channel channel = new Bootstrap()
                .group(eventExecutors)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new StringEncoder());
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                log.info((String) msg);
                            }
                        });
                    }
                }).connect(new InetSocketAddress("localhost", 8888))
                .addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        System.out.println("连接已建立");
                    }
                })
                .sync()
                .channel();
        Scanner scanner = new Scanner(System.in);
        while (true){
            String next = scanner.next();
            channel.writeAndFlush(next);
            if(next.startsWith("end")){
                channel.close();
                break;
            }
        }
        ChannelFuture channelFuture = channel.closeFuture();
//        System.out.println("waiting close");
//        channelFuture.sync();
//        log.info("处理关闭后的连接");
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                log.info("处理关闭后的连接");
                eventExecutors.shutdownGracefully();
                log.info("eventLoop已关闭");
            }
        });
        channelFuture.sync();
    }
}
