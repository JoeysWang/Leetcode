

/**
 * 分治 ，分解成 Y=x的n/2次方, result=Y*Y
 * 需要注意负数次方和 奇数次方
 */
public class Main50计算x的n次幂函数 {


    public double myPow(double x, int n) {
        if (n == 0)
            return 1;
        if (n == 1)
            return x;
        if (n == -1)
            return 1 / x;

        if (n % 2 != 0) {
            double result = myPow(x, (n - 1) / 2);
            return result * result * x;
        } else {
            double result = myPow(x, n / 2);
            return result * result;
        }
    }

    public static void main(String[] args) {
        test();
    }

    public static void test() {
        TestUtil.reset();
        Main50计算x的n次幂函数 solution = new Main50计算x的n次幂函数();
        double eps = 1e-5;

        // 测试用例1: 示例用例 2^10 = 1024
        TestUtil.assertTrue(Math.abs(solution.myPow(2.0, 10) - 1024.0) < eps, "示例: 2^10=1024");

        // 测试用例2: 示例用例 2^-2 = 0.25
        TestUtil.assertTrue(Math.abs(solution.myPow(2.0, -2) - 0.25) < eps, "示例: 2^-2=0.25");

        // 测试用例3: 边界用例 x^0 = 1
        TestUtil.assertTrue(Math.abs(solution.myPow(2.0, 0) - 1.0) < eps, "边界: 2^0=1");

        // 测试用例4: 边界用例 负数底数 (-2)^3 = -8
        TestUtil.assertTrue(Math.abs(solution.myPow(-2.0, 3) - (-8.0)) < eps, "边界: (-2)^3=-8");

        // 测试用例5: 普通用例 3^5 = 243
        TestUtil.assertTrue(Math.abs(solution.myPow(3.0, 5) - 243.0) < eps, "普通: 3^5=243");

        TestUtil.printSummary();
    }

}
