package com.bsren.javaStd.collections;

//不支持索引层数变化
class skipListTest {

    public final Integer levels;

    public final Index[] indexHead;
    public final dataNode dump;

    //TODO 创建一个最小的头节点和最大的尾节点，如果存在这样的节点的话

    public skipListTest(dataNode head,int num){
        levels = getLevelNum(num);
        indexHead = new Index[levels];
        dump = new dataNode(-1,head);
        Index node = new Index(levels-1,head.val,null,null);
        node.dataNode=head;
        indexHead[levels-1]=node;
        for(int i=levels-2;i>=0;i--){
            Index node_up = new Index(i, head.val, null, node);
            indexHead[i]=node_up;
            node=node_up;
        }
    }

    public class Index {
        public int level;
        public int val;
        public Index next;
        public Index under;

        public dataNode dataNode;

        public Index(int level, int val, Index next, Index under){
            this.level=level;
            this.val=val;
            this.next=next;
            this.under=under;
        }
    }

    public class dataNode{
        int val;
        dataNode next;

        public dataNode(int val,dataNode next){
            this.val=val;
            this.next=next;
        }
    }

    public static int getLevelNum(int totalNum){
        totalNum-=1;
        int level = 0,cur;
        while ((cur = totalNum>>1)!=0){
            level++;
            totalNum = cur;
        }
        return level;
    }

    public boolean find(int key){
        Index index = findIndex(key, indexHead[0]);
        dataNode dataNode = index.dataNode;
        while (dataNode!=null && dataNode.val<key){
            dataNode=dataNode.next;
        }
        if(dataNode==null){
            return false;
        }
        return dataNode.val == key;
    }

    public Index findIndex(int key,Index head){
        while (head.next!=null){
            if(head.next.val<key){
                head=head.next;
            }else{
                if(head.under==null){
                    return head;
                }
                findIndex(key,head.under);
            }
        }
        while (head.under!=null){
            head=head.under;
        }
        return head;
    }



    public static void main(String[] args) {
        System.out.println(getLevelNum(17));
    }
}
