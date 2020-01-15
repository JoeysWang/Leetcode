public class Main122买卖股票的最佳时机2 {

    /**
     * 可以买卖无数次 ，但是要卖掉才能买
     */
    public static void main(String[] args) {


        int[] a = new int[]{7, 1, 5, 3, 6, 4};
        Solution solution = new Solution();
        System.out.println("max: " + solution.maxProfit(a));
    }

    static class Solution {
        /**
         * dp[i][j]
         * i表示第i天， j=1表示持有股票,j=0表示不持有股票,
         *
         */
        public int maxProfit(int[] prices) {
            if (prices.length < 2) return 0;
            int[][] dp = new int[prices.length][2];
            dp[0][0] = 0;
            dp[0][1] = -prices[0];

            int res = 0;

            for (int i = 1; i < prices.length; i++) {

                dp[i][0] = Math.max(
                        dp[i - 1][0],  //维持现状
                        prices[i] + dp[i - 1][1] //卖股票
                );

                dp[i][1] = Math.max(
                        dp[i - 1][0] - prices[i], //买一股之前必须从没有股票的情况里去买
                        dp[i - 1][1]//维持现状
                );
                System.out.println("dp[" + i + "][0]=" + dp[i][0] + "   dp[" + i + "][1]=" + dp[i][1] + " #");
            }

            //返回最后一天没有股票
            return dp[prices.length - 1][0];
        }

    }
}
