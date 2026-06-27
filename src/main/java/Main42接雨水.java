

public class Main42接雨水 {
    /**
     * 给定 n 个非负整数表示每个宽度为 1 的柱子的高度图，计算按此排列的柱子，下雨之后能接多少雨水。
     */
    public static void main(String[] args) {
        test();
    }

    public static void test() {
        TestUtil.reset();
        Solution solution = new Solution();

        // 测试用例1: 示例用例
        TestUtil.assertEquals(6, solution.trap(new int[]{0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1}), "示例: 标准高度图");

        // 测试用例2: 示例用例2
        TestUtil.assertEquals(9, solution.trap(new int[]{4, 2, 0, 3, 2, 5}), "示例: 高度图2");

        // 测试用例3: 边界用例 - 空数组
        TestUtil.assertEquals(0, solution.trap(new int[]{}), "边界: 空数组");

        // 测试用例4: 边界用例 - 无法积水
        TestUtil.assertEquals(0, solution.trap(new int[]{1, 2, 3, 4, 5}), "边界: 递增无法积水");

        // 测试用例5: 普通用例
        TestUtil.assertEquals(3, solution.trap(new int[]{3, 0, 3}), "普通: 简单积水");

        TestUtil.printSummary();
    }

    public static class Solution {

        /**
         * 使用双指针算法，单独计算当前柱子，(当前位置的左右最高的墙)取矮的-当前柱子高度
         * 用备忘录记住左右最低的墙
         */
        public int trap(int[] height) {

            int length = height.length;
            if (length <= 0) {
                return 0;
            }
            int l_max = height[0];
            int r_max = height[length - 1];

            int res = 0;

            int left = 0;
            int right = length - 1;

            while (left <= right) {
                l_max = Math.max(l_max, height[left]);

                r_max = Math.max(r_max, height[right]);

                if (l_max < r_max) {
                    res += l_max - height[left];
                    left++;
                } else {
                    res += r_max - height[right];
                    right--;
                }

            }
            return res;
        }
    }


}
