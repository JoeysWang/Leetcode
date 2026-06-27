

public class Main7整数反转 {

    public static void main(String[] args) {
        Solution solution = new Solution();


        System.out.println(" result = " + solution.reverse(-124145));
        test();
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

    public static void test() {
        TestUtil.reset();
        Solution solution = new Solution();

        // 测试用例1: 示例用例 - 123 → 321
        TestUtil.assertEquals(321, solution.reverse(123), "示例用例: 123反转");

        // 测试用例2: 示例用例 - -123 → -321
        TestUtil.assertEquals(-321, solution.reverse(-123), "示例用例: -123反转");

        // 测试用例3: 示例用例 - 120 → 21
        TestUtil.assertEquals(21, solution.reverse(120), "示例用例: 120反转");

        // 测试用例4: 边界用例 - 0 → 0
        TestUtil.assertEquals(0, solution.reverse(0), "边界用例: 0反转");

        // 测试用例5: 溢出用例 - 超出int范围返回0
        TestUtil.assertEquals(0, solution.reverse(1534236469), "溢出用例: 超出int范围");

        TestUtil.printSummary();
    }
}
