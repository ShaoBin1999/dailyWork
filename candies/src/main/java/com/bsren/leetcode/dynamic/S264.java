package com.bsren.leetcode.dynamic;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

public class S264 {

    public int nthUglyNumber(int n) {
        int[] dp = new int[n + 1];
        dp[1] = 1;
        int p2 = 1, p3 = 1, p5 = 1;
        for (int i = 2; i <= n; i++) {
            int num2 = dp[p2] * 2, num3 = dp[p3] * 3, num5 = dp[p5] * 5;
            dp[i] = Math.min(Math.min(num2, num3), num5);
            if (dp[i] == num2) {
                p2++;
            }
            if (dp[i] == num3) {
                p3++;
            }
            if (dp[i] == num5) {
                p5++;
            }
        }
        return dp[n];
    }

    public static void main(String[] args) {
        System.out.println(new S264().nthUglyNumber1(10));
    }
    public int nthUglyNumber1(int n) {
        int[] f = new int[]{2,3,5};
        PriorityQueue<Long> p = new PriorityQueue<>();
        Set<Long> set = new HashSet<>();
        p.add(1L);
        set.add(1L);
        long poll = 1;
        for (int i=1;i<n;i++){
            poll = p.poll();
            for (int i1 : f) {
                long value = i1*poll;
                if(set.add(value)){
                    p.add(value);
                }
            }
        }
        return Math.toIntExact(p.poll());
    }
}
