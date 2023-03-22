package com.bsren.leetcode;

import org.apache.commons.collections.EnumerationUtils;

import java.util.*;

class Good{
    int price;
    int coupon;

    public Good(int price, int coupon) {
        this.price = price;
        this.coupon = coupon;
    }
}

public class Main {

    public static void main(String[] args) {
        System.out.println(new Main().longestConsecutive(new int[]{0,3,7,2,5,8,4,6,0,1}));
    }


    public boolean checkPalindromeFormation(String a, String b) {
        int la = a.length();
        int lb = b.length();
        if(la==0 && lb==0){
            return true;
        } else if(la==0){
            return fun(b);
        }else if(lb==0){
            return fun(a);
        }
        int i,j = b.length()-1;
        for (i=0;i<j;i++){
            if(a.charAt(i)==b.charAt(j)){
                j--;
            }else {
                break;
            }
        }
        //aavca
        //cvvva
        if(fun(b.substring(i,j+1)) || fun(a.substring(i,j+1))){
            return true;
        }
        j = a.length()-1;
        for (i=0;i<j;i++){
            if(b.charAt(i)==a.charAt(j)){
                j--;
            }else {
                break;
            }
        }
        if(fun(a.substring(i,j+1)) || fun(b.substring(i,j+1))){
            return true;
        }
        return false;
    }

    private boolean fun(String b) {
        if(b==null || b.isEmpty()){
            return true;
        }
        int l = 0,r = b.length()-1;
        while (l<r){
            if(b.charAt(l)!=b.charAt(r)){
                return false;
            }
            l++;r--;
        }
        return true;
    }

    public int longestConsecutive(int[] nums) {
        HashMap<Integer,List<Integer>> map = new HashMap<>();
        for (int num : nums) {
            if(map.containsKey(num-1) && map.containsKey(num+1)){
                List<Integer> l1 = map.get(num - 1);
                List<Integer> l2 = map.get(num + 1);
                map.remove(num-1);
                map.remove(num+1);
                List<Integer> l = new ArrayList<>();
                l.add(l1.get(0));
                l.add(l2.get(1));
                map.put(l1.get(0),l);
                map.put(l2.get(1),l);
            }else if(map.containsKey(num-1)){
                List<Integer> list = map.get(num - 1);
                list.set(1,num);
                map.put(num,list);
                if(list.get(0)!=num-1){
                    map.remove(num-1);
                }
            }else if(map.containsKey(num+1)){
                List<Integer> list = map.get(num + 1);
                list.set(0,num);
                map.put(num,list);
                if(list.get(1)!=num+1){
                    map.remove(num+1);
                }
            }else if(!map.containsKey(num)){
                List<Integer> list = new ArrayList<>();
                list.add(num);
                list.add(num);
                map.put(num,list);
            }
        }
        int ans = 0;
        for (Map.Entry<Integer, List<Integer>> entry : map.entrySet()) {
            List<Integer> value = entry.getValue();
            ans = Math.max(ans,value.get(1)-value.get(0)+1);
        }
        return ans;
    }
//    public static void main(String[] args) {
//        int n,x,y;
//        Scanner sc = new Scanner(System.in);
//        n = sc.nextInt();
//        x = sc.nextInt();
//        y = sc.nextInt();
//        PriorityQueue<int[]> p = new PriorityQueue<>(new Comparator<int[]>() {
//            @Override
//            public int compare(int[] o1, int[] o2) {
//                if(o1[0]-o1[1]<o2[0]-o2[1]){
//                    return -1;
//                }else if(o1[0]-o1[1]==o2[0]-o2[1]){
//                    return o2[0]-o1[0];
//                }else {
//                    return 1;
//                }
//            }
//        });
//        for (int i = 0; i < n; i++) {
//            p.add(new int[]{sc.nextInt(),sc.nextInt()});
//        }
//        int num=0;
//        int money = 0;
//        while (!p.isEmpty()){
//            int[] remove = p.remove();
//            int add = 0;
//            if(y>0){
//                y--;
//                add = remove[0]-remove[1];
//            }else {
//                add = remove[0];
//            }
//            money+=add;
//            if(money>x){
//                money-=add;
//                break;
//            }
//            num++;
//        }
//        System.out.println(num+" "+money);
//    }
//    public static void main(String[] args) {
//        int n,x,y;
//        Scanner sc = new Scanner(System.in);
//        n = sc.nextInt();
//        x = sc.nextInt();
//        y = sc.nextInt();
//        PriorityQueue<int[]> p = new PriorityQueue<>(new Comparator<int[]>() {
//            @Override
//            public int compare(int[] o1, int[] o2) {
//                if(o1[0]-o1[1]<o2[0]-o2[1]){
//                    return -1;
//                }else if(o1[0]-o1[1]==o2[0]-o2[1]){
//                    return o2[0]-o1[0];
//                }else {
//                    return 1;
//                }
//            }
//        });
//        for (int i = 0; i < n; i++) {
//            p.add(new int[]{sc.nextInt(),sc.nextInt()});
//        }
//        int num=0;
//        int money = 0;
//        while (!p.isEmpty()){
//            int[] remove = p.remove();
//            int add = 0;
//            if(y>0){
//                y--;
//                add = remove[0]-remove[1];
//            }else {
//                add = remove[0];
//            }
//            money+=add;
//            if(money>x){
//                money-=add;
//                break;
//            }
//            num++;
//        }
//        System.out.println(num+" "+money);
//    }

//    public static void main(String[] args) {
//        Scanner sc = new Scanner(System.in);
//        String s = sc.nextLine();
//        int len = s.length();
//        if(len<=2){
//            System.out.println("aa");
//            return;
//        }
//        int head = 0,tail = len-1;
//        int mistake = 0;
//        List<Integer> list = new ArrayList<>();
//        while (head<tail){
//            if(s.charAt(head)!=s.charAt(tail)){
//                mistake++;
//                list.add(head);
//            }
//            head++;
//            tail--;
//        }
//        if(mistake==0){
//            int cur = 0;
//            while (cur<len){
//                if(s.charAt(cur)=='a'){
//                    cur++;
//                }else {
//                    break;
//                }
//            }
//            if(cur!=len){
//                String out = s.substring(0,cur)+'a'+s.substring(cur+1,len-cur-1)+'a'
//                        +s.substring(len-cur,len);
//                System.out.println(out);
//            }
//        }
//        else if(mistake==1){
//            int cur = list.get(0);
//            char c = s.charAt(cur);
//            char c1 = s.charAt(len-cur-1);
//            String out = s.substring(0,cur)+'a'+s.substring(cur+1,len-cur-1)+'a'
//                    +s.substring(len-cur,len);
//            if(c!='a' && c1!='a'){
//                System.out.println(out);
//            }else{
//                if(len%2==1){
//                    StringBuilder sb = new StringBuilder();
//                    sb.append(out.substring(0,len/2)).append("a").append(out.substring(len/2+1));
//                    System.out.println(sb);
//                }
//            }
//        }else if(mistake==2){
//            int ind1 = list.get(0);
//            int ind2 = list.get(1);
//            int ind11 = len-1-ind1;
//            int ind22 = len-1-ind2;
//            char c;
//            if(s.charAt(ind1)<s.charAt(ind11)){
//                c = s.charAt(ind1);
//            }else {
//                c = s.charAt(ind11);
//            }
//            char b;
//            if(s.charAt(ind2)<s.charAt(ind22)){
//                b = s.charAt(ind2);
//            }else b = s.charAt(ind22);
//            StringBuilder sb = new StringBuilder();
//            for (int i=0;i<len;i++){
//                if(i==ind1 || i==ind11){
//                    sb.append(c);
//                }else if(i==ind2 || i==ind22){
//                    sb.append(b);
//                }else {
//                    sb.append(s.charAt(i));
//                }
//            }
//            System.out.println(sb);
//        }
//    }

//    public static void main(String[] args) {
//        Scanner sc = new Scanner(System.in);
//        int len = sc.nextInt();
//        int k = sc.nextInt();
//        int[] colors = new int[len];
//        for (int i = 0; i < len; i++) {
//            colors[i] = sc.nextInt();
//        }
//        Deque<int[]> deque = new LinkedList<>();
//        HashMap<Integer,Integer> set = new HashMap<>();
//        int cur = 0;
//        while (cur<colors.length){
//            set.put(colors[cur],set.getOrDefault(colors[cur],0)+1);
//            deque.addLast(new int[]{colors[cur],cur});
//            cur++;
//            if(set.size()==k){
//                break;
//            }
//        }
//        int ans = deque.size();
//        if(cur==colors.length){
//            System.out.println(cur);
//        }else {
//            for (;cur<colors.length;cur++){
//                if (!set.containsKey(colors[cur])) {
//                    while (set.size() == k) {
//                        int[] ints = deque.removeFirst();
//                        int color = ints[0];
//                        set.put(color, set.get(color) - 1);
//                        if (set.get(color) == 0) {
//                            set.remove(color);
//                        }
//                    }
//                }
//                set.put(colors[cur],set.getOrDefault(colors[cur],0)+1);
//                deque.addLast(new int[]{colors[cur], cur});
//                ans = Math.max(ans,deque.size());
//            }
//            System.out.println(ans);
//        }
//    }

    public List<List<Integer>> combinationSum(int[] candidates, int target) {
        int len = candidates.length;
        List<List<Integer>> res = new ArrayList<>();
        if (len == 0) {
            return res;
        }

        // 排序是剪枝的前提
        Arrays.sort(candidates);
        Deque<Integer> path = new ArrayDeque<>();
        dfs(candidates, 0, len, target, path, res);
        return res;
    }

    private void dfs(int[] candidates, int begin, int len, int target, Deque<Integer> path, List<List<Integer>> res) {
        // 由于进入更深层的时候，小于 0 的部分被剪枝，因此递归终止条件值只判断等于 0 的情况
        if (target == 0) {
            res.add(new ArrayList<>(path));
            return;
        }

        for (int i = begin; i < len; i++) {
            // 重点理解这里剪枝，前提是候选数组已经有序，
            if (target - candidates[i] < 0) {
                break;
            }

            path.addLast(candidates[i]);
            dfs(candidates, i, len, target - candidates[i], path, res);
            path.removeLast();
        }
    }
}
