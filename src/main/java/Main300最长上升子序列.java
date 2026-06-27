import data.TreeNode;

import java.util.Arrays;

public class Main300最长上升子序列 {

    /**
     * 给你一个整数数组 nums ，找到其中最长严格递增子序列的长度。
     * <p>
     * 子序列是由数组派生而来的序列，删除（或不删除）数组中的元素而不改变其余元素的顺序。
     * 例如，[3,6,2,7] 是数组 [0,3,1,6,2,2,7] 的子序列。
     * <p>
     * 示例 1：
     * <p>
     * 输入：nums = [10,9,2,5,3,7,101,18]
     * 输出：4
     * 解释：最长递增子序列是 [2,3,7,101]，因此长度为 4 。
     */
    public static void main(String[] args) {
        test();
    }

    public class Solution {


        public int lengthOfLIS(int[] nums) {
            if (nums.length == 0) return 0;
            int dp[] = new int[nums.length];
            //dp[i]为 以下标i作为结尾，的最长严格递增子序列的长度。
            Arrays.fill(dp, 1);
            for (int i = 1; i < nums.length; i++) {
                for (int j = 0; j < i; j++) {
                    if (nums[i] > nums[j]) {
                        dp[i] = Math.max(dp[i], dp[j] + 1);
                    }
                }
            }

            int res=1;
            for (int i = 0; i < dp.length; i++) {
                res = Math.max(res, dp[i]);
            }
            return res;

        }

    }

    public static void test() {
        TestUtil.reset();
        Main300最长上升子序列 outer = new Main300最长上升子序列();
        Solution solution = outer.new Solution();

        // 测试用例1: 示例用例
        TestUtil.assertEquals(4, solution.lengthOfLIS(new int[]{10, 9, 2, 5, 3, 7, 101, 18}), "示例用例: [10,9,2,5,3,7,101,18]");

        // 测试用例2: 边界用例 - 空数组
        TestUtil.assertEquals(0, solution.lengthOfLIS(new int[]{}), "边界用例: 空数组");

        // 测试用例3: 边界用例 - 单元素
        TestUtil.assertEquals(1, solution.lengthOfLIS(new int[]{5}), "边界用例: 单元素");

        // 测试用例4: 边界用例 - 全部递增
        TestUtil.assertEquals(5, solution.lengthOfLIS(new int[]{1, 2, 3, 4, 5}), "边界用例: 完全递增");

        // 测试用例5: 边界用例 - 全部递减
        TestUtil.assertEquals(1, solution.lengthOfLIS(new int[]{5, 4, 3, 2, 1}), "边界用例: 完全递减");

        // 测试用例6: 普通用例 - 有重复元素
        TestUtil.assertEquals(1, solution.lengthOfLIS(new int[]{7, 7, 7, 7, 7, 7, 7}), "普通用例: 全部相同元素");

        // 测试用例7: 普通用例
        TestUtil.assertEquals(4, solution.lengthOfLIS(new int[]{0, 1, 0, 3, 2, 3}), "普通用例: [0,1,0,3,2,3]");

        TestUtil.printSummary();
    }

}
