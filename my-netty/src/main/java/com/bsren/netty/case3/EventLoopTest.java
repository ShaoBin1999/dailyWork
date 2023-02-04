package com.bsren.netty.case3;

import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.NettyRuntime;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class EventLoopTest {


    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup(2);
        //iterator轮询，简单的负载均衡
        System.out.println(eventLoopGroup.next());
        System.out.println(eventLoopGroup.next());
        System.out.println(eventLoopGroup.next());
        eventLoopGroup.next().submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.info("run1");
            }
        });
        eventLoopGroup.next().scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.info("gaga");
            }
        },0,1, TimeUnit.SECONDS);
        eventLoopGroup.next().submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.info("run2");
            }
        });
        Thread.sleep(10000);
        eventLoopGroup.shutdownGracefully();
        //获取电脑核数
        System.out.println(NettyRuntime.availableProcessors());
    }
}
