package tree.lca;

import data.TreeNode;

/**
 * LeetCode 236. 二叉树的最近公共祖先
 * 难度：中等
 * <p>
 * 题目：
 * 给定一个二叉树，找到该树中两个指定节点的最近公共祖先。
 * <p>
 * 最近公共祖先定义：
 * 对于有根树 T 的两个节点 p、q，最近公共祖先表示为一个节点 x，
 * 满足 x 是 p、q 的祖先且 x 的深度尽可能大。
 * 一个节点也可以是它自己的祖先。
 * <p>
 * 示例：
 * 输入：root = [3,5,1,6,2,0,8,null,null,7,4], p = 5, q = 1
 * 输出：3
 * <p>
 * 练习重点：
 * 1. 递归查找左右子树
 * 2. 如果左右子树分别找到 p 和 q，当前节点就是答案
 * <p>
 * 思路总结：
 * 递归函数 lowestCommonAncestor(root, p, q) 的含义是：
 * 在以 root 为根的子树里，寻找 p 和 q 的最近公共祖先。
 * <p>
 * 递归返回值可以理解成一个“信号”：
 * 1. 返回 null：这棵子树里没有找到 p 或 q。
 * 2. 返回 p 或 q：这棵子树里找到了其中一个目标节点。
 * 3. 返回某个祖先节点：这棵子树里已经同时找到了 p 和 q，答案已经确定。
 * <p>
 * 为什么 root == p 或 root == q 可以直接返回？
 * 因为当前子树已经找到一个目标节点了，要把这个信号交给上一层判断。
 * 这不代表最终答案一定是当前节点。
 * <p>
 * 例如：
 * 3
 * / \
 * 5   1
 * /
 * 2
 * <p>
 * 如果 p = 5, q = 2：
 * 左子树返回 5，右子树返回 2。
 * 对节点 3 来说，left 和 right 都不为 null，所以 3 才是最近公共祖先。
 * <p>
 * 如果 q 在 p 的子树下面，那么 p 本身就是答案。
 * LeetCode 236 默认 p 和 q 都存在于树中，所以遇到 p 或 q 可以放心返回。
 */
public class Main236二叉树的最近公共祖先 {

    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null)
            return null;
        if (root == p || root == q)
            return root;

        TreeNode left = lowestCommonAncestor(root.left, p, q);
        TreeNode right = lowestCommonAncestor(root.right, p, q);
        if (left != null && right != null)
            return root;

        if (left != null)
            return left;

        return right;
    }
}
