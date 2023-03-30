package com.bsren.leetcode;

import java.util.ArrayList;
import java.util.List;

public class S1641 {

    public static void main(String[] args) {
        int n = 2;
        System.out.println(new S1641().countVowelStrings(n));
    }

    char[] s = new char[]{'1','2','3','4','5'};
    List<String> list = new ArrayList<>();
    List<String> pre = new ArrayList<>();
    public int countVowelStrings(int n) {
        dfs(0,n);
        return pre.size();
    }

    private void dfs(int i, int n) {
        if(i==n){
            return;
        }
        if(i==0){
            pre.add("1");
            pre.add("2");
            pre.add("3");
            pre.add("4");
            pre.add("5");
            dfs(1,n);
        }else {
            for (String s1 : pre) {
                for (int j=4;j>=0;j--){
                    if(s[j]>=s1.charAt(s1.length()-1)){
                        list.add(s1+s[j]);
                    }else {
                        break;
                    }
                }
            }
            pre = list;
            list = new ArrayList<>();
            dfs(i+1,n);
        }
    }
}
