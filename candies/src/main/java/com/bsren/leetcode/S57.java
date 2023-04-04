package com.bsren.leetcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class S57 {

    public static void main(String[] args) {
        int[][] ints = new int[1][];
        ints[0]  = new int[]{1,5};
//        ints[1]  = new int[]{3,5};
//        ints[2]  = new int[]{6,7};
//        ints[3]  = new int[]{8,10};
//        ints[4]  = new int[]{12,16};
        System.out.println(Arrays.deepToString(new S57().insert(ints, new int[]{6,8})));
    }

    public int[][] insert(int[][] intervals, int[] newInterval) {
        if(intervals.length==0){
            int[][] a = new int[1][];
            a[0] = newInterval;
            return a;
        }
        List<int[]> l = new ArrayList<>();
        int left = 0;
        int right = intervals.length-1;
        for (; left < intervals.length; left++) {
            if(intervals[left][1]>=newInterval[0]){
                break;
            }
        }
        for (int j=0;j<left;j++){
            l.add(intervals[j]);
        }
        for (;right>=left;right--){
            if(intervals[right][0]<=newInterval[1]){
                break;
            }
        }
        int min = Math.min(left<intervals.length?intervals[left][0]:Integer.MAX_VALUE,newInterval[0]);
        int max = Math.max(right>=0?intervals[right][1]:Integer.MIN_VALUE,newInterval[1]);
        l.add(new int[]{min,max});
        for (int j=right+1;j<intervals.length;j++){
            l.add(intervals[j]);
        }
        int[][] ans = new int[l.size()][2];
        for (int i1 = 0; i1 < l.size(); i1++) {
            ans[i1] = l.get(i1);
        }
        return ans;
    }


}
