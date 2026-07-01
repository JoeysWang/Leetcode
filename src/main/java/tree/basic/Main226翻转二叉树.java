package tree.basic;

import data.TreeNode;

/**
 * LeetCode 226. 翻转二叉树
 * 难度：简单
 * <p>
 * 题目：
 * 给你一棵二叉树的根节点 root，翻转这棵二叉树，并返回其根节点。
 * <p>
 * 示例：
 * 输入：[4,2,7,1,3,6,9]
 * 输出：[4,7,2,9,6,3,1]
 * <p>
 * 练习重点：
 * 1. 递归处理左右子树
 * 2. 前序或后序交换左右孩子都可以
 */
public class Main226翻转二叉树 {

    public TreeNode invertTree(TreeNode root) {

        if (root == null) {
            return null;
        }

        TreeNode left = root.left;
        TreeNode right = root.right;

        root.left = invertTree(right);
        root.right = invertTree(left);

        return root;
    }

}
