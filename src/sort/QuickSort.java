package sort;

import java.util.Arrays;

public class QuickSort {

    public static void main(String[] args) {

        quickSort(new int[]{8, 4, 2, 6, 1, 7}, 6);
    }

    // 快速排序，a是数组，n表示数组的大小
    public static void quickSort(int[] a, int n) {
        System.out.println(Arrays.toString(a) + " -- begin ");

        quickSortInternally(a, 0, n);
    }

    // 快速排序递归函数，begin,r为下标
    private static void quickSortInternally(int[] a, int begin, int end) {
        if (begin >= end) return;
        int pivot = partition(a, begin, end); // 获取分区点
        System.out.println(Arrays.toString(a) + " -- begin=" + begin + " end=" + end + " pivot=" + pivot);

        quickSortInternally(a, begin, pivot);
        quickSortInternally(a, pivot + 1, end);
    }


    /**
     * Partition函数 , 把一个数组分割开来，
     * 返回一个pivot， pivot左边的都比他小，pivot右边的都比他大
     *
     * 实际使用时还是双指针，pivot 会在遇到比他小的数字的时候，交换pivot右边的值
     */
    private static int partition(int[] nums, int begin, int end) {
        //枢轴(也可以是在begin和end之间的随机数)
        int pivot = begin;

        for (int i = begin + 1; i < end; i++) {
            if (nums[i] < nums[begin]) {
                swap(nums, pivot + 1, i);
                pivot++;

            } else {
                continue;
            }
        }

        swap(nums, begin, pivot);

        return pivot;
    }

    private static void swap(int[] nums, int index1, int index2) {
        int temp = nums[index1];
        nums[index1] = nums[index2];
        nums[index2] = temp;
    }


}
