package com.bsren.leetcode;

public class S8 {

    public static void main(String[] args) {
        S8 s8 = new S8();
        System.out.println(s8.myAtoi("-91283472332"));
    }

    public int myAtoi(String s) {
        if(s.length()==0){
            return 0;
        }
        int i = 0;
        char c = 0;
        boolean f = true;
        for (;i<s.length();i++){
             c = s.charAt(i);
            if(c!=' '){
                break;
            }
        }
        if(c=='+'){
            i++;
        }else if(c=='-'){
            i++;
            f = false;
        }else if(!Character.isDigit(c)){
            return 0;
        }
        while (i<s.length() && s.charAt(i)=='0'){
            i++;
        }
        if(i==s.length()){
            return 0;
        }
        int end = i;
        while (end<s.length() && Character.isDigit(s.charAt(end))){
            end++;
        }
        long a = 0;
        for (int j=i;j<end;j++){
            a = a*10-(s.charAt(j)-'0');
            if(a<=Integer.MIN_VALUE){
                break;
            }
        }
        if(a<=Integer.MIN_VALUE){
            if(f){
                return Integer.MAX_VALUE;
            }else {
                return Integer.MIN_VALUE;
            }
        }else {
            if(f){
                return (int) -a;
            }else {
                return (int) a;
            }
        }
    }
}
