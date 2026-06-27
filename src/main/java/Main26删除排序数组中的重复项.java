import java.util.Arrays;

public class Main26删除排序数组中的重复项 {


    public static void main(String[] args) {


        Solution solution = new Solution();

//        System.out.println(Arrays.toString(solution.removeDuplicates(
//                new int[]{0, 0, 1, 1, 2, 3, 4}
//                )));
        test();
    }

    public static void test() {
        TestUtil.reset();
        Solution solution = new Solution();

        // 测试用例1: 示例用例
        int[] nums1 = {1, 1, 2};
        int len1 = solution.removeDuplicates(nums1);
        TestUtil.assertEquals(2, len1, "示例: [1,1,2] 长度");
        TestUtil.assertArrayEquals(new int[]{1, 2}, Arrays.copyOf(nums1, len1), "示例: [1,1,2] 内容");

        // 测试用例2: 多个重复
        int[] nums2 = {0, 0, 1, 1, 1, 2, 2, 3, 3, 4};
        int len2 = solution.removeDuplicates(nums2);
        TestUtil.assertEquals(5, len2, "多个重复: 长度");
        TestUtil.assertArrayEquals(new int[]{0, 1, 2, 3, 4}, Arrays.copyOf(nums2, len2), "多个重复: 内容");

        // 测试用例3: 无重复
        int[] nums3 = {1, 2, 3};
        int len3 = solution.removeDuplicates(nums3);
        TestUtil.assertEquals(3, len3, "无重复: 长度");
        TestUtil.assertArrayEquals(new int[]{1, 2, 3}, Arrays.copyOf(nums3, len3), "无重复: 内容");

        // 测试用例4: 全部相同
        int[] nums4 = {2, 2, 2, 2};
        int len4 = solution.removeDuplicates(nums4);
        TestUtil.assertEquals(1, len4, "全部相同: 长度");
        TestUtil.assertArrayEquals(new int[]{2}, Arrays.copyOf(nums4, len4), "全部相同: 内容");

        TestUtil.printSummary();
    }

    /**
     * 双指针
     */
    static class Solution {
        public int removeDuplicates(int[] nums) {

            int i = 0;

            for (int j = 1; j < nums.length; j++) {
                if (nums[i] == nums[j]) {
                    continue;
                } else {
                    i++;
                    nums[i] = nums[j];
                }

            }


            return i + 1;
        }
    }

    static class Solution2 {


        public int removeDuplicates(int[] a) {
            int slow = 0;
            int fast = 1;

            if (a.length == 0)
                return 0;


            while (fast < a.length) {
                if (a[slow] != a[fast]) {
                    slow++;
                    a[slow] = a[fast];

                }
                fast++;
            }
            return slow + 1;
        }
    }

}
