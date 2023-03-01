package com.bsren.javaStd.sort;

import java.util.Random;
import java.util.Stack;

class Node<V>{
    public int  key;
    public V value;
    public Node next;
    public Node down;

    public Node(int key, V value) {
        this.key = key;
        this.value = value;
    }
}
public class SkipList<V>{

    private Node headNode;

    private Random random;

    int level;

    final int MAX_LEVEL = 32;

    public SkipList(){
        random = new Random();
        headNode = new Node(Integer.MIN_VALUE,null);
        level = 0;
    }

    public Node search(int key){
        Node h = headNode;
        while (h!=null){
            if(h.key==key){
                return h;
            }
            else if(h.next==null || h.next.key>key){
                h = h.down;
            }else{
                h = h.next;
            }
        }
        return null;
    }

    public void delete(int key){
        Node h = headNode;
        while (h!=null){
            if(h.next==null || h.next.key>key){
                h = h.down;
            }else if(h.next.key==key){
                h.next = h.next.next;
                h = h.down;
            }else{
                h = h.next;
            }
        }
    }

    public void add(int key,V value){
        Stack<Node> stack = new Stack<>();
        Node h = headNode;
        while (h!=null){
            if(h.next==null || h.next.key>key){
                stack.push(h);
                h = h.down;
            }else if(h.key==key){
                h.value = value;
                return;
            }else{
                h = h.next;
            }
        }
        int level = 1;
        Node down = null;
        while (!stack.isEmpty()){
            h = stack.pop();
            Node n = new Node(key,value);
            n.down = down;
            down = n;
            if (h.next != null) {
                n.next = h.next;
            }
            h.next = n;
            if(level>MAX_LEVEL){
                break;
            }
            if(random.nextDouble()>0.5){
                break;
            }
            level++;
            if(level>this.level){
                this.level = level;
                Node high = new Node(Integer.MIN_VALUE, null);
                high.down = this.headNode;
                this.headNode = high;
                stack.add(high);
            }
        }
    }

    public void print(){
        Node h = headNode;
        int ind = 1;
        Node last = h;
        while (last.down!=null){
            last = last.down;
        }
        while (h!=null){
            Node next = h.next;
            Node lastNext = last.next;
            System.out.printf("%-8s","head"+ind+"->");
            while (lastNext!=null && next!=null){
                if(next.key==lastNext.key){
                    System.out.printf("%-5s",lastNext.key+"->");
                    next = next.next;
                    lastNext = lastNext.next;
                }else{
                    lastNext = lastNext.next;
                    System.out.printf("%-5s","");
                }
            }
            h = h.down;
            ind++;
            System.out.println();
        }
    }

    public static void main(String[] args) {
        SkipList<Integer>list= new SkipList<>();
//        list.add(Integer.MIN_VALUE,1);
        for(int i=1;i<64;i++) {
            list.add(i,1);
        }
//        System.out.println("---------------------------");
//        list.print();
//        list.delete(4);
//        list.delete(8);
//        System.out.println("---------------------------");
        list.print();
    }



}

