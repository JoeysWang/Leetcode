import java.util.Arrays;

public class Main88合并连个有序数组 {
    public static void main(String[] args) {

        Solution solution = new Solution();
        int[] a = new int[]{0};
        int[] b = new int[]{1};

        solution.merge(a, 0, b, 1);

        System.out.println(Arrays.toString(a));
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
