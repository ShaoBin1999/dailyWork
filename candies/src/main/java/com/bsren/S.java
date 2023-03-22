package com.bsren;

import java.util.*;

class Icon{
    int price;
    int val;

    public Icon(int price, int val) {
        this.price = price;
        this.val = val;
    }
}
public class S {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int m = sc.nextInt();
        int n = sc.nextInt();
        int[][] matrix = new int[m][n];
        Map<Integer,List<int[]>> map = new HashMap<>();
        for (int i=0;i<m;i++){
            for (int j=0;j<n;j++){
                int a = sc.nextInt();
                matrix[i][j] = a;
                List<int[]> list = map.get(a);
                if(list==null){
                    list = new ArrayList<>();
                }
                list.add(new int[]{i,j});
                map.put(a,list);
            }
        }
        int[][] head = new int[m][n];
        head[0][0] = 0;
        for (int i=1;i<n;i++){
            head[0][i] = head[0][i-1]+Math.abs(matrix[0][i]-matrix[0][i-1]);
        }
        for (int i=1;i<m;i++){
            head[i][0] = head[i-1][0]+Math.abs(matrix[i][0]-matrix[i-1][0]);
        }
        for (int i=1;i<m;i++){
            for (int j=1;j<n;j++){
                head[i][j] = Math.min(head[i][j-1]+Math.abs(matrix[i][j]-matrix[i][j-1]),
                        head[i-1][j]+Math.abs(matrix[i][j]-matrix[i-1][j]));
            }
        }
        int[][] tail = new int[m][n];
        tail[m-1][n-1] = 0;
        for (int i=m-2;i>=0;i--){
            tail[i][n-1] = tail[i+1][n-1]+Math.abs(matrix[i][n-1]-matrix[i+1][n-1]);
        }
        for (int i=n-2;i>=0;i--){
            tail[m-1][i] = tail[m-1][i+1]+Math.abs(matrix[m-1][i]-matrix[m-1][i+1]);
        }
        for (int i=m-2;i>=0;i--){
            for (int j=n-2;j>=0;j--){
                tail[i][j] = Math.min(tail[i+1][j]+Math.abs(matrix[i][j]-matrix[i+1][j]),
                        tail[i][j+1]+Math.abs(matrix[i][j]-matrix[i][j+1]));
            }
        }
        int ans = head[m-1][n-1];
        for (Map.Entry<Integer, List<int[]>> entry : map.entrySet()) {
            List<int[]> value = entry.getValue();
            if(value.size()>=2){
                int min1 = Integer.MAX_VALUE;
                int min2 = Integer.MAX_VALUE;
                for (int[] ints : value) {
                    min1 = Math.min(min1,head[ints[0]][ints[1]]);
                    min2 = Math.min(min2,tail[ints[0]][ints[1]]);
                }
                ans = Math.min(ans,min1+min2);
            }
        }
        for (int[] ints : head) {
            System.out.println(Arrays.toString(ints));
        }
        for (int[] ints : tail) {
            System.out.println(Arrays.toString(ints));
        }
        System.out.println(ans);
    }
//    static int max = -1;
//    public static void main(String[] args) {
//        Scanner sc = new Scanner(System.in);
//        int num = sc.nextInt();
//        int money = sc.nextInt();
//        List<List<Icon>> list = new ArrayList<>();
//        for (int i=0;i<num;i++){
//            int iconNum = sc.nextInt();
//            int[] prices = new int[iconNum];
//            for (int j=0;j<iconNum;j++){
//                prices[j] = sc.nextInt();
//            }
//            int[] vals = new int[iconNum];
//            for (int j=0;j<iconNum;j++){
//                vals[j] = sc.nextInt();
//            }
//            List<Icon> l = new ArrayList<>();
//            for (int j=0;j<iconNum;j++){
//                l.add(new Icon(prices[j],vals[j]));
//            }
//            list.add(l);
//        }
//        fun(0,money,list,0);
//        System.out.println(max);
//    }
//
//    public static void fun(int num,int money,List<List<Icon>> list,int val){
//        if(money<0){
//            return;
//        }
//        if(num==list.size()){
//            max = Math.max(max,val);
//            return;
//        }
//        List<Icon> icons = list.get(num);
//        for (Icon icon : icons) {
//            fun(num+1,money-icon.price,list,val+icon.val);
//        }
//    }
}
