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


}
