package dp.backpack;

import tree.util.TestUtil;

/**
 * LeetCode 518. 零钱兑换 II
 * 难度：中等
 * <p>
 * 题目：
 * 给你一个整数数组 coins，表示不同面额的硬币；以及一个整数 amount，表示总金额。
 * 每种硬币的数量是无限的。
 * <p>
 * 请你计算并返回可以凑成总金额的硬币组合数。
 * 如果任何硬币组合都无法凑出总金额，返回 0。
 * <p>
 * 示例 1：
 * 输入：amount = 5, coins = [1, 2, 5]
 * 输出：4
 * 解释：
 * 5 = 5
 * 5 = 2 + 2 + 1
 * 5 = 2 + 1 + 1 + 1
 * 5 = 1 + 1 + 1 + 1 + 1
 * <p>
 * 示例 2：
 * 输入：amount = 3, coins = [2]
 * 输出：0
 * <p>
 * 示例 3：
 * 输入：amount = 10, coins = [10]
 * 输出：1
 * <p>
 * 思路：
 * 这题是「完全背包 + 求组合数」。
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
 * 凑出 amount 的组合数量。
 * <p>
 * 状态定义：
 * dp[j] = 凑出金额 j 的组合数。
 * <p>
 * 初始化：
 * dp[0] = 1。
 * <p>
 * 这个 1 的意思是：凑出金额 0 有一种方式，就是什么硬币都不选。
 * 它不是最终题意里的一个真实硬币组合，而是为了让后面的转移能成立。
 * <p>
 * 状态转移：
 * 当前正在考虑 coin。
 * 如果要凑出金额 j，并且使用了至少一枚 coin，
 * 那么前面需要先凑出 j - coin。
 * <p>
 * dp[j] += dp[j - coin]
 * <p>
 * 最关键的区别：循环顺序
 * <p>
 * 本题问的是「组合数」，不是「排列数」。
 * 所以 1 + 2 和 2 + 1 只能算同一种。
 * <p>
 * 正确写法：
 * 外层枚举硬币，内层枚举金额。
 * <p>
 * 这样每次只是在「已经处理过的硬币种类」里构造组合，
 * 不会因为顺序不同而重复计算。
 * <p>
 * 错误类比：
 * 如果像爬楼梯那样外层枚举金额，内层枚举最后一枚硬币，
 * 就会把 1+2 和 2+1 当成两种排列。
 */
public class Main518零钱兑换II {

    public static void main(String[] args) {
        Solution solution = new Solution();

        System.out.println("change: " + solution.change(5, new int[]{1, 2, 5}));

        test();
    }

    public static void test() {
        TestUtil.reset();
        Solution solution = new Solution();

        TestUtil.assertEquals(4, solution.change(5, new int[]{1, 2, 5}), "示例1: amount=5, coins=[1,2,5]");
        TestUtil.assertEquals(0, solution.change(3, new int[]{2}), "示例2: amount=3, coins=[2] 凑不出");
        TestUtil.assertEquals(1, solution.change(10, new int[]{10}), "示例3: amount=10, coins=[10]");
        TestUtil.assertEquals(1, solution.change(0, new int[]{1, 2, 5}), "边界: amount=0");
        TestUtil.assertEquals(1, solution.change(5, new int[]{1}), "边界: 只能用 1 元硬币");
        TestUtil.assertEquals(10, solution.change(10, new int[]{1, 2, 5}), "普通: amount=10, coins=[1,2,5]");

        TestUtil.assertEquals(9, solution.changePermutationLikeClimbStairs(5, new int[]{1, 2, 5}), "对比: 爬楼梯视角会算排列数");

        TestUtil.printSummary();
    }

    static class Solution {

        /**
         * 正确解法：组合数。
         * <p>
         * 外层枚举硬币，内层金额正序遍历。
         * <p>
         * 为什么金额要正序？
         * 因为每种硬币可以无限使用。
         * <p>
         * 为什么硬币要放外层？
         * 因为本题统计组合，不统计排列。
         */
        public int change(int amount, int[] coins) {
            int[] dp = new int[amount + 1];
            dp[0] = 1;

            for (int coin : coins) {
                for (int j = coin; j <= amount; j++) {
                    dp[j] += dp[j - coin];
                }
            }

            return dp[amount];
        }

        /**
         * 对比写法：爬楼梯视角。
         * <p>
         * 外层枚举金额，内层枚举最后一枚硬币。
         * 这个方法求的是排列数，不是本题答案。
         * <p>
         * amount = 5, coins = [1,2,5] 时：
         * 组合数是 4；
         * 排列数是 9。
         */
        public int changePermutationLikeClimbStairs(int amount, int[] coins) {
            int[] dp = new int[amount + 1];
            dp[0] = 1;

            for (int j = 1; j <= amount; j++) {
                for (int coin : coins) {
                    if (j >= coin) {
                        dp[j] += dp[j - coin];
                    }
                }
            }

            return dp[amount];
        }
    }
}
