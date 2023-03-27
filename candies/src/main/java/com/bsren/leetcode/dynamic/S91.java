package com.bsren.leetcode.dynamic;

public class S91 {

    public static void main(String[] args) {
        System.out.println(new S91().numDecodings("12"));
    }
    public int numDecodings(String s) {
        int n = s.length();
        int[] dp = new int[n+1];
        dp[0] = 1;
        for (int i=1;i<=n;i++){
            if(s.charAt(i-1)!='0'){
                dp[i] = dp[i-1];
            }
            if(i==1){
                continue;
            }
            int num = 10*(s.charAt(i-2)-'0')+s.charAt(i-1)-'0';
            if(num>=10 && num<=26){
                dp[i]+=dp[i-2];
            }
        }
        return dp[n];
    }
}
