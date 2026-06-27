import java.util.Stack;

public class Main20有效的括号 {

    public boolean isValid(String s) {

        if (s == null || s.length() < 1) return true;

        Stack<Character> stack = new Stack<>();

        for (char c : s.toCharArray()) {


            if (c == '(') stack.push(')');
            else if (c == '[') stack.push(']');
            else if (c == '{') stack.push('}');

            else {
                if (stack.isEmpty()|| stack.pop()!=c)
                    return false;

            }
        }


        return stack.isEmpty();
    }

    public static void main(String[] args) {
        test();
    }

    public static void test() {
        TestUtil.reset();
        Main20有效的括号 obj = new Main20有效的括号();

        // 测试用例1: 示例用例
        TestUtil.assertTrue(obj.isValid("()"), "示例: ()");

        // 测试用例2: 多种括号混合
        TestUtil.assertTrue(obj.isValid("()[]{}"), "混合括号: ()[]{}");

        // 测试用例3: 无效括号
        TestUtil.assertFalse(obj.isValid("(]"), "无效: (]");

        // 测试用例4: 嵌套括号
        TestUtil.assertTrue(obj.isValid("([{}])"), "嵌套: ([{}])");

        // 测试用例5: 交叉括号（无效）
        TestUtil.assertFalse(obj.isValid("([)]"), "交叉无效: ([)]");

        // 测试用例6: 空字符串
        TestUtil.assertTrue(obj.isValid(""), "边界: 空字符串");

        TestUtil.printSummary();
    }
}
