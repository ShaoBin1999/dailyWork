package com.bsren.leetcode.tu;

import org.apache.poi.ss.formula.functions.Na;

import java.util.*;

public class Solution743 {

   public static final int INF = Integer.MAX_VALUE/2;

    public int networkDelayTime1(int[][] times, int n, int k) {
        boolean[] visited = new boolean[n];
        int[][] edges = new int[n][n];
        int[] dist = new int[n];
        Arrays.fill(dist,INF);
        for (int i=0;i<n;i++){
            Arrays.fill(edges[i],INF);
        }
        for (int[] time : times) {
            edges[time[0]-1][time[1]-1] = time[2];
        }
        dist[k-1] = 0;
        for (int i=0;i<n;i++){
            int t = -1;
            for (int j=0;j<n;j++){
                if(!visited[j] && (t==-1 || dist[j]<dist[t])){
                    t = j;
                }
            }
            visited[t] = true;
            for (int j=0;j<n;j++){
                dist[j] = Math.min(dist[j],dist[t]+edges[t][j]);
            }
        }
        int ans = -1;
        for (int i=0;i<n;i++){
            ans = Math.max(ans,dist[i]);
        }
        return ans==INF?-1:ans;
    }

    public int networkDelayTime2(int[][] times, int n, int k) {
        List<int[]>[] g = new List[n];
        int[] dist = new int[n];
        for (int i = 0; i < n; ++i) {
            dist[i] = INF;
            g[i] = new ArrayList<>();
        }
        for (int[] t : times) {
            g[t[0] - 1].add(new int[]{t[1] - 1, t[2]});
        }
        dist[k - 1] = 0;
        PriorityQueue<int[]> q = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
        q.offer(new int[]{0, k - 1});
        boolean[] visited = new boolean[n];
        while (!q.isEmpty()) {
            int[] p = q.poll();
            int u = p[1];
            if (visited[u]) {
                continue;
            }

            visited[u] = true;
            for (int[] ne : g[u]) {
                int v = ne[0], w = ne[1];
                if (dist[v] > dist[u] + w) {
                    dist[v] = dist[u] + w;
                    q.offer(new int[]{dist[v], v});
                }
            }
        }
        int ans = 0;
        for (int d : dist) {
            ans = Math.max(ans, d);
        }
        return ans == INF ? -1 : ans;
    }

}
