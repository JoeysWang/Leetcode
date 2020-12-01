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
