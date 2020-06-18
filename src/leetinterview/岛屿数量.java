package leetinterview;

import java.util.ArrayDeque;
import java.util.Queue;

public class 岛屿数量 {

    class Solution {
        Queue<Integer> queue = new ArrayDeque<>();

        public int numIslands(char[][] grid) {

            int rows = grid.length;
            int cols = grid[0].length;
            if (rows == 0) return 0;
            if (grid[0].length == 0) return 0;


            int count = 0;
            boolean[][] visited = new boolean[rows][grid[0].length];

            queue.add(0);

            while (!queue.isEmpty()) {
                int size = queue.size();

                for (int i = 0; i < size; i++) {

                    Integer position = queue.poll();
                    int x = position / rows;
                    int y = position % rows;


                    if (x >= cols) continue; //越界
                    if (y >= rows) continue; //越界
                    if (x < 0) continue;//越界
                    if (y < 0) continue;//越界


                    if (visited[x][y]) continue;
                    if (grid[x][y] == '0') continue;

                    if (!visited[x][y] && grid[x][y] == '1') {
                        count++;
                        grid[x][y] = '0';
                    }
                    visited[x][y] = true;
                    queue.add((x + 1) * rows + y);
                    queue.add((x) * rows + y + 1);
                    queue.add((x - 1) * rows + y);
                    queue.add((x) * rows + y - 1);

                }
            }
            return count;
        }



    }
}
