public class Main11盛水最多的容器 {

    public static void main(String[] args) {
        Solution solution = new Solution();


    }

    /**
     * 双指针  要获取最大面积，必须尽量让两个板都最长，
     */
    static class Solution {

        public int maxArea(int[] height) {
            int left = 0;
            int right = height.length - 1;

            int area = 0;
            while (true) {

                int min = Math.min(height[left], height[right]);
                if (height[left] == height[right])
                    min = height[left];

                area = Math.max(area, min * (right - left));
                if (height[left] < height[right]) {
                    left++;
                } else
                    right--;

                if (left + 1 == right)
                    break;
            }


            return area;
        }
    }
}
