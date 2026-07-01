package tree.build;

import data.TreeNode;

/**
 * LeetCode 105. 从前序与中序遍历序列构造二叉树
 * 难度：中等
 *
 * 题目：
 * 给定两个整数数组 preorder 和 inorder，其中 preorder 是二叉树的先序遍历，
 * inorder 是同一棵树的中序遍历，请构造二叉树并返回其根节点。
 *
 * 示例：
 * 输入：preorder = [3,9,20,15,7], inorder = [9,3,15,20,7]
 * 输出：[3,9,20,null,null,15,7]
 *
 * 练习重点：
 * 1. 前序数组的第一个元素是根节点
 * 2. 在中序数组中找到根节点，划分左右子树
 * 3. 使用下标区间递归，避免频繁创建新数组
 */
public class Main105从前序与中序遍历序列构造二叉树 {

    public TreeNode buildTree(int[] preorder, int[] inorder) {
        if (preorder.length<1)
            return null;
        int root = preorder[0];
        TreeNode node = new TreeNode(root);
        int index =0;
        for (int i = 0; i < inorder.length; i++) {
            if (inorder[i]==root)
                index = i;
        }
        node.left = buildTree( )

    }
}
