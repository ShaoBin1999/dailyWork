package com.bsren.javaStd.callback.simpleCase;

public class test {
    public static void main(String[] args) {
        int a=520,b=250;
        RSB rsb = new RSB();
        WRQ wrq = new WRQ();

        rsb.useCalculator(a,b);
        wrq.useCalculator(b,a);
    }
}
