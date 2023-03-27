package com.bsren.leetcode.dynamic;

import java.util.*;

public class S279 {

    public int numSquares(int n) {
        int[] f = new int[n+1];
        for (int i=1;i<=n;i++){
            int min = Integer.MAX_VALUE;
            for (int j=1;j*j<=i;j++){
                min = Math.min(min,f[i-j*j]);
            }
            f[i] = min+1;
        }
        return f[n];
    }

    /**
     * 28.
     * 插班生
     * 一排学生共
     * �
     * n 个人在一起听课，分别坐在位置
     * 1
     * ,
     * 2
     * ,
     * 3...
     * �
     * 1,2,3...n，每个人都有一个听课认真度
     * �
     * �
     * a
     * i
     * ​
     *  ，这时来了一个调皮的插班生，他会影响别人听课。具体来说，距离他
     * �
     * �
     * �
     * dis 的学生（距离即两个人位置之差的绝对值），听课认真度将减少
     * �
     * −
     * �
     * �
     * �
     * n−dis，注意，一位学生的听课认真度不会因为插班生的到来而被降低到负数（即最少被降低到 0）。总共有
     * �
     * n 个座位供插班生选择，被占领座位的学生将被踢出班级，其他人的座位不变，但听课认真度会减少。现在想知道，如果插班生坐在了位置
     * 1
     * ,
     * 2
     * ,
     * 3
     * ,
     * 4...
     * �
     * 1,2,3,4...n，所有学生的听课认真度之和分别是多少。（插班生没有听课认真度）
     * 时间限制：C/C++ 3秒，其他语言6秒
     * 空间限制：C/C++ 256M，其他语言512M
     * 输入描述：
     * 第一行输入一个正整数 ， 表示学生的个数。
     *
     * 接下来一行输入  个正整数 ，表示每个学生的听课认真度。
     *
     * 输出描述：
     * 输出  个正整数，其中第  个正整数表示当插班生坐在位置  时，所有学生的听课认真度之和。
     * @param args
     */
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int num = sc.nextInt();
        int[] l = new int[num];
        for (int i = 0; i < num; i++) {
            l[i] = sc.nextInt();
        }
        int[] ans = new int[num];

    }
}
