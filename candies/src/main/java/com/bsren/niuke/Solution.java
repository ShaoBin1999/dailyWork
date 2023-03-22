package com.bsren.niuke;

import com.google.common.collect.Lists;

import java.util.*;

public class Solution {
//    public static void main(String[] args) {
//        Scanner sc = new Scanner(System.in);
//        int m = sc.nextInt();
//        int n = sc.nextInt();
//        int mod = 99997867;
//        int ans = 0;
//        List<Integer> list = new ArrayList<>(m);
//        for (int i=0;i<m;i++){
//            list.add(sc.nextInt());
//        }
//        for (int i=0;i<m-2;i++){
//            int max = list.get(i)+n;
//            int j = fun(list,max,i);
//            int between = j-i;
//            if(between>=2){
//                ans+=(between*(between-1))/2;
//                ans = ans%mod;
//            }
//        }
//        System.out.println(ans);
//    }
//
//
//    private static int fun(List<Integer> list, int max, int i) {
//        int left = i+1,right = list.size()-1;
//        int mid;
//        int ret = -1;
//        while (left<=right){
//            mid = (left+right+1)/2;
//            if(list.get(mid)<=max){
//                ret = mid;
//                left = mid+1;
//            }else {
//                right = mid-1;
//            }
//        }
//        return ret;
//    }

//    public static void main(String[] args) {
//        Scanner sc = new Scanner(System.in);
//        String s = sc.nextLine();
//        String[] strings = s.split(" ");
//        int[] map = new int[10];
//        for (String string : strings) {
//            int n = Integer.parseInt(string);
//            map[n]+=1;
//        }
//        int[] dump = Arrays.copyOf(map,10);
//        List<Integer> l = new ArrayList<>();
//        for (int i=1;i<=9;i++){
//            if(map[i]==4){
//                continue;
//            }
//            map = Arrays.copyOf(dump,10);
//            map[i]+=1;
//            for (int j=1;j<=9;j++){
//                if(map[j]>=2){
//                    map[j]-=2;
//                    if(fun(map)){
//                        l.add(i);
//                        break;
//                    }
//                    map[j]+=2;
//                }
//            }
//
//        }
//        if(l.isEmpty()){
//            System.out.println(0);
//        }else {
//            StringBuilder sb = new StringBuilder();
//            for (Integer integer : l) {
//                sb.append(integer);
//                sb.append(" ");
//            }
//            sb.deleteCharAt(sb.length()-1);
//            System.out.println(sb);
//        }
//    }

//    private static boolean fun(int[] map) {
//        boolean f = true;
//        for (int i : map) {
//            if(i!=0){
//                f = false;
//                break;
//            }
//        }
//        if(f){
//            return true;
//        }
//        for (int i=1;i<=9;i++){
//            if(i+2<=9){
//                if(map[i]>=1 && map[i+1]>=1 && map[i+2]>=1){
//                    int[] temp = Arrays.copyOf(map,10);
//                    temp[i]-=1;
//                    temp[i+1]-=1;
//                    temp[i+2]-=1;
//                    if(fun(temp)) return true;
//                }
//            }
//            if(i-2>=1){
//                if(map[i]>=1 && map[i-1]>=1 && map[i-2]>=1){
//                    int[] temp = Arrays.copyOf(map,10);
//                    temp[i]-=1;
//                    temp[i-1]-=1;
//                    temp[i-2]-=1;
//                    if(fun(temp)) return true;
//                }
//            }
//            if(i>=2 && i<=8){
//                if(map[i]>=1 && map[i+1]>=1 && map[i-1]>=1){
//                    int[] temp = Arrays.copyOf(map,10);
//                    temp[i]-=1;
//                    temp[i+1]-=1;
//                    temp[i-1]-=1;
//                    if(fun(temp)) return true;
//                }
//            }
//            if(map[i]>=3){
//                int[] temp = Arrays.copyOf(map,10);
//                temp[i]-=3;
//                if(fun(temp)) return true;
//            }
//        }
//        return false;
//    }

    static int min = Integer.MAX_VALUE;
    static boolean[] visited;
//    public static void main(String[] args) {
//        Scanner sc = new Scanner(System.in);
//        int num = sc.nextInt();
//        visited = new boolean[num];
//        int[][] r = new int[num][num];
//        for (int i=0;i<num;i++){
//            for (int j=0;j<num;j++){
//                r[i][j] = sc.nextInt();
//            }
//        }
//        visited[0] = true;
//        fun(r,0,0);
//        System.out.println(min);
//    }

    private static void fun(int[][] r, int i,int money) {
        boolean f = true;
        for (boolean b : visited) {
            if (!b) {
                f = false;
                break;
            }
        }
        if(f){
            min = Math.min(money+r[i][0],min);
            return;
        }
        int[] ints = r[i];
        for (int j=0;j<ints.length;j++){
            if(!visited[j] && j!=i){
                visited[j] = true;
                fun(r,j,money+r[i][j]);
                visited[j] = false;
            }
        }
    }

    public static void main(String[] args) {
        Solution s = new Solution();
        int[] l1 = new int[]{4,5,2,1};
        int[] l2 = new int[]{3,10,21};
        System.out.println(Arrays.toString(s.answerQueries(l1, l2)));
    }
    public int[] answerQueries(int[] nums, int[] queries) {

        TreeMap<Integer,Integer> map = new TreeMap<>();
        for (int num : nums) {
            map.put(num,map.getOrDefault(num,0)+1);
        }
        int[] ans = new int[queries.length];
        for (int i=0;i<queries.length;i++){
            int n = queries[i];
            int m = 0;
            for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
                int key = entry.getKey();
                int value = entry.getValue();
                if(key*value>=n){
                    m+=n/key;
                    break;
                }
                n-=key*value;
                m+=value;
            }
            ans[i] = m;
        }
        return ans;
    }
}
