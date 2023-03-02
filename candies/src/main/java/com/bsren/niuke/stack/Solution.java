package com.bsren.niuke.stack;

import com.sun.java.swing.plaf.windows.WindowsTabbedPaneUI;

import java.util.*;

public class Solution {

    Stack<Integer> stack1 = new Stack<>();
    Stack<Integer> stack2 = new Stack<>();


    public void push(int node) {
        stack1.push(node);
        if(stack2.isEmpty()){
            stack2.push(node);
        }else {
            if(stack2.peek()<=node){
               stack2.push(stack2.peek());
            }else {
                stack2.push(node);
            }
        }
    }

    public void pop() {
        stack1.pop();
        stack2.pop();
    }

    public int top() {
        return stack1.peek();
    }

    public int min() {
        return stack2.peek();
    }

    public boolean isValid (String s) {
        // write code here
        Stack<Character> stack = new Stack<>();
        for (char c:s.toCharArray()){
            if(c=='(' || c=='{' || c=='['){
                stack.push(c);
            }else {
                if(c==')'){
                    if(!stack.isEmpty() && stack.peek()=='('){
                        stack.pop();
                    }else return false;
                }else if(c==']'){
                    if(!stack.isEmpty() && stack.peek()=='['){
                        stack.pop();
                    }else return false;
                }else {
                    if(!stack.isEmpty() && stack.peek()=='{'){
                        stack.pop();
                    }else return false;
                }
            }
        }
        return stack.isEmpty();
    }




    public void QuickSort(int[] array,int low,int high,int k){
        if(low<high){
            int mid = Partition(array,low,high);
            QuickSort(array,low,mid-1);
            if(k>=mid+1){
                QuickSort(array,mid+1,high);
            }else {
                System.out.println("-----------");
            }
        }
    }

    public void QuickSort(int[] array,int low,int high){
        if(low<high){
            int mid = Partition(array,low,high);
            QuickSort(array,low,mid-1);
            QuickSort(array,mid+1,high);
        }
    }

    public ArrayList<Integer> GetLeastNumbers_Solution(int [] input, int k) {
        QuickSort(input,0,input.length-1,k);
        ArrayList<Integer> l = new ArrayList<>();
        for (int i=0;i<k;i++){
            l.add(input[i]);
        }
        return l;
    }

    public int  Partition(int[] input,int low,int high){
        int num = input[low];
        while (low<high){
            while (low<high && input[high]>=num){
                high--;
            }
            input[low] = input[high];
            while (low<high && input[low]<=num){
                low++;
            }
            input[high] = input[low];
        }
        input[low] = num;
        return low;
    }



    public static void main(String[] args) {
        Solution solution = new Solution();
        solution.Insert(5);
        System.out.println(solution.GetMedian());
        solution.Insert(2);
        System.out.println(solution.GetMedian());
        solution.Insert(3);
        System.out.println(solution.GetMedian());
        solution.Insert(4);
        System.out.println(solution.GetMedian());
        solution.Insert(1);
        System.out.println(solution.GetMedian());
        solution.Insert(6);
        System.out.println(solution.GetMedian());
        solution.Insert(7);
        System.out.println(solution.GetMedian());
        solution.Insert(0);
        System.out.println(solution.GetMedian());
        solution.Insert(8);
        System.out.println(solution.GetMedian());
    }


    PriorityQueue<Integer> q1 = new PriorityQueue<>(new Comparator<Integer>() {
        @Override
        public int compare(Integer o1, Integer o2) {
            return o2-o1;
        }
    });
    PriorityQueue<Integer> q2 = new PriorityQueue<>();
    Double ret = 0.0;
    boolean o = true;
    public void Insert(Integer num) {
        q1.add(num);
        q2.add(q1.peek());
        q1.poll();
        if(q1.size()<q2.size()-1){
            q1.add(q2.poll());
        }
        if(o){
            ret = Double.valueOf(q2.peek());
        }else {
            ret = 0.5*(q1.peek()+q2.peek());
        }
        o=!o;
    }


    public Double GetMedian() {
        return ret;
    }

    public ArrayList<Integer> maxInWindows(int [] num, int size) {
        if(size==0 || size>num.length){
            return new ArrayList<>();
        }
        Deque<int[]> deque = new LinkedList<>();
        ArrayList<Integer> ans = new ArrayList<>();
        for (int i=0;i<num.length;i++){
            if(deque.isEmpty()){
                deque.add(new int[]{i,num[i]});
            }else {
                if(i>=size && deque.getFirst()[0]<=i-size){
                    deque.removeFirst();
                }
                while (!deque.isEmpty() && deque.getLast()[1]<num[i]){
                    deque.removeLast();
                }
                deque.addLast(new int[]{i,num[i]});
            }
            if(i>=size-1){
                ans.add(deque.getFirst()[1]);
            }
        }
        return ans;
    }
}
