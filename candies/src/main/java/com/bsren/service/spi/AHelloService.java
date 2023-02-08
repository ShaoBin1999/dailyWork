package com.bsren.service.spi;

public class AHelloService implements HelloService{
    @Override
    public void say() {
        System.out.println("A");
    }
}
