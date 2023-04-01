package com.bsren.leetcode.week;

import java.util.*;

public class Pinduoduo {
//    public static void main(String[] args) {
//        Scanner sc = new Scanner(System.in);
//        String[] s = sc.nextLine().split(" ");
//        int n = Integer.parseInt(s[0]);
//        int m = Integer.parseInt(s[1]);
//        int[][] l = new int[n][2];
//        for (int i = 0; i < n; i++) {
//            String[] s1 = sc.nextLine().split(" ");
//            if(s1[0].equals("b")){
//                l[i] = new int[]{Integer.parseInt(s1[1]),-1};
//            }else {
//                l[i] = new int[]{Integer.parseInt(s1[1]),Integer.parseInt(s1[2])};
//            }
//        }
//        int ans = 0;
//        int cur = 0;
//        HashMap<Integer,Integer> map = new HashMap<>();
//        PriorityQueue<int[]> p = new PriorityQueue<>(new Comparator<int[]>() {
//            @Override
//            public int compare(int[] o1, int[] o2) {
//                return o2[1]-o1[1];
//            }
//        });
//        while (cur<n){
//            while (cur<n && l[cur][1]<0){
//                map.put(l[cur][0],map.getOrDefault(l[cur][0],0)+1);
//                cur++;
//            }
//            while (cur<n && l[cur][1]>0){
//                p.add(l[cur]);
//                cur++;
//            }
//            while (!p.isEmpty()){
//                int[] poll = p.poll();
//                if(map.containsKey(poll[0])){
//                    ans =
//                    if(map.get(poll[0])==1){
//                        map.remove(poll[0]);
//                    }else {
//                        map.put(poll[0],map.get(poll[0])-1);
//                    }
//                    break;
//                }
//            }
//            p.clear();
//        }
//        System.out.println(ans);
//    }

//    public static void main(String[] args) {
//        Scanner sc = new Scanner(System.in);
//        int n = Integer.parseInt(sc.nextLine());
//        int[] l = new int[n];
//        for (int i = 0; i < n; i++) {
//            l[i] = sc.nextInt();
//        }
//        int max = 0;
//        int min = Integer.MIN_VALUE;
//        int left = 0,right = 0;
//        int cur = 1;
//        int minIdx = 0;
//        boolean f = true;
//        int al = 0,ar = 0;
//        int maxV = -1;
//        while (right<n){
//            if(l[right]==0){
//                int a = 0,b = 0;
//                if(!f){
//                    for (int i=left;i<right;i++){
//                        if(l[i]<0){
//                            a = l[i];
//                            break;
//                        }
//                    }
//                    for (int i=right-1;i>=left;i--){
//                        if(l[i]<0){
//                            b = l[i];
//                            break;
//                        }
//                    }
//                    max = max+Math.max(a,b);
//                }
//                if(max>maxV){
//                    maxV = max;
//                    al = left;
//                    ar = right-1;
//                }
//                left = right+1;
//                right = right+1;
//                max = 0;
//            }else {
//                if(l[right]>0){
//                    max+=l[right];
//                }else {
//                    max-=l[right];
//                    f = !f;
//                    if(l[right]>min){
//                        minIdx = right;
//                        min = l[right];
//                    }
//                }
//            }
//        }
//        if(maxV==-1){
//            System.out.println();
//        }
//        System.out.println();
//    }

//    public static void main(String[] args) {
//        Scanner sc = new Scanner(System.in);
//        int num = sc.nextInt();
//        for (int i = 0; i < num; i++) {
//            int len = sc.nextInt();
//            int[] l = new int[len];
//            for (int i1 = 0; i1 < len; i1++) {
//                l[i1] = sc.nextInt();
//            }
//            int ans = 0;
//            Set<Integer> set = new HashSet<>();
//            for (int j=0;j<len;j++){
//                if(j+1<len && l[j+1]<l[j]){
//                    set.add(l[j]);
//                    for (Integer integer : set) {
//                        for (int p = 0;p<len;p++){
//                            if(l[p]==integer){
//                                l[p]=0;
//                            }
//                        }
//                        ans++;
//                    }
//                    set.clear();
//                }else {
//                    set.add(l[j]);
//                }
//            }
//            System.out.println(ans);
//        }
//    }


//    public static void main(String[] args) {
//        Scanner sc = new Scanner(System.in);
//        int num = sc.nextInt();
//        int[] l = new int[num];
//        if(num==0){
//            System.out.println(-1);
//            return;
//        }
//        for (int i = 0; i < num; i++) {
//            l[i] = sc.nextInt();
//        }
//        int left = 0,right = 1;
//        int max = Integer.MIN_VALUE;
//        int le = 1;
//        int al=0,ar=0;
//        for (;left<num && l[left]!=0;left++){
//            for (right = left+1;right<num && l[right]!=0;right++){
//                int cur = 1;
//                for (int i=left;i<=right;i++){
//                    if(cur==0){
//                        break;
//                    }
//                    cur*=l[i];
//                }
//                if(cur>max){
//                    max = cur;
//                    le = right-left+1;
//                    al = left;
//                    ar = right;
//                }else if(cur==max){
//                    if(right-left>le){
//                        le = right-left;
//                        al = left;
//                        ar = right;
//                    }
//                }
//            }
//        }
//        System.out.println(al+" "+ar);
//    }


    //-8,-4,-2,-1,0,1,2,4,8的最大乘积子数组

    //非降序排列，每次操作可以将一种数字设置为0

    //珠宝

    //一棵树的最小施工数量，从某个点开始修理
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        List<List<Road>> l = new ArrayList<>();
        for (int i=0;i<n;i++){
            l.add(new ArrayList<>());
        }
        for (int i=0;i<n-1;i++){
            int e1  = sc.nextInt();
            int e2 = sc.nextInt();
            int v = sc.nextInt();
            Road road = new Road(e1-1,e2-1,v);
            l.get(e1-1).add(road);
            l.get(e2-1).add(road);
        }
        int ans =0;
        List<Road> roads = l.get(0);
    }

    static class Road{
        int from;
        int to;
        int val;

        public Road(int from, int to, int val) {
            this.from = from;
            this.to = to;
            this.val = val;
        }
    }
}
