package com.bsren.leetcode;

import java.util.*;

public class S433 {

    public static void main(String[] args) {
        String[] s = new String[]{"AAAACCCA","AAACCCCA","AACCCCCA","AACCCCCC","ACCCCCCC","CCCCCCCC","AAACCCCC","AACCCCCC"};
        String s1 = "AAAACCCC";
        String s2 = "CCCCCCCC";
        System.out.println(new S433().minMutation(s1, s2, s));
    }

    HashSet<String> map = new HashSet<>();
    public int minMutation(String startGene, String endGene, String[] bank) {
        HashSet<String> set = new HashSet<>(Arrays.asList(bank));
        if(!set.contains(endGene)){
            return -1;
        }
        Deque<String> deque = new LinkedList<>();
        deque.addLast(startGene);
        int ans = 0;
        while (!deque.isEmpty()){
            int size = deque.size();
            for (int i=0;i<size;i++){
                String s = deque.removeFirst();
                map.add(s);
                List<String> strings = fun(s);
                for (String string : strings) {
                    if(set.contains(string)){
                        if(endGene.equals(string)){
                            return ans+1;
                        }
                        deque.addLast(string);
                    }
                }
            }
            ans++;
        }
        return -1;
    }

    Character[] characters = new Character[]{'A','C','G','T'};
    public List<String> fun(String  s){
        List<String> ans = new ArrayList<>();
        for (int i=0;i<s.length();i++){
            for (int j=0;j<4;j++){
                StringBuilder t = new StringBuilder(s);
                t.deleteCharAt(i);
                t.insert(i,characters[j]);
                if(!map.contains(t.toString())){
                    ans.add(t.toString());
                    map.add(t.toString());
                }
            }
        }
        return ans;
    }
//    public int minMutation(String startGene, String endGene, String[] bank) {
//        Set<String> set = new HashSet<>(Arrays.asList(bank));
//        bank = set.toArray(new String[0]);
//        boolean f = false;
//        int idx = -1;
//        int size = bank.length;
//        int end = -1;
//        for (int i=0;i<bank.length;i++) {
//            String s = bank[i];
//            if(s.equals(endGene)){
//                end = i;
//                f = true;
//            }else if(s.equals(startGene)){
//                idx = i;
//            }
//        }
//        if(!f){
//            return -1;
//        }
//        int[][] edges;
//        edges = new int[size][size];
//        for (int i=0;i<size;i++){
//            Arrays.fill(edges[i],Integer.MAX_VALUE/3);
//            edges[i][i] = 0;
//        }
//        for (int i=0;i<size;i++){
//            for (int j=i+1;j<size;j++){
//                if(fun(bank[i],bank[j])){
//                    edges[i][j] = 1;
//                    edges[j][i] = 1;
//                }
//            }
//        }
//        for (int i=0;i<size;i++){
//            for (int j=0;j<size;j++){
//                for (int h=0;h<size;h++){
//                    if(edges[i][j]>edges[i][h]+edges[h][j]){
//                        edges[i][j] = edges[i][h]+edges[h][j];
//                        edges[j][i] = edges[i][j];
//                    }
//                }
//            }
//        }
//        if(idx!=-1){
//            return edges[idx][end]==Integer.MAX_VALUE/3?-1:edges[idx][end];
//        }else {
//            int min = 10000;
//            for (int i=0;i<size;i++){
//                if(fun(startGene,bank[i]) && edges[i][end]!=Integer.MAX_VALUE/3){
//                    min = Math.min(min,1+edges[i][end]);
//                }
//            }
//            return min==10000?-1:min;
//        }
//    }
//
//    private boolean fun(String startGene, String s) {
//        if(s.length()!=startGene.length()){
//            return false;
//        }
//        boolean f = false;
//        for (int i = 0; i < s.length(); i++) {
//            if(s.charAt(i)!=startGene.charAt(i)){
//                if(!f){
//                    f = true;
//                }else {
//                    return false;
//                }
//            }
//        }
//        return f;
//    }
}
