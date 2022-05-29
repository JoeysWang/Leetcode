

public class Main00并查集 {


    public static class QuickUnionUF {
        // 连通分量个数
        private int count;
        // 存储一棵树
        private int[] parent; // 记录树的“重量”
        private int[] size;

        public QuickUnionUF(int n) {
            this.count = n;
            parent = new int[n];
            size = new int[n];
            for (int i = 0; i < n; i++) {
                parent[i] = i;
                size[i] = 1;
            }
        }

        public void union(int p, int q) {
            int rootP = find(p);
            int rootQ = find(q);
            if (rootP == rootQ)
                return;
            // 小树接到大树下面，较平衡
            if (size[rootP] > size[rootQ]) {
                parent[rootQ] = rootP;
                size[rootP] += size[rootQ];
            } else {
                parent[rootP] = rootQ;
                size[rootQ] += size[rootP];
            }
            count--;
        }

        public boolean connected(int p, int q) {
            int rootP = find(p);
            int rootQ = find(q);
            return rootP == rootQ;
        }

        private int find(int x) {
            while (parent[x] != x) {
                //进行路径压缩
                //取出他的父节点
                int parents = parent[x];
                //取出爷爷节点
                int grandpa = parent[parents];
                //让他指向爷爷节点
                parent[x] = grandpa;

                x = parent[x];
            }
            return x;
        }

        public int count() {
            return count;
        }
    }


}
