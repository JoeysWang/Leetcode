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
}
