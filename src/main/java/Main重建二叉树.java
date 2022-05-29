import data.TreeNode;

public class Main重建二叉树 {


    public class Solution {

        //前序 + 中序 的重建
        public TreeNode Construct(int[] preOrder, int[] inOrder, int length) {
            // 空指针判断
            if (preOrder == null || inOrder == null || length <= 0) {
                return null;
            }
            return rebuild(preOrder,
                    0,
                    preOrder.length - 1,
                    inOrder,
                    0,
                    inOrder.length - 1);
        }

        public TreeNode rebuild(
                int[] preOrder,
                int preStart,
                int preEnd,
                int[] inOrder,
                int inStart,
                int inEnd
        ) {
            if (preStart > preEnd || preStart > preOrder.length)
                return null;

            int rootValue = preOrder[preStart];
            TreeNode node = new TreeNode(rootValue);
            int rootInOrder = 0;
            for (int i = inStart; i <= inEnd; i++) {
                if (rootValue == inOrder[i]) {
                    rootInOrder = i;
                }
            }
            int preLeftEnd = 0;
            for (int i = preStart + 1; i <= preEnd; i++) {
                if (preOrder[i] == inOrder[rootInOrder - 1]) {
                    preLeftEnd = i;
                }
            }


            node.left = rebuild(preOrder,
                    preStart + 1,
                    preLeftEnd,
                    inOrder,
                    inStart,
                    rootInOrder - 1);

            node.right = rebuild(preOrder,
                    preLeftEnd + 1,
                    preEnd,
                    inOrder,
                    rootInOrder + 1,
                    inEnd);
            return node;
        }


    }

}
