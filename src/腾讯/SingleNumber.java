package 腾讯;

import java.util.PriorityQueue;
import java.util.Queue;

//给定一个非空整数数组，除了某个元素只出现一次以外，其余每个元素均出现了三次。找出那个只出现了一次的元素。
//
//        说明：
//
//        你的算法应该具有线性时间复杂度。 你可以不使用额外空间来实现吗？
//
//        示例 1:
//
//        输入: [2,2,3,2]
//        输出: 3
//        示例 2:
//
//        输入: [0,1,0,1,0,1,99]
//        输出: 99
public class SingleNumber {

    public static void main(String[] args) {


    }

    public static class Solution {
        public int singleNumber(int[] nums) {
            Queue<Integer> queue = new PriorityQueue<>();

            for (int num : nums) {
                queue.offer(num);

            }
            return 0;
        }
    }
}
