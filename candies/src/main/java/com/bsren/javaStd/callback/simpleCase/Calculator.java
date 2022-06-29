package com.bsren.javaStd.callback.simpleCase;

public class Calculator {
    public void add(int a, int b, Job job){
        int result = a+b;
        job.fillBlank(a,b,result);
    }
}
