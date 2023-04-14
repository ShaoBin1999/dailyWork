package com.bsren;

import java.util.*;

public class SHuawei {

//    public static void main(String[] args) {
//
////        int[] s = new int[]{1,4,2,5,5,1,6};
////        System.out.println(fun(s,2));
//        Scanner scanner = new Scanner(System.in);
//        String[] s = scanner.nextLine().split(" ");
//        int[] list = new int[s.length];
//        int sum = 0;
//        for (int i = 0; i < s.length; i++) {
//            list[i] = (Integer.parseInt(s[i]));
//            sum+=list[i];
//        }
//        int num = scanner.nextInt();
//        if(sum<=num){
//            System.out.println(-1);
//        }else{
//            int left = 1, right = num+1;
//            int mid;
//            int ans = 0;
//            while (left<=right){
//                mid = (left+right)/2;
//                if(fun(list,mid)<=num){
//                    ans = mid;
//                    left = mid+1;
//                }else {
//                    right = mid-1;
//                }
//            }
//            System.out.println(ans);
//        }
//
//    }


//    static class Node{
//        int id;
//        int val;
//        int parent;
//    }
//
//    public static void main(String[] args) {
//        Scanner scanner = new Scanner(System.in);
//        String s = scanner.nextLine();
//        int cnt = Integer.parseInt(s);
//        Map<Integer,Node> map = new HashMap<>();
//        for (int i=0;i<cnt;i++){
//            int id = scanner.nextInt();
//            int parent = scanner.nextInt();
//            int val = scanner.nextInt();
//            Node node = new Node();
//            node.parent = parent;
//            node.val = val;
//            node.id = id;
//            map.put(id,node);
//        }
//        int ans = Integer.MIN_VALUE;
//        for (Map.Entry<Integer, Node> entry : map.entrySet()) {
//            Node node = entry.getValue();
//            int base = node.val;
//            ans = Math.max(base,ans);
//            while (node.parent!=-1){
//                node = map.get(node.parent);
//                base+=node.val;
//                ans = Math.max(base,ans);
//            }
//        }
//        System.out.println(ans);
//    }

    static class Node{
        int id;
        int level;
        int val;
        List<Integer> child = new ArrayList<>();
        List<Integer> parent = new ArrayList<>();
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String[] s = sc.nextLine().split(" ");
        int n = Integer.parseInt(s[0]);
        int m = Integer.parseInt(s[1]);
        HashMap<Integer,List<Integer>> map = new HashMap<>();
        for (int i=0;i<n;i++){
            map.put(i,new ArrayList<>());
        }
        String[] s1 = sc.nextLine().split(" ");
        int cur = 0;
        HashMap<Integer,Node> edge = new HashMap<>();
        for (String s2 : s1) {
            List<Integer> list = map.get(Integer.parseInt(s2));
            list.add(cur);
            Node node = new Node();
            node.id = cur;
            node.level = Integer.parseInt(s2);
            edge.put(cur,node);
            cur++;
        }
        for (int i = 0; i < m; i++) {
            String[] s2 = sc.nextLine().split(" ");
            Node node = edge.get(i);
            for (int j=1;j<s2.length;j++){
                int child = Integer.parseInt(s2[j]);
                node.child.add(child);
                edge.get(child).parent.add(i);
            }
        }
        for (int i=n-2;i>=0;i--){
            List<Integer> list = map.get(i);
            Integer head = list.get(0);
            int v = 120;
            Node node = edge.get(head);
            Node c = node;
            while (true){
            }

        }
    }
    private static int fun(int[] list, int mid) {
        int ans = 0;
        for (int integer : list) {
            ans+=Math.min(integer,mid);
        }
        return ans;
    }
}
