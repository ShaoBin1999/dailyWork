package com.bsren.niuke.part2;

import javax.management.remote.rmi.RMIConnectionImpl;
import java.util.ArrayList;
import java.util.List;

public class Solution {

    public static void main(String[] args) {
        String s1 = "1.0.1";
        String s2 = "1";
        System.out.println(new Solution().compare(s1,s2));
    }
    public int search (int[] nums, int target) {
        // write code here
        int left = 0,right = nums.length-1;
        int mid;
        while (left<=right){
            mid = left+(right-left+1)/2;
            if(nums[mid]==target){
                return mid;
            }else if(nums[mid]<target){
                left = mid+1;
            }else {
                right = mid-1;
            }
        }
        return -1;
    }

    public boolean Find(int target, int [][] array) {
        if(array.length==0){
            return false;
        }
        if(array[0].length==0){
            return false;
        }
        return find(target,array,0,array.length-1,0,array[0].length-1);
    }

    public boolean find(int target,int[][] array,int x1,int x2,int y1,int y2){
        if(x1==x2 || y1==y2){
            return false;
        }
        int xmid = (x1+x2)/2, ymid = (y1+y2)/2;
        int num = array[xmid][ymid];
        if(num==target){
            return true;
        }else if(num>target){
            if(find(target,array,x1,xmid,y1,y2)) return true;
            if(find(target,array,xmid,x2,y1,ymid)) return true;
        }else {
            if(find(target,array,xmid+1,x2,y1,y2)) return true;
            if(find(target,array,x1,xmid+1,ymid+1,y2)) return true;
        }
        return false;
    }



    public int findPeakElement (int[] nums) {
        // write code here
        int left = 0,right = nums.length-1;
        int mid;
        while (left<right){
            mid = (left+right)/2;
            if(nums[mid]>nums[mid+1]){
                right = mid;
            }else {
                left = mid+1;
            }
        }
        return right;
    }

    public int InversePairs(int [] array) {
        if(array.length<2){
            return 0;
        }
        List<Integer> list = new ArrayList<>();
        int n = array.length;
        list.add(array[n-1]);
        int add = 0;
        for (int i=n-2;i>=0;i--){
            int addNum = addNum(list, 0, list.size(), array[i]);
            add+=addNum;
        }
        return add%1000000007;
    }

    public int addNum(List<Integer> list,int begin,int end,int num){
        int mid;
        while (begin<end){
            mid = (end+begin)/2;
            if(list.get(mid)<num){
                begin = mid+1;
            }else {
                end = mid;
            }
        }
        list.add(begin,num);
        return begin;
    }

    public int minNumberInRotateArray(int [] array) {
        if(array.length==0){
            return 0;
        }
        int i = 0, j = array.length-1;
        while (i<j){
            int m = (i+j)/2;
            if(array[m]>array[j]){
                i = m+1;
            }else if(array[m]<array[j]){
                j = m;
            }else {
                j--;
            }
        }
        return array[i];
    }



    public int compare (String version1, String version2) {
        // write code here
        int v1 = version1.indexOf(".");
        int v2 = version2.indexOf(".");
        int s1 = 0,s2 = 0;
        if(v1==-1){
            s1 = Integer.parseInt(version1);
            version1 = "-1";
        }else s1 = Integer.parseInt(version1.substring(0,v1));
        if(v2==-1){
            s2 = Integer.parseInt(version2);
            version2 = "-1";
        }else s2 = Integer.parseInt(version2.substring(0,v2));
        if(s1==s2){
            if(version1.equals("-1") && version2.equals("-1")){
                return 0;
            }
            return compare(version1.substring(v1+1),version2.substring(v2+1));
        }else if(s1>s2){
            return 1;
        }else {
            return -1;
        }
    }
}
