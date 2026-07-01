package tree.basic;

import data.TreeNode;

/**
 * LeetCode 101. 对称二叉树
 * 难度：简单
 * <p>
 * 题目：
 * 给你一个二叉树的根节点 root，检查它是否轴对称。
 * <p>
 * 示例：
 * 输入：[1,2,2,3,4,4,3]
 * 输出：true
 * <p>
 * 练习重点：
 * 1. 同时比较两棵子树
 * 2. 左子树的左边要和右子树的右边比较
 * 3. 左子树的右边要和右子树的左边比较
 */
public class Main101对称二叉树 {

    public boolean isSymmetric(TreeNode root) {
        if (root == null) {
            return true;
        }
        return isEq(root.left, root.right);
    }

    public boolean isEq(TreeNode left, TreeNode right) {
        if (left == null && right == null) {
            return true;
        }
        if (left == null || right == null) {
            return false;
        }

        return left.val == right.val
                && isEq(left.left, right.right)
                && isEq(left.right, right.left);
    }
}
