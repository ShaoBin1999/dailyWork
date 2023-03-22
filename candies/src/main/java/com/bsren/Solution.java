package com.bsren;

import com.bsren.niuke.TreeNode;
import io.netty.buffer.ByteBuf;
import org.springframework.boot.test.json.GsonTester;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

public class Solution {

//    public static void main(String[] args) {
//        Scanner sc = new Scanner(System.in);
//        int n = sc.nextInt();
//        int l = sc.nextInt();
//        int[][] list = new int[n][2];
//        for (int i = 0; i < n; i++) {
//            list[i][0] = sc.nextInt();
//            list[i][1] = sc.nextInt();
//        }
//        Arrays.sort(list, (o1, o2) -> {
//            if(o1[0]==o2[0]){
//                return o2[1]-o1[1];
//            }else {
//                return o1[0]-o2[0];
//            }
//        });
//        if(list[0][0]!=0){
//            System.out.println("-1");
//            return;
//        }
//        int r = list[0][1];
//        int cur = 1;
//        int ans = 1;
//        while (r<l){
//            int max = r;
//            boolean find = false;
//            for (int i = cur;i<n;i++){
//                if(list[i][1]<=r){
//                    continue;
//                }
//                if(list[i][0]>r){
//                    break;
//                }
//                if(max<list[i][1]){
//                    find = true;
//                    cur = i+1;
//                    max = list[i][1];
//                }
//            }
//            if(!find){
//                break;
//            }else {
//                r = max;
//                ans++;
//            }
//        }
//        if(r<l){
//            System.out.println(-1);
//        }else {
//            System.out.println(ans);
//        }
//    }

    int minimumRestDays(int[] company, int[] gym) {
        // j: 0休息 1工作 2健身
        // f[i][j] 截止到前i天, 第i天做选择j时的最小休息日
        int n = company.length;
        int[][] f = new int[n][3];
        for (int i=0;i<n;i++){
            Arrays.fill(f[i],Integer.MAX_VALUE);
        }
        f[0][0] = 1;
        if (company[0] == 1) f[0][1] = 0;
        if (gym[0] == 1) f[0][2] = 0;

        for(int i=1;i<n;i++) {
            if(company[i] == 1) {
                // 因为不会连续两天工作或锻炼, 所以前一天一定是另外的选择
                f[i][1] = Math.min(f[i-1][0], f[i-1][2]);
            }
            if(gym[i] == 1) {
                f[i][2] = Math.min(f[i-1][0], f[i-1][1]);
            }

            // 无条件可以选择休息
            f[i][0] = Math.min(f[i - 1][2], Math.min(f[i - 1][0], f[i - 1][1])) + 1;
        }

        return Math.min(f[n-1][0], Math.min(f[n-1][1], f[n-1][2]));
    }

    public int findUnsortedSubarray(int[] nums) {
        if(nums.length==1){
            return 0;
        }
        Deque<int[]>deque = new LinkedList<>();
        deque.add(new int[]{nums[0],0});
        int left = nums.length;
        for (int i = 1; i < nums.length; i++) {
            while (!deque.isEmpty() && deque.getLast()[0]>nums[i]){
                int[] last = deque.removeLast();
                left = Math.min(left,last[1]);
            }
            deque.addLast(new int[]{nums[i],i});
        }
        deque.clear();
        int right = 0;
        deque.add(new int[]{nums[nums.length-1],nums.length-1});
        for (int i = nums.length-2; i>=0;i--) {
            while (!deque.isEmpty() && deque.getLast()[0]<nums[i]){
                int[] last = deque.removeLast();
                right = Math.max(right,last[1]);
            }
            deque.addLast(new int[]{nums[i],i});
        }
        if(right>=left){
            return right-left+1;
        }
        return 0;
    }

    public String compress (String str) {
        // write code here
        int x = -1;//第一个"]"对应的"["的索引
        int y = -1;//"|"的索引
        int z = -1;//第一个"]"的索引
        int index = 0;
        String result = str;
        while (index <str.length()){
            if(str.charAt(index) == '['){
                x = index;
            }else if(str.charAt(index) == '|'){
                y = index;
            }else if(str.charAt(index) == ']'){
                z = index;//如果找到']'，说明已经找到了第一个[m|S];此时退出循环，进行解压缩
                break;
            }
            index++;
        }
        if(x != -1){//x不等于-1说明存在待解压的字符串。
            String temp = str.substring(y+1,z);
            int count = Integer.parseInt(str.substring(x+1,y));
            String resultTemp = String.join("",Collections.nCopies(count,temp));
            result = str.substring(0,x) + resultTemp + str.substring(z+1);
            result = compress(result);//递归解压缩。
        }
        return result;
    }

    public int[] findBuilding (int[] heights) {
        // write code here
        int n = heights.length;
        int[] left = new int[n];
        int[] right = new int[n];
        Stack<Integer> d1 = new Stack<>();
        Stack<Integer> d2 = new Stack<>();
        for (int i=0;i<n;i++){
            left[i] = d1.size();
            while (!d1.isEmpty() && d1.peek()<=heights[i]){
                d1.pop();
            }
            d1.push(heights[i]);
        }
        for (int i=n-1;i>=0;i--){
            right[i] = d2.size();
            while (!d2.isEmpty() && d2.peek()<=heights[i]){
                d2.pop();
            }
            d2.push(heights[i]);
        }
        int[] ans = new int[n];
        for (int i=0;i<n;i++){
            ans[i] = 1+left[i]+right[i];
        }
        return ans;
    }


    public int minOperations (String str) {
        // write code here

        int[] chars = new int[26];
        for (char c : str.toCharArray()) {
            int v = c-'a';
            chars[v]++;
        }
        int ans = 0;
        Arrays.sort(chars);
        int j = 0;
        for (int i=0;i<26;i++){
            if(chars[i]==0 || chars[i]==1){
            }else if(chars[i]==2){
                ans+=1;
            }else {
                while (chars[j]==0 && chars[i]>=3){
                    chars[i]-=2;
                    chars[j]+=1;
                    ans+=1;
                    j++;
                }
                if(chars[i]>=2){
                    ans+=(chars[i]-1);
                    chars[i] = 1;
                }
            }
        }
        return ans;
    }

    int getTreeSum(TreeNode node){
        int ans = 1;
        int k = height(node);
        int mod = 1000000007;
        while (k-->0){
            ans*=2;
            ans%=mod;
        }
        return ans-1;
    }

    private int height(TreeNode node) {
        if(node==null){
            return 0;
        }
        return 1+Math.max(height(node.right),height(node.left));
    }

    public int minOperations1 (String str) {
        // write code here
        int ans = Integer.MAX_VALUE;
        int cur = 0;
        for (int i=0;i<str.length();i++){
            char c = str.charAt(i);
            if(c=='0'){
                cur++;
                i = i+1;
            }
        }
        ans = cur;
        cur = 0;
        for (int i=0;i<str.length();i++){
            char c = str.charAt(i);
            if(c=='1'){
                cur++;
                i = i+1;
            }
        }
        ans = Math.min(ans,cur);
        return ans;

    }
    public int getSubarrayNum (ArrayList<Integer> a, int x) {
        int mod = 1000000007;
        // write code here
        int[][] l = new int[a.size()][2];
        for (int i = 0; i < a.size(); i++) {
            Integer integer = a.get(i);
            int n2 = 0, n5 = 0;
            while (integer%2==0){
                n2+=1;
                integer = integer>>>1;
            }
            while (integer%5==0){
                n5+=1;
                integer = integer/5;
            }
            l[i][0] = n2;
            l[i][1] = n5;
        }
        int[][] pre = new int[a.size()+1][2];
        for (int i=1;i<=a.size();i++){
            pre[i][0]=pre[i-1][0]+l[i-1][0];
            pre[i][1]=pre[i-1][1]+l[i-1][1];
        }
        int ans = 0;
        for (int i=0;i<a.size();i++){
            for (int j=i+1;j<=a.size();j++){
                int n2 = pre[j][0]-pre[i][0];
                int n5 = pre[j][1]-pre[i][1];
                if(Math.min(n2,n5)>=x){
                    ans = (ans+a.size()-j+1)%mod;
                    break;
                }
            }
        }
        return ans;
    }

    public int maxArea(int[] height) {
        int num = height.length;
        int left = 0,right = num-1;
        int l = height[0],r = height[num-1];
        int ans = (right-left)*Math.min(l,r);
        while (left<right){
            while (left<right && height[left]<=l){
                left++;
            }
            l = height[left];
            ans =Math.max (ans,(right-left)*Math.min(l,r));
            while (left<right && height[right]<=r){
                right--;
            }
            r = height[right];
            ans =Math.max (ans,(right-left)*Math.min(l,r));
        }
        return ans;
    }

    public int bestTeamScore(int[] scores, int[] ages) {
        int len = scores.length;
        int[][] dp = new int[len][2];
        for (int i = 0; i < len; i++) {
            dp[i] = new int[]{scores[i],ages[i]};
        }
        Arrays.sort(dp, new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2) {
                if(o1[0]==o2[0]){
                    return o1[1]-o2[1];
                }else {
                    return o1[0]-o2[0];
                }
            }
        });
        int[] d = new int[len];
        int ans = 0;
        for (int i=0;i<len;i++){
            for (int j = i-1;j>=0;j--){
                if(dp[j][1]<=dp[i][1]){
                    d[i] = Math.max(d[i],d[j]);
                }
            }
            d[i]+=dp[i][0];
            ans = Math.max(ans,d[i]);
        }
        return ans;
    }


    //小红拿到了一个数组
    //每次操作小红可以选择数组中的任意一个数减去
    //小红一共能进行k 次。k 次操作之后，数组的最大值尽可能小。请你返回这个最大值。
    public int minMax (ArrayList<Integer> a, int k, int x) {
        // write code here
        PriorityQueue<Integer> p = new PriorityQueue<>(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o2-o1;
            }
        });
        p.addAll(a);
        while (k-->0){
            Integer poll = p.poll();
            poll = poll-x;
            p.add(poll);
        }
        return p.peek();
    }

    public int howMany (String S, int k) {
        // write code here
        int[] l = new int[26];
        int ans = 0;
        for (char c : S.toCharArray()) {
            int idx = c-'a';
            l[idx]++;
            if(l[idx]==k){
                ans+=1;
            }
        }
        return ans;
    }

    public TreeNode xorTree (TreeNode root, ArrayList<ArrayList<Integer>> op) {
        // write code here
        if(root==null){
            return null;
        }
        Map<Integer,TreeNode> nodeMap = new HashMap<>();
        Deque<TreeNode> nodes = new LinkedList<>();
        nodes.addLast(root);
        while (!nodes.isEmpty()){
            int size = nodes.size();
            for (int i=0;i<size;i++){
                TreeNode treeNode = nodes.removeFirst();
                nodeMap.put(treeNode.val,treeNode);
                treeNode.val = 0;
                if(treeNode.left!=null){
                    nodes.addLast(treeNode.left);
                }
                if(treeNode.right!=null){
                    nodes.addLast(treeNode.right);
                }
            }
        }
        for (ArrayList<Integer> list : op) {
            Integer val = list.get(1);
            TreeNode treeNode = nodeMap.get(list.get(0));
            fun(treeNode,val);
        }
        return root;
    }

    public void fun(TreeNode root,int val){
        if(root==null){
            return;
        }
        root.val = root.val ^ val;
        fun(root.left,val);
        fun(root.right,val);
    }

    public int minCnt (String s) {
        // write code here
        int ans= 0;
        for (char c : s.toCharArray()) {
            if(c=='1'){
                ans++;
            }
        }
        return ans-1;
    }

    public static void main(String[] args) {
        Solution s = new Solution();
        System.out.println(s.numsOfStrings(7, 3));
    }
    Map<Integer,Integer> map = new HashMap<>();
    public int numsOfStrings (int n, int k) {
        if(k==1){
            k=2;
        }
        // write code here
        fun(n,k,0,0);
        int ans = 0;
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            Integer key = entry.getKey();
            int v = (int) ((26*Math.pow(25,key-1))%1000000);
            ans = (ans+v)%10000000;
        }
        return ans;
    }

    private void fun(int n, int k, int cur,int step) {
        if(n==cur){
            map.put(step,map.getOrDefault(step,0)+1);
            return;
        }
        if(n-cur>=k){
            for (int i=0;i<=(n-cur-k);i++){
                fun(n,k,cur+k+i,step+1);
            }
        }
    }
}
