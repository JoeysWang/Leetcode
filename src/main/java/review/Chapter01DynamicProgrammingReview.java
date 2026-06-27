package review;

import data.TreeNode;

/**
 * 第一章、动态规划系列。
 *
 * 默写目标：
 * 1. 对每道题先写状态定义，再写选择和 base case。
 * 2. 能从递归 + 备忘录改写成 DP table。
 * 3. 能解释遍历顺序，必要时做状态压缩。
 *
 * 对应题目：
 * 300, 354, 673, 416, 494, 1049, 322, 518, 279, 377, 698, 473,
 * 72, 583, 712, 887, 375, 1143, 1092, 516, 1312, 877, 486, 1140,
 * 10, 44, 651, 650, 28, 459, 686, 435, 452, 646, 121, 122, 123,
 * 188, 309, 714, 198, 213, 337.
 */
public class Chapter01DynamicProgrammingReview {

    public static void main(String[] args) {
        System.out.println("第一章：在本文件中默写动态规划题。");
    }

    // 最长递增子序列
    public int lengthOfLIS(int[] nums) {
        throw todo("300 O(n^2) LIS");
    }

    public int lengthOfLISBinarySearch(int[] nums) {
        throw todo("300 O(n log n) LIS");
    }

    public int maxEnvelopes(int[][] envelopes) {
        throw todo("354 Russian Doll Envelopes");
    }

    // 0-1 背包、完全背包、子集背包
    public int zeroOneKnapsack(int capacity, int[] weights, int[] values) {
        throw todo("0-1 背包");
    }

    public boolean canPartition(int[] nums) {
        throw todo("416 Partition Equal Subset Sum");
    }

    public int findTargetSumWays(int[] nums, int target) {
        throw todo("494 Target Sum");
    }

    public int coinChange(int[] coins, int amount) {
        throw todo("322 Coin Change");
    }

    public int change(int amount, int[] coins) {
        throw todo("518 Coin Change II");
    }

    public int numSquares(int n) {
        throw todo("279 Perfect Squares");
    }

    // 编辑距离、LCS、子序列模板
    public int minDistance(String word1, String word2) {
        throw todo("72 Edit Distance");
    }

    public int longestCommonSubsequence(String text1, String text2) {
        throw todo("1143 Longest Common Subsequence");
    }

    public int longestPalindromeSubseq(String s) {
        throw todo("516 Longest Palindromic Subsequence");
    }

    public int minInsertions(String s) {
        throw todo("1312 Minimum Insertion Steps");
    }

    // 高楼扔鸡蛋
    public int superEggDrop(int k, int n) {
        throw todo("887 Super Egg Drop");
    }

    public int superEggDropByMoves(int k, int n) {
        throw todo("887 用操作次数重新定义状态");
    }

    // 博弈问题
    public boolean predictTheWinner(int[] nums) {
        throw todo("486 Predict the Winner");
    }

    public boolean stoneGame(int[] piles) {
        throw todo("877 Stone Game");
    }

    // 正则、四键键盘、KMP
    public boolean isMatchRegex(String s, String p) {
        throw todo("10 Regular Expression Matching");
    }

    public int maxA(int n) {
        throw todo("651 4 Keys Keyboard");
    }

    public int strStrKmp(String haystack, String needle) {
        throw todo("28 KMP");
    }

    // 区间调度
    public int eraseOverlapIntervals(int[][] intervals) {
        throw todo("435 Non-overlapping Intervals");
    }

    public int findMinArrowShots(int[][] points) {
        throw todo("452 Minimum Number of Arrows");
    }

    // 股票买卖
    public int maxProfitOneTransaction(int[] prices) {
        throw todo("121 Best Time to Buy and Sell Stock");
    }

    public int maxProfitUnlimitedTransactions(int[] prices) {
        throw todo("122 Best Time to Buy and Sell Stock II");
    }

    public int maxProfitKTransactions(int k, int[] prices) {
        throw todo("188 Best Time to Buy and Sell Stock IV");
    }

    public int maxProfitWithCooldown(int[] prices) {
        throw todo("309 Best Time to Buy and Sell Stock with Cooldown");
    }

    public int maxProfitWithFee(int[] prices, int fee) {
        throw todo("714 Best Time to Buy and Sell Stock with Transaction Fee");
    }

    // 打家劫舍
    public int robLine(int[] nums) {
        throw todo("198 House Robber");
    }

    public int robCircle(int[] nums) {
        throw todo("213 House Robber II");
    }

    public int robTree(TreeNode root) {
        throw todo("337 House Robber III");
    }

    private static UnsupportedOperationException todo(String topic) {
        return new UnsupportedOperationException("TODO 默写: " + topic);
    }
}
