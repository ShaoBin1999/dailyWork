package com.bsren.leetcode;

public class S50 {

    public static void main(String[] args) {
        S50 s50 = new S50();
        System.out.println(s50.myPow(2, 10));
    }

    public double myPow(double x, int n) {
        return n<0?Pow(x,-n):Pow(x,n);
    }

    public double Pow(double x, long n) {
        if(n==0){
            return 1.0;
        }
        double factor = x;
        double cur = 1;
        while (n!=0){
            if((n&1)==1){
                cur = cur*factor;
            }
            factor = factor*factor;
            n = n>>>1;
        }
        return cur;
    }

}
