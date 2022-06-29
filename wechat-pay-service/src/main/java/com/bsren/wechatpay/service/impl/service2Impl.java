package com.bsren.wechatpay.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.bsren.wechatpay.service.service1;
import com.bsren.wechatpay.service.service2;
import org.apache.http.entity.ContentType;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

@Service
public class service2Impl implements service2 {

    @Resource
    private service1 service1;

    @Override
    public void addJson(JSONObject jsonObject) {
        service1.handler(jsonObject);
    }

    @Override
    public JSONObject notify(HttpServletRequest request, HttpServletResponse response) {
        String data = readData(request);
        JSONObject jsonObject = new JSONObject().getJSONObject(data);
        response.setContentType(ContentType.APPLICATION_JSON.toString());
//        try {
//            request.getParameterMap().
//            response.getOutputStream().write(JSONUtil.toJsonStr(jsonObject).getBytes(StandardCharsets.UTF_8));
//            response.flushBuffer();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        return null;
    }
    public String readData(HttpServletRequest request) {
        BufferedReader br = null;
        try {
            StringBuilder result = new StringBuilder();
            br = request.getReader();
            for (String line; (line = br.readLine()) != null; ) {
                if (result.length() > 0) {
                    result.append("\n");
                }
                result.append(line);
            }
            return result.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
