package sort;import java.util.Arrays;

public class InsertSort {


    public static void main(String[] args) {

        int[] a = new int[]{2, 14, 4, 8, 22, 3, 5, 1, 12, 6, 7, 23, 11, 10,18,15,19};

        insertSort(a, a.length);
        System.out.println(Arrays.toString(a));

    }

    public static void insertSort(int[] a, int n) {
        if (n <= 1) return;

        for (int i = 1; i < n; i++) {

            int current = a[i];

            //从i开始  从后向前
            for (int j = i - 1; j >= 0; j--) {
                if (a[j] > current) {
                    a[j + 1] = a[j];
                    a[j] = current;
                } else {
                    break;
                }

            }


        }

    }
}
