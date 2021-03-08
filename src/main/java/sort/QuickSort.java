package sort;

import java.util.Arrays;

public class QuickSort {

    public static void main(String[] args) {

        quickSort(new int[]{5, 8, 3, 4, 6, 1, 7}, 7);
    }

    // 快速排序，a是数组，n表示数组的大小
    public static void quickSort(int[] a, int n) {
        System.out.println(Arrays.toString(a) + " -- begin ");

        quickSortInternally(a, 0, n);
        System.out.println(Arrays.toString(a) + " -- end ");
    }

    // 快速排序递归函数，begin,r为下标
    private static void quickSortInternally(int[] a, int begin, int end) {
        if (begin >= end) return;
        int pivot = partition(a, begin, end); // 获取分区点
        System.out.println(Arrays.toString(a) + " -- begin=" + begin + " end=" + end + " pivot=" + a[pivot]);

        quickSortInternally(a, begin, pivot);
        quickSortInternally(a, pivot + 1, end);
    }


    /**
     * Partition函数 , 把一个数组分割开来，
     * 返回一个pivot， pivot左边的都比他小，pivot右边的都比他大
     * <p>
     * 实际使用时还是双指针，因为pivot右边的值应该都要比它小，
     * 所以在遇到比pivot小的数字的时候，
     * 把这个值交换到pivot的右边，然后pivot加1，最后再把一开始的pivot和最后的pivot交换
     */
    private static int partition(int[] nums, int begin, int end) {
        //枢轴(也可以是在begin和end之间的随机数)
        int pivot = begin;
        //缓存一开始的pivot，最后在循环结束后交换到指定的pivot位置
        int originPivot = pivot;


        for (int i = begin + 1; i < end; i++) {
            if (nums[i] < nums[begin]) {
                swap(nums, pivot + 1, i);
                pivot++;

            } else {
                continue;
            }
        }

        swap(nums, originPivot, pivot);

        return pivot;
    }


    private static void swap(int[] nums, int index1, int index2) {
        System.out.println("\t" + Arrays.toString(nums) + " -- 交换=" + nums[index1] + " " + nums[index2]);

        int temp = nums[index1];
        nums[index1] = nums[index2];
        nums[index2] = temp;
    }


}
