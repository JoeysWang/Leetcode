package tree.util;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class TestUtil {

    private static int passCount = 0;
    private static int failCount = 0;

    public static void assertEquals(Object expected, Object actual, String testName) {
        if (Objects.equals(expected, actual)) {
            System.out.println("PASS: " + testName);
            passCount++;
        } else {
            System.out.println("FAIL: " + testName);
            System.out.println("  Expected: " + expected);
            System.out.println("  Actual:   " + actual);
            failCount++;
        }
    }

    public static void assertArrayEquals(int[] expected, int[] actual, String testName) {
        if (Arrays.equals(expected, actual)) {
            System.out.println("PASS: " + testName);
            passCount++;
        } else {
            System.out.println("FAIL: " + testName);
            System.out.println("  Expected: " + Arrays.toString(expected));
            System.out.println("  Actual:   " + Arrays.toString(actual));
            failCount++;
        }
    }

    public static void assertArrayEquals2D(List<List<Integer>> expected, List<List<Integer>> actual, String testName) {
        if (expected == null && actual == null) {
            System.out.println("PASS: " + testName);
            passCount++;
            return;
        }
        if (expected == null || actual == null || expected.size() != actual.size()) {
            System.out.println("FAIL: " + testName);
            System.out.println("  Expected: " + expected);
            System.out.println("  Actual:   " + actual);
            failCount++;
            return;
        }

        boolean equal = true;
        for (int i = 0; i < expected.size(); i++) {
            if (!expected.get(i).equals(actual.get(i))) {
                equal = false;
                break;
            }
        }

        if (equal) {
            System.out.println("PASS: " + testName);
            passCount++;
        } else {
            System.out.println("FAIL: " + testName);
            System.out.println("  Expected: " + expected);
            System.out.println("  Actual:   " + actual);
            failCount++;
        }
    }

    public static void assertTrue(boolean condition, String testName) {
        if (condition) {
            System.out.println("PASS: " + testName);
            passCount++;
        } else {
            System.out.println("FAIL: " + testName);
            failCount++;
        }
    }

    public static void assertFalse(boolean condition, String testName) {
        assertTrue(!condition, testName);
    }

    public static void printSummary() {
        System.out.println();
        System.out.println("==================================================");
        System.out.println("测试完成!");
        System.out.println("通过: " + passCount);
        System.out.println("失败: " + failCount);
        System.out.println("总计: " + (passCount + failCount));
        System.out.println("==================================================");

        if (failCount > 0) {
            System.exit(1);
        }
    }

    public static void reset() {
        passCount = 0;
        failCount = 0;
    }
}
