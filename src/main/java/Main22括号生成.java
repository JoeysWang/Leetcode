import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main22括号生成 {

//
//    例如，给出 n = 3，生成结果为：
//
//            [
//            "((()))",
//            "(()())",
//            "(())()",
//            "()(())",
//            "()()()"
//            ]

    public static void main(String[] args) {
        generateParenthesis(5);
        test();
    }

    public static void test() {
        TestUtil.reset();

        // 测试用例1: n=1
        List<String> expected1 = Arrays.asList("()");
        List<String> result1 = generateParenthesis(1);
        assertListEqualsIgnoreOrder(expected1, result1, "n=1");

        // 测试用例2: n=2
        List<String> expected2 = Arrays.asList("(())", "()()");
        List<String> result2 = generateParenthesis(2);
        assertListEqualsIgnoreOrder(expected2, result2, "n=2");

        // 测试用例3: n=3
        List<String> expected3 = Arrays.asList("((()))", "(()())", "(())()", "()(())", "()()()");
        List<String> result3 = generateParenthesis(3);
        assertListEqualsIgnoreOrder(expected3, result3, "n=3");

        TestUtil.printSummary();
    }

    private static void assertListEqualsIgnoreOrder(List<String> expected, List<String> actual, String testName) {
        if (expected == null && actual == null) {
            TestUtil.assertTrue(true, testName);
            return;
        }
        if (expected == null || actual == null || expected.size() != actual.size()) {
            TestUtil.assertTrue(false, testName + " (size mismatch: expected=" + (expected == null ? "null" : expected.size()) + ", actual=" + (actual == null ? "null" : actual.size()) + ")");
            return;
        }
        List<String> sortedExpected = new ArrayList<>(expected);
        List<String> sortedActual = new ArrayList<>(actual);
        java.util.Collections.sort(sortedExpected);
        java.util.Collections.sort(sortedActual);
        TestUtil.assertTrue(sortedExpected.equals(sortedActual), testName);
    }

    public static List<String> generateParenthesis(int n) {

        List<String> res = new ArrayList<>();

        if (n == 0) return res;


        helper(res, "", n, n);


        return res;


    }

    private static void helper(List<String> res, String s, int left, int right) {

        if (left > right) {
            return;
        }

        if (left == 0 && right == 0) {
            res.add(s);
            return;
        }


        if (left > 0) {
            helper(res, s + "(", left - 1, right);
        }
        if (right > 0) {
            helper(res, s + ")", left, right - 1);
        }

    }
}
