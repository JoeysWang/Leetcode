import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Queue;

public class Main239滑动窗口最大值 {

    /**
     * 给定一个数组 nums，有一个大小为 k 的滑动窗口从数组的最左侧移动到数组的最右侧。
     * 你只可以看到在滑动窗口内的 k 个数字。滑动窗口每次只向右移动一位。
     * <p>
     * 返回滑动窗口中的最大值。
     * <p>
     * 输入: nums = [1,3,-1,-3,5,3,6,7], 和 k = 3
     * 输出: [3,3,5,5,6,7]
     *
     * TIP( 因为要遍历窗口里的最大值，所以window里存 nums的下标会好操作一些)
     * <p>
     * => [(1,3,-1) ,-3,5,3,6,7]  //保证窗口里最左边的是最大值，
     * => [(_,3,-1) ,-3,5,3,6,7]
     * => [_, (3,-1,-3) ,5,3,6,7]
     * => [_, 3,(_,_,5) ,3,6,7]   //因为 5 > -1, 5 > -3, 所以只保留 5 .
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/sliding-window-maximum
     */
    public static void main(String[] args) {

        int[] data = new int[]{1, 3, 1, 2, 0, 5};
        Main239滑动窗口最大值 solution = new Main239滑动窗口最大值();

        int[] ints = solution.maxSlidingWindow(data, 3);


        System.out.println(Arrays.toString(ints));
    }

    ArrayDeque<Integer> deq = new ArrayDeque<Integer>();
    int[] nums;

    public void clean_deque(int i, int k) {
        // remove indexes of elements not from sliding window
        if (!deq.isEmpty() && deq.getFirst() == i - k)
            deq.removeFirst();

        // remove from deq indexes of all elements
        // which are smaller than current element nums[i]
        while (!deq.isEmpty() &&
                nums[i] > nums[deq.getLast()])
            deq.removeLast();
    }

    public int[] maxSlidingWindow(int[] nums, int k) {
        int n = nums.length;
        if (n * k == 0) return new int[0];
        if (k == 1) return nums;

        // init deque and output
        this.nums = nums;
        int max_idx = 0;
        for (int i = 0; i < k; i++) {
            clean_deque(i, k);
            deq.addLast(i);
            // compute max in nums[:k]
            if (nums[i] > nums[max_idx]) max_idx = i;
        }
        int[] output = new int[n - k + 1];
        output[0] = nums[max_idx];

        // build output
        for (int i = k; i < n; i++) {
            clean_deque(i, k);
            deq.addLast(i);
            output[i - k + 1] = nums[deq.getFirst()];
        }
        return output;
    }

}
