package com.bsren.leetcode.sort;

import java.util.Arrays;

public class S215 {

    public static void main(String[] args) throws InterruptedException {
        while (true){
            Thread.sleep(1000);
            System.out.println(1);
        }
    }

    public int findKthLargest(int[] nums, int k) {
        QuickSort(nums,0,nums.length-1,k);
        System.out.println(Arrays.toString(nums));
        return nums[nums.length-k];
    }

    public void QuickSort(int[] array,int low,int high,int k){
        if(low<high){
            int mid = Partition(array,low,high);
            QuickSort(array,mid+1,high);
            if(mid>array.length-k){
                QuickSort(array,low,mid-1);
            }
        }
    }
    public void QuickSort(int[] array,int low,int high){
        if(low<high){
            int mid = Partition(array,low,high);
            QuickSort(array,low,mid-1);
            QuickSort(array,mid+1,high);
        }
    }

    public int Partition(int[] nums,int begin,int end){
        int mid = nums[begin];
        while (begin<end){
            while (begin<end && nums[end]>=mid){
                end--;
            }
            nums[begin] = nums[end];
            while (begin<end && nums[begin]<=mid){
                begin++;
            }
            nums[end] = nums[begin];
        }
        nums[begin] = mid;
        return begin;
    }

}
