package com.bsren.leetcode;

import com.bsren.niuke.node.ListNode;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class S1019 {

    static class Node extends ListNode{

        int idx;
        public Node(int val,int idx){
            super(val);
            this.idx = idx;
        }
    }
    public int[] nextLargerNodes(ListNode head) {
        Deque<ListNode> deque = new LinkedList<>();
        ListNode cur = head;
        List<Node> list = new ArrayList<>();
        int idx = 0;
        while (cur!=null){
            while (!deque.isEmpty() && deque.getLast().val<cur.val){
                ListNode node = deque.removeLast();
                list.add(new Node(cur.val,idx));
            }
            deque.addLast(new Node(cur.val,idx));
            cur = cur.next;
            idx++;
        }
        int[] ans = new int[idx];
        for (Node node : list) {
            ans[node.idx]=node.val;
        }
        return ans;
    }
}
