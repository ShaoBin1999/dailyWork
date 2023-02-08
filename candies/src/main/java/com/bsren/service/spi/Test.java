package com.bsren.service.spi;

import java.util.ServiceLoader;

public class Test {

    public static void main(String[] args) {
        ServiceLoader<HelloService> services = ServiceLoader.load(HelloService.class);
        for (HelloService service : services) {
            service.say();
        }
    }
}
