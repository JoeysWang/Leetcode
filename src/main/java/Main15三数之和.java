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
        test();

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

    public static void test() {
        TestUtil.reset();
        Solution solution = new Solution();

        // 测试用例1: 示例用例 - [-1,0,1,2,-1,-4] → [[-1,0,1],[-1,-1,2]]
        List<List<Integer>> result1 = solution.threeSum(new int[]{-1, 0, 1, 2, -1, -4});
        TestUtil.assertEquals(2, result1.size(), "示例用例: 应找到2组三元组");
        TestUtil.assertTrue(result1.contains(Arrays.asList(-1, 0, 1)), "示例用例: 包含[-1,0,1]");
        TestUtil.assertTrue(result1.contains(Arrays.asList(-1, -1, 2)), "示例用例: 包含[-1,-1,2]");

        // 测试用例2: 边界用例 - 空数组 → []
        List<List<Integer>> result2 = solution.threeSum(new int[]{});
        TestUtil.assertEquals(0, result2.size(), "边界用例: 空数组无解");

        // 测试用例3: 无解用例 - [1,2,3] → []
        List<List<Integer>> result3 = solution.threeSum(new int[]{1, 2, 3});
        TestUtil.assertEquals(0, result3.size(), "无解用例: [1,2,3]无三元组");

        // 测试用例4: 全零用例 - [0,0,0] → [[0,0,0]]
        List<List<Integer>> result4 = solution.threeSum(new int[]{0, 0, 0});
        TestUtil.assertEquals(1, result4.size(), "全零用例: 应找到1组三元组");
        TestUtil.assertTrue(result4.contains(Arrays.asList(0, 0, 0)), "全零用例: 包含[0,0,0]");

        // 测试用例5: 重复元素 - [-1,0,1,0] → [[-1,0,1]]
        List<List<Integer>> result5 = solution.threeSum(new int[]{-1, 0, 1, 0});
        TestUtil.assertEquals(1, result5.size(), "重复元素用例: 应找到1组三元组");
        TestUtil.assertTrue(result5.contains(Arrays.asList(-1, 0, 1)), "重复元素用例: 包含[-1,0,1]");

        TestUtil.printSummary();
    }
}
