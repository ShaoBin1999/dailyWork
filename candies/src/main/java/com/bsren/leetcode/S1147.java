package com.bsren.leetcode;

public class S1147 {


    public static void main(String[] args) {
        String s = "vwsuvmbwknmnvwsuvmbwk";
        System.out.println(new S1147().longestDecomposition(s));
    }

    public int longestDecomposition(String text) {
        int len = text.length();
        if(len<=1){
            return len;
        }
        char c = text.charAt(0);
        int i = len-1;
        int l;
        while (true){
            for (;i>=1;i--){
                if(text.charAt(i)==c){
                    break;
                }
            }
            if(i==0){
                return 1;
            }
            l = 1;
            for (;l<text.length();l++){
                if(i+l>=len || text.charAt(l)!=text.charAt(i+l)){
                    break;
                }
            }
            if(i+l==len){
                return 2+longestDecomposition(text.substring(l,i));
            }
            i--;
        }
    }
}
