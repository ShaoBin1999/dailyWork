package com.bsren.niuke.tree;

import com.bsren.niuke.TreeNode;
import org.apache.commons.collections4.SplitMapUtils;
import org.apache.poi.ss.formula.functions.T;
import org.checkerframework.checker.units.qual.A;

import java.util.*;

public class Solution {


    public int[] preorderTraversal (TreeNode root) {
        // write code here
        Deque<TreeNode> nodes = new LinkedList<>();
        List<Integer> list = new ArrayList<>();
        TreeNode node = root;
        while (!nodes.isEmpty() || node!=null){
            while (node!=null){
                list.add(node.val);
                nodes.addLast(node);
                node = node.left;
            }
            node = nodes.removeLast();
            node = node.right;
        }
        int[] ans = new int[list.size()];
        for (int i=0;i<ans.length;i++){
            ans[i] = list.get(i);
        }
        return ans;
    }

    public int[] inorderTraversal (TreeNode root) {
        // write code here
        if(root==null){
            return new int[0];
        }
        List<Integer> list = new ArrayList<>();
        Deque<TreeNode> deque = new LinkedList<>();
        TreeNode node = root;
        while (!deque.isEmpty() || node!=null){
            while (node!=null){
                deque.addLast(node);
                node = node.left;
            }
            node = deque.removeLast();
            list.add(node.val);
            node = node.right;
        }
        int[] ans = new int[list.size()];
        for (int i=0;i<list.size();i++){
            ans[i] = list.get(i);
        }
        return ans;
    }


    public int[] postorderTraversal (TreeNode root) {
        // write code here
        List<Integer> list = new ArrayList<>();
        fun1(root,list);
        int[] ans = new int[list.size()];
        for (int i=0;i<list.size();i++){
            ans[i] = list.get(i);
        }
        return ans;
    }

    private void fun1(TreeNode root, List<Integer> list) {
        if(root==null){
            return;
        }
        fun1(root.left,list);
        fun1(root.right,list);
        list.add(root.val);
    }

    public ArrayList<ArrayList<Integer>> levelOrder (TreeNode root) {
        // write code here
        ArrayList<ArrayList<Integer>> list = new ArrayList<>();
        if(root==null){
            return list;
        }
        TreeNode node = root;
        Deque<TreeNode> deque = new LinkedList<>();
        deque.add(node);
        while (!deque.isEmpty()){
            ArrayList<Integer> l = new ArrayList<>();
            int size = deque.size();
            for (int i=0;i<size;i++){
                TreeNode poll = deque.poll();
                l.add(poll.val);
                if(poll.left!=null){
                    deque.addLast(poll.left);
                }
                if(poll.right!=null){
                    deque.addLast(poll.right);
                }
            }
            list.add(l);
        }
        return list;
    }

    public ArrayList<ArrayList<Integer>> Print(TreeNode pRoot) {
        ArrayList<ArrayList<Integer>> l = new ArrayList<>();
        if(pRoot==null){
            return l;
        }
        boolean f = false;
        Deque<TreeNode> deque = new LinkedList<>();
        deque.add(pRoot);
        while (!deque.isEmpty()){
            int size = deque.size();
            ArrayList<Integer> arrayList = new ArrayList<>();
            for (int i=0;i<size;i++){
                TreeNode poll = deque.poll();
                arrayList.add(poll.val);
                if(poll.left!=null){
                    deque.addLast(poll.left);
                }
                if(poll.right!=null){
                    deque.addLast(poll.right);
                }
            }
            if(f){
                Collections.reverse(arrayList);
            }
            f = !f;
            l.add(arrayList);
        }
        return l;
    }

    public int maxDepth (TreeNode root) {
        // write code here
        if(root==null){
            return 0;
        }
        int left = maxDepth(root.left);
        int right = maxDepth(root.right);
        return 1+Math.max(left,right);
    }

    public boolean hasPathSum (TreeNode root, int sum) {
        // write code here
        if(root==null){
            return false;
        }
        if(root.val==sum){
            if(root.left==null && root.right==null){
                return true;
            }
        }
        boolean left,right;
        left = hasPathSum(root.left,sum-root.val);
        right = hasPathSum(root.right,sum-root.val);
        return left || right;
    }

    public TreeNode Convert(TreeNode pRootOfTree) {
        if(pRootOfTree==null){
            return null;
        }
        Deque<TreeNode> deque = new LinkedList<>();
        TreeNode node = pRootOfTree;
        List<TreeNode> list = new ArrayList<>();
        while (!deque.isEmpty() || node!=null){
            while (node!=null){
                deque.addLast(node);
                node = node.left;
            }
            node = deque.removeLast();
            list.add(node);
            node = node.right;
        }
        int size = list.size();
        for (int i=0;i<size;i++){
            list.get(i).right = i+1<size?list.get(i+1):null;
            list.get(i).left = i>0? list.get(i-1):null;
        }
        return list.get(0);
    }

    boolean isSymmetrical(TreeNode pRoot) {
        if(pRoot==null){
            return true;
        }
        return fun2(pRoot.left,pRoot.right);
    }

    private boolean fun2(TreeNode left, TreeNode right) {
        if(left==null && right==null){
            return true;
        }
        else if(left!=null && right!=null){
            return left.val==right.val && fun2(left.left,right.right) && fun2(left.right,right.left);
        }else {
            return false;
        }
    }

    public TreeNode mergeTrees (TreeNode t1, TreeNode t2) {
        // write code here
        if(t1==null){
            return t2;
        }
        else if(t2==null){
            return t1;
        }
        t1.val +=t2.val;
        t1.left = mergeTrees(t1.left,t2.left);
        t1.right = mergeTrees(t1.right,t2.right);
        return t1;
    }

    public TreeNode Mirror (TreeNode pRoot) {
        // write code here
        if(pRoot==null){
            return null;
        }
        TreeNode mirror = Mirror(pRoot.right);
        pRoot.right = Mirror(pRoot.left);
        pRoot.left = mirror;
        return pRoot;
    }

    public boolean isValidBST (TreeNode root) {
        // write code here
        if(root==null){
            return true;
        }
        return isValidBST(root.left,Long.MIN_VALUE,root.val)
                && isValidBST(root.right,root.val,Long.MAX_VALUE);
    }

    public boolean isValidBST(TreeNode node,long min,long max){
        if(node==null){
            return true;
        }
        return node.val>min && node.val<max && isValidBST(node.left,min,node.val)
                    && isValidBST(node.right,node.val,max);
    }


    public static void main(String[] args) {
        TreeNode n1 = new TreeNode(1);
        TreeNode n2 = new TreeNode(2);
        TreeNode n3 = new TreeNode(3);
        TreeNode n4 = new TreeNode(4);
        TreeNode n5 = new TreeNode(5);
        TreeNode n6 = new TreeNode(6);
        TreeNode n7 = new TreeNode(7);
        n1.left = n2;
        n1.right = n3;
        n2.left = n4;
        n2.right = n5;
        n3.left = n6;
        n3.right = n7;
        String serialize = new Solution().Serialize(n1);
        System.out.println(serialize);
        TreeNode treeNode = new Solution().Deserialize(serialize);
        System.out.println(treeNode);
    }
    int ans = 0;
    int dfs(TreeNode root, int idx){
        if(root == null) return 0;
        ans = Math.max(ans, idx);
        //遍历左右子树
        return 1 + dfs(root.left, idx * 2) + dfs(root.right, idx * 2 + 1);
    }
    public boolean isCompleteTree (TreeNode root) {
        // dfs判断节点的序号是否等于编号
        return dfs(root, 1) == ans;
    }

    public boolean IsBalanced_Solution(TreeNode root) {
        return deep(root) != -1;
    }
    public int deep(TreeNode node){
        if(node==null) return 0;
        int left=deep(node.left);
        if(left == -1 ) return -1;
        int right=deep(node.right);
        if(right == -1 ) return -1;

        //两个子节点深度之差小于一
        if((left-right)>1 || (right-left)>1){
            return -1;
        }
        //父节点需要向自己的父节点报告自己的深度
        return (Math.max(left, right))+1;
    }

    public int lowestCommonAncestor (TreeNode root, int p, int q){
        // write code here
        if(p==root.val){
            return p;
        }else if(q==root.val){
            return q;
        }
        if(p<root.val && q<root.val) {
            return lowestCommonAncestor(root.left, p, q);
        }
        else if(p>root.val && q> root.val){
            return lowestCommonAncestor(root.right,p,q);
        }
        else {
            return root.val;
        }
    }

    public int lowestCommonAncestor1 (TreeNode root, int o1, int o2) {
        return CommonAncestor(root, o1, o2).val;
    }

    public TreeNode CommonAncestor (TreeNode root, int o1, int o2) {
        if (root == null || root.val == o1 || root.val == o2) { // 如果root为空，或者root为o1、o2中的一个，则它们的最近公共祖先就为root
            return root;
        }

        TreeNode left = CommonAncestor(root.left,o1,o2);    // 递归遍历左子树，只要在左子树中找到了o1或o2，则先找到谁就返回谁
        TreeNode right = CommonAncestor(root.right,o1,o2);  // 递归遍历右子树，只要在右子树中找到了o1或o2，则先找到谁就返回谁
        if (left == null) {  // 如果在左子树中o1和o2都找不到，则o1和o2一定都在右子树中，右子树中先遍历到的那个就是最近公共祖先（一个节点也可以是它自己的祖先）
            return right;
        }else if (right == null) { // 否则，如果left不为空，在左子树中有找到节点（o1或o2），这时候要再判断一下右子树中的情况，
            // 如果在右子树中，o1和o2都找不到，则 o1和o2一定都在左子树中，左子树中先遍历到的那个就是最近公共祖先（一个节点也可以是它自己的祖先）
            return left;
        }else{
            return root; // 否则，当 left和 right均不为空时，说明 o1、o2节点分别在 root异侧, 最近公共祖先即为 root
        }
    }

    Map<Integer, TreeNode> parent = new HashMap<Integer, TreeNode>();
    Set<Integer> visited = new HashSet<>();

    public void dfs(TreeNode root) {
        if (root.left != null) {
            parent.put(root.left.val, root);
            dfs(root.left);
        }
        if (root.right != null) {
            parent.put(root.right.val, root);
            dfs(root.right);
        }
    }

    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        dfs(root);
        while (p != null) {
            visited.add(p.val);
            p = parent.get(p.val);
        }
        while (q != null) {
            if (visited.contains(q.val)) {
                return q;
            }
            q = parent.get(q.val);
        }
        return null;
    }

    boolean f = false;
    List<List<TreeNode>> l = new ArrayList<>();
    public int lowestCommonAncestor2 (TreeNode root, int o1, int o2) {
        fun4(root,o1,new ArrayList<>());
        f = false;
        fun4(root,o2,new ArrayList<>());
        int i = 0;
        List<TreeNode> l1 = l.get(0);
        List<TreeNode> l2 = l.get(1);
        int size = Math.min(l1.size(),l2.size());
        for (;i<size;i++){
            if(l1.get(i)!=l2.get(i)){
                break;
            }
        }
        return l1.get(i-1).val;
    }


    public void fun4(TreeNode treeNode,int o,List<TreeNode> list){
        if(treeNode==null || f){
            return;
        }
        list.add(treeNode);
        if(treeNode.val==o){
            l.add(new ArrayList<>(list));
            f = true;
        }
        fun4(treeNode.left,o,list);
        fun4(treeNode.right,o,list);
        list.remove(list.size()-1);
    }

    String Serialize(TreeNode root) {
        if(root==null){
            return "";
        }
        Deque<TreeNode> deque = new LinkedList<>();
        deque.add(root);
        List<String> list = new ArrayList<>();
        while (!deque.isEmpty()){
            int size = deque.size();
            for (int i=0;i<size;i++){
                TreeNode poll = deque.removeFirst();
                list.add(poll==null? "#":String.valueOf(poll.val));
                if(poll!=null) {
                    deque.add(poll.left);
                    deque.add(poll.right);
                }
            }
        }
        int size = list.size();
        while (list.get(size-1).equals("#")){
            size--;
        }
        return String.join(",",list.subList(0,size));
    }
    TreeNode Deserialize(String str) {
        if(str.equals("")){
            return null;
        }
        String[] strings = str.split(",");
        TreeNode head = new TreeNode(Integer.parseInt(strings[0]));
        List<TreeNode> list = new ArrayList<>();
        list.add(head);
        int cnt = 0;
        int ind = 0;// 1,2,3,4,5,6,7
        while (ind<strings.length){
            int add = list.size()-cnt;
            int preSize = list.size();
            for (int i=0;i<add;i++){
                TreeNode parent = list.get(i + cnt);
                int c = ind+2*i+1;
                if(c>=strings.length){
                    break;
                }
                if(!strings[c].equals("#")){
                    TreeNode T = new TreeNode(Integer.parseInt(strings[c]));
                    parent.left = T;
                    list.add(T);
                }
                c+=1;
                if(c>=strings.length){
                    break;
                }
                if(!strings[c].equals("#")){
                    TreeNode T = new TreeNode(Integer.parseInt(strings[c]));
                    parent.right = T;
                    list.add(T);
                }
            }
            cnt = preSize;
            ind += add*2;
        }
        return head;
    }


    public TreeNode reConstructBinaryTree(int [] pre,int [] vin) {
        int m = pre.length;
        int n = vin.length;
        if(m==0 || n==0){
            return null;
        }
        int head = pre[0];
        TreeNode node = null;
        for (int i=0;i<n;i++){
            if(vin[i]==head){
                node = new TreeNode(head);
                node.left = reConstructBinaryTree(Arrays.copyOfRange(pre,1,i+1),Arrays.copyOfRange(
                        vin,0,i
                ));
                node.right = reConstructBinaryTree(Arrays.copyOfRange(pre,i+1,m),Arrays.copyOfRange(
                        vin,i+1,n
                ));
            }
        }
        return node;
    }


    public int[] solve (int[] xianxu, int[] zhongxu) {
        // write code here
        TreeNode treeNode = reConstructBinaryTree(xianxu, zhongxu);
        List<Integer> list = new ArrayList<>();
        Deque<TreeNode> deque = new LinkedList<>();
        deque.addLast(treeNode);
        while (!deque.isEmpty()){
            int size = deque.size();
            for (int i=0;i<size;i++){
                TreeNode node = deque.removeFirst();
                if(node.left!=null){
                    deque.addLast(node.left);
                }
                if(node.right!=null){
                    deque.addLast(node.right);
                }
                if(i==size-1){
                    list.add(node.val);
                }
            }
        }
        int[] ans = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            ans[i] = list.get(i);
        }
        return ans;
    }
}
