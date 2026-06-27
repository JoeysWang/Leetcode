

import java.util.HashMap;
import java.util.Map;

/**
 * 求众数
 */
public class Main169多数元素 {

//    public int majorityElement(int[] nums) {
//
//    }

    public int majorityElement(int[] nums) {
        Map<Integer, Integer> counts = new HashMap<>();
        for (int num : nums) {
            counts.put(num, counts.getOrDefault(num, 0) + 1);
            if (counts.get(num) > nums.length / 2) {
                return num;
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        test();
    }

    public static void test() {
        TestUtil.reset();
        Main169多数元素 solution = new Main169多数元素();

        // 测试用例1: 示例用例 [3,2,3] → 3
        TestUtil.assertEquals(3, solution.majorityElement(new int[]{3, 2, 3}), "示例用例 [3,2,3]");

        // 测试用例2: 示例用例 [2,2,1,1,1,2,2] → 2
        TestUtil.assertEquals(2, solution.majorityElement(new int[]{2, 2, 1, 1, 1, 2, 2}), "示例用例 [2,2,1,1,1,2,2]");

        // 测试用例3: 边界用例 - 单个元素
        TestUtil.assertEquals(1, solution.majorityElement(new int[]{1}), "边界用例 单个元素 [1]");

        // 测试用例4: 所有元素相同
        TestUtil.assertEquals(5, solution.majorityElement(new int[]{5, 5, 5, 5}), "所有元素相同 [5,5,5,5]");

        // 测试用例5: 普通用例
        TestUtil.assertEquals(3, solution.majorityElement(new int[]{3, 3, 4}), "普通用例 [3,3,4]");

        TestUtil.printSummary();
    }
}
