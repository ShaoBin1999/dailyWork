package com.bsren.algorithm.math;

import com.bsren.common.MyException;

import java.util.concurrent.ConcurrentSkipListMap;

public class String2Num {

    public static void main(String[] args) throws MyException {
        System.out.println(parseInt("10000", 2));
        new ConcurrentSkipListMap<>();
    }

    public static int parseInt(String s,int radix) throws MyException {
        if(s==null){
            throw new MyException();
        }
        if(radix<2 || radix>36){
            throw new MyException();
        }
        int result=0;
        boolean negative = false;
        int i=0,len = s.length();
        int limit = -Integer.MAX_VALUE;
        int mulMin;
        int digit;
        if(len==0){
            throw new MyException();
        }
        char firstChar = s.charAt(0);
        if(firstChar < '0'){
            if(firstChar == '-'){
                negative=true;
                limit = Integer.MIN_VALUE;
            }else if(firstChar != '+'){
                throw new MyException();
            }
            if(len==1){
                throw new MyException();
            }
            i++;
        }
        mulMin=limit/radix;
        while (i<len){
            if(result<mulMin){
                throw new MyException();
            }
            //解析单个字符到给定进制
            digit = Character.digit(s.charAt(i++),radix);
            if(digit<0){
                throw new MyException();
            }
            result*=radix;
            if(result<limit+digit){
                throw new MyException();
            }
            result-=digit;
        }
        return negative?result:-result;
    }
}
