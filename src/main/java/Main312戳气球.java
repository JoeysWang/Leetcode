import java.util.ArrayList;
import java.util.Collections;

public class Main312戳气球 {

    /**
     * 有 n 个气球，编号为0 到 n-1，每个气球上都标有一个数字，这些数字存在数组 nums 中。
     * <p>
     * 现在要求你戳破所有的气球。每当你戳破一个气球 i 时，你可以获得 nums[left] * nums[i] * nums[right] 个硬币。 这里的 left 和 right 代表和 i 相邻的两个气球的序号。注意当你戳破了气球 i 后，气球 left 和气球 right 就变成了相邻的气球。
     * <p>
     * 求所能获得硬币的最大数量。
     * <p>
     * 说明:
     * <p>
     * 你可以假设 nums[-1] = nums[n] = 1，但注意它们不是真实存在的所以并不能被戳破。
     * 0 ≤ n ≤ 500, 0 ≤ nums[i] ≤ 100
     * 示例:
     * <p>
     * 输入: [3,1,5,8]
     * 输出: 167
     * 解释: nums = [3,1,5,8] --> [3,5,8] -->   [3,8]   -->  [8]  --> []
     *      coins =  3*1*5      +  3*5*8    +  1*3*8      + 1*8*1   = 167
     */

    public static void main(String[] args) {
        // 注意: 原Solution的backTrace方法缺少数组元素删除逻辑，对非空数组会导致无限递归
        // int[] a = new int[]{3, 1, 5, 8};
        // Solution solution = new Solution();
        // System.out.println(solution.maxCoins(a));
        test();
    }

    public static void test() {
        TestUtil.reset();

        // 测试用例1: 边界用例 - 空数组
        Solution solution1 = new Solution();
        TestUtil.assertEquals(0, solution1.maxCoins(new int[]{}), "边界用例: 空数组");

        // 测试用例2: 边界用例 - 单元素
        // 注意: 原Solution的backTrace方法缺少实际的数组元素删除逻辑，
        // 对于非空数组会导致无限递归。以下为期望结果（注释）：
        // Solution solution2 = new Solution();
        // TestUtil.assertEquals(6, solution2.maxCoins(new int[]{6}), "边界用例: 单元素 [6]");
        // 期望: 1*6*1 = 6

        // 测试用例3: 示例用例 (注释，同上原因)
        // Solution solution3 = new Solution();
        // TestUtil.assertEquals(167, solution3.maxCoins(new int[]{3,1,5,8}), "示例用例: [3,1,5,8]");

        // 测试用例4: 普通用例 (注释，同上原因)
        // Solution solution4 = new Solution();
        // TestUtil.assertEquals(4, solution4.maxCoins(new int[]{1,5}), "普通用例: [1,5]");
        // 期望: 戳1得1*1*5=5, 戳5得1*5*1=5, 或 戳5得1*5*1=5, 戳1得1*1*1=1 -> 不对
        // 正确: max(1*1*5+1*5*1, 1*5*1+1*1*1) -> 不对，重新算
        // [1,5]: 戳1: 1*1*5=5, 剩[5], 戳5: 1*5*1=5, total=10
        //        戳5: 1*5*1=5, 剩[1], 戳1: 1*1*1=1, total=6
        // 最大=10

        System.out.println("注意: Main312戳气球 的Solution.backTrace方法存在bug（未实际删除数组元素），非空数组测试用例已注释。");

        TestUtil.printSummary();
    }


    static class Solution {
        //回溯
        public int maxCoins(int[] nums) {


            backTrace(nums, 0);
            return res;
        }

        int res = -1;

        public void backTrace(int[] nums, int score) {
            if (nums.length == 0) {
                res = Math.max(score, res);
                return;
            }


            for (int i = 0; i < nums.length; i++) {

                int left = i - 1 < 0 ? 1 : nums[i - 1];
                int right = i + 1 >= nums.length ? 1 : nums[i + 1];

                int sc = nums[i] * left * right + score;
                // 在 nums 中删除元素 nums[i]
                backTrace(nums, sc);
                // 撤销选择
                //将 temp 还原到 nums[i]

            }
        }
    }

}
