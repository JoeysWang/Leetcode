package dp.tree;

import data.TreeNode;
import tree.util.TestUtil;
import java.util.HashMap;
import java.util.Map;

/**
 * LeetCode 337. 打家劫舍 III
 * 难度：中等
 * <p>
 * 题目：
 * 在上次打劫完一条街道之后，小偷又发现了一个新的可行窃地区。
 * 这个地区的所有房屋的排列类似于一棵二叉树。每个房屋的直接相连房屋只有一个（即父节点或子节点）。
 * 两个直接相连的房子被偷窃时会自动报警。
 * <p>
 * 给定树的根节点 root，返回在不触动警报的情况下，小偷能盗取的最高金额。
 * <p>
 * 示例 1：
 * 输入：[3,2,3,null,3,null,1]
 *         3
 *        / \
 *       2   3
 *        \   \
 *         3   1
 * 输出：7
 * 解释：小偷一晚能够盗取的最高金额 = 3 + 3 + 1 = 7（抢根 3、左孙子 3、右孙子 1）。
 * <p>
 * 示例 2：
 * 输入：[3,4,5,1,3,null,1]
 *         3
 *        / \
 *       4   5
 *      / \   \
 *     1   3   1
 * 输出：9
 * 解释：小偷一晚能够盗取的最高金额 = 4 + 5 = 9（抢左子 4、右子 5）。
 * <p>
 * 练习重点：
 * 1. 树形 DP：每个节点返回两个状态——「抢该节点」与「不抢该节点」的最大收益
 * 2. 若抢当前节点，则其左右子节点都不能抢，只能取孙子节点
 * 3. 若不抢当前节点，则左右子节点各自取最优（抢或不抢均可）
 * 4. 可用 memo 记忆化消除重复子问题（抢的分支会访问孙子节点，存在大量重叠）
 */
public class Main337打家劫舍III {

    public static void main(String[] args) {
        test();
    }

    public static void test() {
        TestUtil.reset();
        Solution solution = new Solution();

        // 测试用例1: 边界用例 - 空树
        TestUtil.assertEquals(0, solution.rob(null), "边界用例: 空树");

        // 测试用例2: 边界用例 - 单节点
        TreeNode single = new TreeNode(5);
        TestUtil.assertEquals(5, solution.rob(single), "边界用例: 单节点 [5]");

        // 测试用例3: 示例用例 [3,2,3,null,3,null,1]
        //        3
        //       / \
        //      2   3
        //       \   \
        //        3   1
        TreeNode root1 = new TreeNode(3);
        root1.left = new TreeNode(2);
        root1.right = new TreeNode(3);
        root1.left.right = new TreeNode(3);
        root1.right.right = new TreeNode(1);
        TestUtil.assertEquals(7, solution.rob(root1), "示例用例: [3,2,3,null,3,null,1] 期望7");

        // 测试用例4: 示例用例 [3,4,5,1,3,null,1]
        //        3
        //       / \
        //      4   5
        //     / \   \
        //    1   3   1
        TreeNode root2 = new TreeNode(3);
        root2.left = new TreeNode(4);
        root2.right = new TreeNode(5);
        root2.left.left = new TreeNode(1);
        root2.left.right = new TreeNode(3);
        root2.right.right = new TreeNode(1);
        TestUtil.assertEquals(9, solution.rob(root2), "示例用例: [3,4,5,1,3,null,1] 期望9");

        // 测试用例5: 普通用例 - 三层链
        //        4
        //       /
        //      1
        //     /
        //    2
        //   /
        //  3
        TreeNode root3 = new TreeNode(4);
        root3.left = new TreeNode(1);
        root3.left.left = new TreeNode(2);
        root3.left.left.left = new TreeNode(3);
        TestUtil.assertEquals(7, solution.rob(root3), "普通用例: 左链 [4,1,2,3] 期望7 (抢4和3)");

        TestUtil.printSummary();
    }

    private static class Solution {
        Map<TreeNode, Integer> memo = new HashMap<>();

        public int rob(TreeNode root) {
            return dp(root);
        }

        public int dp(TreeNode root) {

            if (root == null) return 0;

            if (memo.get(root) != null) return memo.get(root);


            int rob = (root.left == null ? 0 : dp(root.left.left) + dp(root.left.right))
                    + (root.right == null ? 0 : dp(root.right.left) + dp(root.right.right))
                    + root.val;


            int noRob = dp(root.left) + dp(root.right);

            int res = Math.max(rob, noRob);
            memo.put(root, res);


            return res;

        }


    }
}
