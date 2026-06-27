import data.TreeNode;
import java.util.LinkedList;
import java.util.Queue;

public class Main104二叉树的最大深度 {

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
        Main104二叉树的最大深度 solution = new Main104二叉树的最大深度();

        // 测试用例1: 示例用例 - [3,9,20,null,null,15,7] 深度为3
        TreeNode tree1 = buildTree(new Integer[]{3, 9, 20, null, null, 15, 7});
        TestUtil.assertEquals(3, solution.maxDepth(tree1), "示例用例: [3,9,20,null,null,15,7]深度3");

        // 测试用例2: 边界用例 - 空树深度为0
        TestUtil.assertEquals(0, solution.maxDepth(null), "边界用例: 空树深度0");

        // 测试用例3: 边界用例 - 单节点深度为1
        TreeNode tree3 = buildTree(new Integer[]{1});
        TestUtil.assertEquals(1, solution.maxDepth(tree3), "边界用例: 单节点深度1");

        // 测试用例4: 左斜树
        TreeNode tree4 = buildTree(new Integer[]{1, 2, null, 3, null, 4});
        TestUtil.assertEquals(4, solution.maxDepth(tree4), "普通用例: 左斜树深度4");

        // 测试用例5: 完全二叉树
        TreeNode tree5 = buildTree(new Integer[]{1, 2, 3, 4, 5, 6, 7});
        TestUtil.assertEquals(3, solution.maxDepth(tree5), "普通用例: 完全二叉树深度3");

        TestUtil.printSummary();
    }

    public int maxDepth(TreeNode root) {
        return depth(root, 0);
    }

    /**
     *  dfs
     */
    public int depth(TreeNode treeNode, int cureentLevel) {
        if (treeNode == null)
            return 0;

        if (treeNode.left == null && treeNode.right == null)
            return cureentLevel + 1;

        return Math.max(depth(treeNode.left, cureentLevel+1), depth(treeNode.right, cureentLevel+1));

    }
}
