package com.bsren.leetcode;

/**
 * 摩尔投票法
 */
public class MainElement {


    public static void main(String[] args) {
        int[] nums = new int[]{1,2,2,3,2,1};
        MainElement mainElement = new MainElement();
        System.out.println(mainElement.majorityElement(nums));
    }
    public int majorityElement(int[] nums) {
        int candidate = -1;
        int count = 0;
        for (int num : nums) {
            if (count == 0) {
                candidate = num;
            }
            if (num == candidate) {
                count++;
            } else {
                count--;
            }
        }
        count = 0;
        int length = nums.length;
        for (int num : nums) {
            if (num == candidate) {
                count++;
            }
        }
        return count * 2 > length ? candidate : -1;
    }

}
