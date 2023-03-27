package com.bsren.leetcode;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.*;

public class Solution1630 {

    public static void main(String[] args) {
        Solution1630 s = new Solution1630();
        System.out.println(s.numDecodings("06"));
    }
    public List<Boolean> checkArithmeticSubarrays(int[] nums, int[] l, int[] r) {
        List<Boolean> ans = new ArrayList<>();
        for (int i=0;i<l.length;i++){
            int left = l[i],right = r[i];
            int min = Integer.MAX_VALUE;
            int max = Integer.MIN_VALUE;
            if(left+1>right){
                ans.add(false);
                continue;
            }
            boolean[] f = new boolean[right-left+1];
            for (int p = left;p<=right;p++){
                min = Math.min(min,nums[p]);
                max = Math.max(max,nums[p]);
            }
            if((max-min)%(right-left)!=0){
                ans.add(false);
                continue;
            }
            int de = (max-min)/(right-left);
            if(de==0){
                ans.add(true);
                continue;
            }
            for (int p=left;p<=right;p++){
                int c = (nums[p]-min)/de;
                if((nums[p]-min)%de==0){
                    f[c] = true;
                }
            }
            boolean flag = true;
            for (boolean b : f) {
                if(!b){
                    flag = false;
                    break;
                }
            }
            ans.add(flag);
        }
        return ans;
    }

    public int numDecodings(String s) {
        int[] memo = new int[s.length() + 1];
        Arrays.fill(memo, -1);
        return dfs(s, 0, memo);

    }

    private int dfs(String s, int i, int[] memo) {
        if (i == s.length())
            return 1;
        if (s.charAt(i) == '0')
            return 0;

        if (memo[i] != -1)
            return memo[i];

        int count = dfs(s, i + 1, memo);
        if (i < s.length() - 1 && (s.charAt(i) == '1' || s.charAt(i) == '2' && s.charAt(i + 1) < '7'))
            count += dfs(s, i + 2, memo);
        memo[i] = count;
        return count;
    }
}
