package com.bsren.leetcode.dynamic;

import java.util.HashMap;
import java.util.Map;

public class L1039 {

    Map<Integer,Integer> map = new HashMap<>();
    public int minScoreTriangulation(int[] values) {
        int n = values.length;
        return dp(0,n-1,values);
    }

    private int dp(int i, int j, int[] values) {
        if(i+2>j){
            return 0;
        }else if(i+2==j){
            return values[i]*values[i+1]*values[j];
        }
        int key = i*values.length+j;//用于hash
        if(!map.containsKey(key)){
            int min = Integer.MAX_VALUE;
            for (int h = i+1;h<j;h++){
                min = Math.min(min,dp(i,h,values)+dp(h,j,values)+values[i]*values[j]*values[h]);
            }
            map.put(key,min);
        }
        return map.get(key);
    }




}
