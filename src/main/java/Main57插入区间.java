import java.util.ArrayList;
import java.util.Arrays;

/**
 * 给你一个 无重叠的 ，按照区间起始端点排序的区间列表。
 *
 * 在列表中插入一个新的区间，你需要确保列表中的区间仍然有序且不重叠（如果有必要的话，可以合并区间）。
 * 
 * 输入：intervals = [[1,2],[3,5],[6,7],[8,10],[12,16]], newInterval = [4,8]
 * 输出：[[1,2],[3,10],[12,16]]
 * 解释：这是因为新的区间 [4,8] 与 [3,5],[6,7],[8,10] 重叠。
 * 
 * 
 * ./img/57_01.png
 * ./img/57_02.png
 * 链接：https://leetcode.cn/problems/insert-interval
 */
public class Main57插入区间 {

    /**
     * 输入：intervals = [[1,2],[3,5],[6,7],[8,10],[12,16]], newInterval = [4,8]
     * 输出：[[1,2],[3,10],[12,16]]
     * 解释：这是因为新的区间 [4,8] 与 [3,5],[6,7],[8,10] 重叠。
     */
    public static void main(String[] args) {
        Solution solution = new Solution();
        int[][] intervals = new int[][] {
                new int[] { 1, 2 },
                new int[] { 3, 5 },
                new int[] { 6, 7 },
                new int[] { 8, 10 },
                new int[] { 12, 16 }
        };
        int[] newInterval = new int[] { 4, 8 };
        int[][] res = solution.insert(intervals, newInterval);
        for (int i = 0; i < res.length; i++) {
            System.out.println(Arrays.toString(res[i]));
        }

        test();
    }

    public static void test() {
        TestUtil.reset();
        Solution solution = new Solution();

        // 测试用例1: 示例用例
        int[][] intervals1 = {{1, 2}, {3, 5}, {6, 7}, {8, 10}, {12, 16}};
        int[][] expected1 = {{1, 2}, {3, 10}, {12, 16}};
        TestUtil.assertTrue(Arrays.deepEquals(expected1, solution.insert(intervals1, new int[]{4, 8})), "示例: 插入[4,8]");

        // 测试用例2: 边界用例 - 空列表
        int[][] expected2 = {{5, 7}};
        TestUtil.assertTrue(Arrays.deepEquals(expected2, solution.insert(new int[][]{}, new int[]{5, 7})), "边界: 空列表插入");

        // 测试用例3: 无重叠
        int[][] intervals3 = {{1, 2}, {7, 9}};
        int[][] expected3 = {{1, 2}, {3, 5}, {7, 9}};
        TestUtil.assertTrue(Arrays.deepEquals(expected3, solution.insert(intervals3, new int[]{3, 5})), "普通: 无重叠插入");

        // 测试用例4: 完全覆盖
        int[][] intervals4 = {{3, 5}, {6, 8}};
        int[][] expected4 = {{1, 10}};
        TestUtil.assertTrue(Arrays.deepEquals(expected4, solution.insert(intervals4, new int[]{1, 10})), "普通: 完全覆盖");

        TestUtil.printSummary();
    }

    static class Solution {

        public int[][] insert(int[][] intervals, int[] newInterval) {
            if (intervals == null || intervals.length == 0 || newInterval == null || newInterval.length == 0) {
                return new int[][]{newInterval};
            }
            ArrayList<int[]> res = new ArrayList<>();

            int i = 0;

            // 选出区间小于newInterval区间的
            while (i < intervals.length && intervals[i][1] < newInterval[0]) {
                res.add(intervals[i]);
                i++;
            }
            // 合并区间有重叠的
            while (i < intervals.length && intervals[i][0] <= newInterval[1]) {
                newInterval[0] = Math.min(intervals[i][0], newInterval[0]);
                newInterval[1] = Math.max(intervals[i][1], newInterval[1]);
                i++;
            }
            res.add(newInterval);

            // 剩下区间大于newInterval的
            while (i < intervals.length) {
                res.add(intervals[i]);
                i++;
            }
            int[][] result = new int[res.size()][2];
            for (int j = 0; j < res.size(); j++) {
                result[j] = res.get(j);
            }

            return result;
        }
    }

}
