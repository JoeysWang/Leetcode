public class Main121买卖股票的最佳时机 {


    public static void main(String[] args) {
        int[] a = new int[]{7, 1, 5, 3, 6, 4};

        Solution solution = new Solution();
        System.out.println("max: " + solution.maxProfit(a));
    }

    static class Solution {


        /**
         * 动态规划
         * dp[i][j]
         *    dp[i]表示 (j=0没有股票， j=1有股票) 的最大值
         *
         * 状态转移方程
         *    dp[i][0] = max(昨天没有股票的最大值， 卖掉一股的最大值)
         *    dp[i][1] = max(昨天有股票的最大值(负的), 买一股的最大值(负的))  //从两个负数里取一个不那么负的
         *
         * [7,1,5,3,6,4]
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
                        -prices[i], //买一股
                        dp[i - 1][1]//维持现状
                );
            }


            for (int i = 0; i < prices.length; i++) {
                res = Math.max(res, dp[i][0]);
            }


            return res;
        }

        /**
         * dp[i][j][k]
         * i表示第i天， j=1表示持有股票,j=0表示不持有股票, k=1表示最大值,k=0表示最小值
         */
        public int maxProfitk(int[] prices) {

            int[][][] dp = new int[prices.length][2][2];
            int result = 0;


            dp[0][1][0] = prices[0];
            dp[0][1][1] = prices[0];
            dp[0][0][1] = 0;
            dp[0][0][0] = 0;


            for (int i = 2; i < prices.length; i++) {
                if (i == 5)
                    System.out.println("break ");


                //拥有股票的最大值
                dp[i][1][1] = Math.max(prices[i], dp[i - 1][1][1]);

                //拥有股票的最小值
                dp[i][1][0] = Math.min(prices[i], dp[i - 1][1][0]);

                //没有股票的最大值
                dp[i][0][1] = Math.max(prices[i] - dp[i - 2][1][0], dp[i - 1][0][1]);
                //没有股票的最小值
                dp[i][0][0] = prices[i] - dp[i - 2][1][1];

            }


            for (int i = 0; i < prices.length; i++) {
                result = Math.max(result, dp[i][0][1]);
            }


            return result;
        }
    }
}
