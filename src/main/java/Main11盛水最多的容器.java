

public class Main11盛水最多的容器 {

    public static void main(String[] args) {
        Solution solution = new Solution();
        test();
    }

    /**
     * 双指针  要获取最大面积，必须尽量让两个板都最长，
     */
    static class Solution {

        public int maxArea(int[] height) {
            int left = 0;
            int right = height.length - 1;

            int area = 0;
            while (left != right) {

                int min = Math.min(height[left], height[right]);
                if (height[left] == height[right])
                    min = height[left];

                area = Math.max(area, min * (right - left));
                if (height[left] < height[right]) {
                    left++;
                } else
                    right--;


            }


            return area;
        }
    }

    public static void test() {
        TestUtil.reset();
        Solution solution = new Solution();

        // 测试用例1: 示例用例 - [1,8,6,2,5,4,8,3,7] → 49
        TestUtil.assertEquals(49, solution.maxArea(new int[]{1, 8, 6, 2, 5, 4, 8, 3, 7}), "示例用例: 盛水最多的容器");

        // 测试用例2: 边界用例 - 两个元素 [1,1] → 1
        TestUtil.assertEquals(1, solution.maxArea(new int[]{1, 1}), "边界用例: 两个等高板");

        // 测试用例3: 普通用例 - [4,3,2,1,4] → 16
        TestUtil.assertEquals(16, solution.maxArea(new int[]{4, 3, 2, 1, 4}), "普通用例: 对称高度");

        // 测试用例4: 递增序列 - [1,2,1] → 2
        TestUtil.assertEquals(2, solution.maxArea(new int[]{1, 2, 1}), "普通用例: 三块板");

        TestUtil.printSummary();
    }
}
