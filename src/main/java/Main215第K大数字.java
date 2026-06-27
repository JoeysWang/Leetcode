import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

public class Main215第K大数字 {
    public static void main(String[] args) {
        Solution solution = new Solution();


        int res = solution.findKthLargestPartition(new int[]{3, 2, 1, 5, 6, 4}, 2);

        System.out.println(res);
        test();
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

    public static void test() {
        TestUtil.reset();

        // 测试用例1: 示例用例 [3,2,1,5,6,4], k=2 → 5
        {
            Solution solution = new Solution();
            TestUtil.assertEquals(5, solution.findKthLargestPartition(new int[]{3, 2, 1, 5, 6, 4}, 2), "示例用例 [3,2,1,5,6,4] k=2");
        }

        // 测试用例2: 示例用例 [3,2,3,1,2,4,5,5,6], k=4 → 4
        {
            Solution solution = new Solution();
            TestUtil.assertEquals(4, solution.findKthLargestPartition(new int[]{3, 2, 3, 1, 2, 4, 5, 5, 6}, 4), "示例用例 [3,2,3,1,2,4,5,5,6] k=4");
        }

        // 测试用例3: 边界用例 单个元素
        {
            Solution solution = new Solution();
            TestUtil.assertEquals(1, solution.findKthLargestPartition(new int[]{1}, 1), "边界用例 单个元素 [1] k=1");
        }

        // 测试用例4: 两个元素 [2,1], k=1 → 2
        {
            Solution solution = new Solution();
            TestUtil.assertEquals(2, solution.findKthLargestPartition(new int[]{2, 1}, 1), "普通用例 [2,1] k=1");
        }

        // 测试用例5: 使用小顶堆方法 [3,2,1,5,6,4], k=2 → 5
        {
            Solution solution = new Solution();
            TestUtil.assertEquals(5, solution.findKthLargestByQueue(new int[]{3, 2, 1, 5, 6, 4}, 2), "小顶堆方法 [3,2,1,5,6,4] k=2");
        }

        TestUtil.printSummary();
    }
}
