package com.bsren.leetcode;

public class S831 {

    public static void main(String[] args) {
        S831 s = new S831();
        System.out.println(s.maskPII("86-(10)12345678"));
    }

    public String maskPII(String s) {
        char c = '@';
        int of = s.indexOf(c);
        StringBuilder sb = new StringBuilder();
        if(of==-1){
            for (int i = 0; i < s.toCharArray().length; i++) {
                if(Character.isDigit(s.charAt(i))){
                    sb.append(s.charAt(i));
                }
            }
            int n = sb.length();
            StringBuilder ans = new StringBuilder();
            if(n>10){
                ans.append("+");
                for (int i=0;i<n-10;i++){
                    ans.append("*");
                }
                ans.append("-");
            }
            for (int i=0;i<3;i++){
                ans.append("*");
            }
            ans.append("-");
            for (int i=0;i<3;i++){
                ans.append("*");
            }
            ans.append("-");
            ans.append(sb.substring(n-4,n));
            return ans.toString();
        }
        else {
            sb.append(Character.toLowerCase(s.charAt(0)));
            if(of!=1){
                sb.append("*****");
                sb.append(Character.toLowerCase(s.charAt(of-1)));
            }
            sb.append(c);
            for (int i=of+1;i<s.length();i++){
                sb.append(Character.toLowerCase(s.charAt(i)));
            }
            return sb.toString();
        }
    }
}
