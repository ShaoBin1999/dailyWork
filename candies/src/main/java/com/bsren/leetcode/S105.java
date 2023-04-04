package com.bsren.leetcode;

import com.bsren.niuke.TreeNode;

public class S105 {

    public TreeNode buildTree(int[] preorder, int[] inorder) {
        return buildTree(preorder,inorder,0,preorder.length-1,0,inorder.length-1);
    }

    private TreeNode buildTree(int[] preorder, int[] inorder, int preL, int preR, int inL, int inR) {
        if(preR<preL){
            return null;
        }
        int head = preorder[preL];
        int idx = inL;
        for (;idx<=inR;idx++){
            if(inorder[idx]==head){
                break;
            }
        }
        int leftLen = idx-inL;
        TreeNode node = new TreeNode(head);
        node.left = buildTree(preorder,inorder,preL+1,preL+leftLen,inL,idx-1);
        node.right = buildTree(preorder,inorder,preL+leftLen+1,preR,idx+1,inR);
        return node;
    }
}
