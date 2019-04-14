public class Main11盛水最多的容器 {

    public int maxArea(int[] height) {

        if (height.length < 2)
            return 0;


        int l = 0;
        int r = height.length - 1;

        int result = 0;

        while (l < r) {


            result = Math.max(
                    result,
                    Math.min(height[l], height[r]) * (r - l)
            );

            if (height[l] < height[r])
                l++;
            else
                r--;


        }


        return result;
    }
}
