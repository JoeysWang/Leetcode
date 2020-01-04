public class Main7整数反转 {

    public static void main(String[] args) {
        Solution solution = new Solution();


        System.out.println(" result = " + solution.reverse(-124145));

    }

    /**
     * int max =  2147483647
     * int min = -2147483648
     *
     * 所以翻转之前的最高位不能大于 7
     */
    public static class Solution {
        public int reverse(int x) {
            if (x < 0) {
                if (x < -Integer.MAX_VALUE)
                    return 0;
                return -reverse(-x);
            }


            int now = 0;

            while (x > 0) {
                if (now > Integer.MAX_VALUE / 10)
                    return 0;
                if (now == Integer.MAX_VALUE / 10 && x % 10 > Integer.MAX_VALUE % 10) {
                    return 0;
                }

                now = now * 10 + x % 10;
                x = x / 10;
            }


            return now;
        }
    }
}
