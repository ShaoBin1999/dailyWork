package com.bsren.leetcode;

public class S41 {

    public int firstMissingPositive(int[] nums) {
        int len = nums.length;
        for (int i = 0; i < len; i++) {
            while (nums[i]!=i+1 && nums[i]<=len && nums[i]>=1){
                int nextIdx = nums[i]-1;
                if(nums[nextIdx]==nums[i]){
                    break;
                }
                int temp = nums[i];
                nums[i] = nums[nextIdx];
                nums[nextIdx] = temp;
            }
        }
        for (int i=0;i<len;i++){
            if(nums[i]!=i+1){
                return i+1;
            }
        }
        return len+1;
    }
}
