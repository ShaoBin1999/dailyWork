package com.bsren.javaStd.sort;

public class part2 {

    public static void main(String[] args) {
        int[] array = new int[]{-14,-11,-12,-13,0,0,0,0,0,-3,10,0,1,3,-1,50,530,-2,5,3,56,7,7,8,0};
        new part2().QuickSortTopK(array,10);
    }

    public void QuickSortTopK(int[] array,int k){
        QuickSort(array,0,array.length-1,k);
        for (int i : array) {
            System.out.println(i);
        }
    }

    public void QuickSort(int[] array,int low,int high,int k){
        if(low<high){
            int mid = Partition(array,low,high);
            QuickSort(array,low,mid-1);
            if(k>=mid+1){
                QuickSort(array,mid+1,high);
            }else {
                System.out.println("-----------");
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
