import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class Main14最长公共前缀 {
    //    输入: ["flower","flow","flight"]
//    输出: "fl"

    /**
     * 二分查找计算中位数的公式：
     *
     * int overflow = (right - left) & 1; //是否为奇数，奇数在后面+1·
     * int mid = left + (right - left) / 2 + overflow;
     *
     * 0 1 2 3 4 5
     */
    public static void main(String[] args) {

        Solution solution = new Solution();
        String res = solution.longestCommonPrefix2(new String[]{"flower", "flowe", "flower"});
        System.out.println("res: " + res);
        test();
    }

    public static class Solution {

        //二分查找
        public String longestCommonPrefix2(String[] strs) {
            if (strs == null || strs.length < 1)
                return "";
            String res = strs[0];

            for (String str : strs) {
                if (str.length() < res.length())
                    res = str;
            }
            return findBydivde(strs, res, 0, res.length());
        }

        // 0 1 2 3 4
        // 0 1 2 3 4 6
        private String findBydivde(String[] strs, String common, int left, int right) {
            String res = "";
            while (left <= right) {
                int overflow = (right - left) & 1;

                int mid = left + (right - left) / 2 + overflow;

                if (left == right) mid = right;

                if (isFind(strs, common, mid)) {
                    res = common.substring(0, mid);
                    left = mid + 1;
                } else {
                    right = mid - 1;
                }

            }
            return res;
        }

        private boolean isFind(String[] strs, String common, int end) {
            if (end == 0) return false;

            boolean compared = true;
            String compare = common.substring(0, end);
            for (String str : strs) {
                if (!str.startsWith(compare)) {
                    compared = false;
                    break;
                }
            }
            return compared;
        }


        //暴力
        public String longestCommonPrefix(String[] strs) {

            if (strs == null || strs.length < 1)
                return "";


            HashSet<Character> set = new HashSet<>();

            String res = "";

            int index = 0;

            while (true) {

                set.clear();
                for (String str : strs) {
                    if (index >= str.length()) return res;
                    set.add(str.charAt(index));
                }
                if (set.size() > 1) return res;
                res = res + set.iterator().next();
                index++;
            }


        }
    }

    public static void test() {
        TestUtil.reset();
        Solution solution = new Solution();

        // 测试用例1: 示例用例 - ["flower","flow","flight"] → "fl" (二分法)
        TestUtil.assertEquals("fl", solution.longestCommonPrefix2(new String[]{"flower", "flow", "flight"}), "示例用例: 二分法找公共前缀");

        // 测试用例2: 无公共前缀 - ["dog","racecar","car"] → ""
        TestUtil.assertEquals("", solution.longestCommonPrefix2(new String[]{"dog", "racecar", "car"}), "无公共前缀: 二分法");

        // 测试用例3: 边界用例 - 空数组 → ""
        TestUtil.assertEquals("", solution.longestCommonPrefix2(new String[]{}), "边界用例: 空数组");

        // 测试用例4: 单个元素 - ["alone"] → "alone"
        TestUtil.assertEquals("alone", solution.longestCommonPrefix2(new String[]{"alone"}), "边界用例: 单个元素");

        // 测试用例5: 暴力法 - ["flower","flow","flight"] → "fl"
        TestUtil.assertEquals("fl", solution.longestCommonPrefix(new String[]{"flower", "flow", "flight"}), "示例用例: 暴力法找公共前缀");

        // 测试用例6: 暴力法无公共前缀
        TestUtil.assertEquals("", solution.longestCommonPrefix(new String[]{"dog", "racecar", "car"}), "无公共前缀: 暴力法");

        TestUtil.printSummary();
    }
}
