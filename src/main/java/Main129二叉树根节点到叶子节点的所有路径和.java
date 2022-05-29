import data.TreeNode;

public class Main129二叉树根节点到叶子节点的所有路径和 {
    public class Solution {
        /**
         * @param root TreeNode类
         * @return int整型
         */

        public int sumNumbers(TreeNode root) {
            return dfs(root, 0);
        }

        private int dfs(TreeNode root, int previous) {
            if (root == null) return 0;

            int current = previous * 10 + root.val;

            if (root.left == null && root.right == null) {
                //到达叶子节点
                return current;
            }

            return dfs(root.left, current) + dfs(root.right, current);
        }
    }
}
