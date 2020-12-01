import data.TreeNode;import java.util.HashMap;
import java.util.Map;

public class Main337打家劫舍III {

    public static void main(String[] args) {

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
