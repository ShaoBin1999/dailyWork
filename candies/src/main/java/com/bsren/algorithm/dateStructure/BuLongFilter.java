package com.bsren.algorithm.dateStructure;

import java.util.BitSet;
import java.util.concurrent.atomic.AtomicInteger;

public class BuLongFilter {

    public static void main(String[] args) throws Exception {
        BuLongFilter buLongFilter = new BuLongFilter(MisJudgementRate.SMALL, 100, null);
        for(int i=0;i<100;i++){
            buLongFilter.add(i);
        }
    }

    private final Integer size;

    private final BitSet bitSet;

    private final SimpleHash[] simpleHashes;

    private final MisJudgementRate misJudgementRate;

    private final Double autoClearRate;

    private final AtomicInteger useCount = new AtomicInteger(0);

    public static class SimpleHash {
        private int seed;

        public SimpleHash(int seed) {
            this.seed = seed;
        }

        public int hash(Object value) {
            int h;
            return (value == null) ? 0 : ((seed*59813513) & (h = value.hashCode()) ^ (h >>> 16));
        }
    }

    public enum MisJudgementRate {


        VERY_SMALL(new int[]{2,3,5,7}),

        SMALL(new int[]{2, 3, 5, 7, 11, 13, 17, 19}),

        MIDDLE(new int[] { 2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53 }),

        HIGH(new int[] { 2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97,
                101, 103, 107, 109, 113, 127, 131 });
        private int[] seeds;

        MisJudgementRate(int[] seeds) {
            this.seeds = seeds;
        }

    }

    public BuLongFilter(MisJudgementRate rate,int dataSize,Double autoClearRate) throws Exception {
        long bitSize = (long) rate.seeds.length *dataSize;
        if(bitSize<0 || bitSize> Integer.MAX_VALUE){
            throw new Exception("位数过大");
        }
        simpleHashes = new SimpleHash[rate.seeds.length];
        misJudgementRate = rate;
        buildHashFunc(misJudgementRate);
        bitSet = new BitSet((int)bitSize);
        size = (int)bitSize;
        System.out.println(size);
        this.autoClearRate = autoClearRate;
    }

    public void buildHashFunc(MisJudgementRate rate){
        for(int i=0;i<rate.seeds.length;i++){
            simpleHashes[i] = new SimpleHash(rate.seeds[i]);
        }
    }

    public void add(Object value){
        checkClear();
        if(!contains(value)){
            for(SimpleHash f: simpleHashes){
                bitSet.set(f.hash(value)%size);
            }
            useCount.getAndIncrement();
        }else{
            System.out.println(value+"already exist");
        }

    }

    public boolean contains(Object value){
        boolean ret = true;
        for(SimpleHash f:simpleHashes){
            ret = ret& bitSet.get(f.hash(value)%size);
        }
        return ret;
    }

    private void checkClear() {
        if(autoClearRate!=null){
            if(getUseRate()>=autoClearRate){
                synchronized (this){
                    if(getUseRate()>=autoClearRate){
                        bitSet.clear();
                        useCount.set(0);
                    }
                }
            }
        }
    }

    public double getUseRate(){
        return (double) useCount.intValue()/(double) bitSet.length();
    }
}
