package com.bsren.hacker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class S4 {

    public static List<Integer> matchingStrings(List<String> stringList, List<String> queries) {
        // Write your code here
        HashMap<String,Integer> map = new HashMap<>();
        for (String s : stringList) {
            map.put(s,map.getOrDefault(s,0)+1);
        }
        List<Integer> l = new ArrayList<>();
        for (String query : queries) {
            l.add(map.get(query)==null?0:map.get(query));
        }
        return l;
    }

}
