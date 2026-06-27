import data.TreeNode;

public class Main98验证二叉搜索树 {

    public static void main(String[] args) {
        test();
    }

    /**
     * 辅助方法：根据数组构建二叉树（层序遍历），null表示空节点
     */
    private static TreeNode buildTree(Integer[] values) {
        if (values == null || values.length == 0 || values[0] == null) return null;
        TreeNode root = new TreeNode(values[0]);
        java.util.Queue<TreeNode> queue = new java.util.LinkedList<>();
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
        Main98验证二叉搜索树 solution = new Main98验证二叉搜索树();

        // 测试用例1: 示例用例 - 合法BST [2,1,3]
        TreeNode tree1 = buildTree(new Integer[]{2, 1, 3});
        TestUtil.assertTrue(solution.isValidBST(tree1), "示例用例: [2,1,3]是合法BST");

        // 测试用例2: 示例用例 - 非法BST [5,1,4,null,null,3,6]
        TreeNode tree2 = buildTree(new Integer[]{5, 1, 4, null, null, 3, 6});
        TestUtil.assertFalse(solution.isValidBST(tree2), "示例用例: [5,1,4,null,null,3,6]不是合法BST");

        // 测试用例3: 边界用例 - 空树
        TestUtil.assertTrue(solution.isValidBST(null), "边界用例: 空树是合法BST");

        // 测试用例4: 边界用例 - 单节点
        TreeNode tree4 = buildTree(new Integer[]{1});
        TestUtil.assertTrue(solution.isValidBST(tree4), "边界用例: 单节点是合法BST");

        // 测试用例5: 非法BST - 左子节点大于根
        TreeNode tree5 = buildTree(new Integer[]{5, 6, 7});
        TestUtil.assertFalse(solution.isValidBST(tree5), "普通用例: 左子节点大于根不是BST");

        // 测试用例6: 较大合法BST [5,1,4,null,null,3,6] 改为 [5,3,7,1,4,null,null]
        TreeNode tree6 = buildTree(new Integer[]{5, 3, 7, 1, 4, 6, 8});
        TestUtil.assertTrue(solution.isValidBST(tree6), "普通用例: 较大合法BST");

        TestUtil.printSummary();
    }

    public boolean isValidBST(TreeNode root) {
        if (root == null) return true;


        return valid(root.left, null, root.val)
                && valid(root.right, root.val,null);

    }

    public boolean valid(TreeNode root, Integer min, Integer max) {
        if (root == null) return true;

        //如果min有值，说明这个节点需要比min大才可以
        if (min != null && root.val <= min) return false;

        //如果max有值，这个节点需要比max小才可以
        if (max != null && root.val >= max) return false;


        //如果 val在min和max之间，继续遍历下面的节点
        return valid(root.left, min, root.val)
                && valid(root.right, root.val, max);

    }
}
