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

    public static void main(String[] args) {
        test();
    }

    public static void test() {
        TestUtil.reset();
        Main16最接近三数之和 obj = new Main16最接近三数之和();

        // 测试用例1: 示例用例
        TestUtil.assertEquals(2, obj.threeSumClosest(new int[]{-1, 2, 1, -4}, 1), "示例: [-1,2,1,-4] target=1");

        // 测试用例2: 三数之和恰好等于target
        TestUtil.assertEquals(6, obj.threeSumClosest(new int[]{1, 2, 3}, 6), "恰好等于target");

        // 测试用例3: 普通用例
        TestUtil.assertEquals(3, obj.threeSumClosest(new int[]{0, 1, 2}, 3), "普通: [0,1,2] target=3");

        // 测试用例4: 负数target
        TestUtil.assertEquals(-2, obj.threeSumClosest(new int[]{-1, -1, 0, 2}, -3), "负数target");

        TestUtil.printSummary();
    }
}
