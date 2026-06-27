import java.util.Arrays;

public class Main88合并连个有序数组 {
    public static void main(String[] args) {

        Solution solution = new Solution();
        int[] a = new int[]{0};
        int[] b = new int[]{1};

        solution.merge(a, 0, b, 1);

        System.out.println(Arrays.toString(a));
        test();
    }

    public static void test() {
        TestUtil.reset();
        Solution solution = new Solution();

        // 测试用例1: 示例用例
        int[] nums1_1 = {1, 2, 3, 0, 0, 0};
        int[] nums2_1 = {2, 5, 6};
        solution.merge(nums1_1, 3, nums2_1, 3);
        TestUtil.assertArrayEquals(new int[]{1, 2, 2, 3, 5, 6}, nums1_1, "示例用例: [1,2,3]+[2,5,6]");

        // 测试用例2: nums1为空（m=0）
        int[] nums1_2 = {0};
        int[] nums2_2 = {1};
        solution.merge(nums1_2, 0, nums2_2, 1);
        TestUtil.assertArrayEquals(new int[]{1}, nums1_2, "边界用例: nums1为空");

        // 测试用例3: nums2为空（n=0）
        int[] nums1_3 = {1};
        int[] nums2_3 = {};
        solution.merge(nums1_3, 1, nums2_3, 0);
        TestUtil.assertArrayEquals(new int[]{1}, nums1_3, "边界用例: nums2为空");

        // 测试用例4: 两个数组完全交错
        int[] nums1_4 = {1, 3, 5, 0, 0, 0};
        int[] nums2_4 = {2, 4, 6};
        solution.merge(nums1_4, 3, nums2_4, 3);
        TestUtil.assertArrayEquals(new int[]{1, 2, 3, 4, 5, 6}, nums1_4, "普通用例: 交错合并");

        // 测试用例5: nums2所有元素都小于nums1
        int[] nums1_5 = {4, 5, 6, 0, 0, 0};
        int[] nums2_5 = {1, 2, 3};
        solution.merge(nums1_5, 3, nums2_5, 3);
        TestUtil.assertArrayEquals(new int[]{1, 2, 3, 4, 5, 6}, nums1_5, "普通用例: nums2全部更小");

        TestUtil.printSummary();
    }

    static class Solution {

        /**
         * 使用双指针 ，一个nums1的结尾， 一个在nums1+nums2的结尾，
         */
        public void merge(int[] nums1, int m, int[] nums2, int n) {

            int p1 = m - 1;
            int p2 = n - 1;

            while (p1 >= 0 && p2 >= 0) {
                int a = nums1[p1];
                int b = nums2[p2];

                if (a < b) {
                    nums1[p1 + p2 + 1] = nums2[p2];
                    p2--;
                } else {
                    nums1[p1 + p2 + 1] = nums1[p1];
                    p1--;
                }
            }
            if (p2 >= 0) {
                for (int i = 0; i <= p2; i++) {
                    nums1[i] = nums2[i];
                }
            }

        }

    }
}
