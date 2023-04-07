package com.bsren.leetcode;

import java.util.HashMap;
import java.util.Map;

public class S96 {

    public static void main(String[] args) {
        System.out.println(new S96().numTrees(3));
    }

//    public int numTrees(int n) {
//        int[] dp = new int[n+1];
//        dp[0] = 1;
//        dp[1] = 1;
//        for (int i=2;i<=n;i++){
//            for (int j=1;j<=i;j++){
//                dp[i]+=dp[j-1]*dp[i-j];
//            }
//        }
//        return dp[n];
//    }

    public int numTrees(int n) {
        return fun(1,n);
    }

    Map<Integer,Integer> map = new HashMap<>();
    private int fun(int left, int right) {
        if(left>=right){
            return 1;
        }
        int cnt = 0;
        if(map.containsKey(right-left+1)){
            return map.get(right-left+1);
        }
        for (int i=left;i<=right;i++){
            cnt+=fun(left,i-1)*fun(i+1,right);
        }
        map.put(right-left+1,cnt);
        return cnt;
    }

//    public int numTrees(int n) {
//        return fun(1,n);
//    }
//
//    private int fun(int left, int right) {
//        if(left>=right){
//            return 1;
//        }
//        int cnt = 0;
//        for (int i=left;i<=right;i++){
//            cnt+=fun(left,i-1)*fun(i+1,right);
//        }
//        return cnt;
//    }

}
