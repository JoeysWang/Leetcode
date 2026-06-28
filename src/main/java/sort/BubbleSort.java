package sort;

import java.util.Arrays;

/**
 * 冒泡排序
 * <p>
 * 记忆：相邻比较，大的往右冒。
 * 每一轮结束后，当前未排序区间的最大值都会到最右边。
 * <p>
 * 时间复杂度：最好 O(n)，平均/最坏 O(n^2)
 * 空间复杂度：O(1)，原地排序，稳定
 */
public class BubbleSort {

    public static void main(String[] args) {
        int[] a = new int[]{2, 14, 4, 8, 22, 3, 5, 1, 12, 6, 7, 23, 11, 10, 18, 15, 19};

        bubbleSort(a);
        System.out.println(Arrays.toString(a));
    }

    public static void bubbleSort(int[] a) {
        if (a.length < 2) {
            return;
        }
        for (int i = 0; i < a.length - 1; i++) {

            for (int j = 0; j < a.length - i - 1; j++) {

                if (a[j] > a[j + 1]) {
                    swap(a, j, j + 1);
                }

            }

        }

    }
//    public static void bubbleSort(int[] a, int n) {
//        if (a == null || n <= 1) return;
//
//        n = Math.min(n, a.length);
//
//        // 外层控制轮数：第 i 轮结束后，倒数第 i + 1 个位置已经排好。
//        for (int i = 0; i < n - 1; i++) {
//            boolean swapped = false;
//
//            // 内层只比较还没排好的区间，相邻逆序就交换。
//            for (int j = 0; j < n - 1 - i; j++) {
//                if (a[j] > a[j + 1]) {
//                    swap(a, j, j + 1);
//                    swapped = true;
//                }
//            }
//
//            // 一整轮没有交换，说明数组已经有序，可以提前结束。
//            if (!swapped) return;
//        }
//    }

    private static void swap(int[] a, int index1, int index2) {
        int temp = a[index1];
        a[index1] = a[index2];
        a[index2] = temp;
    }
}
