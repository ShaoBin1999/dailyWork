package com.bsren.leetcode;

import java.util.HashMap;
import java.util.Map;

public class S2404 {


    public static void main(String[] args) {
        int[] s = new int[]{0,1,2,2,4,4,1};
        System.out.println(new S2404().mostFrequentEven(s));

    }
    public int mostFrequentEven(int[] nums) {
        HashMap<Integer,Integer> map = new HashMap<>();
        for (int num : nums) {
            if((num&1)==0){
                map.put(num,map.getOrDefault(num,0)+1);
            }
        }
        int ans = -1;
        int max = 0;
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            if(entry.getValue()>max){
                ans=entry.getKey();
                max = entry.getValue();
            }else if(entry.getValue()==max){
                if(entry.getKey()<ans){
                    ans = entry.getKey();
                }
            }
        }
        return ans;
    }
}
