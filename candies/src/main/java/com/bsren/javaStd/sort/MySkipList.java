package com.bsren.javaStd.sort;


import java.util.Random;
import java.util.Stack;

public class MySkipList {

    private Node headNode;

    private Random random;

    private int level;

    final int MAX_LEVEL = 32;

    public MySkipList(){
        headNode = new Node(Integer.MIN_VALUE,null);
        random = new Random();
        level = 0;
    }

    public Node search(int key){
        Node n = this.headNode;
        while (n!=null){
            if(n.key==key){
                return n;
            }else if(n.next==null || n.next.key>key){
                n = n.down;
            }else {
                n = n.next;
            }
        }
        return null;
    }

    public void delete(int key){
        Node n = this.headNode;
        while (n!=null){
            if(n.next==null || n.next.key>key){
                n = n.down;
            }else if(n.next.key==key){
                n.next = n.next.next;
                n = n.down;
            }else {
                n = n.next;
            }
        }
    }

    public <T> void add(int key,T value){
        Stack<Node> stack = new Stack<>();
        Node n = this.headNode;
        while (n!=null){
            if(n.key==key){
                n.value = value;
                return;
            }else if(n.next==null || n.next.key>key){
                stack.push(n);
                n = n.down;
            }else {
                n = n.next;
            }
        }
        int level = 1;
        Node down = null;
        while (!stack.isEmpty()){
            Node pop = stack.pop();
            Node node = new Node(key,value);
            node.down = down;
            down = node;
            if(pop.next!=null){
                node.next = pop.next;
            }
            pop.next = node;
            if(level>MAX_LEVEL){
                break;
            }
            if(random.nextDouble()>0.5){
                break;
            }
            level++;
            if(level>this.level){
                this.level = level;
                Node head = new Node(Integer.MIN_VALUE,null);
                head.down = this.headNode;
                this.headNode = head;
                stack.push(head);
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
