package com.bsren.leetcode.week;

import java.util.*;

public class Week4 {

    public int diagonalPrime(int[][] nums) {
        int ans = 0;
        for (int i=0;i<nums.length;i++){
            if(isPrime(nums[i][i])){
                ans = Math.max(ans,nums[i][i]);
            }
            if(isPrime(nums[i][nums.length-i-1])){
                ans = Math.max(ans,nums[i][nums.length-i-1]);
            }
        }
        return ans;
    }

    public static boolean isPrime(int n){
        if(n==1){
            return false;
        }else if(n==2){
            return true;
        }
        for (int i=2;i<=(int) Math.sqrt(n);i++){
            if(n%i==0){
                return false;
            }
        }
        return true;
    }

    public long[] distance(int[] nums) {
        HashMap<Integer, List<Integer>> map = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            if(!map.containsKey(nums[i])){
                map.put(nums[i],new ArrayList<>());
            }
            map.get(nums[i]).add(i);
        }
        Map<Integer,long[]> m = new HashMap<>();
        for (Map.Entry<Integer, List<Integer>> entry : map.entrySet()) {
            Integer key = entry.getKey();
            List<Integer> list = entry.getValue();
            if(list.size()<100){
                continue;
            }
            int size = list.size();
            int bucket = (int) Math.sqrt(size);
            int len = (bucket+size-1)/bucket;
            long[] sum = new long[len];
            for (int i=0;i<len;i++){
                sum[i/bucket]+=list.get(i);
            }
            m.put(key,sum);
        }
        long[] ans = new long[nums.length];
        for (int i=0;i<nums.length;i++){
            long add = 0;
            List<Integer> list = map.get(nums[i]);
            if(m.containsKey(nums[i])){
                long[] longs = m.get(nums[i]);
                int bucket = (int) Math.sqrt(list.size());
                int left = i/bucket-1;
                int right = i/bucket+1;
                int len = longs.length;
                if(left>=0){
                    add+=((long) i *(left+1)*bucket-longs[left]);
                }
                if(right<len){
                    add+=longs[right]-longs[i/bucket]- (long) i *bucket*(right-i);
                }
                for (int j=(1+left)*bucket;j<(left+2)*bucket && j<list.size();j++){
                    add+=Math.abs(i-list.get(j));
                }
            }else{
                if(list.size()>1){
                    for (int integer : list) {
                        add+=Math.abs(integer-i);
                    }
                }
            }
            ans[i] = add;
        }
        return ans;
    }


    public static void main(String[] args) {

    }

    public int minimizeMax(int[] nums, int p) {
        return 0;
    }


}
