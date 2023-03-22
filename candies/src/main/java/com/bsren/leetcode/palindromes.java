package com.bsren.leetcode;

import jdk.internal.org.objectweb.asm.tree.IincInsnNode;
import org.checkerframework.checker.units.qual.C;

import java.util.*;

public class palindromes {



    public int superpalindromesInRange(String sL, String sR) {
        long L = Long.valueOf(sL);
        long R = Long.valueOf(sR);
        int MAGIC = 100000;
        int ans = 0;

        // count odd length;
        for (int k = 1; k < MAGIC; ++k) {
            StringBuilder sb = new StringBuilder(Integer.toString(k));
            for (int i = sb.length() - 2; i >= 0; --i)
                sb.append(sb.charAt(i));
            long v = Long.valueOf(sb.toString());
            v *= v;
            if (v > R) break;
            if (v >= L && isPalindrome(v)) ans++;
        }

        // count even length;
        for (int k = 1; k < MAGIC; ++k) {
            StringBuilder sb = new StringBuilder(Integer.toString(k));
            for (int i = sb.length() - 1; i >= 0; --i)
                sb.append(sb.charAt(i));
            long v = Long.valueOf(sb.toString());
            v *= v;
            if (v > R) break;
            if (v >= L && isPalindrome(v)) ans++;
        }

        return ans;
    }

    public boolean isPalindrome(long x) {
        return x == reverse(x);
    }

    public long reverse(long x) {
        long ans = 0;
        while (x > 0) {
            ans = 10 * ans + x % 10;
            x /= 10;
        }

        return ans;
    }

    public String trans(String s, int n) {

        String[] strArray = s.split(" ", -1);

        StringBuilder strbuild = new StringBuilder();

        for (int i = strArray.length - 1; i >= 0; i--) {
            strbuild.append(reverse(strArray[i])); //数组转换为字符串
            //最后一个字符串后面不再附加空格
            if(i==0) {
                break;
            }
            //字符串之间附加空格
            strbuild.append(" ");
        }
        return strbuild.toString();
    }

    //大小写转换
    private String reverse(String s){
        StringBuilder res= new StringBuilder();
        for(char ch:s.toCharArray()){
            if(Character.isLowerCase(ch)){
                res.append(Character.toUpperCase(ch));
            }
            else if(Character.isUpperCase(ch)){
                res.append(Character.toLowerCase(ch));
            }
        }
        return res.toString();
    }


    public static void main(String[] args) {
        String s = "       a ";
        String[] s1 = s.split(" ");
        for (String c1 : s1) {
            System.out.println(c1);
        }
    }

}
