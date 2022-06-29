package com.bsren.javaStd.callback.simpleCase2;

import java.util.Map;

public class Handler {
    public void addMap(Map<String,String> map, Callback callback){
        callback.func(map);
    }
}
