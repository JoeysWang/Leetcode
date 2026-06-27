import data.TreeNode;

/**
 * 给定一个二叉搜索树, 找到该树中两个指定节点的最近公共祖先。
 * <p>
 * 百度百科中最近公共祖先的定义为：
 * “对于有根树 T 的两个结点 p、q，最近公共祖先表示为一个结点 x，
 * 满足 x 是 p、q 的祖先且 x 的深度尽可能大（一个节点也可以是它自己的祖先）。”
 * <p>
 * 链接：https://leetcode-cn.com/problems/lowest-common-ancestor-of-a-binary-search-tree
 */
public class Main235二叉搜索树的最近公共祖先 {

    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null || p == null || q == null) return null;


        if (p.val <= root.val && q.val >= root.val)
            return root;

        if (p.val < root.val && q.val < root.val)
            return lowestCommonAncestor(root.left, p, q);
        if (p.val > root.val && q.val > root.val)
            return lowestCommonAncestor(root.right, p, q);


        return root;
    }

    public static void main(String[] args) {
        test();
    }

    /**
     * 构建BST:
     *         6
     *        / \
     *       2   8
     *      / \ / \
     *     0  4 7  9
     *       / \
     *      3   5
     */
    private static TreeNode buildTestBST() {
        TreeNode root = new TreeNode(6);
        root.left = new TreeNode(2);
        root.right = new TreeNode(8);
        root.left.left = new TreeNode(0);
        root.left.right = new TreeNode(4);
        root.right.left = new TreeNode(7);
        root.right.right = new TreeNode(9);
        root.left.right.left = new TreeNode(3);
        root.left.right.right = new TreeNode(5);
        return root;
    }

    public static void test() {
        TestUtil.reset();
        Main235二叉搜索树的最近公共祖先 solution = new Main235二叉搜索树的最近公共祖先();
        TreeNode root = buildTestBST();

        // 测试用例1: 示例用例 p=2, q=8 → 6
        TreeNode p1 = root.left;       // 2
        TreeNode q1 = root.right;      // 8
        TestUtil.assertEquals(6, solution.lowestCommonAncestor(root, p1, q1).val, "示例用例 p=2, q=8 → LCA=6");

        // 测试用例2: 示例用例 p=2, q=4 → 2 (节点可以是自己的祖先)
        TreeNode p2 = root.left;           // 2
        TreeNode q2 = root.left.right;     // 4
        TestUtil.assertEquals(2, solution.lowestCommonAncestor(root, p2, q2).val, "示例用例 p=2, q=4 → LCA=2");

        // 测试用例3: p=0, q=5 → 2
        TreeNode p3 = root.left.left;          // 0
        TreeNode q3 = root.left.right.right;   // 5
        TestUtil.assertEquals(2, solution.lowestCommonAncestor(root, p3, q3).val, "普通用例 p=0, q=5 → LCA=2");

        // 测试用例4: p=7, q=9 → 8
        TreeNode p4 = root.right.left;     // 7
        TreeNode q4 = root.right.right;    // 9
        TestUtil.assertEquals(8, solution.lowestCommonAncestor(root, p4, q4).val, "普通用例 p=7, q=9 → LCA=8");

        // 测试用例5: 边界用例 p=root, q=叶子 → root
        TreeNode p5 = root;                    // 6
        TreeNode q5 = root.left.right.left;    // 3
        TestUtil.assertEquals(6, solution.lowestCommonAncestor(root, p5, q5).val, "边界用例 p=6(root), q=3 → LCA=6");

        TestUtil.printSummary();
    }
}
