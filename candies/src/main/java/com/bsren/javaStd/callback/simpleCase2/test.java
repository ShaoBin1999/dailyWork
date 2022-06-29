package com.bsren.javaStd.callback.simpleCase2;

import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;

@Service
public class test {
    @Resource
    private Callback callback;

    @Test
    public void add() {
        callback.func(new HashMap<>());
    }
}
