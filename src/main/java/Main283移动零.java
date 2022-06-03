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
