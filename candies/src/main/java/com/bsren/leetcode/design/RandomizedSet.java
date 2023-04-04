package com.bsren.leetcode.design;

import java.util.Map;
import java.util.Random;

public class RandomizedSet {

    int[] cnt = new int[256];
    Node[] objects = new Node[256];
    int total = 0;
    public RandomizedSet() {

    }

    public boolean insert(int val) {
        int bucket = Math.abs(val%256);
        Node node = new Node(val);
        Node object = objects[bucket];
        if(object==null){
            objects[bucket] = node;
            cnt[bucket]++;
            total++;
            return true;
        }else {
            while (true){
                Node next = object.next;
                if(object.val==val){
                    return false;
                }
                if(next==null){
                    object.next = node;
                    node.prev = object;
                    cnt[bucket]++;
                    total++;
                    return true;
                }
                object = next;
            }
        }
    }

    public static void main(String[] args) {
        RandomizedSet set = new RandomizedSet();
        set.insert(1);
        set.insert(2);
        System.out.println(set.insert(-1));
        System.out.println(set.remove(1));
        System.out.println(set.getRandom());
    }

    public boolean remove(int val) {
        int bucket = Math.abs(val % 256);
        Node object = objects[bucket];
        if(object==null){
            return false;
        }
        while (object!=null){
            Node next = object.next;
            if(object.val==val){
                if(object==objects[bucket]){
                    objects[bucket] = next;
                    if(next!=null){
                        next.prev = null;
                    }
                }else {
                    object.prev.next = next;
                    if(next!=null){
                        next.prev = object.prev;
                    }
                }
                total--;
                cnt[bucket]--;
                return true;
            }
            object = next;
        }
        return false;
    }

    public int getRandom() {
        Random random = new Random();
        int nextInt = random.nextInt(total)+1;
        int idx = 0;
        for (;idx<256;idx++){
            if(nextInt-cnt[idx]<=0){
                break;
            }else {
                nextInt-=cnt[idx];
            }
        }
        Node node = objects[idx];
        while (--nextInt>0){
            node = node.next;
        }
        return node.val;
    }
}

class Node{
    int val;
    Node prev;
    Node next;

    public Node(int val) {
        this.val = val;
    }

}
