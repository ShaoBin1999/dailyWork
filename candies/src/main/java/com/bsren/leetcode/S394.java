package com.bsren.leetcode;

import java.util.Stack;

public class S394 {


    public static void main(String[] args) {
        String s = "3[a2[c]]";
        System.out.println(new S394().decodeString(s));
    }

//    public String decodeString(String s) {
//        Stack<Integer> stack1 = new Stack<>();
//        Stack<StringBuilder> stack2 = new Stack<>();
//        StringBuilder sb = new StringBuilder();
//        int num = 0;
//        for (int i = 0; i < s.toCharArray().length; i++) {
//            char c = s.charAt(i);
//            if(Character.isDigit(c)){
//                num = num*10+c-'0';
//            }else if(c=='['){
//                stack1.push(num);
//                stack2.push(sb);
//                sb = new StringBuilder();
//                num=0;
//            }else if(c==']'){
//                Integer pop = stack1.pop();
//                StringBuilder pre = stack2.pop();
//                for (int h=0;h<pop;h++){
//                    pre.append(sb);
//                }
//                sb = pre;
//            }else {
//                sb.append(c);
//            }
//        }
//        return sb.toString();
//    }
    public String decodeString(String s) {
        if(s.indexOf('[')==-1){
            return s;
        }
        int idx = 0;
        for (int i=0;i<s.length();i++){
            if(Character.isDigit(s.charAt(i))){
                idx = i;
                break;
            }
        }
        int num = 0;
        int l = idx;
        for (int i=idx;i<s.length();i++){
            char c = s.charAt(i);
            if(Character.isDigit(c)){
                num = num*10+c-'0';
            }else {
                l = i;
                break;
            }
        }
        int f = 1;
        int r = l+1;
        while (r<s.length()){
            if(s.charAt(r)=='['){
                f++;
            }else if(s.charAt(r)==']'){
                f--;
                if(f==0){
                    break;
                }
            }
            r++;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(s.substring(0,idx));
        for (int i=0;i<num;i++){
            sb.append(s.substring(l+1,r));
        }
        if(r+1<s.length()){
            sb.append(s.substring(r+1));
        }
        return decodeString(String.valueOf(sb));
    }

}
