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
    String res = "";

    public String longestPalindrome(String s) {

        for (int i = 0; i < s.length(); i++) {
            helper(s, i, i);//奇数情况下
            helper(s, i, i + 1);//偶数情况下
        }

        return res;
    }

    private void helper(String s, int left, int right) {

        while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
            left--;
            right++;
        }
        String current = s.substring(left + 1, right);

        if (current.length() > res.length())
            res = current;

    }


}
