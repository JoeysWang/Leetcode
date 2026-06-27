

public class Main9回文数 {

    class Solution {


        public boolean isPalindrome(int x) {

            if (x<0)
                return false;


            int begin = x;
            int revers = 0;

            while (begin > 0) {


                int yu = begin % 10;
                revers = revers * 10 + yu;


                begin = begin / 10;

            }


            return revers == x;
        }
    }

    public static void main(String[] args) {
        test();
    }

    public static void test() {
        TestUtil.reset();
        Main9回文数 outer = new Main9回文数();
        Solution solution = outer.new Solution();

        // 测试用例1: 示例用例 - 121 是回文数
        TestUtil.assertTrue(solution.isPalindrome(121), "示例用例: 121是回文数");

        // 测试用例2: 示例用例 - -121 不是回文数
        TestUtil.assertFalse(solution.isPalindrome(-121), "示例用例: -121不是回文数");

        // 测试用例3: 示例用例 - 10 不是回文数
        TestUtil.assertFalse(solution.isPalindrome(10), "示例用例: 10不是回文数");

        // 测试用例4: 边界用例 - 0 是回文数
        TestUtil.assertTrue(solution.isPalindrome(0), "边界用例: 0是回文数");

        // 测试用例5: 普通用例 - 12321 是回文数
        TestUtil.assertTrue(solution.isPalindrome(12321), "普通用例: 12321是回文数");

        TestUtil.printSummary();
    }
}
