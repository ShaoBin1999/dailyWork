package com.bsren.leetcode.tu;

import org.apache.tomcat.jni.LibraryNotFoundError;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;

public class Solution1 {


//    boolean[] visited;
//    public int findCircleNum(int[][] isConnected) {
//        int n = isConnected.length;
//        visited = new boolean[n];
//        int ans = 0;
//        for (int i=0;i<n;i++){
//            if(!visited[i]){
//                dfs(isConnected,i);
//                ans++;
//            }
//        }
//        return ans;
//    }
//
//    private void dfs(int[][] isConnected, int i) {
//        visited[i] = true;
//        for (int next=0;next<isConnected.length;next++){
//            if(!visited[next] && isConnected[i][next]==1){
//                dfs(isConnected,next);
//            }
//        }
//    }
//    public int findCircleNum(int[][] isConnected) {
//        int ans = 0;
//        int num = isConnected.length;
//        Deque<Integer> deque = new LinkedList<>();
//        boolean[] visited = new boolean[num];
//        for (int i = 0; i < num; i++) {
//            if (!visited[i]) {
//                deque.add(i);
//                while (!deque.isEmpty()) {
//                    Integer poll = deque.poll();
//                    visited[poll] = true;
//                    for (int j = 0; j < num; j++) {
//                        if (!visited[j] && isConnected[poll][j] == 1) {
//                            deque.add(j);
//                        }
//                    }
//                }
//                ans++;
//            }
//        }
//        return ans;
//    }

    public int findCircleNum(int[][] isConnected) {
        int n = isConnected.length;
        int[] parent = new int[n];
        for (int i=0;i<n;i++){
            parent[i] = i;
        }
        int ans = 0;
        for (int i=0;i<n;i++){
            for (int j = i+1;j<n;j++){
                if(isConnected[i][j]==1){
                    union(parent,i,j);
                }
            }
        }
        for (int i=0;i<n;i++){
            if(parent[i]==i){
                ans++;
            }
        }
        return ans;
    }

    public void union(int[] parent,int ind1,int ind2){
        parent[find(parent,ind1)] = find(parent,ind2);
    }

    public int find(int[] parent,int ind){
        if(parent[ind]!=ind){
            parent[ind] = find(parent,parent[ind]);
        }
        return parent[ind];
    }
}