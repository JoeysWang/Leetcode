

public class Main198打家劫舍 {

    /**
     * 你是一个专业的小偷，计划偷窃沿街的房屋。每间房内都藏有一定的现金，
     * 影响你偷窃的唯一制约因素就是相邻的房屋装有相互连通的防盗系统，
     * 如果两间相邻的房屋在同一晚上被小偷闯入，系统会自动报警。
     *
     * 给定一个代表每个房屋存放金额的非负整数数组，计算你 不触动警报装置的情况下 ，一夜之内能够偷窃到的最高金额。
     *
     * 示例 1：
     * <p>
     * 输入：[1,2,3,1]
     * 输出：4
     * 解释：偷窃 1 号房屋 (金额 = 1) ，然后偷窃 3 号房屋 (金额 = 3)。
     *      偷窃到的最高金额 = 1 + 3 = 4 。
     * 示例 2：
     * <p>
     * 输入：[2,7,9,3,1]
     * 输出：12
     * 解释：偷窃 1 号房屋 (金额 = 2), 偷窃 3 号房屋 (金额 = 9)，接着偷窃 5 号房屋 (金额 = 1)。
     *      偷窃到的最高金额 = 2 + 9 + 1 = 12 。
     */

    public static void main(String[] args) {
        int a[] = new int[]{2, 7, 9, 3, 1};
        Solution solution = new Solution();
        System.out.println(solution.rob2(a));
        test();
    }

    private static class Solution {


        public int rob(int[] nums) {
            //从前往后递归
            return dp(nums, 0);
        }


        public int dp(int[] nums, int start) {
            if (start >= nums.length) return 0;

            return Math.max(
                    dp(nums, start + 1),
                    dp(nums, start + 2) + nums[start]
            );
        }

        public int rob2(int[] nums) {
            //从后往前

            int dp[] = new int[nums.length + 2];
            dp[nums.length + 1] = 0;
            dp[nums.length] = 0;

            //状态循环 i 代表从第i开始抢，最多能抢到多少
            for (int i = nums.length - 1; i >= 0; i--) {

                dp[i] = Math.max(
                        dp[i + 1],
                        dp[i + 2] + nums[i]
                );
            }

            return dp[0];

        }
    }

    public static void test() {
        TestUtil.reset();
        Solution solution = new Solution();

        // 测试用例1: 示例用例 [1,2,3,1] → 4
        TestUtil.assertEquals(4, solution.rob2(new int[]{1, 2, 3, 1}), "示例用例 [1,2,3,1]");

        // 测试用例2: 示例用例 [2,7,9,3,1] → 12
        TestUtil.assertEquals(12, solution.rob2(new int[]{2, 7, 9, 3, 1}), "示例用例 [2,7,9,3,1]");

        // 测试用例3: 边界用例 - 单个元素
        TestUtil.assertEquals(0, solution.rob2(new int[]{}), "边界用例 空数组");

        // 测试用例4: 边界用例 - 单个元素
        TestUtil.assertEquals(5, solution.rob2(new int[]{5}), "边界用例 单个元素 [5]");

        // 测试用例5: 两个元素取较大
        TestUtil.assertEquals(2, solution.rob2(new int[]{1, 2}), "两个元素 [1,2]");

        // 测试用例6: 普通用例 [2,1,1,2] → 4
        TestUtil.assertEquals(4, solution.rob2(new int[]{2, 1, 1, 2}), "普通用例 [2,1,1,2]");

        TestUtil.printSummary();
    }
}
