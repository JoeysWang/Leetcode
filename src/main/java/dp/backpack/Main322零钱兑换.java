package dp.backpack;

import tree.util.TestUtil;

import java.util.Arrays;

/**
 * LeetCode 322. 零钱兑换
 * 难度：中等
 * <p>
 * 题目：
 * 给你一个整数数组 coins，表示不同面额的硬币；以及一个整数 amount，表示总金额。
 * 每种硬币的数量是无限的。
 * <p>
 * 请你计算并返回可以凑成总金额所需的最少硬币个数。
 * 如果没有任何一种硬币组合能组成总金额，返回 -1。
 * <p>
 * 示例 1：
 * 输入：coins = [1, 2, 5], amount = 11
 * 输出：3
 * 解释：11 = 5 + 5 + 1
 * <p>
 * 示例 2：
 * 输入：coins = [2], amount = 3
 * 输出：-1
 * <p>
 * 示例 3：
 * 输入：coins = [1], amount = 0
 * 输出：0
 * <p>
 * 思路：
 * 这题是「完全背包 + 求最小值」。
 * <p>
 * 1. 容量是什么？
 * amount，也就是目标金额。
 * <p>
 * 2. 物品是什么？
 * coins 里的每一种硬币面额。
 * <p>
 * 3. 每个物品能选几次？
 * 无限次，所以是完全背包。
 * <p>
 * 4. 要求什么？
 * 凑出 amount 的最少硬币数量。
 * <p>
 * 状态定义：
 * dp[j] = 凑出金额 j 所需的最少硬币数量。
 * <p>
 * 初始化：
 * dp[0] = 0，凑出金额 0 不需要硬币。
 * 其他 dp[j] 初始化成 INF，表示暂时凑不出来。
 * <p>
 * 状态转移：
 * 如果最后一枚硬币选 coin，那么前面需要先凑出 j - coin。
 * <p>
 * dp[j] = min(dp[j], dp[j - coin] + 1)
 * <p>
 * 和爬楼梯的关系：
 * 爬楼梯是「到第 i 阶，可以从 i-1 或 i-2 来」。
 * 零钱兑换是「凑金额 j，可以从 j-coin 来」。
 * <p>
 * 区别是：
 * 爬楼梯通常求方案数；
 * 本题求最少硬币数，所以转移里用 min。
 */
public class Main322零钱兑换 {

    public static void main(String[] args) {
        Solution solution = new Solution();

        System.out.println("coinChange: " + solution.coinChange(new int[]{1, 2, 5}, 11));

        test();
    }

    public static void test() {
        TestUtil.reset();
        Solution solution = new Solution();

        TestUtil.assertEquals(3, solution.coinChange(new int[]{1, 2, 5}, 11), "示例1: 11 = 5 + 5 + 1");
        TestUtil.assertEquals(-1, solution.coinChange(new int[]{2}, 3), "示例2: coins=[2], amount=3 凑不出");
        TestUtil.assertEquals(0, solution.coinChange(new int[]{1}, 0), "示例3: amount=0");
        TestUtil.assertEquals(2, solution.coinChange(new int[]{1}, 2), "边界: 只能用 1 元硬币");
        TestUtil.assertEquals(4, solution.coinChange(new int[]{1, 2, 5, 10}, 27), "普通: 27 = 10 + 10 + 5 + 2");
        TestUtil.assertEquals(20, solution.coinChange(new int[]{186, 419, 83, 408}, 6249), "大数: LeetCode 经典用例");

        TestUtil.assertEquals(3, solution.coinChangeLikeClimbStairs(new int[]{1, 2, 5}, 11), "爬楼梯视角: 示例1");
        TestUtil.assertEquals(-1, solution.coinChangeLikeClimbStairs(new int[]{2}, 3), "爬楼梯视角: 凑不出");

        TestUtil.printSummary();
    }

    static class Solution {

        /**
         * 标准完全背包写法。
         * <p>
         * 外层枚举硬币，内层金额正序遍历。
         * 正序的原因：同一种硬币可以无限重复使用。
         */
        public int coinChange(int[] coins, int amount) {
            int[] dp = new int[amount + 1];
            Arrays.fill(dp, Integer.MAX_VALUE);
            dp[0] = 0;


            for (int coin : coins) {
                for (int i = coin; i <= amount; i++) {
                    dp[i] = Math.min(dp[i], dp[amount - coin] + 1);
                }

            }
            if (dp[amount] == Integer.MAX_VALUE)
                return -1;

            return dp[amount];
        }

        /**
         * 爬楼梯视角写法。
         * <p>
         * 外层枚举金额 j，内层枚举「最后一枚硬币」coin。
         * <p>
         * 对 322 来说，这样写也可以，因为本题求的是最小硬币数，
         * 不关心 1+2 和 2+1 算不算不同顺序。
         * <p>
         * 但到了 518 零钱兑换 II，题目求组合数，这个写法就会把顺序重复计算进去。
         */
        public int coinChangeLikeClimbStairs(int[] coins, int amount) {
            int INF = amount + 1;
            int[] dp = new int[amount + 1];
            Arrays.fill(dp, INF);

            dp[0] = 0;

            for (int j = 1; j <= amount; j++) {
                for (int coin : coins) {
                    if (j >= coin) {
                        dp[j] = Math.min(dp[j], dp[j - coin] + 1);
                    }
                }
            }

            return dp[amount] == INF ? -1 : dp[amount];
        }
    }
}
