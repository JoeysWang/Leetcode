package sort;

import java.util.Arrays;

/**
 * 选择排序
 *
 * 记忆：每一轮从未排序区间里选最小值，放到未排序区间的最前面。
 * 和冒泡不同：冒泡是相邻交换很多次，选择是一轮最多交换一次。
 *
 * 时间复杂度：任何情况都是 O(n^2)
 * 空间复杂度：O(1)，原地排序，不稳定
 */
public class SelectionSort {

    public static void main(String[] args) {
        int[] a = new int[]{2, 14, 4, 8, 22, 3, 5, 1, 12, 6, 7, 23, 11, 10, 18, 15, 19};

        selectionSort(a, a.length);
        System.out.println(Arrays.toString(a));
    }

    public static void selectionSort(int[] a, int n) {
        if (a == null || n <= 1) return;

        n = Math.min(n, a.length);

        // 外层控制已排序区间：每轮把最小值放到 i 的位置。
        for (int i = 0; i < n - 1; i++) {
            int minIndex = i;

            // 内层在未排序区间 [i + 1, n - 1] 中寻找最小值下标。
            for (int j = i + 1; j < n; j++) {
                if (a[j] < a[minIndex]) {
                    minIndex = j;
                }
            }

            // 找到更小的值后，和未排序区间的第一个元素交换。
            if (minIndex != i) {
                swap(a, i, minIndex);
            }
        }
    }

    private static void swap(int[] a, int index1, int index2) {
        int temp = a[index1];
        a[index1] = a[index2];
        a[index2] = temp;
    }
}
