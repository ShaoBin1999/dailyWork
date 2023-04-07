package com.bsren.hacker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class S1 {

    public static void main(String[] args) {
        List<Integer> ranked = new ArrayList<>();
        ranked.add(100);
        ranked.add(90);
        ranked.add(90);
        ranked.add(80);
        List<Integer> player = new ArrayList<>();
        player.add(70);
        player.add(80);
        player.add(105);
        System.out.println(climbingLeaderboard(ranked, player));
    }

    public static List<Integer> climbingLeaderboard(List<Integer> ranked, List<Integer> player) {
        // Write your code here
        List<Integer> ans = new ArrayList<>(player.size());
        List<Integer> r = new ArrayList<>();
        for (int i=0;i<ranked.size();i++){
            if(i>0 && ranked.get(i).equals(ranked.get(i-1))){
                continue;
            }
            r.add(ranked.get(i));
        }
        for (Integer integer : player) {
            ans.add(insert(r, integer));
        }
        return ans;
    }

    public static int insert(List<Integer> list,int val){
        int left = 0;
        int right = list.size()-1;
        int mid;
        while (left<=right){
            mid = (left+right)/2;
            if(list.get(mid)>val){
                left = mid+1;
            }else {
                right = mid-1;
            }
        }
        return left+1;
    }


}
