package com.bsren.leetcode.dynamic;

import com.bsren.niuke.TreeNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Solution {



    public int longestMountain(int[] arr) {
        if(arr.length<3){
            return 0;
        }
        int ans = 0;
        int left = 0;
        int before = arr[0];
        int cur = 1;
        boolean l = false,r = false;
        while (cur<arr.length){
            if(arr[cur]>before){
                while (cur<arr.length && arr[cur]>before){
                    before = arr[cur];
                    cur++;
                    l = true;
                }
                if(cur==arr.length){
                    return ans;
                }
                while (cur<arr.length && arr[cur]<before){
                    before = arr[cur];
                    cur++;
                    r = true;
                }
                if(l && r){
                    ans = Math.max(ans,cur-left);
                    cur--;
                }
                if(cur==arr.length){
                    return Math.max(ans,cur-left);
                }
            }
            left = cur;
            before = arr[cur];
            cur++;
            l = false;
            r = false;
        }
        return ans;
    }

    public int numSquares(int n) {
        int[] l = new int[n+1];
        Arrays.fill(l,Integer.MAX_VALUE);
        l[1] = 1;
        for (int i=2;i<=n;i++){
            if(i*i<=n){
                l[i*i] = 1;
            }
            for (int j = i-1;j>=1;j--){
                l[i] = Math.min(l[i],l[j]+l[i-j]);
            }
        }
        return l[n];
    }

    public int lengthOfLongestSubstring(String s) {
        if(s.length()==0){
            return 1;
        }
        HashMap<Character,Integer> map = new HashMap<>();
        int ans = 0;
        int left = 0;
        for (int i=0;i<s.length();i++){
            char c = s.charAt(i);
            if(map.containsKey(c)){
                Integer integer = map.get(c);
                left = Math.max(left,integer+1);
            }
            ans = Math.max(i-left+1,ans);
            map.put(c,i);
        }
        return ans;
    }

    public boolean isSymmetric(TreeNode root) {
        if(root==null){
            return true;
        }
        return isSymmetric(root.left,root.right);

    }

    private boolean isSymmetric(TreeNode left, TreeNode right) {
        if(left==null && right==null){
            return true;
        }else if(left!=null && right!=null){
            return left.val==right.val && isSymmetric(left.left,right.right)
                    && isSymmetric(left.right,right.left);
        }else {
            return false;
        }
    }

    public double myPow(double x, int n) {
        if(x == 0) return 0;
        long b = n;
        double res = 1.0;
        if(b < 0) {
            x = 1 / x;
            b = -b;
        }
        while(b > 0) {
            if((b & 1) == 1) res *= x;
            x *= x;
            b >>= 1;
        }
        return res;
    }

    public boolean canJump(int[] nums) {
        if(nums.length==1){
            return true;
        }
        int cur = 1;
        int maxJump = 1;
        while (cur<=maxJump){
            maxJump = Math.max(maxJump,cur+nums[cur-1]);
            if(maxJump>=nums.length){
                return true;
            }
            cur++;
        }
        return false;
    }

    public int jump(int[] nums) {
        int begin = 0,end = 0;
        int ans = 0;
        while (end<nums.length-1){
            int temp = 0;
            for (int i=begin;i<=end;i++){
                temp = Math.max(nums[i]+i,temp);
            }
            begin = end+1;
            end = temp;
            ans++;
        }
        return ans;
    }

    public static void main(String[] args) {
        System.out.println(new Solution().jump(new int[]{2,4,1,0,2}));
    }

}
