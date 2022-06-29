package com.bsren.wechatpay.service;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface service2 {
    void addJson(JSONObject jsonObject);

    JSONObject notify(HttpServletRequest request, HttpServletResponse response);
}
