package com.bsren.leetcode;

public class S287 {

    public static void main(String[] args) {
        int[] s = new int[]{1,1,2,3,4};
        System.out.println(new S287().findDuplicate(s));
    }

    public int findDuplicate(int[] nums) {
        int ans = nums[0];
        ans = ans ^ 1;
        for (int i = 1; i < nums.length; i++) {
            ans = ans ^ nums[i];
            ans = ans ^ (i+1);
        }
        return ans;
    }
}
