package com.bsren.leetcode;

public class S4 {

//    public static void main(String[] args) {
//        int[] nums = new int[]{1,2};
//        int[] b = new int[]{3,4};
//        S4 s4 = new S4();
//        System.out.println(s4.findMedianSortedArrays(nums, b));
//    }
//
//    public double findMedianSortedArrays(int[] nums1, int[] nums2) {
//        int len1 = nums1.length;
//        int len2 = nums2.length;
//        if(((len1+len2) & 1)==1){
//            return find(nums1,0,len1,nums2,0,len2,(len1+len2+1)/2);
//        }else {
//            return find(nums1,0,len1,nums2,0,len2,(len1+len2)/2)/2+
//                    find(nums1,0,len1,nums2,0,len2,(len1+len2)/2+1)/2;
//        }
//    }
//
//    private double find(int[] nums1, int left1, int right1, int[] nums2, int left2, int right2, int k) {
//        int len1 = right1-left1;
//        int len2 = right2-left2;
//        if(len1>len2){
//            return find(nums2,left2,right2,nums1,left1,right1,k);
//        }
//        if(len1==0){
//            return nums2[left2+k-1];
//        }
//        if (k == 1) return Math.min(nums1[left1], nums2[left2]);
//        int i = left1+Math.min(len1,k/2)-1;
//        int j = left2+Math.min(len2,k/2)-1;
//        if(nums1[i]>nums2[j]){
//            return find(nums1,left1,right1,nums2,j+1,right2,k-(j-left2+1));
//        }else {
//            return find(nums1,i+1,right1,nums2,left2,right2,k-(i-left1+1));
//        }
//    }
    public double findMedianSortedArrays(int[] nums1, int[] nums2) {
        int len1 = nums1.length;
        int len2 = nums2.length;
        if(((len1+len2)&1)==1){
            return find(nums1,0,len1,nums2,0,len2,(len1+len2+1)>>>1);
        }else {
            return find(nums1,0,len1,nums2,0,len2,(len1+len2+1)>>>1)/2.0+
                    find(nums1,0,len1,nums2,0,len2,(len1+len2+2)>>>1)/2.0;
        }
    }

    private double find(int[] nums1, int left1, int right1, int[] nums2, int left2, int right2, int k) {
        int len1 = right1-left1;
        int len2 = right2-left2;
        if(len1>len2){
            return find(nums2,left2,right2,nums1,left1,right1,k);
        }
        if(len1==0){
            return nums2[left2+k-1];
        }
        if(k==1){
            return Math.min(nums1[left1],nums2[left2]);
        }
        int i = left1+Math.min(len1,k/2)-1;
        int j = left2+Math.min(len2,k/2)-1;
        if(nums1[i]>nums2[j]){
            return find(nums1,left1,right1,nums2,j+1,right2,k-(j-left2+1));
        }else {
            return find(nums1,i+1,right1,nums2,left2,right2,k-(i-left1+1));
        }
    }



}
