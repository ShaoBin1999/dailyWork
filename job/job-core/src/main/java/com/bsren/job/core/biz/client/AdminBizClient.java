package com.bsren.job.core.biz.client;

import com.bsren.job.core.biz.AdminBiz;
import com.bsren.job.core.biz.model.CallbackParam;
import com.bsren.job.core.biz.model.RegistryParam;
import com.bsren.job.core.biz.model.ReturnT;

import java.util.List;

//TODO 可以再增加一些配置
public class AdminBizClient implements AdminBiz {


    private String addressUrl ;
    private String accessToken;
    private int timeout = 3;


    public AdminBizClient(String addressUrl, String accessToken) {
        this.addressUrl = addressUrl;
        this.accessToken = accessToken;
    }



    @Override
    public ReturnT<String> callback(List<CallbackParam> callbackParams) {
        return null;
    }

    @Override
    public ReturnT<String> registry(RegistryParam registryParam) {
        return null;
    }

    @Override
    public ReturnT<String> registryRemove(RegistryParam registryParam) {
        return null;
    }
}
