

public class Main231_2的幂 {

    /**
     * 这个数字n转换成2进制，应该只有一个1
     * 比如 8 = 1000
     * 4 = 100
     * 2 = 10
     */
    public boolean isPowerOfTwo(int n) {
        if (n <= 0)
            return false;

        return (n & (n - 1)) == 0;

    }

    /**
     * 计算 value 有几个二进制1
     * @param args
     */
    public static void main(String[] args) {
        int value = 0b10101;
        int count=0;
        while (value != 0) {
            System.out.println(Integer.toBinaryString(value)+" & "+Integer.toBinaryString(value-1));
            count++;
            value = value & (value - 1);
            System.out.println("after:"+Integer.toBinaryString(value));
        }
        System.out.println("count is "+count);
        test();
    }

    public static void test() {
        TestUtil.reset();
        Main231_2的幂 solution = new Main231_2的幂();

        // 测试用例1: 示例用例 1 → true (2^0 = 1)
        TestUtil.assertTrue(solution.isPowerOfTwo(1), "示例用例 n=1 (2^0)");

        // 测试用例2: 示例用例 16 → true (2^4)
        TestUtil.assertTrue(solution.isPowerOfTwo(16), "示例用例 n=16 (2^4)");

        // 测试用例3: 示例用例 3 → false
        TestUtil.assertFalse(solution.isPowerOfTwo(3), "示例用例 n=3 非2的幂");

        // 测试用例4: 边界用例 0 → false
        TestUtil.assertFalse(solution.isPowerOfTwo(0), "边界用例 n=0");

        // 测试用例5: 边界用例 负数 → false
        TestUtil.assertFalse(solution.isPowerOfTwo(-1), "边界用例 n=-1");

        // 测试用例6: 2 → true (2^1)
        TestUtil.assertTrue(solution.isPowerOfTwo(2), "普通用例 n=2 (2^1)");

        // 测试用例7: 1024 → true (2^10)
        TestUtil.assertTrue(solution.isPowerOfTwo(1024), "普通用例 n=1024 (2^10)");

        TestUtil.printSummary();
    }
}
