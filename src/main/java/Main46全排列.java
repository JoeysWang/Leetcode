import java.util.ArrayList;
import java.util.List;

public class Main46全排列 {

    /**
     * 回溯
     */
    public static void main(String[] args) {

        Solution solution = new Solution();

        List<List<Integer>> res = solution.permute(new int[]{1, 2, 3});

        System.out.println(res);

        test();
    }

    public static void test() {
        TestUtil.reset();
        Solution solution = new Solution();

        // 测试用例1: 示例用例 [1,2,3] → 6种排列
        List<List<Integer>> result1 = solution.permute(new int[]{1, 2, 3});
        TestUtil.assertEquals(6, result1.size(), "示例: [1,2,3] 排列数量");
        List<List<Integer>> expected1 = new java.util.ArrayList<>();
        expected1.add(java.util.Arrays.asList(1, 2, 3));
        expected1.add(java.util.Arrays.asList(1, 3, 2));
        expected1.add(java.util.Arrays.asList(2, 1, 3));
        expected1.add(java.util.Arrays.asList(2, 3, 1));
        expected1.add(java.util.Arrays.asList(3, 1, 2));
        expected1.add(java.util.Arrays.asList(3, 2, 1));
        TestUtil.assertArrayEquals2D(expected1, result1, "示例: [1,2,3] 排列内容");

        // 测试用例2: 边界用例 [1] → 1种排列
        List<List<Integer>> result2 = solution.permute(new int[]{1});
        TestUtil.assertEquals(1, result2.size(), "边界: [1] 排列数量");
        List<List<Integer>> expected2 = new java.util.ArrayList<>();
        expected2.add(java.util.Arrays.asList(1));
        TestUtil.assertArrayEquals2D(expected2, result2, "边界: [1] 排列内容");

        // 测试用例3: 普通用例 [1,2] → 2种排列
        List<List<Integer>> result3 = solution.permute(new int[]{1, 2});
        TestUtil.assertEquals(2, result3.size(), "普通: [1,2] 排列数量");
        List<List<Integer>> expected3 = new java.util.ArrayList<>();
        expected3.add(java.util.Arrays.asList(1, 2));
        expected3.add(java.util.Arrays.asList(2, 1));
        TestUtil.assertArrayEquals2D(expected3, result3, "普通: [1,2] 排列内容");

        TestUtil.printSummary();
    }

    public static class Solution {
        public List<List<Integer>> permute(int[] nums) {
            List<List<Integer>> res = new ArrayList<>();

            boolean[] marked = new boolean[nums.length];
            backTrace(nums, marked, new ArrayList<>(), res);

            return res;
        }

        private void backTrace(int[] nums, boolean[] marked, List<Integer> resNow, List<List<Integer>> res) {
            if (resNow.size() == nums.length) {
                res.add(new ArrayList<>(resNow));
                return;
            }
            for (int j = 0; j < nums.length; j++) {
                if (marked[j]) continue;
                resNow.add(nums[j]);
                marked[j] = true;
                //深入到下一层
                backTrace(nums, marked, resNow, res);

                //从下一层回来  继续循环
                marked[j] = false;
                resNow.remove(resNow.size() - 1);


            }

        }
    }
}
