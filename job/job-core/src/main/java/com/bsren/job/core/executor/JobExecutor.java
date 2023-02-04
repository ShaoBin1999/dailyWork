package com.bsren.job.core.executor;

import com.bsren.job.core.biz.AdminBiz;
import com.bsren.job.core.biz.client.AdminBizClient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JobExecutor {

    private String adminAddress;

    private String accessToken;

    private String executorName;

    private String executorAddress;

    private String ip;

    private int port;

    private String logPath;

    private int logRetentionDays;

    public void start(){

        initLogPath(logPath);

        initAdminBizClient(adminAddress,accessToken);

        initCallbackThread();

        initEmbedServer(adminAddress,port,executorName,accessToken);

    }

    private void initEmbedServer(String adminAddress, int port, String executorName, String accessToken) {
    }


    private void initCallbackThread() {
    }

    // 这里是一个client可能有多个admin进行任务的添加
    // 但是我觉得这样没什么必要，因为这会导致多对多的关系
    // 所以先用单个的admin进行代替吧
    private static AdminBiz adminBiz;
    private void initAdminBizClient(String adminAddress,String accessToken) {
        adminBiz = new AdminBizClient(adminAddress,accessToken);
    }

    public static AdminBiz getAdminBiz(){
        return adminBiz;
    }

    private void initLogPath(String logPath) {


    }

}
