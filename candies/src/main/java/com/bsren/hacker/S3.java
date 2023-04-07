package com.bsren.hacker;

import java.math.BigInteger;

public class S3 {

    public static void main(String[] args) {
        extraLongFactorials(30);
    }

    public static void extraLongFactorials(int n) {
        // Write your code here
        BigInteger bigInteger = new BigInteger("1");
        for (int i=1;i<=n;i++){
            bigInteger = bigInteger.multiply(new BigInteger(String.valueOf(i)));
        }
        System.out.println(bigInteger);
    }
}
