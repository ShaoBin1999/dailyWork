package com.bsren.wechatpay.controller;


import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.bsren.wechatpay.service.service2;
import org.apache.http.entity.ContentType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Enumeration;

@RestController
@RequestMapping
public class helloController {

    @Resource
    private service2 service2;

    @GetMapping("/hello")
    @ResponseBody
    public void fun(HttpServletRequest request){
        System.out.println("hello");
    }

    @GetMapping("/hel")
    @ResponseBody
    public void fun1(){
        System.out.println("hello");
        JSONObject jsonObject = new JSONObject();
        service2.addJson(jsonObject);
        System.out.println(jsonObject);
    }


    @PostMapping("/add")
    public JSONObject fun2(@RequestParam Integer num){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("rsb",num);
        return jsonObject;
    }

    @GetMapping("/get")
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType(ContentType.APPLICATION_JSON.toString());
        ServletOutputStream outputStream = response.getOutputStream();
        //获得所有请求头字段的枚举集合
        Enumeration<String> headers = request.getHeaderNames();
        JSONObject jsonObject = new JSONObject();
        while (headers.hasMoreElements()) {
            //获得请求头字段的值
            String value = request.getHeader(headers.nextElement());
            jsonObject.put(headers.nextElement(),value);
        }
        jsonObject.put("getContentType()",request.getContentType());
        jsonObject.put("getContentLength()",request.getContentLength());
        jsonObject.put("getCharacterEncoding()",request.getCharacterEncoding());
        byte[] bytes = JSONUtil.toJsonStr(jsonObject).getBytes(StandardCharsets.UTF_8);
        outputStream.write(bytes);
        outputStream.close();
    }

    @GetMapping("/get1")
    public void doGet1(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType(ContentType.APPLICATION_JSON.toString());
        ServletOutputStream outputStream = response.getOutputStream();
        //获得所有请求头字段的枚举集合
        Enumeration<String> headers = request.getHeaderNames();
        JSONObject jsonObject = new JSONObject();
        while (headers.hasMoreElements()) {
            //获得请求头字段的值
            String value = request.getHeader(headers.nextElement());
            jsonObject.put(headers.nextElement(),value);
        }
        jsonObject.put("getContentType()",request.getContentType());
        jsonObject.put("getContentLength()",request.getContentLength());
        jsonObject.put("getCharacterEncoding()",request.getCharacterEncoding());

        byte[] bytes = jsonObject.toJSONString().getBytes(StandardCharsets.UTF_8);
        outputStream.write(bytes);
        outputStream.close();
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doGet(request, response);
    }


    @PostMapping("/notify")
    public JSONObject Notify(HttpServletRequest request, HttpServletResponse response) throws IOException, GeneralSecurityException {
        JSONObject jsonObject = service2.notify(request, response);
        return jsonObject;
    }
}
