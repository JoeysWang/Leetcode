import java.util.Collections;

public class Main34在排序数组中查找元素的第一个和最后一个位置 {

    /**
     * 给定一个按照升序排列的整数数组 nums，和一个目标值 target。找出给定目标值在数组中的开始位置和结束位置。
     * <p>
     * 如果数组中不存在目标值 target，返回 [-1, -1]。
     * 输入：nums = [5,7,7,8,8,10], target = 8
     * 输出：[3,4]
     */

    public static void main(String[] args) {
        int nums[] = {5, 7, 7, 8, 8, 10};
        Solution solution = new Solution();

        int[] range = solution.searchRange(nums, 8);
        System.out.println("range: " + range[0] + " " + range[1]);

        test();
    }

    public static void test() {
        TestUtil.reset();
        Solution solution = new Solution();

        // 测试用例1: 示例用例
        TestUtil.assertArrayEquals(new int[]{3, 4}, solution.searchRange(new int[]{5, 7, 7, 8, 8, 10}, 8), "示例: target=8");

        // 测试用例2: 目标值不存在
        TestUtil.assertArrayEquals(new int[]{-1, -1}, solution.searchRange(new int[]{5, 7, 7, 8, 8, 10}, 6), "边界: target不存在");

        // 测试用例3: 空数组
        TestUtil.assertArrayEquals(new int[]{-1, -1}, solution.searchRange(new int[]{}, 0), "边界: 空数组");

        // 测试用例4: 只有一个元素且匹配
        TestUtil.assertArrayEquals(new int[]{0, 0}, solution.searchRange(new int[]{1}, 1), "边界: 单元素匹配");

        // 测试用例5: 所有元素相同
        TestUtil.assertArrayEquals(new int[]{0, 2}, solution.searchRange(new int[]{8, 8, 8}, 8), "普通: 全部相同");

        TestUtil.printSummary();
    }

    private static class Solution {

        /**
         * 思路：
         * 分别查找左边第一个符合的，和右边第一个符合的，使用二分查找
         */
        public int[] searchRange(int[] nums, int target) {
            int leftIdx = find(nums, target, true);
            int rightIdx = find(nums, target, false);

            if (leftIdx >= 0 && leftIdx < nums.length && rightIdx < nums.length)
                return new int[]{leftIdx, rightIdx};
            return new int[]{-1, -1};

        }

        private int find(int[] nums, int target, boolean lower) {
            int start = 0;
            int end = nums.length;
            int res = -1;

            while (start <= end) {

                int mid = (end - start) / 2 + start;
                if (mid>=nums.length)
                    break;
                if (nums[mid] < target) {
                    start = mid + 1;
                }
                if (nums[mid] > target) {
                    end = mid - 1;
                }
                if (nums[mid] == target) {
                    res = mid;
                    if (lower) {
                        end = end - 1;
                    } else
                        start = start + 1;
                }
            }

            return res;
        }


    }
}
