package com.bsren.leetcode;

public class S2367 {
    public static void main(String[] args) {
        S2367 s2367 = new S2367();
        int[] nums = new int[]{4,5,6,7,8,9};
        int diff = 2;
        System.out.println(s2367.arithmeticTriplets(nums,diff));
    }

    public int arithmeticTriplets(int[] nums, int diff) {
        int ans = 0;
        int n = nums.length;
        int left = 0,mid = 1,right = 2;
        while (left+2<n){
            mid = left+1;
            while (mid+1<n && nums[mid]<nums[left]+diff){
                mid++;
            }
            if(mid+1==n){
                break;
            }
            if(nums[mid]==nums[left]+diff){
                right = mid+1;
                while (right<n && nums[right]<nums[mid]+diff){
                    right++;
                }
                if(right==n){
                    break;
                }
                if(nums[right]==nums[mid]+diff){
                    ans++;
                }
            }
            left++;
        }
        return ans;
    }
}
