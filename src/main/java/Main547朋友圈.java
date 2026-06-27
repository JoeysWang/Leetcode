

public class Main547朋友圈 {
    /**
     * 并查集
     */

    public static void main(String[] args) {
        test();
    }

    static class Solution {
        int[] parent;

        public int findCircleNum(int[][] isConnected) {
            int n = isConnected.length;
            parent = new int[n];
            for (int i = 0; i < n; i++) {
                parent[i] = i;
            }

            for (int i = 0; i < n; i++) {
                for (int j = i + 1; j < n; j++) {
                    if (isConnected[i][j] == 1) {
                        union(i, j);
                    }
                }
            }

            int count = 0;
            for (int i = 0; i < n; i++) {
                if (parent[i] == i) {
                    count++;
                }
            }
            return count;
        }

        private int find(int x) {
            if (parent[x] != x) {
                parent[x] = find(parent[x]);
            }
            return parent[x];
        }

        private void union(int x, int y) {
            int rootX = find(x);
            int rootY = find(y);
            if (rootX != rootY) {
                parent[rootX] = rootY;
            }
        }
    }

    public static void test() {
        TestUtil.reset();
        Solution solution = new Solution();

        // 测试用例1: 示例用例
        // [[1,1,0],[1,1,0],[0,0,1]] -> 2
        int[][] m1 = {
            {1, 1, 0},
            {1, 1, 0},
            {0, 0, 1}
        };
        TestUtil.assertEquals(2, solution.findCircleNum(m1), "示例用例: 3人矩阵 2个朋友圈");

        // 测试用例2: 示例用例 - 全部相连
        // [[1,1,1],[1,1,1],[1,1,1]] -> 1
        int[][] m2 = {
            {1, 1, 1},
            {1, 1, 1},
            {1, 1, 1}
        };
        TestUtil.assertEquals(1, solution.findCircleNum(m2), "示例用例: 全部相连 1个朋友圈");

        // 测试用例3: 边界用例 - 单人
        // [[1]] -> 1
        int[][] m3 = {{1}};
        TestUtil.assertEquals(1, solution.findCircleNum(m3), "边界用例: 单人");

        // 测试用例4: 普通用例 - 互不相连
        // [[1,0,0],[0,1,0],[0,0,1]] -> 3
        int[][] m4 = {
            {1, 0, 0},
            {0, 1, 0},
            {0, 0, 1}
        };
        TestUtil.assertEquals(3, solution.findCircleNum(m4), "普通用例: 互不相连 3个朋友圈");

        // 测试用例5: 普通用例 - 4人矩阵
        // [[1,0,0,1],[0,1,1,0],[0,1,1,1],[1,0,1,1]] -> 1
        int[][] m5 = {
            {1, 0, 0, 1},
            {0, 1, 1, 0},
            {0, 1, 1, 1},
            {1, 0, 1, 1}
        };
        TestUtil.assertEquals(1, solution.findCircleNum(m5), "普通用例: 4人全部连通");

        TestUtil.printSummary();
    }
}
