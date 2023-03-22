package com.bsren.huawei;

import cn.hutool.core.thread.NamedThreadFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class S1 {

    static int[] visited;
    static int ans;
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int num = scanner.nextInt();
        int[] w = new int[num];
        for (int i=0;i<num;i++){
            w[i] = scanner.nextInt();
        }
        List<List<Integer>> l = new ArrayList<>();
        for (int i=0;i<num;i++){
            l.add(new ArrayList<>());
        }
        int j = num-1;
        while (j-->0){
            int l1 = scanner.nextInt();
            int l2 = scanner.nextInt();
            if(w[l1-1]>w[l2-1]){
                l.get(l2-1).add(l1-1);
            }
            else l.get(l1-1).add(l2-1);
        }
        visited = new int[num];
        Arrays.fill(visited,-1);
        for (int i=0;i<num;i++){
            ans = Math.max(ans,fun(l,w,i));
        }
        System.out.println(ans);
    }

    private static int fun(List<List<Integer>> l, int[] w, int i) {
        if(visited[i]!=-1){
            return visited[i];
        }
        List<Integer> list = l.get(i);
        for (Integer next : list) {
            if(w[next]>w[i]){
                visited[i] = Math.max(visited[i],1+fun(l,w,next));
            }
        }
        visited[i] = Math.max(1,visited[i]);
        return visited[i];
    }
}
