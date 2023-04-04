package com.bsren.leetcode.design;

import java.util.LinkedList;
import java.util.Queue;

public class MyStack {

    Queue<Integer> queue1;
    Queue<Integer> queue2;

    public static void main(String[] args) {
        MyStack myStack = new MyStack();
        myStack.push(1);
        myStack.push(2);
        System.out.println(myStack.top());
        System.out.println(myStack.top());
    }

    public MyStack() {
        queue1 = new LinkedList<>();
        queue2 = new LinkedList<>();
    }

    public void push(int x) {
        if(!queue1.isEmpty() || queue2.isEmpty()){
            queue1.offer(x);
        }else{
            queue1.offer(x);
        }
    }

    public int pop() {
        if(queue1.isEmpty()){
            while (queue2.size()!=1){
                queue1.offer(queue2.poll());
            }
            return queue2.poll();
        }else {
            while (queue1.size()!=1){
                queue2.offer(queue1.poll());
            }
            return queue1.poll();
        }
    }

    public int top() {
        if(queue1.isEmpty()){
            while (queue2.size()!=1){
                queue1.offer(queue2.poll());
            }
            int peek = queue2.poll();
            queue1.offer(peek);
            return peek;
        }else {
            while (queue1.size()!=1){
                queue2.offer(queue1.poll());
            }
            int peek = queue1.poll();
            queue2.offer(peek);
            return peek;
        }
    }

    public boolean empty() {
        return queue1.isEmpty() && queue2.isEmpty();
    }
}
