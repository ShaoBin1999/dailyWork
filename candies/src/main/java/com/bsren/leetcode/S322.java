package com.bsren.leetcode;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class S322 {

    public static void main(String[] args) {
        S322 s322 = new S322();
        System.out.println(s322.coinChange(new int[]{ 2}, 3));
    }

    public int coinChange(int[] coins, int amount) {
        if(amount==0){
            return -1;
        }
        int[] dp = new int[amount+1];
        Arrays.fill(dp,Integer.MAX_VALUE/2);
        Arrays.sort(coins);
        for (int coin : coins) {
            if(coin<dp.length){
                dp[coin] = 1;
            }
        }
        for (int i=1;i<=amount;i++){
            for (int j = coins.length-1;j>=0;j--){
                if(i-coins[j]>=1){
                    dp[i] = Math.min(dp[i],dp[i-coins[j]]+1);
                }
            }
        }
        return dp[amount]==Integer.MAX_VALUE/2?-1:dp[amount];
    }


}
