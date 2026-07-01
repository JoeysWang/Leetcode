package sort;

import java.util.Arrays;

/**
 * 快速排序
 * <p>
 * 记忆：选基准，分两边；左边小，右边大；再递归排左右。
 * 这里用第一个元素作基准，partition 返回基准最后所在下标。
 * <p>
 * 时间复杂度：平均 O(nlogn)，最坏 O(n^2)
 * 空间复杂度：平均 O(logn)，原地排序，不稳定
 */
public class QuickSort {

    public static void main(String[] args) {

        int[] a = new int[]{5, 8, 3, 4, 6, 1, 7};
        quickSort(a, a.length);
        System.out.println(Arrays.toString(a));
    }

    // 快速排序：n 表示参与排序的元素个数。
    public static void quickSort(int[] a, int n) {
        if (a == null || n <= 1) return;

        n = Math.min(n, a.length);
        quickSortInternally(a, 0, n - 1);
    }

    // 左右闭区间：[begin, end]。
    private static void quickSortInternally(int[] a, int begin, int end) {
        if (begin >= end) return;

        int pivot = partition(a, begin, end); // 获取分区点

        quickSortInternally(a, begin, pivot - 1);
        quickSortInternally(a, pivot + 1, end);
    }


    /**
     * partition：把数组切成三段。
     * [begin + 1, lessEnd] 是小于 pivotValue 的区间。
     * [lessEnd + 1, i) 是大于等于 pivotValue 的区间。
     * 扫描结束后，把 begin 上的基准值交换到 lessEnd，基准值就归位了。
     */
    private static int partition(int[] nums, int begin, int end) {
        int pivotValue = nums[begin];
        int lessEnd = begin;

        for (int i = begin + 1; i <= end; i++) {
            if (nums[i] < pivotValue) {
                lessEnd++;
                swap(nums, lessEnd, i);
            }
        }

        swap(nums, begin, lessEnd);

        return lessEnd;
    }


    private static void swap(int[] nums, int index1, int index2) {
        int temp = nums[index1];
        nums[index1] = nums[index2];
        nums[index2] = temp;
    }


}
