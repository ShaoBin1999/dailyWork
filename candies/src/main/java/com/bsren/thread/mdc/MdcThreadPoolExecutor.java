package com.bsren.thread.mdc;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.util.Map;
import java.util.concurrent.*;

@Slf4j
public class MdcThreadPoolExecutor extends ThreadPoolExecutor {

    public MdcThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
    }

    @Override
    public void execute(final Runnable runnable) {
        // 获取父线程MDC中的内容，必须在run方法之前，否则等异步线程执行的时候有可能MDC里面的值已经被清空了，这个时候就会返回null
        final Map<String, String> context = MDC.getCopyOfContextMap();
        super.execute(new Runnable() {
            @Override
            public void run() {
                // 将父线程的MDC内容传给子线程
                MDC.setContextMap(context);
                try {
                    // 执行异步操作
                    runnable.run();
                } finally {
                    // 清空MDC内容
                    MDC.clear();
                }
            }
        });
    }

    public static void main(String[] args) {
        MDC.put("1","2");
        MdcThreadPoolExecutor executor=new MdcThreadPoolExecutor(1, 10, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(600), new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                // 打印日志，并且重启一个线程执行被拒绝的任务
                log.error("Task：{},rejected from：{}", r.toString(), executor.toString());
                // 直接执行被拒绝的任务，JVM另起线程执行
                r.run();
            }
        });

        log.info("父线程日志");
        executor.execute(new Runnable() {
            @Override
            public void run() {
                log.info("子线程日志");
                log.debug(MDC.get("1"));
            }
        });
    }

}
