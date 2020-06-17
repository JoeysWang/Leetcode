package offer;

import data.TreeNode;

import java.util.HashMap;

public class Offer7重建二叉树 {

    /**
     * 输入某二叉树的前序遍历和中序遍历的结果，请重建该二叉树。假设输入的前序遍历和中序遍历的结果中都不含重复的数字。
     * <p>
     *  
     * <p>
     * 例如，给出
     * <p>
     * 前序遍历 preorder = [3,9,20,15,7]
     * 中序遍历 inorder = [9,3,15,20,7]
     * 返回如下的二叉树：
     * <p>
     * 3
     * / \
     * 9  20
     * /  \
     * 15   7
     *  
     * <p>
     * 限制：
     * <p>
     * 0 <= 节点个数 <= 5000
     * <p>
     * 链接：https://leetcode-cn.com/problems/zhong-jian-er-cha-shu-lcof
     */
    public static void main(String[] args) {
        int a = 0xff;
        int b = a & 1;
        System.out.println(String.format(" a =%d , b = %d", a, b));
        System.out.println(Integer.toBinaryString(a));

    }

    public static class Solution {

        HashMap<Integer, Integer> midOrderIndexMap = new HashMap<>();
        int[] preorder;

        public TreeNode buildTree(int[] preorder, int[] inorder) {
            for (int i = 0; i < inorder.length; i++) {
                midOrderIndexMap.put(inorder[i], i);
            }
            this.preorder = preorder;

            return recurise(0, 0, inorder.length - 1);
        }


        public TreeNode recurise(int pre_root_index, int in_left_index, int in_right_index) {

            if (in_left_index > in_right_index) {
                return null;
            }

            TreeNode root = new TreeNode(preorder[pre_root_index]);


            int inOrderRootIndex = midOrderIndexMap.get(preorder[0]);
            root.left = recurise(
                    pre_root_index + 1,
                    in_left_index, //中序遍历的 左边界
                    inOrderRootIndex - 1 ////中序遍历的 右边界
            );

            /**
             *  前序遍历 preorder = [3, 9, 20,15,7]
             *  中序遍历 inorder = [9,3, 15,20,7]
             */
            //右子树的根节点是 ：
            // 当前根节点pre_root_index + 左子树长度leftTreeLength  +1

            //左子树长度 要用中序遍历 来算：
            // 长度 = 中序遍历当前根节点 - 左边界

            int leftTreeLength = inOrderRootIndex - in_left_index;

            root.right = recurise(
                    pre_root_index + inOrderRootIndex + 1,
                    pre_root_index + leftTreeLength + 1,
                    in_right_index
            );

            return root;
        }
    }

}
