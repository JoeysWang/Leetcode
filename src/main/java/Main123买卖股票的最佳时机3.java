

public class Main123买卖股票的最佳时机3 {

    /**
     * 可以买卖2次
     */
    public static void main(String[] args) {


        int[] a = new int[]{7, 1, 5, 3, 6, 4};
        Solution solution = new Solution();
        System.out.println("max: " + solution.maxProfit(a));
    }

    static class Solution {
        /**
         * dp[i][j][k]
         * i表示第i天， j=1表示持有股票,j=0表示不持有股票,
         * k 表示买了多少次
         */
        public int maxProfit(int[] prices) {
            if (prices.length < 2) return 0;
            int k = 2;
            int[][][] dp = new int[prices.length][2][k];
            dp[0][0][0] = 0;
            dp[0][1][0] = -prices[0];


            for (int i = 1; i < prices.length; i++) {


                for (int _k = 0; _k < k; _k++) {


                    dp[i][0][_k] = Math.max(
                            dp[i - 1][0][_k],//维持现状
                            prices[i] + dp[i - 1][1][_k] //卖掉一股
                    );
                    if (_k == 0)
                        dp[i][1][_k] = Math.max(
                                -prices[i], //买一股，因为之前从来没买过，所以不用关心之前的剩余财富
                                dp[i - 1][1][0]//维持现状
                        );
                    else
                        dp[i][1][_k] = Math.max(
                                dp[i - 1][0][_k - 1] - prices[i], //买一股之前必须从没有股票的情况里去买
                                dp[i - 1][1][_k]//维持现状
                        );

                    System.out.println("k=" + _k + " dp[" + i + "][不持有]=" + dp[i][0][_k] + "   dp[" + i + "][持有]=" + dp[i][1][_k] + " #");

                }


            }

            int res = 0;
            for (int _k = 0; _k < k; _k++) {

                res = Math.max(dp[prices.length - 1][0][_k], res);

            }

            //返回最后一天没有股票
            return res;
        }

    }
}
