package com.bsren.leetcode;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;

public class S496 {

    public static void main(String[] args) {
        int[] n1 = new int[]{2,4};
        int[] n2 = new int[]{1,2,3,4};
        System.out.println(Arrays.toString(new S496().nextGreaterElement(n1, n2)));
    }

    public int[] nextGreaterElement(int[] nums1, int[] nums2) {
        HashMap<Integer,Integer> map = new HashMap<>();
        Stack<Integer> stack = new Stack<>();
        int len = nums2.length;
        for (int i=len-1;i>=0;i--){
            while (!stack.isEmpty() && nums2[i]>stack.peek()){
                stack.pop();
            }
            if(!stack.isEmpty()){
                map.put(nums2[i],stack.peek());
            }else {
                map.put(nums2[i],-1);
            }
            stack.push(nums2[i]);
        }
        int[] ans = new int[nums1.length];
        for (int i=0;i<nums1.length;i++){
            ans[i] = map.get(nums1[i]);
        }
        return ans;
    }


}
