import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Main1两数之和 {
    public static void main(String[] args) {
        twoSum(new int[]{3, 2, 4}, 6);
        test();
    }

    public static int[] twoSum(int[] nums, int target) {

        Map<Integer, Integer> numset = new HashMap<>();

        for (int i = 0; i < nums.length; i++) {
            numset.put(nums[i], i);
        }


        for (int i = 0; i < nums.length; i++) {
            if (numset.containsKey(target - nums[i])
                    && numset.get(target - nums[i]) != i) {
                int[] result = new int[2];
                result[0] = i;
                result[1] = numset.get(target - nums[i]);
                return result;
            }
        }

        return null;

    }

    public static void test() {
        TestUtil.reset();

        // 测试用例1: 示例用例 - [2,7,11,15], target=9 → [0,1]
        TestUtil.assertArrayEquals(new int[]{0, 1}, twoSum(new int[]{2, 7, 11, 15}, 9), "示例用例: 两数之和");

        // 测试用例2: 示例用例 - [3,2,4], target=6 → [1,2]
        TestUtil.assertArrayEquals(new int[]{1, 2}, twoSum(new int[]{3, 2, 4}, 6), "示例用例: 两数之和");

        // 测试用例3: 边界用例 - 相邻元素 [3,3], target=6 → [0,1]
        TestUtil.assertArrayEquals(new int[]{0, 1}, twoSum(new int[]{3, 3}, 6), "边界用例: 相同元素");

        TestUtil.printSummary();
    }

}
