import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Main104二叉树的最大深度 {

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
