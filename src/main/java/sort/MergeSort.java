package sort;import java.util.Arrays;

/**
 * 归并排序
 *
 * 把一个数组不断从中间对半劈开, 直到数组只有两个元素，进行排序
 * 把刚才的排序结果，一层一层合并回去
 *
 * 归并排序的时间复杂度任何情况下都是 O(nlogn) ,非原地排序
 */
public class MergeSort {
    public static void main(String[] args) {

        MergeSortSolution solution = new MergeSortSolution();
        int[] result = solution.mergeSort(new int[]{6,1241,2,54,3213,9,214,21,12,3,4,55,23,2});
        System.out.println("result  " + Arrays.toString(result));

    }

    public static class MergeSortSolution {

        public int[] mergeSort(int[] nums) {

            if (nums == null || nums.length < 2) return nums;

            int left = 0;
            int right = nums.length-1;

            int middle = left + (right - left) / 2;
            return merge(sortOrDivide(nums, left, middle), sortOrDivide(nums, middle+1, right));
        }
        private int[] sortOrDivide(int[] nums, int left, int right) {

            if (left == right) {
                return new int[]{nums[left]};
            }
            if (left == right - 1) {

                int[] result = new int[2];
                if (nums[left] < nums[right]) {
                    result[0] = nums[left];
                    result[1] = nums[right];
                } else {
                    result[1] = nums[left];
                    result[0] = nums[right];
                }
                return result;
            }
            int middle = left + (right - left) / 2;
            return merge(sortOrDivide(nums, left, middle), sortOrDivide(nums, middle+1, right));

        }

        private int[] merge(int[] left, int[] right) {
            int leftLength = left.length;
            int rightLength = right.length;

            int[] nums = new int[leftLength + rightLength];

            int postion = 0;
            int i = 0;
            int j = 0;
            while (i != leftLength && j != rightLength) {
                if (left[i] < right[j]) {
                    nums[postion] = left[i];
                    i++;
                } else {
                    nums[postion] = right[j];
                    j++;
                }
                postion++;
            }
            while (i != leftLength) {
                nums[postion] = left[i];
                i++;
                postion++;
            }
            while (j != rightLength) {
                nums[postion] = right[j];
                j++;
                postion++;
            }

            return nums;
        }


    }

}
