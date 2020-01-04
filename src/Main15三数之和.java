import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main15三数之和 {

//
//    例如, 给定数组 nums = [-1, 0, 1, 2, -1, -4]，
//
//    满足要求的三元组集合为：
//            [
//            [-1, 0, 1],
//            [-1, -1, 2]
//            ]
//

    public static void main(String[] args) {
        Solution solution = new Solution();

        solution.threeSum(new int[]{-1, 0, 1, 2, -1, -4});


    }

    public static class Solution {

        /**
         * 如果当前的数和上一个数相同，就跳过这一次!
         * 从小到大排序， 右边的都比当前的数大， 有重复的，就把指针跳过一次
         *
         */
        public List<List<Integer>> threeSum(int[] nums) {
            Arrays.sort(nums);
            System.out.println("nums:" + Arrays.toString(nums));
            List<List<Integer>> result = new ArrayList<>();

            for (int i = 0; i < nums.length; i++) {

                //如果当前的数和上一个数相同，就跳过这一次!
                if (i > 0 && nums[i] == nums[i - 1]) continue;

                int L = i + 1;
                int R = nums.length - 1;

                int sum = -nums[i];

                while (L < R) {


                    if (nums[L] + nums[R] == sum) {

                        List<Integer> answer = new ArrayList<Integer>();
                        answer.add(nums[i]);
                        answer.add(nums[L]);
                        answer.add(nums[R]);
                        result.add(answer);

                        while (L < R && nums[L] == nums[L + 1]) L++;
                        while (L < R && nums[R] == nums[R - 1]) R--;
                        L++;
                        R--;

                    } else if (nums[L] + nums[R] < sum) {
                        L++;
                    } else
                        R--;
                }

            }
            return result;


        }

    }

    public List<List<Integer>> threeSum(int[] nums) {

        List<List<Integer>> res = new ArrayList<>();
        Arrays.sort(nums);
        for (int i = 0; i < nums.length - 2; i++) {
            if (i > 0 && nums[i] == nums[i - 1]) continue;
            int L = i + 1;
            int R = nums.length - 1;
            int sum = -nums[i];


            while (L < R) {
                if (nums[L] + nums[R] == sum) {
                    res.add(Arrays.asList(nums[i], nums[L], nums[R]));

                    //跳过重复元素
                    while (L < R && nums[L] == nums[L + 1]) L++;
                    while (L < R && nums[R] == nums[R - 1]) R--;

                    L++;
                    R--;


                } else if (nums[L] + nums[R] < sum) {
                    //左边太小，右移一位
                    L++;
                } else {
                    //右边太小，左移一位
                    R--;
                }
            }
        }

        return res;
    }
}
