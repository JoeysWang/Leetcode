import java.util.Arrays;

public class Main1143最长公共子序列 {

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
    public class Solution {


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
