package com.bsren.leetcode;


import java.util.*;

public class S128 {

    public static void main(String[] args) {
        int[] s = new int[]{0,3,7,2,5,8,4,6,0,1};
        System.out.println(new S128().longestConsecutive(s));
    }

//    public int longestConsecutive(int[] nums) {
//        HashMap<Integer, List<Integer>> map = new HashMap<>();
//        for (int num : nums) {
//            if(map.containsKey(num)){
//                continue;
//            }
//            if(map.containsKey(num-1) && map.containsKey(num+1)){
//                List<Integer> list1 = map.get(num - 1);
//                List<Integer> list2 = map.get(num + 1);
//                list1.addAll(list2);
//                list1.add(num);
//                int n = num+1;
//                while (map.containsKey(n)){
//                    map.put(n,list1);
//                    n++;
//                }
//                map.put(num,list1);
//            }else if(map.containsKey(num-1)){
//                List<Integer> list = map.get(num - 1);
//                list.add(num);
//                map.put(num,list);
//            }else if(map.containsKey(num+1)){
//                List<Integer> list = map.get(num + 1);
//                list.add(num);
//                map.put(num,list);
//            }else {
//                List<Integer> list = new ArrayList<>();
//                list.add(num);
//                map.put(num,list);
//            }
//        }
//        int ans = 0;
//        for (Map.Entry<Integer, List<Integer>> entry : map.entrySet()) {
//            ans = Math.max(ans,entry.getValue().size());
//        }
//        return ans;
//    }

    public int longestConsecutive(int[] nums) {
        Set<Integer> set = new HashSet<>();
        for (int num : nums) {
            set.add(num);
        }
        int ans = 0;
        for (Integer integer : set) {
            if(set.contains(integer-1)){
                continue;
            }
            int cur = 1;
            int num = integer+1;
            while (set.contains(num)){
                num++;
                cur++;
            }
            ans = Math.max(ans,cur);
        }
        return ans;
    }

}
