package com.bsren.leetcode.design;

import net.bytebuddy.agent.ByteBuddyAgent;

public class NumArray {


    public static void main(String[] args) {
        NumArray numArray = new NumArray(new int[]{1,3,5});
        System.out.println(numArray.sumRange(0, 2));
    }
    int[] nums;
    int size;
    int[] sum;
    public NumArray(int[] nums) {
        this.nums = nums;
        int n = nums.length;
        size = (int) Math.sqrt(n);
        sum = new int[(n + size - 1) / size]; // n/size 向上取整
        for (int i = 0; i < n; i++) {
            sum[i / size] += nums[i];
        }
    }

    public void update(int index, int val) {
        sum[index/size] = sum[index/size]+val-this.nums[index];
        this.nums[index] = val;
    }

    public int sumRange(int left, int right) {
        int l = left / size, l1 = left%size;
        int r = right/size, r1 = right%size;
        int sum = 0;
        if(l==r){
            for (int i=left;i<=right;i++){
                sum+=nums[i];
            }
            return sum;
        }
        int sum_left = 0;
        int sum_right = 0;
        for (int i=0;i<l1;i++){
            sum_left+=nums[l*size+i];
        }
        for (int i=0;i<=r1;i++){
            sum_right+=nums[r*size+i];
        }

        for (int i=l;i<r;i++){
            sum+=this.sum[i];
        }
        sum = sum +sum_right-sum_left;
        return sum;
    }
}
