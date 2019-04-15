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

    public List<List<Integer>> threeSum(int[] nums) {

        List<List<Integer>> res = new ArrayList<>();
        Arrays.sort(nums);
        for (int i = 0; i < nums.length - 2; i++) {

            if (i>0 && nums[i]==nums[i-1]) continue;


            int L = i + 1;
            int R = nums.length - 1;
            int sum = 0 - nums[i];


            while (L < R) {
                if (nums[L] + nums[R] == sum) {
                    res.add(Arrays.asList(nums[i], nums[L], nums[R]));

                    while (L < R && nums[L] == nums[L + 1]) L++;
                    while (L < R && nums[R] == nums[R - 1]) R--;

                    L++;
                    R--;


                } else if (nums[L] + nums[R] < sum) {
                    L++;
                } else R--;
            }
        }

        return res;
    }
}
