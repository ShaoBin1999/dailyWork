package com.bsren.leetcode.week;

import java.util.*;

public class Baidu {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int num = Integer.parseInt(sc.nextLine());
        String s = sc.nextLine();
        List<List<Integer>> l = new ArrayList<>();
        for (int i=0;i<num;i++){
            l.add(new ArrayList<>());
        }
        for (int i=1;i<=num-1;i++){
            int i1 = sc.nextInt();
            int i2 = sc.nextInt();
            l.get(i1-1).add(i2-1);
            l.get(i2-1).add(i1-1);
        }
        int ans = 0;
        int c1 = 0,c2 = 0;
        for (int i=0;i<num-1;i++){
            List<Integer> list = l.get(c1);
            Integer integer = list.get(c2);
            List<List<Integer>> ne = new ArrayList<>(l);
            ne.get(c1).remove(integer);
            List<Integer> list1 = ne.get(c1);
            List<Integer> list2 = ne.get(integer);
            int v1 = fun(list1,ne,s,s.charAt(c1));
            int v2 = fun(list2,ne,s,s.charAt(c2));
            ans+=Math.abs(v1-v2);
            c2++;
            if(c2==list.size()){
                c2=0;
                c1++;
            }
        }
        System.out.println(ans);
    }

    private static int fun(List<Integer> list1, List<List<Integer>> ne,String s,char c) {
        if(list1.size()==0){
            return 0;
        }
        int ans = 0;
        for (Integer integer : list1) {
            if(s.charAt(integer)!=c){
                char t;
                if(c=='R'){
                    t = 'B';
                }else {
                    t = 'R';
                }
                ans = 1+fun(ne.get(integer),ne,s,t);
            }else {
                ans = 1+fun(ne.get(integer),ne,s,c);
            }
        }
        return ans;
    }


    public static int hourglassSum(List<List<Integer>> arr) {
        // Write your code here
        int ans = Integer.MIN_VALUE;
        for (int i=1;i<=4;i++){
            for (int j=1;j<=4;j++){
                int base = 0;
                base+=arr.get(i-1).get(j-1);
                base+=arr.get(i-1).get(j);
                base+=arr.get(i-1).get(j+1);
                base+=arr.get(i+1).get(j-1);
                base+=arr.get(i+1).get(j);
                base+=arr.get(i+1).get(j+1);
                base+=arr.get(i).get(j);
                ans = Math.max(ans,base);
            }
        }
        return ans;
    }
}
