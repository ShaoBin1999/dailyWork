package com.bsren.leetcode;

public class S1041 {

    public static void main(String[] args) {
        System.out.println(new S1041().isRobotBounded("GLRLLGLL"));
    }
    public boolean isRobotBounded(String instructions) {
        int begin_dir = 0;
        int[] cur = new int[]{0,0};
        int cur_dir = begin_dir;
        for (char c : instructions.toCharArray()) {
            if(c=='G'){
                fun(cur, cur_dir);
            }else if(c=='L'){
                cur_dir-=1;
            }else {
                cur_dir+=1;
            }
        }
        if(cur_dir%4==0 && (cur[0]!=0 || cur[1]!=0)){
            return false;
        }
        return true;
    }

    private void fun(int[] cur, int cur_dir) {
        int dir = Math.abs(cur_dir%4);
        if(dir==0){
            cur[1]++;
        }else if(dir==1){
            cur[0]++;
        }else if(dir==2){
            cur[1]--;
        }else {
            cur[0]--;
        }
    }
}
