

public class Main29两数相除 {

    public static void main(String[] args) {
        System.out.println(divide(-2147483648, -1));
        test();
    }

    public static void test() {
        TestUtil.reset();

        // 测试用例1: 正数整除
        TestUtil.assertEquals(3, divide(6, 2), "正数整除: 6/2");

        // 测试用例2: 正数有余数
        TestUtil.assertEquals(3, divide(10, 3), "正数有余数: 10/3");

        // 测试用例3: 负数除以正数
        TestUtil.assertEquals(-3, divide(-7, 2), "负数/正数: -7/2");

        // 测试用例4: 正数除以负数
        TestUtil.assertEquals(-3, divide(7, -2), "正数/负数: 7/-2");

        // 测试用例5: 负数除以负数
        TestUtil.assertEquals(3, divide(-6, -2), "负数/负数: -6/-2");

        // 测试用例6: 被除数为0
        TestUtil.assertEquals(0, divide(0, 1), "被除数为0: 0/1");

        TestUtil.printSummary();
    }

    public static int divide(int dividend, int divisor) {
        // 处理溢出情况
        if (dividend == Integer.MIN_VALUE && divisor == -1) {
            return Integer.MAX_VALUE;
        }

        // 确定符号
        boolean sign = (dividend > 0) ^ (divisor > 0);

        // 转换为负数避免溢出
        long newDividend = Math.abs((long) dividend);
        long newDivisor = Math.abs((long) divisor);

        int result = 0;
        // 使用减法计算除法
        while (newDividend >= newDivisor) {
            newDividend -= newDivisor;
            result++;
        }

        return sign ? -result : result;
    }


}
