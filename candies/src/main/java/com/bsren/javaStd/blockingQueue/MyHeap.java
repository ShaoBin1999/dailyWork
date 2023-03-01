package com.bsren.javaStd.blockingQueue;

import java.util.PriorityQueue;

public class MyHeap {

    public static void main(String[] args) {
        MyHeap myHeap = new MyHeap(64);
        myHeap.add(1);
        myHeap.add(3);
        myHeap.add(10);
        myHeap.add(4);
        myHeap.add(6);
        myHeap.add(-1);
        myHeap.add(2);
        while (!myHeap.isEmpty()){
            System.out.println(myHeap.pop());
        }
    }

    int[] objects;

    int size;


    MyHeap(int capacity){
        objects = new int[capacity];
        size = 0;
    }

    public boolean isEmpty(){
        return size==0;
    }

    public void add(int value){
        int newSize = size+1;
        if(newSize>objects.length){
            grow();
        }
        if(size==0){
            objects[0] = value;
        }else {
            siftUp(objects,value,size);
        }
        size=newSize;
    }

    private void siftUp(int[] objects, int value, int k) {
        while (k>0){
            int parent = (k-1)>>>1;
            if(value>objects[parent]){
                break;
            }
            objects[k] = objects[parent];
            k = parent;
        }
        objects[k] = value;
    }

    public int pop(){
        if(size==0){
            throw new NullPointerException();
        }
        int newSize = size-1;
        int ret = objects[0];
        int last = objects[newSize];
        siftDown(0,last,objects,newSize);
        size = newSize;
        return ret;
    }

    private void siftDown(int k, int last, int[] objects, int size) {
        if(size==0){
            return;
        }
        int half = size>>>1;
        while (k<half){
            int child = (k<<1)+1;
            int val = objects[child];
            int right = child+1;
            if(right<size && objects[right]<objects[child]){
                child = right;
                val = objects[child];
            }
            if(last<=val){
                break;
            }
            objects[k] = val;
            k = child;
        }
        objects[k] = last;
    }

    public void grow(){

    }


}
