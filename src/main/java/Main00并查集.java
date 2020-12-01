

public class Main00并查集 {


    public static class QuickUnionUF {

        private int[] roots;

        /**
         * 初始化,
         * 使得一开始的每个节点i的root都是自己：(它的老大是它自己)
         * root[i] = i
         */
        public QuickUnionUF(int N) {
            roots = new int[N];
            for (int i = 0; i < N; i++) {
                roots[i] = i;
            }
        }


        /**
         * 查找节点i的老大:
         * 假设 3->2->1,
         * 查找1的老大是2,2的老大是3，3的老大是3，所以1的老大是3，
         * 找到后进行路径压缩，使得3后面的节点全部直接指向3，比如1不指向2了，而是直接指向3
         * 3->2
         *  \>1
         */
        public int findRoot(int i) {
            int root = i;
            while (root != roots[root]) {
                root = roots[root];
            }
            //这里进行路径压缩,把3后面的节点，全部指向3，1不指向2了，而是直接指向3
            while (roots[i] != i) {
                int tmp = roots[i];
                roots[i] = root;
                i = tmp;
            }
            return root;
        }

        /**
         * 判断p q是否有相同root
         */
        public boolean connected(int p,int q){
            return findRoot(p)==findRoot(q);
        }


    }


}
