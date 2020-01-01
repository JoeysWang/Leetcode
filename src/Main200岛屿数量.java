import java.util.LinkedList;
import java.util.Queue;

/**
 * 输入:
 * 11110
 * 11010
 * 11000
 * 00000
 *
 * 输出: 1
 *
 * 可以用并查集、dfs 、bfs
 */
public class Main200岛屿数量 {

    public static void main(String[] args) {
        Main200岛屿数量 solution = new Main200岛屿数量();
//
        char[][] grid1 = {
                {'1', '1', '1', '1', '0'},
                {'1', '1', '0', '1', '0'},
                {'1', '1', '0', '0', '0'},
                {'0', '0', '0', '0', '0'}};
        int numIslands1 = solution.numIslands(grid1);
        System.out.println(numIslands1);

        char[][] grid2 = {
                {'1', '1', '0', '0', '0'},
                {'1', '1', '0', '0', '0'},
                {'0', '0', '1', '0', '0'},
                {'0', '0', '0', '1', '1'}};
        int numIslands2 = solution.numIslands(grid2);
        System.out.println(numIslands2);
        char[][] grid3 = {
                {'1', '1', '1'},
                {'0', '1', '0'},
                {'1', '1', '1'}};
        int numIslands3 = solution.numIslands(grid3);
        System.out.println(numIslands3);
        char[][] grid4 = {
                {'1', '1', '1', '1', '1', '1', '1'},
                {'0', '0', '0', '0', '0', '0', '1'},
                {'1', '1', '1', '1', '1', '0', '1'},
                {'1', '0', '0', '0', '1', '0', '1'},
                {'1', '0', '1', '0', '1', '0', '1'},
                {'1', '0', '1', '1', '1', '0', '1'},
                {'1', '1', '1', '1', '1', '1', '1'}};
        int numIslands4 = solution.numIslands(grid4);
        System.out.println(numIslands4);

    }

    public int numIslands(char[][] grid) {
        return numIslandsUN(grid);
    }

    //    int[][] directions = {{-1, 0}, {0, -1}, {1, 0}, {0, 1}};
    class UnionFind {
        int count;
        int[] roots;

        public UnionFind(char[][] grid) {
            count = 0;
            int rows = grid.length;
            int cols = grid[0].length;
            roots = new int[rows * cols];
            for (int i = 0; i < rows; ++i) {
                for (int j = 0; j < cols; ++j) {
                    if (grid[i][j] == '1') {
                        roots[i * cols + j] = i * cols + j;
                        ++count;
                    }
                }
            }
        }

        public int find(int xy) {
            if (roots[xy] != xy)
                roots[xy] = find(roots[xy]);
            return roots[xy];
        }

        public void union(int xy1, int xy2) {
            int root1 = find(xy1);
            int root2 = find(xy2);
            if (root1 != root2) {
                roots[root1] = root2;
                --count;
            }
        }

        public int getCount() {
            return count;
        }
    }

    public int numIslandsUNF(char[][] grid) {
        if (grid == null || grid.length == 0) {
            return 0;
        }
        int total = 0, row = grid.length, col = grid[0].length;
        UnionFind uf = new UnionFind(grid);
        for (int i = 0; i < row; ++i) {
            for (int j = 0; j < col; ++j) {
                if (grid[i][j] == '1') {
                    grid[i][j] = '0';
                    int old = i * col + j;
                    if (i - 1 >= 0 && grid[i - 1][j] == '1') {
                        uf.union(old, (i - 1) * col + j);
                    }
                    if (i + 1 < row && grid[i + 1][j] == '1') {
                        uf.union(old, (i + 1) * col + j);
                    }
                    if (j - 1 >= 0 && grid[i][j - 1] == '1') {
                        uf.union(old, i * col + j - 1);
                    }
                    if (j + 1 < col && grid[i][j + 1] == '1') {
                        uf.union(old, i * col + j + 1);
                    }
                }
            }
        }
        return uf.getCount();
    }

    /**
     * 并查集
     */
    int mI;
    int mJ;

    private int  numIslandsUN(char[][] grid) {

        rows = grid.length;
        if (rows == 0) return 0;
        cols = grid[0].length;

        MyUnion unionUF = new MyUnion(grid);
        System.out.println("unionUF before:" + unionUF.count);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                mI = i;
                mJ = j;
                if (grid[i][j] == '1') {
                    int old = i * cols + j;

                    //如果这个节点右边的也是1，他俩合并
                    if (j + 1 < cols && grid[i][j + 1] == '1')
                        unionUF.union(old, i * cols + j + 1);

                    //如果这个节点下边的也是1，他俩合并
                    if (i + 1 < rows && grid[i + 1][j] == '1')
                        unionUF.union(old, (i + 1) * cols + j);
                }
            }
        }
        System.out.println("unionUF after:" + unionUF.count);

        return unionUF.count;
    }


    public static class MyUnion {
        private int[] roots;
        private int count = 0;


        public MyUnion(char[][] grid) {
            int rows = grid.length;
            int cols = grid[0].length;
            roots = new int[rows * cols];

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {

                    if (grid[i][j] == '1') {

                        roots[i * cols + j] = i * cols + j;      //把二维坐标转化成一维坐标
                        count += 1;
                    }
                }
            }
        }

        public int find(int xy) {
            if (roots[xy] != xy)
                roots[xy] = find(roots[xy]);
            return roots[xy];
        }
//        public int find(int xy) {
//            //去找这个坐标的root
//            int root = roots[xy];
//
//            //如果（它的根的根 != 它的根），说明还没到顶，继续找
//
//            while (roots[root] != root) {
//                root = roots[root];
//            }
//
//            //找到了root就是答案，但是这里要优化一下这个xy的指向，
//            //使它所有的父节点都直接指向最顶的根
////            while (roots[xy] != xy) {
////                int tempRoot = roots[xy];
////                roots[xy] = root;
////                xy = tempRoot;
////            }
//
//            return root;
//        }

        public void union(int xy1, int xy2) {
            int root1 = find(xy1);
            int root2 = find(xy2);

            if (root1 != root2) {
                //把2的root指向1
                roots[root1] = root2;

                //每合并一次，count就要-1
                count--;
            }

        }
    }


    /**
     * flood_fill
     */
    char[][] grid;
    // 标记数组，标记了 grid 的坐标对应的格子是否被访问过
    private boolean[][] marked;
    // grid 的行数
    private int rows;
    // grid 的列数
    private int cols;


    /**
     * DFS flood_fill
     */
    private int numIslandsDFS(char[][] grid) {
        rows = grid.length;
        if (rows == 0) {
            return 0;
        }
        cols = grid[0].length;
        this.grid = grid;
        marked = new boolean[rows][cols];
        int count = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                //如果没有被访问过，且是1,就把它周围的全部染色，
                //这片岛屿染色完成后，继续寻找下一片未被染色的岛屿
                if (!marked[i][j] && grid[i][j] == '1') {
                    count++;
                    dfs(i, j);
                }
            }
        }
        return count;
    }

    private void dfs(int row, int col) {
        if (row >= rows) return; //越界
        if (col >= cols) return; //越界
        if (row < 0) return;//越界
        if (col < 0) return;//越界

        //已经访问过了，就不再继续dfs了
        if (marked[row][col])
            return;

        marked[row][col] = true;

        if (grid[row][col] == '1')
            grid[row][col] = '0';
        else
            return;

        //四个方向都要扫描
        dfs(row + 1, col);
        dfs(row, col + 1);
        dfs(row - 1, col);
        dfs(row, col - 1);
    }


    /**
     * BFS 广度优先 flood_fill
     */
    private int numIslandsBFS(char[][] grid) {
        rows = grid.length;
        if (rows == 0) return 0;
        cols = grid[0].length;
        this.grid = grid;
        marked = new boolean[rows][cols];

        int count = 0;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (!marked[i][j] && this.grid[i][j] == '1') {
                    count++;
                    bfs(i, j);
                }

            }
        }
        return count;
    }

    private void bfs(int row, int col) {


        //已经访问过了，就不再继续dfs了
        if (marked[row][col])
            return;

        Queue<Integer> queue = new LinkedList<>();
        queue.add((row * cols + col));
        while (!queue.isEmpty()) {
            int levelSize = queue.size();

            for (int i = 0; i < levelSize; i++) {
                Integer current = queue.poll();
                Integer colCurrent = current / cols;
                Integer rowCurrent = current % cols;

                if (colCurrent >= rows) continue; //越界
                if (rowCurrent >= cols) continue; //越界
                if (colCurrent < 0) continue;//越界
                if (rowCurrent < 0) continue;//越界

                if (marked[colCurrent][rowCurrent])
                    continue;
                if (grid[colCurrent][rowCurrent] == '0')
                    continue;
                marked[colCurrent][rowCurrent] = true;

                queue.add((colCurrent + 1) * cols + rowCurrent);
                queue.add((colCurrent) * cols + rowCurrent + 1);
                queue.add((colCurrent - 1) * cols + rowCurrent);
                queue.add((colCurrent) * cols + rowCurrent - 1);
                grid[colCurrent][rowCurrent] = '0';

            }
        }
    }
}