import java.util.Arrays;

public class Main283移动零 {
    /**
     * 给定一个数组 nums，编写一个函数将所有 0 移动到数组的末尾，同时保持非零元素的相对顺序。
     * 请注意 ，必须在不复制数组的情况下原地对数组进行操作。
     * 示例 1:
     * 输入: nums = [0,1,0,3,12]
     * 输出: [1,3,12,0,0]
     * 
     * 示例 2:
     * 输入: nums = [0]
     * 输出: [0]
     * 
     * @param args
     */
    public static void main(String[] args) {
        Solution solution = new Solution();

        int[] nums = new int[] { 0, 1, 0, 3, 12 };
        solution.moveZeroes(nums);

        System.out.println(Arrays.toString(nums));
        test();
    }

    public static void test() {
        TestUtil.reset();
        Solution solution = new Solution();

        // 测试用例1: 示例用例 [0,1,0,3,12] -> [1,3,12,0,0]
        int[] nums1 = {0, 1, 0, 3, 12};
        solution.moveZeroes(nums1);
        TestUtil.assertArrayEquals(new int[]{1, 3, 12, 0, 0}, nums1, "示例用例: [0,1,0,3,12]");

        // 测试用例2: 示例用例 [0] -> [0]
        int[] nums2 = {0};
        solution.moveZeroes(nums2);
        TestUtil.assertArrayEquals(new int[]{0}, nums2, "示例用例: [0]");

        // 测试用例3: 边界用例 - 全零
        int[] nums3 = {0, 0, 0};
        solution.moveZeroes(nums3);
        TestUtil.assertArrayEquals(new int[]{0, 0, 0}, nums3, "边界用例: 全零数组");

        // 测试用例4: 边界用例 - 无零
        int[] nums4 = {1, 2, 3};
        solution.moveZeroes(nums4);
        TestUtil.assertArrayEquals(new int[]{1, 2, 3}, nums4, "边界用例: 无零数组");

        // 测试用例5: 普通用例 - 末尾有零
        int[] nums5 = {1, 0, 2, 0, 3, 0};
        solution.moveZeroes(nums5);
        TestUtil.assertArrayEquals(new int[]{1, 2, 3, 0, 0, 0}, nums5, "普通用例: [1,0,2,0,3,0]");

        // 测试用例6: 边界用例 - 单元素非零
        int[] nums6 = {5};
        solution.moveZeroes(nums6);
        TestUtil.assertArrayEquals(new int[]{5}, nums6, "边界用例: 单元素非零");

        TestUtil.printSummary();
    }

    static class Solution {
        /**
         * 双指针，start指针用来给满足条件的座位赋值，i指针用来扫描池子
         */
        public void moveZeroes(int[] nums) {
            if (nums == null || nums.length == 0) {
                return;
            }
            int start = 0;
            for (int i = 0; i < nums.length; i++) {
                if (nums[i] != 0) {
                    nums[start] = nums[i];
                    start++;
                }
            }
            for (int j = start; j < nums.length; j++) {
                nums[j] = 0;
            }

        }

    }
}
