import java.util.ArrayList;
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
    }

    public static List<String> generateParenthesis(int n) {

        List<String> res = new ArrayList<>();

        if (n == 0) return res;


        helper(res, "", n, n);


        return null;


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
