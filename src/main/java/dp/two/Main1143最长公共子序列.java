package dp.two;

import tree.util.TestUtil;
import java.util.Arrays;

public class Main1143最长公共子序列 {

    public static void main(String[] args) {
        test();
    }

    /**
     * 给定两个字符串text1 和text2，返回这两个字符串的最长公共子序列的长度。
     * <p>
     * 一个字符串的子序列是指这样一个新的字符串：它是由原字符串在不改变字符的相对顺序的情况下删除某些字符（也可以不删除任何字符）后组成的新字符串。
     * 例如，"ace" 是 "abcde" 的子序列，但 "aec" 不是 "abcde" 的子序列。两个字符串的「公共子序列」是这两个字符串所共同拥有的子序列。
     * <p>
     * 若这两个字符串没有公共子序列，则返回 0。
     * <p>
     * 输入：text1 = "abcde", text2 = "ace"
     * 输出：3
     * 解释：最长公共子序列是 "ace"，它的长度为 3。
     */
    public static void test() {
        TestUtil.reset();
        Solution solution = new Solution();

        // 测试用例1: 示例用例
        TestUtil.assertEquals(3, solution.longestCommonSubsequence("abcde", "ace"), "示例用例: abcde和ace");

        // 测试用例2: 完全相同
        TestUtil.assertEquals(3, solution.longestCommonSubsequence("abc", "abc"), "边界用例: 完全相同");

        // 测试用例3: 无公共子序列
        TestUtil.assertEquals(0, solution.longestCommonSubsequence("abc", "def"), "边界用例: 无公共子序列");

        // 测试用例4: 一个为空
        TestUtil.assertEquals(0, solution.longestCommonSubsequence("", "abc"), "边界用例: 空字符串");

        // 测试用例5: 普通用例
        TestUtil.assertEquals(2, solution.longestCommonSubsequence("abc", "ab"), "普通用例: abc和ab");

        // 测试用例6: 较长字符串
        TestUtil.assertEquals(4, solution.longestCommonSubsequence("abcd", "abcd"), "普通用例: 完全匹配");

        TestUtil.printSummary();
    }

    public static class Solution {


        public int longestCommonSubsequence(String text1, String text2) {
            int n = text1.length();
            int m = text2.length();
            if (n == 0 || m == 0) return 0;

            int[][] dp = new int[n + 1][m + 1];
            for (int i=1;i<=n;i++) {

                for (int j=1;j<=m;j++) {

                    if (text1.charAt(i - 1) == text2.charAt(j - 1)) {

                        dp[i][j] = dp[i - 1][j - 1] + 1;
                    } else {

                        dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                    }
                }
            }
            return dp[n][m];
        }

    }

}
