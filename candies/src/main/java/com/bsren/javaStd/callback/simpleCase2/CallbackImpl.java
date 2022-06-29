package com.bsren.javaStd.callback.simpleCase2;

import org.springframework.stereotype.Service;

import java.util.Map;


@Service
public class CallbackImpl implements Callback{
    @Override
    public void func(Map<String, String> map) {
        map.put("1,","2");
    }
}
