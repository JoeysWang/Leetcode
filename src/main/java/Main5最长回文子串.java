import java.util.HashSet;

public class Main5最长回文子串 {

    //    给定一个字符串 s，找到 s 中最长的回文子串。你可以假设 s 的最大长度为 1000。
//
//    示例 1：
//
//    输入: "babad"
//    输出: "bab"
//    注意: "aba" 也是一个有效答案。
//    示例 2：
//
//    输入: "cbbd"
//    输出: "bb"
    public static void main(String[] args) {
        Solution solution = new Solution();
        String res = solution.longestPalindrome("babad");

        System.out.println(res);
        test();
    }

    private static boolean isPalindromeStr(String s) {
        int l = 0, r = s.length() - 1;
        while (l < r) {
            if (s.charAt(l) != s.charAt(r)) return false;
            l++;
            r--;
        }
        return true;
    }

    static class Solution {

        public String longestPalindrome(String s) {
            String res = "";

            for (int i = 0; i < s.length(); i++) {
                // 以 s[i] 为中心的最长回文子串
                String s1 = helper(s, i, i);
                // 以 s[i] 和 s[i+1] 为中心的最长回文子串
                String s2 = helper(s, i, i + 1);

                if (s1.length() >= s2.length())
                    if (s1.length() > res.length()) res = s1;

                if (s2.length() > s1.length())
                    if (s2.length() > res.length()) res = s2;


            }

            return res;
        }

        private String helper(String s, int left, int right) {

            while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
                left--;
                right++;
            }
            String res = s.substring(left + 1, right);
            return res;
        }
    }

    public static void test() {
        TestUtil.reset();
        Solution solution = new Solution();

        // 测试用例1: 示例用例 - "babad" → "bab" 或 "aba"
        String result1 = solution.longestPalindrome("babad");
        TestUtil.assertTrue(isPalindromeStr(result1) && result1.length() == 3, "示例用例: babad");

        // 测试用例2: 示例用例 - "cbbd" → "bb"
        TestUtil.assertEquals("bb", solution.longestPalindrome("cbbd"), "示例用例: cbbd");

        // 测试用例3: 边界用例 - 单字符 "a" → "a"
        TestUtil.assertEquals("a", solution.longestPalindrome("a"), "边界用例: 单字符");

        // 测试用例4: 全相同字符 - "aaaa" → "aaaa"
        TestUtil.assertEquals("aaaa", solution.longestPalindrome("aaaa"), "普通用例: aaaa");

        TestUtil.printSummary();
    }

}
