package review.answer;

/**
 * 第五章答案：计算机技术基础。
 *
 * Shell 和 SQL 写成字符串，方便你直接复制、默写、对照。
 */
public class Chapter05ComputerBasicsAnswer {

    public static void main(String[] args) {
        System.out.println("第五章答案：Shell、SQL、正则和基础概念。");
    }

    public String wordFrequencyCommand() {
        return "cat words.txt | tr -s ' ' '\\n' | sort | uniq -c | sort -nr | awk '{print $2\" \"$1}'";
    }

    public String validPhoneNumbersCommand() {
        return "grep -E '^(\\([0-9]{3}\\) [0-9]{3}-[0-9]{4}|[0-9]{3}-[0-9]{3}-[0-9]{4})$' file.txt";
    }

    public String transposeFileCommand() {
        return "awk '{ for (i = 1; i <= NF; i++) a[NR, i] = $i } NF > max { max = NF } END { for (i = 1; i <= max; i++) { line = a[1, i]; for (j = 2; j <= NR; j++) line = line \" \" a[j, i]; print line } }' file.txt";
    }

    public String tenthLineCommand() {
        return "sed -n '10p' file.txt";
    }

    public String combineTwoTablesSql() {
        return "select p.FirstName, p.LastName, a.City, a.State "
                + "from Person p left join Address a on p.PersonId = a.PersonId;";
    }

    public String secondHighestSalarySql() {
        return "select (select distinct Salary from Employee order by Salary desc limit 1 offset 1) as SecondHighestSalary;";
    }

    public String employeesEarningMoreThanManagersSql() {
        return "select e.Name as Employee "
                + "from Employee e join Employee m on e.ManagerId = m.Id "
                + "where e.Salary > m.Salary;";
    }

    public String duplicateEmailsSql() {
        return "select Email from Person group by Email having count(*) > 1;";
    }

    public String customersWhoNeverOrderSql() {
        return "select c.Name as Customers "
                + "from Customers c left join Orders o on c.Id = o.CustomerId "
                + "where o.Id is null;";
    }

    public String deleteDuplicateEmailsSql() {
        return "delete p1 from Person p1 join Person p2 "
                + "on p1.Email = p2.Email and p1.Id > p2.Id;";
    }

    public String risingTemperatureSql() {
        return "select w1.Id "
                + "from Weather w1 join Weather w2 "
                + "on datediff(w1.RecordDate, w2.RecordDate) = 1 "
                + "where w1.Temperature > w2.Temperature;";
    }

    // 10. Regular Expression Matching：'.' 匹配任意单字符，'*' 匹配前一个字符零次或多次。
    public boolean regexMatch(String s, String p) {
        int m = s.length();
        int n = p.length();
        boolean[][] dp = new boolean[m + 1][n + 1];
        dp[0][0] = true;
        for (int j = 2; j <= n; j++) {
            if (p.charAt(j - 1) == '*') {
                dp[0][j] = dp[0][j - 2];
            }
        }

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                char pc = p.charAt(j - 1);
                if (pc == '*') {
                    dp[i][j] = dp[i][j - 2];
                    char prev = p.charAt(j - 2);
                    if (prev == '.' || prev == s.charAt(i - 1)) {
                        dp[i][j] |= dp[i - 1][j];
                    }
                } else if (pc == '.' || pc == s.charAt(i - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                }
            }
        }
        return dp[m][n];
    }

    // 44. Wildcard Matching：'?' 匹配单字符，'*' 匹配任意长度字符串。
    public boolean wildcardMatch(String s, String p) {
        int m = s.length();
        int n = p.length();
        boolean[][] dp = new boolean[m + 1][n + 1];
        dp[0][0] = true;
        for (int j = 1; j <= n; j++) {
            if (p.charAt(j - 1) == '*') {
                dp[0][j] = dp[0][j - 1];
            }
        }

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                char pc = p.charAt(j - 1);
                if (pc == '*') {
                    dp[i][j] = dp[i][j - 1] || dp[i - 1][j];
                } else if (pc == '?' || pc == s.charAt(i - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                }
            }
        }
        return dp[m][n];
    }

    public String explainProcessThreadFileDescriptor() {
        return "进程是资源分配单位，拥有独立地址空间；线程是 CPU 调度单位，多个线程共享同一进程资源；"
                + "文件描述符是进程打开文件、socket、管道等内核对象后得到的整数句柄。";
    }

    public String explainCookieSessionToken() {
        return "cookie 存在客户端，会随请求发送；session 通常存在服务端，客户端只保存 session id；"
                + "token 通常是可携带身份信息或签名的令牌，服务端可通过校验 token 判断身份。";
    }

    public String explainCryptoBasics() {
        return "哈希是不可逆摘要，常用于完整性校验和密码摘要；对称加密加解密用同一密钥，速度快；"
                + "非对称加密有公钥和私钥，适合密钥交换和身份认证；签名用私钥生成、公钥验证，证明来源和防篡改。";
    }
}
