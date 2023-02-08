package com.bsren.service.spi;



public class BHelloService implements HelloService {
    @Override
    public void say() {
        System.out.println("B");
    }
}
