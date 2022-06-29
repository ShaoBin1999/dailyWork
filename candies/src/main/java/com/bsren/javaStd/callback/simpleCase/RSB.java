package com.bsren.javaStd.callback.simpleCase;

public class RSB {
    private final String name = "rsb";

    public RSB() {
    }

    public class doCal implements Job {

        @Override
        public void fillBlank(int a, int b, int result) {
            System.out.println(name+":"+a+"+"+b+"="+result);
        }
    }

    public void useCalculator(int a,int b){
        new Calculator().add(a,b,new doCal());
    }
}
