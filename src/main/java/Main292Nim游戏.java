

public class Main292Nim游戏 {
//    你和你的朋友，两个人一起玩 Nim 游戏：桌子上有一堆石头，每次你们轮流拿掉 1 - 3 块石头。 拿掉最后一块石头的人就是获胜者。你作为先手。
//
//    你们是聪明人，每一步都是最优解。 编写一个函数，来判断你是否可以在给定石头数量的情况下赢得游戏。
//
//    示例:
//
//    输入: 4
//    输出: false
//    解释: 如果堆中有 4 块石头，那么你永远不会赢得比赛；
//                 因为无论你拿走 1 块、2 块 还是 3 块石头，最后一块石头总是会被你的朋友拿走。

    public static void main(String[] args) {
        Solution solution = new Solution();

        System.out.println(solution.canWinNim(7));
        test();

    }

    public static void test() {
        TestUtil.reset();
        Solution solution = new Solution();

        // 测试用例1: 示例用例 n=4 -> false
        TestUtil.assertFalse(solution.canWinNim(4), "示例用例: n=4 无法获胜");

        // 测试用例2: 边界用例 n=1 -> true
        TestUtil.assertTrue(solution.canWinNim(1), "边界用例: n=1 先手拿1块获胜");

        // 测试用例3: 边界用例 n=2 -> true
        TestUtil.assertTrue(solution.canWinNim(2), "边界用例: n=2 先手拿2块获胜");

        // 测试用例4: 边界用例 n=3 -> true
        TestUtil.assertTrue(solution.canWinNim(3), "边界用例: n=3 先手拿3块获胜");

        // 测试用例5: 普通用例 n=5 -> true (5不是4的倍数)
        TestUtil.assertTrue(solution.canWinNim(5), "普通用例: n=5 可以获胜");

        // 测试用例6: 普通用例 n=8 -> false (8是4的倍数)
        TestUtil.assertFalse(solution.canWinNim(8), "普通用例: n=8 无法获胜");

        // 测试用例7: 普通用例 n=7 -> true
        TestUtil.assertTrue(solution.canWinNim(7), "普通用例: n=7 可以获胜");

        // 测试用例8: 边界用例 n=0 -> false
        TestUtil.assertFalse(solution.canWinNim(0), "边界用例: n=0 无石头");

        // 验证 canWinNim2 方法一致性
        for (int i = 0; i <= 20; i++) {
            TestUtil.assertEquals(solution.canWinNim(i), solution.canWinNim2(i), "一致性验证: n=" + i);
        }

        TestUtil.printSummary();
    }

    static class Solution {

        /**
         * dp 博弈型动态规划
         */
        public boolean canWinNim(int n) {
            if (n == 0) return false;
            if (n <= 3) return true;


            boolean[] dp = new boolean[n + 1];

            dp[0] = false;
            dp[1] = true;
            dp[2] = true;
            dp[3] = true;


            for (int i = 4; i <= n; i++) {
                dp[i] = !(dp[i - 1] && dp[i - 2] && dp[i - 3]);
            }

            return dp[n];
        }

        /**
         * 如果堆中石头的数量 nn 不能被 44 整除，那么你总是可以赢得 Nim 游戏的胜利。
         */
        public boolean canWinNim2(int n) {
            return (n & 3 )!=0;
        }
    }
}
