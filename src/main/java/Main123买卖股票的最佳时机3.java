

public class Main123买卖股票的最佳时机3 {

    /**
     * 可以买卖2次
     */
    public static void main(String[] args) {


//        int[] a = new int[]{3, 3, 5, 0, 0, 3, 1, 4};
//        Solution solution = new Solution();
//        System.out.println("max: " + solution.maxProfit(a));
//

        Tess tess =new Tess();
        tess.a = "a";
        tess.b = "b";
        swap(tess.a,tess.b);
        System.out.println("max: " + tess.a+" "+tess.b);
        test();
    }

    public static void test() {
        TestUtil.reset();
        Solution solution = new Solution();

        // 测试用例1: 示例用例 - [3,3,5,0,0,3,1,4]
        TestUtil.assertEquals(6, solution.maxProfit(new int[]{3, 3, 5, 0, 0, 3, 1, 4}), "示例用例: [3,3,5,0,0,3,1,4]");

        // 测试用例2: 示例用例 - 一直上涨
        TestUtil.assertEquals(4, solution.maxProfit(new int[]{1, 2, 3, 4, 5}), "示例用例: 一直上涨");

        // 测试用例3: 示例用例 - 一直下跌
        TestUtil.assertEquals(0, solution.maxProfit(new int[]{7, 6, 4, 3, 1}), "示例用例: 一直下跌");

        // 测试用例4: 边界用例 - 单个元素
        TestUtil.assertEquals(0, solution.maxProfit(new int[]{1}), "边界用例: 单个元素");

        // 测试用例5: 两次独立交易 (买1卖6=5, 买2卖9=7, 共12)
        TestUtil.assertEquals(12, solution.maxProfit(new int[]{1, 5, 3, 6, 2, 9}), "普通用例: 两次交易");

        // 测试用例6: 两次交易更优 (买1卖4=3, 买2卖5=3, 共6)
        TestUtil.assertEquals(6, solution.maxProfit(new int[]{1, 2, 4, 2, 5}), "普通用例: 两次交易更优");

        TestUtil.printSummary();
    }

    private static void swap(String a, String b) {
        String temp =a;
        a=b;
        b=temp;
    }

    static class Tess{
        String a;
        String b;
    }

    static class Solution {
        /**
         * dp[i][j][k]
         * i表示第i天， j=1表示持有股票,j=0表示不持有股票,
         * k 表示买了多少次
         */
//        public int maxProfit(int[] prices) {
//            if (prices.length < 2) return 0;
//            int k = 2;
//            int[][][] dp = new int[prices.length][2][k];
//            dp[0][0][0] = 0;
//            dp[0][1][0] = -prices[0];
//
//
//            for (int i = 1; i < prices.length; i++) {
//
//
//                for (int _k = 0; _k < k; _k++) {
//
//
//                    dp[i][0][_k] = Math.max(
//                            dp[i - 1][0][_k],//维持现状
//                            prices[i] + dp[i - 1][1][_k] //卖掉一股
//                    );
//                    if (_k == 0)
//                        dp[i][1][_k] = Math.max(
//                                -prices[i], //买一股，因为之前从来没买过，所以不用关心之前的剩余财富
//                                dp[i - 1][1][0]//维持现状
//                        );
//                    else
//                        dp[i][1][_k] = Math.max(
//                                dp[i - 1][0][_k - 1] - prices[i], //买一股之前必须从没有股票的情况里去买
//                                dp[i - 1][1][_k]//维持现状
//                        );
//
//                    System.out.println("k=" + _k + " dp[" + i + "][不持有]=" + dp[i][0][_k] + "   dp[" + i + "][持有]=" + dp[i][1][_k] + " #");
//
//                }
//
//
//            }
//
//            int res = 0;
//            for (int _k = 0; _k < k; _k++) {
//
//                res = Math.max(dp[prices.length - 1][0][_k], res);
//
//            }
//
//            //返回最后一天没有股票
//            return res;
//        }
        public int maxProfit(int[] prices) {
            if (prices.length < 2) return 0;
            int k = 2;
            int dp[][][] = new int[prices.length][2][k];
            for (int i = 0; i < k; i++) {
                dp[0][0][i] = 0;
                dp[0][1][i] = -prices[0];
            }


            for (int i = 1; i < prices.length; i++) {

                for (int j = 0; j < k; j++) {
                    dp[i][0][j] = Math.max(
                            dp[i - 1][0][j],
                            dp[i - 1][1][j] + prices[i]
                    );
                    if (j == 0) {
                        dp[i][1][j] = Math.max(
                                dp[i - 1][1][j],
                                -prices[i]
                        );
                    } else
                        dp[i][1][j] = Math.max(
                                dp[i - 1][1][j],
                                dp[i - 1][0][j - 1] - prices[i]
                        );
                }
            }
            int res = 0;
            for (int i = 0; i < prices.length; i++) {
                for (int j = 0; j < k; j++) {
                    res = Math.max(
                            dp[i][0][j],
                            res
                    );
                }
            }
            return res;
        }
    }
}
