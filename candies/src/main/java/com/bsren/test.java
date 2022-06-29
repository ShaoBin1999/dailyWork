package com.bsren;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class test {
    public static void main(String[] args) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("1",2);
        jsonObject.put("2",3);
        System.out.println(Arrays.toString(jsonObject.toJSONString().getBytes(StandardCharsets.UTF_8)));
        System.out.println(Arrays.toString(JSONUtil.toJsonStr(jsonObject).getBytes(StandardCharsets.UTF_8)));
    }
}
