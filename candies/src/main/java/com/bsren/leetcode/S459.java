package com.bsren.leetcode;

public class S459 {

    public boolean repeatedSubstringPattern(String s) {
        int[] l = new int[26];
        int min = s.length();
        for (char c : s.toCharArray()) {
            l[c-'a']++;
        }
        for (int j : l) {
            if (j != 0) {
                min = Math.min(j, min);
            }
        }
        if(min<2){
            return false;
        }
        int a = fun(l,min);
        if(a==1){
            return false;
        }
        int pattern = s.length()/a;
        int[] p = new int[26];
        String k;
        for (int i=0;i<pattern;i++){
            p[s.charAt(i)-'a']++;
        }
        for (int i = 0; i < p.length; i++) {
            if(p[i]*a!=l[i]){
                return false;
            }
        }
        k = s.substring(0,pattern);
        for (int i=1;i<a;i++){
            if(!k.equals(s.substring(i*pattern,i*pattern+pattern))){
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        System.out.println(new S459().repeatedSubstringPattern("baaabbbababaaabbbababaaabbbababaaabbbababaaabbbababaaabbbababaaabbbababaaabbbababaaabbbababaaabbbaba"));
    }

    private int fun(int[] l,int min) {
        for (int i=2;i<=min;i++){
            boolean f = true;
            for (int k : l) {
                if (k != 0){
                    if(k % i != 0) {
                        f = false;
                        break;
                    }
                }
            }
            if(f){
                return i;
            }
        }
        return 1;
    }


}
