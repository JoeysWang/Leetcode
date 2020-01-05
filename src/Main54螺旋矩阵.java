import java.util.ArrayList;
import java.util.List;

public class Main54螺旋矩阵 {
    public static void main(String[] args) {
        Solution solution = new Solution();
        solution.spiralOrder(new int[][]
                {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}
        );


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
