import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class Main242异位词 {
    public static void main(String[] args) {

      boolean result=  isAnagram("caar","rac");
      System.out.println(result);
      test();
    }
    public static boolean isAnagram(String s, String t) {

        Map<Character, Integer> couts = new HashMap<>();
        Map<Character, Integer> couts2 = new HashMap<>();


        toMap(s, couts);
        toMap(t, couts2);

        boolean result = true;
        if (couts.keySet().size()!=couts2.keySet().size())
            return false;
        Set<Character> characters = couts.keySet();

        for (Character character : characters) {
            if (!couts.get(character).equals(couts2.get(character))) {
                return false;
            }
        }

        return result;
    }

    private static void toMap(String s, Map<Character, Integer> couts) {
        for (char c : s.toCharArray()) {
            if (couts.containsKey(c)) {
                couts.put(c, couts.get(c) + 1);
            } else
                couts.put(c, 1);

        }
    }

    public static void test() {
        TestUtil.reset();

        // 测试用例1: 示例用例 - 是异位词
        TestUtil.assertTrue(isAnagram("anagram", "nagaram"), "示例用例 \"anagram\" 与 \"nagaram\"");

        // 测试用例2: 示例用例 - 不是异位词
        TestUtil.assertFalse(isAnagram("rat", "car"), "示例用例 \"rat\" 与 \"car\"");

        // 测试用例3: 边界用例 - 空字符串
        TestUtil.assertTrue(isAnagram("", ""), "边界用例 两个空字符串");

        // 测试用例4: 长度不同
        TestUtil.assertFalse(isAnagram("ab", "a"), "边界用例 长度不同 \"ab\" 与 \"a\"");

        // 测试用例5: 字符相同但频次不同
        TestUtil.assertFalse(isAnagram("aab", "abb"), "普通用例 \"aab\" 与 \"abb\" 频次不同");

        // 测试用例6: 单个字符
        TestUtil.assertTrue(isAnagram("a", "a"), "边界用例 单个字符 \"a\" 与 \"a\"");

        TestUtil.printSummary();
    }

}
