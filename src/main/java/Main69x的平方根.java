import java.util.Stack;

public class Main69x的平方根 {


    public static void main(String[] args) {
        test();
    }

    public static void test() {
        TestUtil.reset();

        // 测试用例1: 边界用例
        TestUtil.assertEquals(0, mySqrt(0), "边界: sqrt(0)=0");

        // 测试用例2: 示例用例 - 完全平方数
        TestUtil.assertEquals(2, mySqrt(4), "示例: sqrt(4)=2");

        // 测试用例3: 示例用例 - 非完全平方数 (向下取整)
        TestUtil.assertEquals(2, mySqrt(8), "示例: floor(sqrt(8))=2");

        // 测试用例4: 边界用例
        TestUtil.assertEquals(1, mySqrt(1), "边界: sqrt(1)=1");

        // 测试用例5: 普通用例
        TestUtil.assertEquals(4, mySqrt(16), "普通: sqrt(16)=4");

        TestUtil.printSummary();
    }

    /**
     * 函数是单调递增的，可以使用二分查找
     */
    public static int mySqrt(int x) {
        if (x == 0) return 0;
        long left = 0, right = x;
        while (left <= right) {
            long mid = (left + right) / 2;
            if (mid * mid <= x && (mid + 1) * (mid + 1) > x)
                return (int) mid;
            if (mid * mid < x)
                left = mid + 1;
            else
                right = mid - 1;
        }
        return (int) right;
    }
}
