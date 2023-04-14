package com.bsren.leetcode;

public class S65 {

    public static void main(String[] args) {
        String[]  s = new String[]{"2", "0089", "-0.1", "+3.14", "4.", "-.9", "2e10", "-90E3", "3e+7", "+6e-1", "53.5e93", "-123.456e789"};
        for (String s1 : s) {
            System.out.println(new S65().isNumber(s1));
        }
        String[] p = new String[]{"abc", "1a", "1e", "e3", "99e2.5", "--6", "-+3", "95a54e53"};
        for (String s1 : p) {
            System.out.println(new S65().isNumber(s1));
        }
    }

    public boolean isNumber(String s) {
        int e = s.indexOf('e');
        if(e!=-1){
            return isIntegerOrDecimal(s.substring(0,e)) && isInteger(s.substring(e+1),false,false);
        }
        int E = s.indexOf('E');
        if(E!=-1){
            return isIntegerOrDecimal(s.substring(0,E)) && isInteger(s.substring(E+1),false,false);
        }
        return isIntegerOrDecimal(s);
    }

    public boolean isInteger(String s,boolean blankValid,boolean flag){
        if(s.length()==0){
            return blankValid;
        }
        int cur = 0;
        if(s.charAt(0)=='-' || s.charAt(0)=='+'){
            if(flag){
                return false;
            }
            cur++;
        }
        if(cur==s.length()){
            return false;
        }
        while (cur<s.length()){
            if(!Character.isDigit(s.charAt(cur))){
                return false;
            }
            cur++;
        }
        return true;
    }

    public boolean isDecimal(String s){
        int p = s.indexOf('.');
        if(p==-1){
            return false;
        }
        int cur = 0;
        if(s.charAt(0)=='-' || s.charAt(0)=='+'){
            cur++;
        }
        if(s.length()-cur==1){
            return false;
        }
        return isInteger(s.substring(cur,p),true,false)
                &&
                isInteger(s.substring(p+1),true,true);

    }

    public boolean isIntegerOrDecimal(String s){
        return isInteger(s,false,false) || isDecimal(s);
    }
}
