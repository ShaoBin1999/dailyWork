package com.bsren.leetcode;

import java.util.List;

public class S198 {


    public int rob(int[] nums) {
        int len = nums.length;
        int[][] dp = new int[len][2];
        dp[0][0] = 0;
        dp[0][1] = nums[0];
        for (int i = 1; i < nums.length; i++) {
            dp[i][0] = Math.max(dp[i-1][0],dp[i-1][1]);
            dp[i][1] = dp[i-1][0]+nums[i];
        }
        return Math.max(dp[len-1][0],dp[len-1][1]);
    }

    public static void main(String[] args) {
        int[] s = new int[]{805306368,805306368,805306368};
        System.out.println(new S198().minEatingSpeed(s,1000000000));
    }

    public int minEatingSpeed(int[] piles, int h) {
        int left = 1;
        int right = 1000000000;
        int mid;
        int ans = -1;
        while (left<right){
            mid = (left+right)>>>1;
            if(f(piles,mid)<=h){
                ans = mid;
                right = mid;
            }else {
                left= mid+1;
            }
        }
        return ans;
    }

    private int f(int[] piles, int mid) {
        int ans = 0;
        for (int pile : piles) {
            ans+=(mid+pile-1)/mid;
        }
        return ans;
    }


    public int smallestDivisor(int[] nums, int threshold) {
        int left = 1,right = 1000000;
        int mid;
        int ans = -1;
        while (left<=right){
            mid = (left+right)/2;
            if(fun(nums,mid)<threshold){
                ans=mid;
                right = mid-1;
            }else {
                left = mid+1;
            }
        }
        return ans;
    }

    private int fun(int[] nums, int mid) {
        int ans = 0;
        for (int num : nums) {
            ans += (num + mid - 1) / mid;
        }
        return ans;
    }

    public long minimumTime(int[] time, int totalTrips) {
        long ans = 0;
        long left = 1;
        long right = Long.MAX_VALUE-1;
        long mid;
        while (left<=right){
            mid = (left+right)>>>1;
            if(f1(time,mid,totalTrips)>=totalTrips){
                ans = mid;
                right = mid-1;
            }else {
                left = mid+1;
            }
        }
        return ans;
    }

    private long f1(int[] time, long mid,int total) {
        long ans = 0;
        for (int i : time) {
            ans+=mid/i;
            if(ans>=total){
                return total+1;
            }
        }
        return ans;
    }

    public int minimizeArrayValue(int[] nums) {
        int left = 0,right = 1000000000;
        int ans = 0;
        while (left<right){
            int mid = (left+right)>>>1;
            if(f2(nums,mid)){
                ans = mid;
                right = mid-1;
            }else {
                left = mid+1;
            }
        }
        return ans;
    }

    private boolean f2(int[] nums, int mid) {
        long have = 0;
        for (int n:nums){
            if(n<=mid){
                have+=mid-n;
            }else {
                if(have<n-mid){
                    return false;
                }else {
                    have-=(n-mid);
                }
            }
        }
        return true;
    }
}
