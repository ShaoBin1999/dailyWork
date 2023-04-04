package com.bsren.leetcode.week;

import org.checkerframework.checker.units.qual.A;

import java.util.*;

public class Week3 {

    public static void main(String[] args) {
        int[] nums = new int[]{1,2};
        int[] n = new int[]{2,1};
        System.out.println(new Week3().miceAndCheese(nums,n,1));
    }

    public int findTheLongestBalancedSubstring(String s) {
        int left = 0,cur = 0;
        int len = s.length();
        int ans = 0;
        while (cur<len){
            while (cur<len && s.charAt(cur)=='0'){
                cur++;
            }
            if(cur==len){
                break;
            }
            int mid = cur;
            while (cur<len && s.charAt(cur)=='1'){
                cur++;
            }
            ans = Math.max(ans,2*Math.min((mid-left),(cur-mid)));
            left = cur;
        }
        return ans;
    }

    public List<List<Integer>> findMatrix(int[] nums) {
        HashMap<Integer,Integer> map = new HashMap<>();
        int max = 0;
        for (int num : nums) {
            int n = map.getOrDefault(num, 0) + 1;
            map.put(num,n);
            max = Math.max(max,n);
        }
        List<List<Integer>> l = new ArrayList<>();
        for (int i = 0; i < max; i++) {
            l.add(new ArrayList<>());
        }
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                l.get(i).add(entry.getKey());
            }
        }
        return l;
    }


    class pair{
        int a;
        int b;
        public pair(int a, int b) {
            this.a = a;
            this.b = b;
        }
    }
    public int miceAndCheese(int[] reward1, int[] reward2, int k) {
        if(k==0){
            return Arrays.stream(reward2).sum();
        }else if(k==reward1.length){
            return Arrays.stream(reward1).sum();
        }
        int n = reward1.length;
        PriorityQueue<pair> p = new PriorityQueue<>(new Comparator<pair>() {
            @Override
            public int compare(pair o1, pair o2) {
                return (o2.a-o2.b)-(o1.a-o1.b);
            }
        });
        for (int i = 0; i < n; i++) {
            p.add(new pair(reward1[i],reward2[i]));
        }
        int sum = 0;
        for (int i=0;i<k;i++){
            sum+=p.poll().a;
        }
        for (int i=0;i<n-k;i++){
            sum+=p.poll().b;
        }
        return sum;
    }

    int n;
    int[] values;
    Map<Integer, Integer> memo = new HashMap<Integer, Integer>();

    public int minScoreTriangulation(int[] values) {
        this.n = values.length;
        this.values = values;
        return dp(0, n - 1);
    }

    public int dp(int i, int j) {
        if (i + 2 > j) {
            return 0;
        }
        if (i + 2 == j) {
            return values[i] * values[i + 1] * values[j];
        }
        int key = i * n + j;
        if (!memo.containsKey(key)) {
            int minScore = Integer.MAX_VALUE;
            for (int k = i + 1; k < j; k++) {
                minScore = Math.min(minScore, values[i] * values[k] * values[j] + dp(i, k) + dp(k, j));
            }
            memo.put(key, minScore);
        }
        return memo.get(key);
    }
}
