package com.bsren.wechatpay.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.bsren.wechatpay.service.service1;
import org.springframework.stereotype.Service;

@Service
public class service1Impl implements service1 {
    @Override
    public void handler(JSONObject jsonObject) {
        jsonObject.put("1",2);
    }
}
