package com.bsren.hacker;

import java.util.List;

public class S2 {

    public static int hurdleRace(int k, List<Integer> height) {
        // Write your code here
        int max = 0;
        for (Integer integer : height) {
            max = Math.max(max, integer);
        }
        if(k>=max){
            return 0;
        }else {
            return max-k;
        }
    }


}
