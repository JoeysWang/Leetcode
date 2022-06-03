import java.util.Arrays;

public class Main238除自身以外数组的乘积 {
    /**
     * 给你一个整数数组 nums，返回 数组 answer ，其中 answer[i] 等于 nums 中除 nums[i] 之外其余各元素的乘积 。
     * 
     * 题目数据 保证 数组 nums之中任意元素的全部前缀元素和后缀的乘积都在  32 位 整数范围内。
     * 
     * 请不要使用除法，且在 O(n) 时间复杂度内完成此题。
     * 
     * 示例 1:
     * 
     * 输入: nums = [1,2,3,4]
     * 输出: [24,12,8,6]
     * 
     */
    public static void main(String[] args) {
        Solution solution = new Solution();

        int[] nums = new int[] { 1, 2, 3, 4 };// [24,12,8,6]
        // int[] nums = new int[] {-1,1,0,-3,3};//[0,0,9,0,0]
        int[] res = solution.productExceptSelf(nums);

        System.out.println(Arrays.toString(res));
    }

    static class Solution {
        /**
         * 扫描两遍
         * int res[]；
         * 第一遍：
         * res[i] = nums[i]左边的相乘
         * 第二遍：
         * res[i] - nums[i]右边的相乘 * res[i]
         */
        public int[] productExceptSelf(int[] nums) {
            int[] res = new int[nums.length];
            res[0] = 1;
            for (int i = 1; i < nums.length; i++) {
                res[i] = res[i - 1] * nums[i - 1];
            }

            int right = 1;
            for (int i = nums.length - 1; i >= 0; i--) {
                res[i] = res[i] * right;
                right = right * nums[i];
            }

            return res;
        }
    }
}
