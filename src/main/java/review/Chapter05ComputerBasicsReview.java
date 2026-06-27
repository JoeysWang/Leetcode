package review;

/**
 * 第五章、计算机技术。
 *
 * 默写目标：
 * 1. Shell：能用 grep、awk、sed、sort、uniq、head、tail 组合解决文本题。
 * 2. SQL：能写 join、group by、having、delete duplicate、窗口函数。
 * 3. Web 基础：解释 cookie、session、token。
 * 4. 加密基础：区分哈希、对称加密、非对称加密、签名。
 *
 * 对应题目：
 * Shell: 192, 193, 194, 195.
 * SQL: 175, 176, 181, 182, 183, 196, 197.
 * Regex/String: 10, 44, 242, 49, 706.
 */
public class Chapter05ComputerBasicsReview {

    public static void main(String[] args) {
        System.out.println("第五章：在本文件中默写 Shell、SQL、正则和基础概念。");
    }

    // Shell
    public String wordFrequencyCommand() {
        throw todo("192 Word Frequency");
    }

    public String validPhoneNumbersCommand() {
        throw todo("193 Valid Phone Numbers");
    }

    public String transposeFileCommand() {
        throw todo("194 Transpose File");
    }

    public String tenthLineCommand() {
        throw todo("195 Tenth Line");
    }

    // SQL
    public String combineTwoTablesSql() {
        throw todo("175 Combine Two Tables");
    }

    public String secondHighestSalarySql() {
        throw todo("176 Second Highest Salary");
    }

    public String employeesEarningMoreThanManagersSql() {
        throw todo("181 Employees Earning More Than Their Managers");
    }

    public String duplicateEmailsSql() {
        throw todo("182 Duplicate Emails");
    }

    public String customersWhoNeverOrderSql() {
        throw todo("183 Customers Who Never Order");
    }

    public String deleteDuplicateEmailsSql() {
        throw todo("196 Delete Duplicate Emails");
    }

    public String risingTemperatureSql() {
        throw todo("197 Rising Temperature");
    }

    // 正则 / 字符串
    public boolean regexMatch(String s, String p) {
        throw todo("10 Regular Expression Matching");
    }

    public boolean wildcardMatch(String s, String p) {
        throw todo("44 Wildcard Matching");
    }

    // 概念默写：返回你自己的总结文本
    public String explainProcessThreadFileDescriptor() {
        throw todo("进程、线程、文件描述符");
    }

    public String explainCookieSessionToken() {
        throw todo("cookie、session、token");
    }

    public String explainCryptoBasics() {
        throw todo("哈希、对称加密、非对称加密、签名");
    }

    private static UnsupportedOperationException todo(String topic) {
        return new UnsupportedOperationException("TODO 默写: " + topic);
    }
}
