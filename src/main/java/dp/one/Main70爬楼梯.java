package dp.one;

import tree.util.TestUtil;

public class Main70爬楼梯 {

    /**
     * 假设你正在爬楼梯。需要 n 阶你才能到达楼顶。
     *
     * 每次你可以爬 1 或 2 个台阶。你有多少种不同的方法可以爬到楼顶呢？
     *
     * 注意：给定 n 是一个正整数。
     *
     * 示例 1：
     *
     * 输入： 2
     * 输出： 2
     * 解释： 有两种方法可以爬到楼顶。
     * 1.  1 阶 + 1 阶
     * 2.  2 阶
     * 示例 2：
     *
     * 输入： 3
     * 输出： 3
     * 解释： 有三种方法可以爬到楼顶。
     * 1.  1 阶 + 1 阶 + 1 阶
     * 2.  1 阶 + 2 阶
     * 3.  2 阶 + 1 阶
     */
    public static void main(String[] args) {
        Solution solution = new Solution();
        System.out.println("climbStairs: " + solution.climbStairs(141));

        test();
    }

    public static void test() {
        TestUtil.reset();
        Solution solution = new Solution();

        // 测试用例1: 示例用例
        TestUtil.assertEquals(2, solution.climbStairs(2), "示例: n=2");

        // 测试用例2: 示例用例
        TestUtil.assertEquals(3, solution.climbStairs(3), "示例: n=3");

        // 测试用例3: 边界用例
        TestUtil.assertEquals(1, solution.climbStairs(1), "边界: n=1");

        // 测试用例4: 普通用例
        TestUtil.assertEquals(8, solution.climbStairs(5), "普通: n=5");

        // 测试用例5: 普通用例
        TestUtil.assertEquals(13, solution.climbStairs(6), "普通: n=6");

        TestUtil.printSummary();
    }

    static class Solution {
        public int climbStairs(int n) {
            int[] dp = new int[n+3];
            dp[0] = 0;
            dp[1] = 1;
            dp[2] = 2;

            for (int i = 3; i <= n; i++) {
                dp[i] = dp[i - 1] + dp[i - 2];
            }

            return dp[n];

        }
    }
}
