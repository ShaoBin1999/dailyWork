package com.bsren.leetcode;

public class S137 {

    public static void main(String[] args) {
        int[] nums = new int[]{0,1,0,1,0,1,99};
        System.out.println(new S137().singleNumber(nums));
    }

    public int singleNumber(int[] nums) {
        int[] val = new int[32];
        for (int num : nums) {
            int i = 0;
            while (num!=0){
                val[31-i]+=num&1;
                num = num>>>1;
                i++;
            }
        }
        int ans = 0;
        for (int v : val) {
            v = v%3;
            ans = 2*ans+v;
        }
        return ans;
    }


}
