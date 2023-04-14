package com.bsren.leetcode.r1;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class S1711 {

    public static void main(String[] args) {
        String[] s = new String[]{"I","am","a","student","from","a","university","in","a","city"};
        System.out.println(new S1711().findClosest(s,"a","student"));
    }

    public int findClosest(String[] words, String word1, String word2) {
        HashMap<String, List<Integer>> map = new HashMap<>();
        for (int i=0;i<words.length;i++) {
            String word = words[i];
            List<Integer> list = map.get(word);
            if(list==null){
                list = new ArrayList<>();
                list.add(i);
                map.put(word,list);
            }
            else {
                list.add(i);
            }
        }
        List<Integer> list = map.get(word1);
        List<Integer> list1 = map.get(word2);
        int ind1 = 0,ind2 = 0;
        int ans = Integer.MAX_VALUE;
        while (ind1<list.size() && ind2<list1.size()){
            while (ind1<list.size() && list.get(ind1)<list1.get(ind2)){
                ans = Math.min(ans,list1.get(ind2)-list.get(ind1));
                ind1++;
            }
            if(ind1==list.size()){
                break;
            }
            while (ind2<list1.size() && list.get(ind1)>list1.get(ind2)){
                ans = Math.min(ans,list.get(ind1)-list1.get(ind2));
                ind2++;
            }
        }
        return ans;
    }


}
