import java.util.ArrayList;
import java.util.List;

public class Main54螺旋矩阵 {
    public static void main(String[] args) {
        Solution solution = new Solution();
        solution.spiralOrder(new int[][]
                {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}
        );

        test();
    }

    public static void test() {
        TestUtil.reset();

        // 测试用例1: 示例用例 3x3矩阵
        Solution sol1 = new Solution();
        List<Integer> result1 = sol1.spiralOrder(new int[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}});
        TestUtil.assertEquals(java.util.Arrays.asList(1, 2, 3, 6, 9, 8, 7, 4, 5), result1, "示例: 3x3螺旋矩阵");

        // 测试用例2: 边界用例 1x1矩阵
        Solution sol2 = new Solution();
        List<Integer> result2 = sol2.spiralOrder(new int[][]{{1}});
        TestUtil.assertEquals(java.util.Arrays.asList(1), result2, "边界: 1x1矩阵");

        // 测试用例3: 普通用例 2x2矩阵
        Solution sol3 = new Solution();
        List<Integer> result3 = sol3.spiralOrder(new int[][]{{1, 2}, {3, 4}});
        TestUtil.assertEquals(java.util.Arrays.asList(1, 2, 4, 3), result3, "普通: 2x2矩阵");

        // 测试用例4: 普通用例 2x3矩阵
        Solution sol4 = new Solution();
        List<Integer> result4 = sol4.spiralOrder(new int[][]{{1, 2, 3}, {4, 5, 6}});
        TestUtil.assertEquals(java.util.Arrays.asList(1, 2, 3, 6, 5, 4), result4, "普通: 2x3矩阵");

        TestUtil.printSummary();
    }


    public static class Solution {

        int cols = 0;
        int rows = 0;
        boolean[][] marked;

        public List<Integer> spiralOrder(int[][] matrix) {
            List<Integer> res = new ArrayList<>();

            rows = matrix.length;
            if (rows == 0)
                return res;
            cols = matrix[0].length;

            marked = new boolean[rows][cols];
            int i = 0;
            int j = 0;


            while (true) {

                marked[i][j] = true;
                res.add(matrix[i][j]);
                boolean enableLeft = findLeft(i, j);
                boolean enableRight = findRight(i, j);
                boolean enableTop = findTop(i, j);
                boolean enableBottom = findBottom(i, j);

                if (enableRight) {
                    if (enableTop) {
                        i = i - 1;
                        continue;
                    }
                    j = j + 1;
                    continue;
                }


                if (enableLeft) {
                    if (enableBottom) {
                        i = i + 1;
                        continue;
                    }
                    j = j - 1;
                    continue;
                }
                if (enableBottom) {
                    i = i + 1;
                    continue;
                }
                if (enableTop) {
                    i = i - 1;
                    continue;
                }

                if (!enableLeft && !enableRight && !enableTop && !enableBottom)
                    break;
            }


            return res;
        }

        private boolean findBottom(int i, int j) {
            if (i + 1 >= rows)
                return false;
            if (marked[i+ 1][j ])
                return false;
            return true;

        }

        private boolean findTop(int i, int j) {
            if (i - 1 < 0)
                return false;
            if (marked[i- 1][j ])
                return false;
            return true;
        }

        private boolean findRight(int i, int j) {
            if (j + 1 >= cols)
                return false;
            if (marked[i ][j+ 1])
                return false;
            return true;
        }

        private boolean findLeft(int i, int j) {
            if (j - 1 < 0)
                return false;
            if (marked[i ][j- 1])
                return false;
            return true;
        }
    }
}
