package com.bsren.leetcode;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Stack;

public class S71 {

    public static void main(String[] args) {
        String s = "/home//foo/";
        System.out.println(new S71().simplifyPath(s));
    }


    public String simplifyPath(String path) {
        Deque<String> stack = new LinkedList<>();
        int left = 0;
        int cur = 0;
        while (cur<path.length()){
            while (left<path.length() && path.charAt(left)=='/'){
                left++;
            }
            if(left==path.length()){
                break;
            }
            cur = left;
            left--;
            while (cur<path.length() && path.charAt(cur)!='/'){
                cur++;
            }
            String s = path.substring(left+1,cur);
            if(s.equals(".")){
            }else if(s.equals("..")){
                if(!stack.isEmpty()){
                    stack.removeLast();
                }
            }else {
                stack.addLast(s);
            }
            left = cur;
        }
        StringBuilder sb = new StringBuilder();
        while (!stack.isEmpty()){
            sb.append('/').append(stack.removeFirst());
        }
        if(sb.length()==0){
            return "/";
        }
        else return sb.toString();
    }
}
