package com.bsren.leetcode;

import java.util.ArrayList;
import java.util.List;

public class S77 {

    public static void main(String[] args) {
        System.out.println(new S77().combine(5, 3));
    }

    List<List<Integer>> ans = new ArrayList<>();
    public List<List<Integer>> combine(int n, int k) {
        List<Integer> l = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            l.add(i+1);
        }
        dfs(0,l,k,0, new ArrayList<>());
        return ans;
    }

    private void dfs(int ind, List<Integer> l, int k, int cur,List<Integer> list) {
        if(cur==k){
            ans.add(new ArrayList<>(list));
            return;
        }
        if(l.size()-ind+cur<k){
            return;
        }
        dfs(ind+1,l,k,cur,list);
        list.add(l.get(ind));
        dfs(ind+1,l,k,cur+1,list);
        list.remove(list.size()-1);
    }


}
