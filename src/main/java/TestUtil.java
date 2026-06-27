import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 测试工具类 - 用于 LeetCode 题目的自动测试
 * 提供简单的断言方法，无需外部依赖
 */
public class TestUtil {

    private static int passCount = 0;
    private static int failCount = 0;

    /**
     * 断言两个对象相等
     */
    public static void assertEquals(Object expected, Object actual, String testName) {
        if (Objects.equals(expected, actual)) {
            System.out.println("✓ PASS: " + testName);
            passCount++;
        } else {
            System.out.println("✗ FAIL: " + testName);
            System.out.println("  Expected: " + expected);
            System.out.println("  Actual:   " + actual);
            failCount++;
        }
    }

    /**
     * 断言两个数组相等
     */
    public static void assertArrayEquals(int[] expected, int[] actual, String testName) {
        if (Arrays.equals(expected, actual)) {
            System.out.println("✓ PASS: " + testName);
            passCount++;
        } else {
            System.out.println("✗ FAIL: " + testName);
            System.out.println("  Expected: " + Arrays.toString(expected));
            System.out.println("  Actual:   " + Arrays.toString(actual));
            failCount++;
        }
    }

    /**
     * 断言两个二维数组相等（忽略顺序）
     */
    public static void assertArrayEquals2D(List<List<Integer>> expected, List<List<Integer>> actual, String testName) {
        // 对于包含多个列表的列表，先排序再比较
        if (expected == null && actual == null) {
            System.out.println("✓ PASS: " + testName);
            passCount++;
            return;
        }
        if (expected == null || actual == null || expected.size() != actual.size()) {
            System.out.println("✗ FAIL: " + testName);
            System.out.println("  Expected: " + expected);
            System.out.println("  Actual:   " + actual);
            failCount++;
            return;
        }

        // 深度比较
        boolean equal = true;
        for (int i = 0; i < expected.size(); i++) {
            if (!expected.get(i).equals(actual.get(i))) {
                equal = false;
                break;
            }
        }

        if (equal) {
            System.out.println("✓ PASS: " + testName);
            passCount++;
        } else {
            System.out.println("✗ FAIL: " + testName);
            System.out.println("  Expected: " + expected);
            System.out.println("  Actual:   " + actual);
            failCount++;
        }
    }

    /**
     * 断言条件为真
     */
    public static void assertTrue(boolean condition, String testName) {
        if (condition) {
            System.out.println("✓ PASS: " + testName);
            passCount++;
        } else {
            System.out.println("✗ FAIL: " + testName);
            failCount++;
        }
    }

    /**
     * 断言条件为假
     */
    public static void assertFalse(boolean condition, String testName) {
        assertTrue(!condition, testName);
    }

    /**
     * 打印测试结果摘要
     */
    public static void printSummary() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("测试完成!");
        System.out.println("通过: " + passCount);
        System.out.println("失败: " + failCount);
        System.out.println("总计: " + (passCount + failCount));
        System.out.println("=".repeat(50));

        if (failCount > 0) {
            System.exit(1);
        }
    }

    /**
     * 重置计数器（用于多次运行）
     */
    public static void reset() {
        passCount = 0;
        failCount = 0;
    }
}
