import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

public class Main215第K大数字 {
    public static void main(String[] args) {
        Solution solution = new Solution();


        int res = solution.findKthLargestPartition(new int[]{3, 2, 1, 5, 6, 4}, 2);

        System.out.println(res);
    }

    static class Solution {
        /**
         * 小顶堆，把堆顶的最小的扔出去
         */
        public int findKthLargestByQueue(int[] nums, int k) {

            Queue<Integer> queue = new PriorityQueue<>();

            for (int num : nums) {
                queue.add(num);
                if (queue.size() > k)
                    queue.poll();
            }

            return queue.poll();
        }

        /**
         * 用partition函数，找到第N-k小数就行了
         */
        public int findKthLargestPartition(int[] nums, int k) {


            int pivot = partition(nums, 0, nums.length);

            int smallK = nums.length - k;


            while (pivot != smallK) {

                if (pivot < smallK)
                    pivot = partition(nums, pivot + 1, nums.length);
                else
                    pivot = partition(nums, 0, pivot);

            }
            return nums[pivot];

        }

        private int partition(int[] nums, int begin, int end) {

            int pivot = begin;

            for (int i = begin + 1; i < end; i++) {
                if (nums[i] < nums[begin]) {
                    swap(nums, i, begin + 1);
                    pivot++;
                }
            }
            swap(nums, pivot, begin);

            return pivot;
        }


        private void swap(int[] nums, int index1, int index2) {
            int temp = nums[index1];
            nums[index1] = nums[index2];
            nums[index2] = temp;
        }

    }
}
