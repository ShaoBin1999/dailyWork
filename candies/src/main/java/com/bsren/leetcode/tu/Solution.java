package com.bsren.leetcode.tu;

import java.util.*;

public class Solution {

//    public boolean canFinish(int numCourses, int[][] prerequisites) {
//        List<List<Integer>> l = new ArrayList<>();
//        int[] in = new int[numCourses];
//        for (int i=0;i<numCourses;i++){
//            l.add(new ArrayList<>());
//        }
//        for (int[] p : prerequisites) {
//            l.get(p[1]).add(p[0]);
//            in[p[0]]++;
//        }
//        Deque<Integer> deque = new LinkedList<>();
//        for (int i=0;i<numCourses;i++){
//            if(in[i]==0){
//                deque.addLast(i);
//            }
//        }
//        int visited = 0;
//        while (!deque.isEmpty()){
//            visited++;
//            Integer poll = deque.poll();
//            List<Integer> list = l.get(poll);
//            for (Integer integer : list) {
//                in[integer]--;
//                if(in[integer]==0){
//                    deque.addLast(integer);
//                }
//            }
//        }
//        return visited==numCourses;
//    }
//    List<List<Integer>> edges;
//    int[] visited;
//    boolean valid = true;
//    public boolean canFinish(int numCourses, int[][] prerequisites){
//        edges = new ArrayList<>();
//        for (int i = 0; i < numCourses; i++) {
//            edges.add(new ArrayList<>());
//        }
//        visited = new int[numCourses];
//        for (int[] prerequisite : prerequisites) {
//            edges.get(prerequisite[1]).add(prerequisite[0]);
//        }
//        for (int i=0;i<numCourses && valid;i++){
//            if(visited[i]==0){
//                dfs(i);
//            }
//        }
//        return valid;
//    }
//
//    private void dfs(int i) {
//        visited[i] = 1;
//        for (Integer edge : edges.get(i)) {
//            if(visited[edge]==0){
//                dfs(edge);
//                if(!valid){
//                    return;
//                }
//            }else if(visited[edge]==1){
//                valid = false;
//                return;
//            }
//        }
//        visited[i] = 2;
//    }

    public static void main(String[] args) {
        int[][] p = new int[4][];
        p[0] = new int[]{1,0};
        p[1] = new int[]{2,0};
        p[2] = new int[]{3,1};
        p[3] = new int[]{3,2};
        System.out.println(Arrays.toString(new Solution().findOrder(4, p)));
    }
    List<List<Integer>> edges = new ArrayList<>();
    int[] visited;
    boolean flag = true;
    int[] ans;
    int index;
    public int[] findOrder(int numCourses, int[][] prerequisites) {
        for (int i = 0; i < numCourses; i++) {
            edges.add(new ArrayList<>());
        }
        ans = new int[numCourses];
        index = numCourses-1;
        visited = new int[numCourses];
        for (int[] prerequisite : prerequisites) {
            edges.get(prerequisite[1]).add(prerequisite[0]);
        }
        for (int i=0;i<numCourses;i++){
            if(visited[i]==0 && flag){
                dfs(i);
            }
        }
        if(!flag){
            return new int[0];
        }

        return ans;
    }

    private void dfs(int i) {
        visited[i] = 1;
        for (Integer integer : edges.get(i)) {
            if(visited[integer]==1){
                flag = false;
                return;
            }else if(visited[integer]==0){
                dfs(integer);
                if(!flag){
                    return;
                }
            }
        }
        ans[index--] = i;
        visited[i] = 2;
    }

}
