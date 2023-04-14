package com.bsren.leetcode.r1;

public class S1611 {

    public int[] divingBoard(int shorter, int longer, int k) {
        int[] ans = new int[k+1];
        for (int i=0;i<=k;i++){
            ans[i] = shorter*(k-i)+longer*i;
        }
        return ans;
    }


}
