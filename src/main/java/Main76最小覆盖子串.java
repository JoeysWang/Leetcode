import java.security.cert.PolicyQualifierInfo;
import java.util.HashMap;

public class Main76最小覆盖子串 {
    /**
     * 给你一个字符串 S、一个字符串 T，请在字符串 S 里面找出：包含 T 所有字符的最小子串。
     * <p>
     * 示例：
     * <p>
     * 输入: S = "ADOBECODEBANC", T = "ABC"
     * 输出: "BANC"
     * 说明：
     * <p>
     * 如果 S 中不存这样的子串，则返回空字符串 ""。
     * 如果 S 中存在这样的子串，我们保证它是唯一的答案。
     */

    public static void main(String[] args) {

        String S = "ADOBECODEBANC";
        String T = "ABC";

        System.out.println(minWindow(S, T));
    }

    class Solution {

        public String minWindow(String s, String t) {
            String res = "";
            HashMap<Character, Integer> window = new HashMap<>();

            //初始化需要的个数
            HashMap<Character, Integer> needs = new HashMap<>();
            for (int i = 0; i < t.length(); i++) {
                char charNow = t.charAt(i);
                needs.put(charNow, needs.getOrDefault(charNow, 0) + 1);
            }

            //开始滑动窗口
            int left = 0, right = 0;
            int match = 0;

            int resStart = 0;
            int resLength = Integer.MAX_VALUE;

            while (right < s.length()) {
                char charNow = s.charAt(right);
                right++;

                //开始判断窗口内是否合法
                if (needs.containsKey(charNow)) {
                    window.put(charNow, window.getOrDefault(charNow, 0) + 1);

                    if (window.get(charNow).equals(needs.get(charNow))) {
                        match++;
                    }
                }
                while (match == needs.size()) {
                    if (right - left < resLength) {
                        resStart = left;
                        resLength = right - left;
                    }


                    char charRemoved = s.charAt(left);
                    left++;

                    if (window.containsKey(charRemoved)) {
                        if (window.get(charRemoved).equals(needs.get(charRemoved))) {
                            match--;
                        }
                        window.put(charRemoved, window.get(charRemoved) - 1);

                    }

                }

            }


            return resLength == Integer.MAX_VALUE ? "" : res.substring(resStart, resStart + resLength);
        }
    }

    public static String minWindow(String s, String t) {

        HashMap<Character, Integer> needs = new HashMap<>();

        HashMap<Character, Integer> window = new HashMap<>();

        for (int i = 0; i < t.length(); i++) {
            Integer integer = needs.getOrDefault(t.charAt(i), 0);
            needs.put(t.charAt(i), integer + 1);
        }

        // 记录最小覆盖子串的起始索引及长度
        int start = 0, len = Integer.MAX_VALUE;


        int left = 0, right = 0;
        //valid 变量表示窗口中满足 need 条件的字符个数
        int valid = 0;

        while (right < s.length()) {
            // 开始滑动
            // c 是将移入窗口的字符
            char c = s.charAt(right);

            right++;
            // 进行窗口内数据的一系列更新
            if (needs.containsKey(c)) {
                //往窗口里加
                window.put(c, window.getOrDefault(c, 0) + 1);
                if (window.get(c).equals(needs.get(c))) {
                    valid++;
                }
            }

            //当valid计数等于 need的长度时，说明字符串数量已经匹配了
            while (needs.size() == valid) {
                // 在这里更新最小覆盖子串
                if (right - left < len) {
                    start = left;
                    len = right - left;
                }

                char d = s.charAt(left);
                left++;
                // 进行窗口内数据的一系列更新
                if (needs.containsKey(d)) {
                    if (needs.get(d).equals(window.get(d))) {
                        valid--;
                    }
                    window.put(d, window.get(d) - 1);

                }


            }

        }


        // 返回最小覆盖子串
        return len == Integer.MAX_VALUE ?
                "" : s.substring(start, start + len);
    }

}
