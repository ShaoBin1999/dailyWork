package com.bsren.niuke.node;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

public class N1 {

    public ListNode ReverseList(ListNode head) {
        ListNode tail = null;
        ListNode next;
        while (head!=null){
            next = head.next;
            head.next = tail;
            tail = head;
            head = next;
        }
        return tail;
    }

    public ListNode reverseBetween (ListNode head, int m, int n) {
        ListNode begin = new ListNode(0);
        begin.next = head;
        ListNode ans = begin;
        for (int i=1;i<m;i++){
            begin = begin.next;
        }
        ListNode next = begin.next;
        ListNode end = begin;
        for (int i=0;i<=n-m;i++){
            end = end.next;
        }
        ListNode next1 = end.next;
        end.next = null;
        begin.next = ReverseList(next);
        next.next = next1;
        return ans.next;
    }

    public ListNode reverseKGroup (ListNode head, int k) {
        //找到每次翻转的尾部
        ListNode tail = head;
        //遍历k次到尾部
        for(int i = 0; i < k; i++){
            //如果不足k到了链表尾，直接返回，不翻转
            if(tail == null)
                return head;
            tail = tail.next;
        }
        //翻转时需要的前序和当前节点
        ListNode pre = null;
        ListNode cur = head;
        //在到达当前段尾节点前
        while(cur != tail){
            //翻转
            ListNode temp = cur.next;
            cur.next = pre;
            pre = cur;
            cur = temp;
        }
        //当前尾指向下一段要翻转的链表
        head.next = reverseKGroup(tail, k);
        return pre;
    }

    public ListNode reverseWithEnd(ListNode head,ListNode end){
        ListNode tail = end.next;
        ListNode next;
        while (head!=null){
            next = head.next;
            head.next = tail;
            tail = head;
            head = next;
        }
        return tail;
    }

    public ListNode Merge(ListNode list1,ListNode list2) {
        //一个已经为空了，直接返回另一个
        if(list1 == null)
            return list2;
        if(list2 == null)
            return list1;
        //加一个表头
        ListNode head = new ListNode(0);
        ListNode cur = head;
        //两个链表都要不为空
        while(list1 != null && list2 != null){
            //取较小值的节点
            if(list1.val <= list2.val){
                cur.next = list1;
                //只移动取值的指针
                list1 = list1.next;
            }else{
                cur.next = list2;
                //只移动取值的指针
                list2 = list2.next;
            }
            //指针后移
            cur = cur.next;
        }
        //哪个链表还有剩，直接连在后面
        if(list1 != null)
            cur.next = list1;
        else
            cur.next = list2;
        //返回值去掉表头
        return head.next;
    }

    public ListNode mergeKLists(ArrayList<ListNode> lists) {
        ListNode cur = new ListNode(0);
        ListNode ans = cur;
        PriorityQueue<ListNode> q = new PriorityQueue<>(new Comparator<ListNode>() {
            @Override
            public int compare(ListNode o1, ListNode o2) {
                return o1.val-o2.val;
            }
        });
        for (ListNode list : lists) {
            if(list!=null){
                q.add(list);
            }
        }
        ListNode poll;
        while ((poll=q.poll())!=null){
            if(poll.next!=null){
                q.add(poll.next);
            }
            cur.next = poll;
            cur = cur.next;
        }
        cur.next = null;
        return ans.next;
    }

    public ListNode FindKthToTail (ListNode pHead, int k) {
        // write code here
        if(pHead==null){
            return null;
        }
        ListNode begin = pHead;
        ListNode end = pHead;
        for (int i=0;i<k;i++){
            end = end.next;
            if(end==null){
                if(i==k-1){
                    return pHead;
                }else return null;
            }
        }
        while (end!=null){
            end = end.next;
            begin = begin.next;
        }
        return begin;
    }

    public ListNode removeNthFromEnd (ListNode head, int n) {
        // write code here
        if(head==null){
            return null;
        }
        ListNode c = head;
        int k=0;
        while (c!=null){
            c = c.next;
            k++;
        }
        if(k==n){
            return head.next;
        }
        c = head;
        for (int i=1;i<=k-n-1;i++){
            c = c.next;
        }
        c.next = c.next.next;
        return head;
    }

    public ListNode FindFirstCommonNode(ListNode pHead1, ListNode pHead2) {
        if(pHead1==null || pHead2==null){
            return null;
        }
        int l1 = 0, l2 = 0;
        ListNode l = pHead1;
        while (l!=null){
            l = l.next;
            l1++;
        }
        l = pHead2;
        while (l!=null){
            l = l.next;
            l2++;
        }
        l = pHead1;
        ListNode node = pHead2;
        for (int i=0;i<l1+l2;i++){
            if(l==node){
                return node;
            }
            l = l.next;
            node = node.next;
            if(l==null){
                l = pHead2;
            }
            if(node==null){
                node = pHead1;
            }
        }
        return null;
    }


    public static void main(String[] args) {
        ListNode l1 = new ListNode(1);
        ListNode l2 = new ListNode(2);
        ListNode l3 = new ListNode(2);
        ListNode l4 = new ListNode(4);
        ListNode l5 = new ListNode(4);
        l1.next = l2;
        l2.next = l3;
        l3.next = l4;
        l4.next = l5;
        ListNode node = new N1().deleteDuplicates1(l3);
        while (node!=null){
            System.out.println(node.val);
            node = node.next;
        }
    }

    public ListNode addInList (ListNode head1, ListNode head2) {
        // write code here
        head1 = ReverseList(head1);
        head2 = ReverseList(head2);
        ListNode re = new ListNode(0);
        ListNode l = re;
        int add=0;
        while (head1!=null && head2!=null){
            add += head1.val+head2.val;
            l.next = new ListNode(add%10);
            add/=10;
            head1 = head1.next;
            head2 = head2.next;
            l = l.next;
        }
        if(head1==null){
            while (head2!=null){
                add += head2.val;
                l.next = new ListNode(add%10);
                add/=10;
                head2 = head2.next;
                l = l.next;
            }
        }
        if(head2==null){
            while (head1!=null){
                add +=head1.val;
                l.next = new ListNode(add%10);
                add/=10;
                head1 = head1.next;
                l = l.next;
            }
        }
        if(add!=0){
            l.next = new ListNode(1);
        }
        return ReverseList(re.next);
    }

    public ListNode sortInList (ListNode head) {
        // write code here
        PriorityQueue<ListNode> p = new PriorityQueue<>(new Comparator<ListNode>() {
            @Override
            public int compare(ListNode o1, ListNode o2) {
                return o1.val - o2.val;
            }
        });
        ListNode re = new ListNode(0);
        ListNode cur = re;
        while (head!=null){
            p.add(head);
            head = head.next;
        }
        ListNode poll = null;
        while ((poll=p.poll())!=null){
            cur.next = poll;
            cur = cur.next;
        }
        cur.next = null;
        return re.next;
    }

    public boolean isPail (ListNode head) {
        // write code here
        if(head==null || head.next==null){
            return true;
        }
        ListNode l1 = head;
        ListNode l2 = head;
        while (l2.next!=null && l2.next.next!=null){
            l1 = l1.next;
            l2 = l2.next.next;
        }
        l2 = ReverseList(l1.next);
        while (l2!=null && head!=null){
            if(head.val!=l2.val){
                return false;
            }
            head = head.next;
            l2 = l2.next;
        }
        return true;
    }

    public ListNode oddEvenList (ListNode head) {
        // write code here
        if(head==null || head.next==null){
            return head;
        }
        ListNode even = head.next;
        ListNode odd = head;
        ListNode next = even.next;
        ListNode o = even;
        boolean f = true;
        while (next!=null){
            ListNode next1 = next.next;
            if(f){
                o.next = next1;
                o = next.next;
                next.next = even;
                odd.next = next;
                odd = odd.next;
            }
            f = !f;
            next = next1;
        }
        return head;
    }

    public ListNode deleteDuplicates (ListNode head) {
        // write code here
        if(head==null){
            return null;
        }
        ListNode cur = head;
        ListNode n = cur.next;
        while (n!=null){
            while (n!=null && n.val==cur.val){
                n = n.next;
            }
            if(n==null){
                cur.next = null;
                return head;
            }
            cur.next = n;
            cur = cur.next;
        }
        return head;
    }

    public ListNode deleteDuplicates1 (ListNode head) {
        // write code here
        if(head==null || head.next==null){
            return head;
        }
        ListNode dump = new ListNode(0);
        ListNode c = dump;
        ListNode cur = head.next;
        while (cur!=null){
            if(cur.val==head.val){
                while (cur!=null && cur.val==head.val){
                    cur = cur.next;
                }
                if(cur==null){
                    c.next = null;
                    return dump.next;
                }
            }else {
                c.next = head;
                c = c.next;
            }
            head = cur;
            cur = cur.next;
        }
        if(head.next==null){
            c.next = head;
        }else {
            c.next = null;
        }
        return dump.next;
    }

    public ListNode EntryNodeOfLoop(ListNode head) {
        if(head==null){
            return null;
        }
        ListNode slow = head;
        ListNode fast = head;
        boolean f = false;
        while (fast.next!=null && fast.next.next!=null){
            slow = slow.next;
            fast = fast.next.next;
            if(slow==fast){
                f = true;
                break;
            }
        }
        if(f){
            ListNode c = head;
            while (c!=slow){
                c = c.next;
                slow = slow.next;
            }
            return slow;
        }
        return null;
    }

    public boolean hasCycle(ListNode head) {
        if(head==null){
            return false;
        }
        ListNode slow = head;
        ListNode fast = head;
        while (fast.next!=null && fast.next.next!=null){
            slow = slow.next;
            fast = fast.next.next;
            if(slow==fast){
                return true;
            }
        }
        return false;
    }
}



