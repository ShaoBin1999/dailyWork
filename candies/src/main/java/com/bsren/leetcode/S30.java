package com.bsren.leetcode;

import java.util.*;

public class S30 {

    public static void main(String[] args) {
        String s = "lingmindraboofooowingdingbarrwingmonkeypoundcake";
        String[] w = new String[]{"fooo","barr","wing","wing","ding"};
        System.out.println(new S30().findSubstring(s,w));
    }

    public List<Integer> findSubstring(String string, String[] words) {
        int len = words.length;
        int size = words[0].length();
        List<Integer> list = new ArrayList<>();
        for (int k=0;k<size;k++){
            String s = string.substring(k);
            HashMap<String,Integer> map = new HashMap<>();
            for (String word : words) {
                map.put(word,map.getOrDefault(word,0)+1);
            }
            Set<String> set = new HashSet<>(map.keySet());

            for (int i=0;i<(len-1)*size;i+=size){
                String substring = s.substring(i, i + size);
                if(set.contains(substring)){
                    map.put(substring,map.getOrDefault(substring,0)-1);
                    if(map.get(substring)==0){
                        map.remove(substring);
                    }
                }
            }

            for (int i=len*size-size;i<s.length();i+=size){
                if(i+size>s.length()){
                    break;
                }
                String substring = s.substring(i,i+size);
                if(set.contains(substring)){
                    map.put(substring,map.getOrDefault(substring,0)-1);
                    if(map.get(substring)==0){
                        map.remove(substring);
                    }
                    if(map.isEmpty()){
                        list.add(k+i-(len-1)*size);
                    }
                }
                String delete = s.substring(i-(len-1)*size,i-(len-1)*size+size);
                if(set.contains(delete)){
                    map.put(delete,map.getOrDefault(delete,0)+1);
                    if(map.get(delete)==0){
                        map.remove(delete);
                    }
                }

            }
        }

        return list;
    }

}
