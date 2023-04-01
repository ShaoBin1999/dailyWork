package com.bsren.leetcode;

import java.util.Arrays;

public class S1637 {

    public int maxWidthOfVerticalArea(int[][] points) {
        int length = points.length;
        int[] l = new int[length];
        for (int i = 0; i < length; i++) {
            l[i] = points[i][0];
        }
        Arrays.sort(l);
        int ans = 0;
        for (int i=1;i<length;i++){
            ans = Math.max(ans,l[i]-l[i-1]);
        }
        return ans;
    }


}
