

public class Main42接雨水 {
    /**
     * 给定 n 个非负整数表示每个宽度为 1 的柱子的高度图，计算按此排列的柱子，下雨之后能接多少雨水。
     */
    public static void main(String[] args) {

    }

    public static class Solution {

        /**
         * 使用双指针算法，单独计算当前柱子，(当前位置的左右最高的墙)取矮的-当前柱子高度
         * 用备忘录记住左右最低的墙
         */
        public int trap(int[] height) {

            int length = height.length;
            if (length <= 0) {
                return 0;
            }
            int l_max = height[0];
            int r_max = height[length - 1];

            int res = 0;

            int left = 0;
            int right = length - 1;

            while (left <= right) {
                l_max = Math.max(l_max, height[left]);

                r_max = Math.max(r_max, height[right]);

                if (l_max < r_max) {
                    res += l_max - height[left];
                    left++;
                } else {
                    res += r_max - height[right];
                    right--;
                }

            }
            return res;
        }
    }


}
