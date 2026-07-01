package tree.path;

import data.TreeNode;
import tree.util.TestUtil;
import java.util.LinkedList;
import java.util.Queue;

public class Main129二叉树根节点到叶子节点的所有路径和 {

    public static void main(String[] args) {
        test();
    }

    /**
     * 辅助方法：根据数组构建二叉树（层序遍历），null表示空节点
     */
    private static TreeNode buildTree(Integer[] values) {
        if (values == null || values.length == 0 || values[0] == null) return null;
        TreeNode root = new TreeNode(values[0]);
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);
        int i = 1;
        while (!queue.isEmpty() && i < values.length) {
            TreeNode node = queue.poll();
            if (i < values.length && values[i] != null) {
                node.left = new TreeNode(values[i]);
                queue.add(node.left);
            }
            i++;
            if (i < values.length && values[i] != null) {
                node.right = new TreeNode(values[i]);
                queue.add(node.right);
            }
            i++;
        }
        return root;
    }

    public static void test() {
        TestUtil.reset();
        Solution solution = new Solution();

        // 测试用例1: 示例用例 - [1,2,3] -> 12+13=25
        TreeNode tree1 = buildTree(new Integer[]{1, 2, 3});
        TestUtil.assertEquals(25, solution.sumNumbers(tree1), "示例用例: [1,2,3]=12+13=25");

        // 测试用例2: 示例用例 - [4,9,0,5,1] -> 495+491+40=1026
        TreeNode tree2 = buildTree(new Integer[]{4, 9, 0, 5, 1});
        TestUtil.assertEquals(1026, solution.sumNumbers(tree2), "示例用例: [4,9,0,5,1]=1026");

        // 测试用例3: 边界用例 - 空树
        TestUtil.assertEquals(0, solution.sumNumbers(null), "边界用例: 空树返回0");

        // 测试用例4: 边界用例 - 单节点
        TreeNode tree4 = buildTree(new Integer[]{5});
        TestUtil.assertEquals(5, solution.sumNumbers(tree4), "边界用例: 单节点");

        // 测试用例5: 只有左子树
        TreeNode tree5 = buildTree(new Integer[]{1, 2, null, 3});
        // 路径: 1->2->3 = 123
        TestUtil.assertEquals(123, solution.sumNumbers(tree5), "普通用例: 左斜树1->2->3=123");

        TestUtil.printSummary();
    }

    public static class Solution {
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
