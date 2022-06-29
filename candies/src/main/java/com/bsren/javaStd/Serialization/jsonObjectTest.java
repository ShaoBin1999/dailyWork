package com.bsren.javaStd.Serialization;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;

public class jsonObjectTest {
    public static void main(String[] args) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("haha","nihao");
        jsonObject.put("num",1);
        String s = jsonObject.toJSONString();
        System.out.println(s);
        String s1 = JSONUtil.toJsonStr(jsonObject);
        System.out.println(s1);
    }
}
