package com.bsren.niuke;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class DNode{
    int key;
    int value;
    DNode prev;
    DNode next;

    public DNode(int key, int value) {
        this.key = key;
        this.value = value;
    }

    public DNode() {
    }
}

public class LRU {

    private DNode head;

    private DNode tail;

    int capacity;

    int size;

    Map<Integer,DNode> map;
   public LRU(int capacity){
       this.capacity = capacity;
       this.size = 0;
       map = new HashMap<>();
       head = new DNode();
       tail = new DNode();
       head.next = tail;
       tail.prev = head;
   }

    public static void main(String[] args) {
        LRU lru = new LRU(3);
        lru.set(1,1);
        lru.set(2,2);
        lru.set(3,3);
        System.out.println(lru.get(1));
        lru.set(4,4);
        System.out.println(lru.get(2));
    }
    public int get(int key) {
        // write code here
        DNode dNode = map.get(key);
        if(dNode==null){
            return -1;
        }
        moveToHead(dNode);
        return dNode.value;
    }

    public void set(int key, int value) {
        // write code here
        DNode dNode = map.get(key);
        if(dNode!=null){
            dNode.value = value;
            moveToHead(dNode);
        }else {
            dNode = new DNode(key,value);
            if(size==capacity){
                DNode remove = removeFromTail();
                map.remove(remove.key);
                size--;
            }
            addNodeToHead(dNode);
            map.put(dNode.key,dNode);
        }
        size++;
    }

    private void moveToHead(DNode dNode) {
       removeNode(dNode);
       addNodeToHead(dNode);
    }

    private void addNodeToHead(DNode dNode) {
       dNode.next = this.head.next;
       dNode.next.prev = dNode;
       this.head.next = dNode;
       dNode.prev = head;
    }

    private DNode removeFromTail(){
        DNode remove = tail.prev;
        removeNode(remove);
        return remove;
    }

    private void removeNode(DNode dNode) {
       dNode.prev.next = dNode.next;
       dNode.next.prev = dNode.prev;
       dNode.prev = null;
       dNode.next = null;
    }


}
