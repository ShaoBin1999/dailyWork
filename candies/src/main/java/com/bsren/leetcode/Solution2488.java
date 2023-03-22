package com.bsren.leetcode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Solution2488 {

    public static void main(String[] args) {
        int[] nums = new int[]{3,2,1,4,5,6};
        System.out.println(new Solution2488().countSubarrays(nums,4));
    }


    public int countSubarrays(int[] nums, int k) {
        int ind = 0;
        for (;ind<nums.length;ind++){
            if(nums[ind]==k){
                break;
            }
        }
        int ans = 0;
        Map<Integer,int[]> map = new HashMap<>();
        map.put(ind,new int[]{0,0});
        for (int i=ind-1;i>=0;i--){
            int[] ints = map.get(i + 1);
            int preLess = ints[0],preMore = ints[1];
            if(nums[i]<k){
                preLess++;
            }else {
                preMore++;
            }
            map.put(i,new int[]{preLess,preMore});
        }
        for (int i=ind+1;i<nums.length;i++){
            int[] ints = map.get(i - 1);
            int preLess = ints[0],preMore = ints[1];
            if(nums[i]<k){
                preLess++;
            }else {
                preMore++;
            }
            map.put(i,new int[]{preLess,preMore});
        }
        for (int left=ind;left>=0;left--){
            int[] ints = map.get(left);
            for (int right = ind;right<nums.length;right++){
                int[] ints1 = map.get(right);
                if(ints[0]+ints1[0]==ints[1]+ints1[1]){
                    ans++;
                }else if(ints[0]+ints1[0]+1==ints[1]+ints1[1]){
                    ans++;
                }
            }
        }
        return ans;
    }
}
