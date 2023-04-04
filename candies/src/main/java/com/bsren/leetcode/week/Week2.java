package com.bsren.leetcode.week;

import java.util.*;

public class Week2 {

    public static void main(String[] args) {
        int[] a = new int[]{1,-10,7,13,6,8};
        int p = 5;
        System.out.println(new Week2().findSmallestInteger(a,p));
    }


    public int[] evenOddBit(int n) {
        int[] ans = new int[2];
        boolean f = true;
        while (n!=0){
            if((n&1)==1){
                if(f) {
                    ans[0]+=1;
                }else {
                    ans[1]+=1;
                }
            }
            f = !f;
            n=n>>>1;
        }
        return ans;
    }

    public boolean checkValidGrid(int[][] grid) {
        if(grid[0][0]!=0){
            return false;
        }
        return fun(0,0,grid,0);
    }

    static int[] dx = {1,2,-1,-2};
    static int[] dy = {2,1,2,1};
    private boolean fun(int i, int j, int[][] grid,int cnt) {
        int m = grid.length;
        int n = grid[0].length;
        if(cnt==m*n-1){
            return true;
        }
        int next = cnt+1;
        for (int h=0;h<4;h++){
            int x = i+dx[h];
            int y1 = j+dy[h];
            if(isValid(x,y1,m,n) && grid[x][y1]==next){
                return fun(x,y1,grid,next);
            }
            int y2 = j-dy[h];
            if(isValid(x,y2,m,n) && grid[x][y2]==next){
                return fun(x,y2,grid,next);
            }
        }
//        if(isValid(i-1,j-2,m,n) && grid[i-1][j-2]==next){
//            return fun(i-1,j-2,grid,next);
//        }
//        if(isValid(i-2,j-1,m,n) && grid[i-2][j-1]==next){
//            return fun(i-2,j-1,grid,next);
//        }
//        if(isValid(i-2,j+1,m,n) && grid[i-2][j+1]==next){
//            return fun(i-2,j+1,grid,next);
//        }
//        if(isValid(i-1,j+2,m,n) && grid[i-1][j+2]==next){
//            return fun(i-1,j+2,grid,next);
//        }
//        if(isValid(i+1,j+2,m,n) && grid[i+1][j+2]==next){
//            return fun(i+1,j+2,grid,next);
//        }
//        if(isValid(i+2,j+1,m,n) && grid[i+2][j+1]==next){
//            return fun(i+2,j+1,grid,next);
//        }
//        if(isValid(i+2,j-1,m,n) && grid[i+2][j-1]==next){
//            return fun(i+2,j-1,grid,next);
//        }
//        if(isValid(i+1,j-2,m,n) && grid[i+1][j-2]==next){
//            return fun(i+1,j-2,grid,next);
//        }
        return false;
    }

    private boolean isValid(int i, int i1, int m, int n) {
        return i>=0 && i<m && i1>=0 && i1<n;
    }

    public int findSmallestInteger(int[] nums, int value) {
        int ans = 0;
        Map<Integer,Integer> map = new HashMap<>();
        for (int num : nums) {
            int a;
            if(num%value==0){
                map.put(0,map.getOrDefault(0,0)+1);
            }else {
                if(num<0){
                    a = value+num%value;
                }else {
                    a = num%value;
                }
                map.put(a,map.getOrDefault(a,0)+1);
            }

        }
        while (true){
            int i = ans % value;
            if(map.containsKey(i)){
                map.put(i,map.get(i)-1);
                if(map.get(i)==0){
                    map.remove(i);
                }
                ans++;
            }else {
                break;
            }
        }
        return ans;
    }
}
