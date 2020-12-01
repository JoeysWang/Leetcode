package offer;import data.TreeNode;

public class 二叉搜索树 {


    static class Solution {


        //是否合法
        public boolean isValidBST(TreeNode root) {
            return isValidBSTHelper(root, null, null);
        }

        private boolean isValidBSTHelper(TreeNode root,
                                         TreeNode max,
                                         TreeNode min) {
            //root 需要做的不只是和左右子节点比较，而是要整个左子树和右子树所有节点比较
            if (root == null)
                return true;

            if (min != null && root.val <= min.val)
                return false;

            if (max != null && root.val >= max.val)
                return false;

            return isValidBSTHelper(root.left, root, min)
                    && isValidBSTHelper(root.right, max, root);

        }

        public boolean isInBST(TreeNode root, int target) {

            if (root == null) return false;
            if (root.val == target) return true;

            if (root.val < target)
                return isInBST(root.right, target);
            else
                return isInBST(root.left, target);

        }

        public TreeNode insertIntoBST(TreeNode root, int val) {

            if (root == null) return new TreeNode(val);

            if (root.val < val) {
                root.right = insertIntoBST(root.right, val);
            } else
                root.left = insertIntoBST(root.left, val);

            return root;
        }

        public TreeNode deleteNode(TreeNode root, int key) {
            if (root.val == key) {
                // 找到啦，进行删除
                if (root.left == null && root.right == null) return null;
                if (root.left == null) return root.right;
                if (root.right == null) return root.left;

                //把右子树 的最小值当做当前节点，然后把最小节点删掉
                TreeNode minNode = getMin(root.right);
                root.val = minNode.val;
                root.right = deleteNode(root.right, minNode.val);


            } else if (root.val > key) {
                root.left = deleteNode(root.left, key);
            } else if (root.val < key) {
                root.right = deleteNode(root.right, key);
            }
            return root;
        }

        private TreeNode getMin(TreeNode node) {
            // BST 最左边的就是最小的
            while (node.left != null) node = node.left;
            return node;
        }
    }
}
