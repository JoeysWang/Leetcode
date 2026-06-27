

public class Main6Z字形变换 {

    //    将一个给定字符串根据给定的行数，以从上往下、从左到右进行 Z 字形排列。
//
//    比如输入字符串为 "LEETCODEISHIRING" 行数为 3 时，排列如下：
//
//    L   C   I   R
//    E T O E S I I G
//    E   D   H   N
//    之后，你的输出需要从左往右逐行读取，产生出一个新的字符串，
//    比如："LCIRETOESIIGEDHN"。
//
//    请你实现这个将字符串进行指定行数变换的函数：
//
//    string convert(string s, int numRows);
//    示例 1:
//
//    输入: s = "LEETCODEISHIRING", numRows = 3
//    输出: "LCIRETOESIIGEDHN"
//    示例 2:
//
//    输入: s = "LEETCODEISHIRING", numRows = 4
//    输出: "LDREOEIIECIHNTSG"
//    解释:
//
//    L     D     R
//    E   O E   I I
//    E C   I H   N
//    T     S     G



//    L
//    E   O
//    E C
//    T      eachCount=(2 * numRows - 2)


    public String convert(String s, int numRows) {
        // 边界情况：只有一行时不需要变换
        if (numRows == 1) return s;

        // 初始化 StringBuilder 数组
        StringBuilder[] sbs = new StringBuilder[numRows];
        for (int i = 0; i < numRows; i++) {
            sbs[i] = new StringBuilder();
        }

        // 每个周期的长度
        int cycleLen = 2 * numRows - 2;

        // 遍历字符串，将每个字符放到对应的行
        for (int i = 0; i < s.length(); i++) {
            int index = i % cycleLen;
            // 如果在前 numRows 个位置，直接放到对应行
            // 如果在后半个周期，需要计算对称位置
            if (index < numRows) {
                sbs[index].append(s.charAt(i));
            } else {
                sbs[cycleLen - index].append(s.charAt(i));
            }
        }

        // 拼接所有行
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < numRows; i++) {
            result.append(sbs[i]);
        }

        return result.toString();
    }

    public static void test() {
        TestUtil.reset();
        Main6Z字形变换 solution = new Main6Z字形变换();

        // 测试用例1: 示例用例1
        String result1 = solution.convert("LEETCODEISHIRING", 3);
        TestUtil.assertEquals("LCIRETOESIIGEDHN", result1, "示例用例1: Z字形变换 numRows=3");

        // 测试用例2: 示例用例2
        String result2 = solution.convert("LEETCODEISHIRING", 4);
        TestUtil.assertEquals("LDREOEIIECIHNTSG", result2, "示例用例2: Z字形变换 numRows=4");

        // 测试用例3: 边界用例 - numRows=1
        String result3 = solution.convert("AB", 1);
        TestUtil.assertEquals("AB", result3, "边界用例: numRows=1");

        // 测试用例4: 普通用例
        String result4 = solution.convert("PAYPALISHIRING", 3);
        TestUtil.assertEquals("PAHNAPLSIIGYIR", result4, "普通用例: PAYPALISHIRING numRows=3");

        TestUtil.printSummary();
    }

    public static void main(String[] args) {
        test();
    }
}
