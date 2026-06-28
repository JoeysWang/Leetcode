package sort;

import java.util.Arrays;

/**
 * 插入排序
 *
 * 记忆：左边有序，右边未排；拿出当前牌 current，往左找位置。
 * 比 current 大的元素都右移，最后把 current 放进空位。
 *
 * 时间复杂度：最好 O(n)，平均/最坏 O(n^2)
 * 空间复杂度：O(1)，原地排序，稳定
 */
public class InsertSort {

    public static void main(String[] args) {

        int[] a = new int[]{2, 14, 4, 8, 22, 3, 5, 1, 12, 6, 7, 23, 11, 10, 18, 15, 19};

        insertSort(a, a.length);
        System.out.println(Arrays.toString(a));

    }

    public static void insertSort(int[] a, int n) {
        if (a == null || n <= 1) return;

        n = Math.min(n, a.length);

        for (int i = 1; i < n; i++) {
            int current = a[i];
            int j = i - 1;

            // 从 current 左边开始找插入位置：大的元素整体右移一格。
            while (j >= 0 && a[j] > current) {
                a[j + 1] = a[j];
                j--;
            }

            // j 停在 <= current 的位置，所以 current 应放到 j + 1。
            a[j + 1] = current;
        }

    }
}
