package com.bsren.leetcode;


import java.util.*;

public class S456 {

    public static void main(String[] args) {
        int[] nums = new int[]{-1,2,3,4,0};
        System.out.println(new S456().find132pattern(nums));
    }

    public boolean find132pattern(int[] nums) {
        List<Integer> list = new ArrayList<>();
        for (int num : nums) {
            list.add(num);
        }
        return fun(list, new LinkedList<>());
    }

    private boolean fun(List<Integer> list, LinkedList<Integer> deque) {
        for (int i=0;i<list.size();i++){
            if(deque.isEmpty()){
                deque.addLast(list.get(i));
            }else {
                if(list.get(i)>=deque.getLast()){
                    deque.addLast(list.get(i));
                }else {
                    if(deque.getFirst()< list.get(i)){
                        return true;
                    }else {
                        List<Integer> l = new ArrayList<>(list);
                        if(fun(l.subList(i+1,l.size()),new LinkedList<>(deque))){
                            return true;
                        }
                        if(fun(l.subList(i,l.size()),new LinkedList<>())){
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
