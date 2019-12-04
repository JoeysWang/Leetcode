import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Main102二叉树的层次遍历 {


    public List<List<Integer>> levelOrder(TreeNode root) {
        return bfs(root);

    }

    /**
     * 宽度优先搜索（BFS）
     * 我们按照高度顺序一层一层的访问整棵树，高层次的节点将会比低层次的节点先被访问到。
     */
    private List<List<Integer>> bfs(TreeNode root) {
        if (root == null) return new ArrayList<>();

        List<List<Integer>> result = new ArrayList<>();
        Queue<TreeNode> queue = new LinkedList<>();

        queue.add(root);

        while (!queue.isEmpty()) {
            int levelSize = queue.size();
            List<Integer> currentResult = new ArrayList<>();


            for (int i = 0; i < levelSize; i++) {
                TreeNode currentNode = queue.poll();

                if (currentNode.left != null)
                    queue.add(currentNode.left);
                if (currentNode.right != null)
                    queue.add(currentNode.right);

                currentResult.add(currentNode.val);
            }
            result.add(currentResult);
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
