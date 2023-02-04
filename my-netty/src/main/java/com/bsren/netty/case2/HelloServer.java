package com.bsren.netty.case2;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

public class HelloServer {

    public static void main(String[] args) {
        new ServerBootstrap()      //启动器
                //BossEventLoop处理可连接事件，WorkerEventLoop(selector,thread)处理可读事件，合称为group
                .group(new NioEventLoopGroup())
                //选择服务器的serverSocketChannel实现
                .channel(NioServerSocketChannel.class)
                //workerEventLoop需要处理什么样的业务
                .childHandler(
                        //channel代表和客户端进行数据读写的通道，initializer初始化，负责添加handler
                        new ChannelInitializer<NioSocketChannel>() {
                            //连接建立后被调用
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline()
                                //将bytebuffer转化为字符串
                                .addLast(new StringDecoder())
                                //自定义的handler
                                .addLast(new ChannelInboundHandlerAdapter(){
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        System.out.println(msg);
                                    }
                                });
                    }
                }).bind(8888);
    }
}
