package com.bsren.job.core.server;

import com.bsren.job.core.biz.ExecutorBiz;
import com.bsren.job.core.biz.impl.ExecutorBizImpl;
import com.bsren.job.core.biz.model.RegistryParam;
import com.bsren.job.core.biz.model.ReturnT;
import com.bsren.job.core.enums.RegistryConfig;
import com.bsren.job.core.handler.EmbedHttpServerHandler;
import com.bsren.job.core.model.JobExecutor;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.*;

import static com.bsren.job.core.executor.JobExecutor.getAdminBiz;


@Slf4j
public class EmbedServer {

    private ExecutorBiz executorBiz;

    private Thread thread;

    public void start(final String address, final int port, final String executorName, final String accessToken){
        executorBiz = new ExecutorBizImpl();
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                EventLoopGroup bossGroup = new NioEventLoopGroup();
                EventLoopGroup workerGroup = new NioEventLoopGroup();
                //定义线程池 todo 里面的参数待考量
                ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                        0,
                        200,
                        60L,
                        TimeUnit.SECONDS,
                        new LinkedBlockingQueue<>(2000),
                        new ThreadFactory() {
                            @Override
                            public Thread newThread(@NotNull Runnable r) {
                                return new Thread(r, "embedServer threadPool" + r.hashCode());
                            }
                        },
                        new RejectedExecutionHandler() {
                            @Override
                            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                                throw new RuntimeException("embedServer threadPool is exhausted");
                            }
                }
                );
                try {
                    ServerBootstrap bootstrap = new ServerBootstrap();
                    //todo 好好学一学netty
                    bootstrap.group(bossGroup,workerGroup)
                            .channel(NioServerSocketChannel.class)
                            .childHandler(new ChannelInitializer<SocketChannel>() {
                                @Override
                                protected void initChannel(SocketChannel socketChannel) throws Exception {
                                    socketChannel.pipeline()
                                            .addLast(new IdleStateHandler(0,0,30*3,TimeUnit.SECONDS))
                                            .addLast(new HttpServerCodec())
                                            .addLast(new HttpObjectAggregator(5 * 1024 * 1024))
                                            .addLast(new EmbedHttpServerHandler(executorBiz, accessToken, threadPoolExecutor));
                                }
                            }).childOption(ChannelOption.SO_KEEPALIVE, true);
                    ChannelFuture future = bootstrap.bind(port).sync();
                    registry(executorName,address);
                    future.channel().closeFuture().sync();
                } catch (InterruptedException e) {
                    log.info("remoting server stop");
                }finally {
                    workerGroup.shutdownGracefully();
                    bossGroup.shutdownGracefully();
                }
            }
        });
        thread.setName("embedServer thread");
        thread.setDaemon(true);
        thread.start();
    }

    public void stop(){
        if(thread!=null && thread.isAlive()){
            thread.interrupt();
        }
//        registryRemove();
        log.info("embedServer destroy success");
    }

    //todo 把这些移到executorBiz里面
    //这里没必要用一个线程去进行注册，多调用几次rpc不就行了
    private boolean registry(String executorName, String address) {
        return registry(executorName,address,10);
    }

    private boolean registry(String executorName, String address,int retryCount) {
        boolean res = false;
        while (retryCount-->0){
            RegistryParam registryParam = new RegistryParam(RegistryConfig.RegistType.EXECUTOR.name(),executorName,address);
            ReturnT<String> returnT = getAdminBiz().registry(registryParam);
            if(returnT!=null && returnT.getCode()==ReturnT.SUCCESS_CODE){
                res = true;
                break;
            }
        }
        if(res){
            log.info(executorName+" in "+address+" registry success");
        }else {
            log.info("after retry "+retryCount+"times,"+executorName+" in "+address+" registry fail");
        }
        return res;
    }

    //remove之后应该也有别的操作
    private boolean registryRemove(String executorName, String address) {
        return registryRemove(executorName,address,10);
    }

    //就算不成功也能强制关停
    private boolean registryRemove(String executorName, String address,int retryCount) {
        boolean res = false;
        while (retryCount-->0){
            RegistryParam registryParam = new RegistryParam(RegistryConfig.RegistType.EXECUTOR.name(), executorName, address);
            ReturnT<String> returnT = getAdminBiz().registryRemove(registryParam);
            if(returnT!=null && returnT.getCode()==ReturnT.SUCCESS_CODE){
                res = true;
                break;
            }
        }
        if(res){
            log.info(executorName+" in "+address+" remove success");
        }else {
            log.info("after retry "+retryCount+"times,"+executorName+" in "+address+" remove fail");
        }
        return res;
    }
}
