package tree.basic;

import data.TreeNode;

/**
 * LeetCode 111. 二叉树的最小深度
 * 难度：简单
 * <p>
 * 题目：
 * 给定一个二叉树，找出其最小深度。
 * 最小深度是从根节点到最近叶子节点的最短路径上的节点数量。
 * <p>
 * 注意：
 * 叶子节点是指没有子节点的节点。
 * 如果一个节点只有左子树或只有右子树，不能把空子树当作深度 0 的叶子路径。
 * <p>
 * 示例：
 * 输入：[3,9,20,null,null,15,7]
 * 输出：2
 */
public class Main111二叉树的最小深度 {

    public int minDepth(TreeNode root) {
        if (root == null) {
            return 0;
        }
        if (root.left == null && root.right == null) {
            return 1;
        }
        if (root.left == null) {
            return minDepth(root.right) + 1;
        }
        if (root.right == null) {
            return minDepth(root.left) + 1;
        }

        return Math.min(minDepth(root.left), minDepth(root.right)) + 1;
    }
}
