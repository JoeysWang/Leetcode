package sort;

import java.util.Arrays;

/**
 * 归并排序
 *
 * 记忆：先拆，再合。
 * 拆：不断从中间劈成左右两半，直到只剩 1 个元素。
 * 合：两个有序小数组合并成一个有序大数组。
 *
 * 时间复杂度：任何情况都是 O(nlogn)
 * 空间复杂度：O(n)，非原地排序，稳定
 */
public class MergeSort {
    public static void main(String[] args) {

        MergeSortSolution solution = new MergeSortSolution();
        int[] result = solution.mergeSort(new int[]{6, 1241, 2, 54, 3213, 9, 214, 21, 12, 3, 4, 55, 23, 2});
        System.out.println("result  " + Arrays.toString(result));

    }

    public static class MergeSortSolution {

        public int[] mergeSort(int[] nums) {

            if (nums == null || nums.length < 2) return nums;

            return sortOrDivide(nums, 0, nums.length - 1);
        }

        private int[] sortOrDivide(int[] nums, int left, int right) {

            // 递归出口：一个元素天然有序。
            if (left == right) {
                return new int[]{nums[left]};
            }

            int middle = left + (right - left) / 2;
            int[] leftNums = sortOrDivide(nums, left, middle);
            int[] rightNums = sortOrDivide(nums, middle + 1, right);

            return merge(leftNums, rightNums);
        }

        private int[] merge(int[] left, int[] right) {
            int leftLength = left.length;
            int rightLength = right.length;

            int[] nums = new int[leftLength + rightLength];

            int position = 0;
            int i = 0;
            int j = 0;

            // 两个数组都已排好序，每次拿较小的头部元素放入结果。
            while (i != leftLength && j != rightLength) {
                if (left[i] <= right[j]) {
                    nums[position] = left[i];
                    i++;
                } else {
                    nums[position] = right[j];
                    j++;
                }
                position++;
            }

            // 其中一边用完后，把另一边剩下的元素直接接到结果后面。
            while (i != leftLength) {
                nums[position] = left[i];
                i++;
                position++;
            }
            while (j != rightLength) {
                nums[position] = right[j];
                j++;
                position++;
            }

            return nums;
        }


    }

}
