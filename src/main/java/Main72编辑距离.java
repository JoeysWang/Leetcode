

/**
 * 动态规划 DP
 * <p>
 * <p>
 * 给定两个单词 word1 和 word2，计算出将 word1 转换成 word2 所使用的最少操作数 。
 * <p>
 * 你可以对一个单词进行如下三种操作：
 * 插入一个字符
 * 删除一个字符
 * 替换一个字符
 * <p>
 * 示例 1:
 * 输入: word1 = "horse", word2 = "ros"
 * 输出: 3
 * 解释:
 * horse -> rorse (将 'h' 替换为 'r')
 * rorse -> rose (删除 'r')
 * rose -> ros (删除 'e')
 */
public class Main72编辑距离 {

    public static void main(String[] args) {
        int min = minDistance1("ac", "a");
        System.out.println("min " + min);
    }

    /**
     * 动态规划，自底向上
     * 状态定义:
     * DP[i][j]代表 word1的第i个字符 到 word2的第j个字符 的最小编辑距离
     * 转移方程:
     * DP[i][j] =
     * if(word1[i] == word2[j])
     * DP[i-1][j-1]
     * else
     * 1 + min(DP[i-1][j] , DP[i][j-1] , DP[i-1][j-1])
     * word1插入   , word1删除   , 直接替换
     */
    public static int minDistance1(String word1, String word2) {
        char[] word1char = word1.toCharArray();
        char[] word2char = word2.toCharArray();

        int[][] dp = new int[word1.length() + 1][word2.length() + 1];

        //当两者有一个字符串为空时， 返回另一个字符串长度
        if (word1char.length == 0) return word2char.length;
        if (word2char.length == 0) return word1char.length;


        for (int i = 1; i <= word1.length(); i++) {
            //初始化 没有字符 到i个字符的距离是 i
            dp[i][0] = i;
            for (int j = 1; j <= word2.length(); j++) {
                //初始化 没有字符 到j个字符的距离是 j
                dp[0][j] = j;
                if (word1char[i - 1] == word2char[j - 1]) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(Math.min(dp[i][j - 1], dp[i - 1][j]), dp[i - 1][j - 1]);
                }

            }
        }

        return dp[word1.length()][word2.length()];
    }


}
