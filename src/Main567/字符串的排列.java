package Main567;

import java.util.HashMap;

public class 字符串的排列 {

    /**
     * 给定两个字符串 s1 和 s2，写一个函数来判断 s2 是否包含 s1 的排列。
     * <p>
     * 换句话说，第一个字符串的排列之一是第二个字符串的子串。
     * <p>
     * 示例1:
     * <p>
     * 输入: s1 = "ab" s2 = "eidbaooo"
     * 输出: True
     * 解释: s2 包含 s1 的排列之一 ("ba").
     *  
     * <p>
     * 示例2:
     * <p>
     * 输入: s1= "ab" s2 = "eidboaoo"
     * 输出: False
     *  
     * <p>
     * 注意：
     * <p>
     * 输入的字符串只包含小写字母
     * 两个字符串的长度都在 [1, 10,000] 之间
     */
    public static void main(String[] args) {

        String s1 = "ab";
        String s2 = "eidboaoo";

        boolean result = checkInclusion(s1, s2);
        System.out.println(result);

    }

    public static boolean checkInclusion(String s1, String s2) {
        HashMap<Character, Integer> needs = new HashMap<>();
        HashMap<Character, Integer> window = new HashMap<>();

        int valid = 0;
        int left = 0, right = 0;

        for (int i = 0; i < s1.length(); i++) {
            needs.put(s1.charAt(i), needs.getOrDefault(s1.charAt(i), 0) + 1);
        }

        for (int i = 0; i < s2.length(); i++) {

            char c = s2.charAt(i);
            right++;

            if (needs.containsKey(c)) {
                window.put(c, window.getOrDefault(c, 0) + 1);
                if (window.get(c).equals(needs.get(c))) {
                    valid++;
                }
            }
            System.out.println(String.format("window: [%d, %d)\n", left, right));
            while ((right - left) >= s1.length()) {
                if (valid == needs.size())
                    return true;
                char d = s2.charAt(left);
                left++;

                if (needs.containsKey(d)) {
                    if (needs.get(d).equals(window.get(d)))
                        valid--;
                    window.put(d, window.get(d) - 1);
                }

            }


        }


        return false;
    }
}
