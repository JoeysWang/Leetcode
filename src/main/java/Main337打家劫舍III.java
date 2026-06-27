import data.TreeNode;import java.util.HashMap;
import java.util.Map;

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
