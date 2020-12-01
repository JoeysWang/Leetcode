import data.TreeNode;

public class Main98验证二叉搜索树 {

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
