package tree.dp;

import data.TreeNode;

/**
 * LeetCode 124. 二叉树中的最大路径和
 * 难度：困难
 *
 * 题目：
 * 二叉树中的路径被定义为一条节点序列，序列中每对相邻节点之间都存在一条边。
 * 同一个节点在一条路径序列中至多出现一次。
 * 该路径至少包含一个节点，且不一定经过根节点。
 * 路径和是路径中各节点值的总和。
 *
 * 给你一个二叉树的根节点 root，返回其最大路径和。
 *
 * 示例：
 * 输入：[-10,9,20,null,null,15,7]
 * 输出：42
 *
 * 练习重点：
 * 1. 递归返回“从当前节点向父节点延伸的最大贡献”
 * 2. 更新答案时可以同时使用左贡献、当前节点、右贡献
 * 3. 负数贡献可以丢弃
 */
public class Main124二叉树中的最大路径和 {

    public int maxPathSum(TreeNode root) {
        throw new UnsupportedOperationException("TODO");
    }
}
