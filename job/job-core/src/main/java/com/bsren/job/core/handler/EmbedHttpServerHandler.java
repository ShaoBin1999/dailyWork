package com.bsren.job.core.handler;

import com.bsren.job.core.biz.ExecutorBiz;
import com.bsren.job.core.biz.model.KillParam;
import com.bsren.job.core.biz.model.LogParam;
import com.bsren.job.core.biz.model.ReturnT;
import com.bsren.job.core.biz.model.TriggerParam;
import com.bsren.job.core.utils.GsonTool;
import com.bsren.job.core.utils.ThrowableUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.timeout.IdleStateEvent;
import javafx.beans.binding.ObjectExpression;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
public class EmbedHttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private ExecutorBiz executorBiz;

    private String accessToken;

    private ThreadPoolExecutor threadPoolExecutor;

    public EmbedHttpServerHandler(ExecutorBiz executorBiz, String accessToken, ThreadPoolExecutor bizThreadPool) {
        this.executorBiz = executorBiz;
        this.accessToken = accessToken;
        this.threadPoolExecutor = bizThreadPool;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest) throws Exception {
        String requestData = fullHttpRequest.content().toString();
        String uri = fullHttpRequest.uri();
        HttpMethod method = fullHttpRequest.method();
        boolean keepAlive = HttpUtil.isKeepAlive(fullHttpRequest);

        String accessToken = fullHttpRequest.headers().get("JOB-ACCESS-TOKEN");
        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Object responseObj = process(method, uri, requestData, accessToken);
                String responseJson = GsonTool.toJson(responseObj);
                writeResponse(channelHandlerContext,keepAlive,responseJson);
            }
        });
    }

    //todo 研究一个request或者一个response的结构
    private void writeResponse(ChannelHandlerContext ctx, boolean keepAlive, String responseJson) {
        DefaultFullHttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK, Unpooled.copiedBuffer(responseJson, Charset.defaultCharset()));
        httpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/html;charset=UTF-8");
        httpResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH,httpResponse.content().readableBytes());
        if(keepAlive){
            httpResponse.headers().set(HttpHeaderNames.CONNECTION,HttpHeaderValues.KEEP_ALIVE);
        }
        ctx.writeAndFlush(httpResponse);
    }

    private Object process(HttpMethod httpMethod,String uri,String requestData,String accessToken){
        // valid
        if (HttpMethod.POST != httpMethod) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "invalid request, HttpMethod not support.");
        }
        if (uri == null || uri.trim().length() == 0) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "invalid request, uri-mapping empty.");
        }
        if (accessToken != null
                && accessToken.trim().length() > 0
                && !accessToken.equals(this.accessToken)) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "The access token is wrong.");
        }


        try {
            switch (uri){
                case "/beat":
                    return executorBiz.beat();
                case "/run":
                    //todo 使用json的方式对传参进行解析
                    TriggerParam triggerParam = GsonTool.fromJson(requestData, TriggerParam.class);
                    return executorBiz.run(triggerParam);
                case "/kill":
                    KillParam killParam = GsonTool.fromJson(requestData,KillParam.class);
                    return executorBiz.kill(killParam);
                case "/log":
                    LogParam logParam = GsonTool.fromJson(requestData,LogParam.class);
                    return executorBiz.log(logParam);
                default:
                    return new ReturnT<String>(ReturnT.FAIL_CODE,"invalid request, uri "+
                            uri+"not found");
            }
        }catch (Exception e){
            log.error(e.getMessage(), e);
            return new ReturnT<String>(ReturnT.FAIL_CODE, "request error:" + ThrowableUtil.toString(e));
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("netty_http server caught exception", cause);
        ctx.close();
    }

    /**
     * 再次搞明白空闲检测
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            ctx.channel().close();      // beat 3N, close if idle
            log.debug("netty_http server close an idle channel.");
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

}
