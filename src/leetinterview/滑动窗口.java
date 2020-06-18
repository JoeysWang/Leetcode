package leetinterview;

import java.util.Comparator;
import java.util.PriorityQueue;

public class 滑动窗口 {

    public static void main(String[] args) {


    }

    static class Solution {


        public int[] maxSlidingWindow(int[] nums, int k) {

            int[] res = new int[nums.length-k+1];

            PriorityQueue<Integer> queue = new PriorityQueue<>(k, new Comparator<Integer>() {
                @Override
                public int compare(Integer o1, Integer o2) {
                    return o2 - o1;
                }
            });


            int left = 0;
            int right = k - 1;

            for (int i = 0; i < k; i++) {
                queue.offer(nums[i]);
            }

            while (right < nums.length ) {
                res[left] = queue.peek();

                int out = nums[left];
                queue.remove(out);
                left++;
                right++;
                if (right<nums.length)
                queue.add(nums[right]);
            }
            return res;
        }
    }
}
