package com.bsren.netty.case2;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;

public class HelloClient {

    public static void main(String[] args) throws InterruptedException {
        // 启动类
        new Bootstrap()
                // 添加eventGroup
                .group(new NioEventLoopGroup())
                // 选择客户端channel实现
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    //连接建立后被调用
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new StringEncoder());
                    }
                }).connect(new InetSocketAddress("localhost",8888))
                .sync()   //阻塞方法，直到连接建立
                .channel()//获取连接对象
                .writeAndFlush("hello,world");

    }
}
