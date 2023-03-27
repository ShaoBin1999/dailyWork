package com.bsren.leetcode.week;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class Week1 {

    public int kItemsWithMaximumSum(int numOnes, int numZeros, int numNegOnes, int k) {
        int ans = 0;
        if(numOnes>=k){
            return k;
        }
        ans+=numOnes;
        k-=numOnes;
        if(numZeros>=k){
            return ans;
        }
        k-=numZeros;
        ans-=k;
        return ans;
    }

    public List<Long> minOperations(int[] nums, int[] queries) {
        Arrays.sort(nums);
        long[] sums = new long[nums.length+1];
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (int i=1;i<=nums.length;i++){
           sums[i] = sums[i-1]+nums[i-1];
           min = Math.min(min,nums[i-1]);
           max = Math.max(max,nums[i-1]);
        }

        List<Long> list = new ArrayList<>();
        for (int query : queries) {
            if(query<=min){
                list.add(sums[nums.length]- (long) query *nums.length);
            }else if(query>=max){
                list.add((long)query*nums.length-sums[nums.length]);
            }else {
                int l = 0,r = nums.length-1;
                int mid;
                while (l<r){
                    mid = (l+r+1)/2;
                    if(nums[mid]<=query){
                        l = mid;
                    }else {
                        r = mid-1;
                    }
                }
                long sum1 = (long) query*(l+1)-sums[l+1];
                long sum2 = sums[nums.length]-sums[l+1]- (long) query *(nums.length-1-l);
                list.add(sum1+sum2);
            }
        }
        return list;
    }

    public boolean findSubarrays(int[] nums) {
        if(nums.length<3){
            return false;
        }
        Map<Integer,Integer> map = new HashMap<>();
        for (int i=1;i<nums.length;i++){
            int val = nums[i]+nums[i-1];
            if(map.containsKey(val)){
                return true;
            }else {
                map.put(val,i-1);
            }
        }
        return false;
    }

    public static void main(String[] args) {
        int[] nums = new int[]{3,2,6,8};
        int[] q = new int[]{1,5};
        System.out.println(new Week1().minOperations(nums,q));
    }

    public boolean primeSubOperation(int[] nums) {
        for (int i = 0; i < nums.length; i++) {
            boolean f = false;
            for (int j=nums[i]-1;j>=2;j--){
                if(i>0){
                    if(isPrime(j) && nums[i]-j>nums[i-1]){
                        nums[i] = nums[i]-j;
                        f = true;
                        break;
                    }
                }else {
                    if(isPrime(j)){
                        nums[i] = nums[i]-j;
                        f = true;
                        break;
                    }
                }
            }
            if(i == 0 || nums[i] > nums[i - 1]){
                continue;
            }
            if(!f){
                return false;
            }
        }
        System.out.println(Arrays.toString(nums));
        return true;
    }

    public boolean isPrime(int n){
        if(n==1){
            return false;
        }else if(n==2 || n==3){
            return true;
        }
        for (int i=2;i<100;i++){
            if(i*i>n){
                break;
            }
            if(n%i==0){
                return false;
            }
        }
        return true;
    }

    private final ReentrantLock[] lockList = {new ReentrantLock(),
            new ReentrantLock(),
            new ReentrantLock(),
            new ReentrantLock(),
            new ReentrantLock()};


    // call the run() method of any runnable to execute its code
    public void wantsToEat(int philosopher,
                           Runnable pickLeftFork,
                           Runnable pickRightFork,
                           Runnable eat,
                           Runnable putLeftFork,
                           Runnable putRightFork) throws InterruptedException {

        int leftFork = (philosopher + 1) % 5;    //左边的叉子 的编号
        int rightFork = philosopher;    //右边的叉子 的编号

        //编号为偶数的哲学家，优先拿起左边的叉子，再拿起右边的叉子
        if (philosopher % 2 == 0) {
            lockList[leftFork].lock();    //拿起左边的叉子
            lockList[rightFork].lock();    //拿起右边的叉子
        }
        //编号为奇数的哲学家，优先拿起右边的叉子，再拿起左边的叉子
        else {
            lockList[rightFork].lock();    //拿起右边的叉子
            lockList[leftFork].lock();    //拿起左边的叉子
        }

        pickLeftFork.run();    //拿起左边的叉子 的具体执行
        pickRightFork.run();    //拿起右边的叉子 的具体执行

        eat.run();    //吃意大利面 的具体执行

        putLeftFork.run();    //放下左边的叉子 的具体执行
        putRightFork.run();    //放下右边的叉子 的具体执行

        lockList[leftFork].unlock();    //放下左边的叉子
        lockList[rightFork].unlock();    //放下右边的叉子
    }

}
