import data.TreeNode;import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Main102二叉树的层次遍历 {


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
