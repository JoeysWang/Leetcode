import data.TreeNode;import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Main102二叉树的层次遍历 {

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
        Main102二叉树的层次遍历 solution = new Main102二叉树的层次遍历();

        // 测试用例1: 示例用例 - [3,9,20,null,null,15,7]
        TreeNode tree1 = buildTree(new Integer[]{3, 9, 20, null, null, 15, 7});
        List<List<Integer>> expected1 = Arrays.asList(
                Arrays.asList(3),
                Arrays.asList(9, 20),
                Arrays.asList(15, 7)
        );
        TestUtil.assertArrayEquals2D(expected1, solution.levelOrder(tree1), "示例用例: [3,9,20,null,null,15,7]");

        // 测试用例2: 边界用例 - 空树
        List<List<Integer>> expected2 = new ArrayList<>();
        TestUtil.assertArrayEquals2D(expected2, solution.levelOrder(null), "边界用例: 空树");

        // 测试用例3: 边界用例 - 单节点
        TreeNode tree3 = buildTree(new Integer[]{1});
        List<List<Integer>> expected3 = Arrays.asList(Arrays.asList(1));
        TestUtil.assertArrayEquals2D(expected3, solution.levelOrder(tree3), "边界用例: 单节点");

        // 测试用例4: 只有左子树
        TreeNode tree4 = buildTree(new Integer[]{1, 2, null, 3});
        List<List<Integer>> expected4 = Arrays.asList(
                Arrays.asList(1),
                Arrays.asList(2),
                Arrays.asList(3)
        );
        TestUtil.assertArrayEquals2D(expected4, solution.levelOrder(tree4), "普通用例: 只有左子树");

        // 测试用例5: DFS方法测试
        TreeNode tree5 = buildTree(new Integer[]{3, 9, 20, null, null, 15, 7});
        TestUtil.assertArrayEquals2D(expected1, dfs(tree5), "普通用例: DFS方法测试");

        TestUtil.printSummary();
    }

    public List<List<Integer>> levelOrder(TreeNode root) {
        return bfs(root);

    }

    /**
     * 广度优先搜索（BFS）
     * 我们按照高度顺序一层一层的访问整棵树，高层次的节点将会比低层次的节点先被访问到。
     * 思路：创建一个queue,这个队列的第一个元素就是root节点
     * 然后不断循环levelSize次，这个levelSize就是每层的元素数量
     * 从队列头部取出一个节点，在队列的尾部加入这个取出来的节点的子节点
     *
     * poll是弹出首元素，peek是获取首元素不弹出
     */
    private List<List<Integer>> bfs(TreeNode root) {
        if (root == null) return new ArrayList<>();
        List<List<Integer>> result = new ArrayList<>();
        //初始化队列
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            /**
             * 获得当前层数的元素数量
             */
            int levelSize = queue.size();

            List<Integer> currentLevelVal = new ArrayList<>();
            /**
             * 然后循环levelSize次，目的是把每个元素都从queue里弹出，
             * 并且把弹出的元素的子元素都加到队列的最后面
             */
            for (int i = 0; i < levelSize; i++) {
                TreeNode treeNode = queue.poll();
                currentLevelVal.add(treeNode.val);
                if (treeNode.left != null) queue.add(treeNode.left);
                if (treeNode.right != null) queue.add(treeNode.right);
            }
            result.add(currentLevelVal);
        }
        return result;
    }

    /**
     * 深度优先搜索（DFS）
     * 在这个策略中，我们采用深度作为优先级，以便从跟开始一直到达某个确定的叶子，然后再返回根到达另一个分支。
     * 深度优先搜索策略又可以根据根节点、左孩子和右孩子的相对顺序被细分为先序遍历，中序遍历和后序遍历。
     */
    public static List<List<Integer>> dfs(TreeNode root) {
        if (root == null) return new ArrayList<>();

        List<List<Integer>> result = new ArrayList<>();

        dfsHelper(root, result, 0);

        return result;
    }


    private static void dfsHelper(TreeNode node, List<List<Integer>> result, int level) {
        if (node == null) return;
        if (result.size() < level + 1) {
            result.add(level, new ArrayList<>());
        }
        result.get(level).add(node.val);

        dfsHelper(node.left, result, level + 1);
        dfsHelper(node.right, result, level + 1);

    }
}
