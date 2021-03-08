import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

public class Main16最接近三数之和 {


//    例如，给定数组 nums = [-1，2，1，-4], 和 target = 1.
//
//    与 target 最接近的三个数的和为 2. (-1 + 2 + 1 = 2).

    class Solution {

//        public int threeSumClosest(int[] nums, int target) {
//            //先排序,再用双指针遍历
//            Arrays.sort(nums);
//            int left = 0, right = 0;
//            int ans = 0;
//
//
//        }
    }

    public int threeSumClosest(int[] nums, int target) {

        int res = nums[0] + nums[1] + nums[nums.length - 1];

        Arrays.sort(nums);


        for (int i = 0; i < nums.length - 2; i++) {

            int L = i + 1;
            int R = nums.length - 1;

            while (L < R) {
                int sum = nums[i] + nums[L] + nums[R];

                if (sum < target) {
                    L++;
                } else
                    R--;

                if (Math.abs(sum - target) < Math.abs(res - target))
                    res = sum;

            }
        }


        return res;
    }
}
