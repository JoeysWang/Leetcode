package review.answer;

import data.TreeNode;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * 第一章答案：动态规划。
 *
 * 读 DP 代码时先看注释里的状态定义，再看转移。
 */
public class Chapter01DynamicProgrammingAnswer {

    public static void main(String[] args) {
        System.out.println("第一章答案：动态规划。");
    }

    // dp[i] 表示以 nums[i] 结尾的最长递增子序列长度。
    public int lengthOfLIS(int[] nums) {
        int[] dp = new int[nums.length];
        Arrays.fill(dp, 1);
        int ans = 0;
        for (int i = 0; i < nums.length; i++) {
            for (int j = 0; j < i; j++) {
                if (nums[j] < nums[i]) {
                    dp[i] = Math.max(dp[i], dp[j] + 1);
                }
            }
            ans = Math.max(ans, dp[i]);
        }
        return ans;
    }

    // piles[len] 表示长度为 len+1 的递增子序列，结尾元素最小是多少。
    public int lengthOfLISBinarySearch(int[] nums) {
        int[] piles = new int[nums.length];
        int size = 0;
        for (int num : nums) {
            int left = 0;
            int right = size;
            while (left < right) {
                int mid = left + (right - left) / 2;
                if (piles[mid] >= num) {
                    right = mid;
                } else {
                    left = mid + 1;
                }
            }
            piles[left] = num;
            if (left == size) {
                size++;
            }
        }
        return size;
    }

    // 宽度升序；宽度相同时高度降序，避免同宽信封互相套。
    public int maxEnvelopes(int[][] envelopes) {
        Arrays.sort(envelopes, (a, b) -> a[0] == b[0] ? b[1] - a[1] : a[0] - b[0]);
        int[] heights = new int[envelopes.length];
        for (int i = 0; i < envelopes.length; i++) {
            heights[i] = envelopes[i][1];
        }
        return lengthOfLISBinarySearch(heights);
    }

    // dp[w] 表示容量为 w 时能装下的最大价值。0-1 背包容量要倒序。
    public int zeroOneKnapsack(int capacity, int[] weights, int[] values) {
        int[] dp = new int[capacity + 1];
        for (int i = 0; i < weights.length; i++) {
            for (int w = capacity; w >= weights[i]; w--) {
                dp[w] = Math.max(dp[w], dp[w - weights[i]] + values[i]);
            }
        }
        return dp[capacity];
    }

    public boolean canPartition(int[] nums) {
        int sum = 0;
        for (int num : nums) {
            sum += num;
        }
        if (sum % 2 == 1) {
            return false;
        }
        int target = sum / 2;
        boolean[] dp = new boolean[target + 1];
        dp[0] = true;
        for (int num : nums) {
            for (int j = target; j >= num; j--) {
                dp[j] = dp[j] || dp[j - num];
            }
        }
        return dp[target];
    }

    // 转换为：选一些数凑成 bag = (sum + target) / 2。
    public int findTargetSumWays(int[] nums, int target) {
        int sum = 0;
        for (int num : nums) {
            sum += num;
        }
        if (Math.abs(target) > sum || (sum + target) % 2 == 1) {
            return 0;
        }
        int bag = (sum + target) / 2;
        int[] dp = new int[bag + 1];
        dp[0] = 1;
        for (int num : nums) {
            for (int j = bag; j >= num; j--) {
                dp[j] += dp[j - num];
            }
        }
        return dp[bag];
    }

    // 完全背包：每个 coin 可以重复使用。
    public int coinChange(int[] coins, int amount) {
        int impossible = amount + 1;
        int[] dp = new int[amount + 1];
        Arrays.fill(dp, impossible);
        dp[0] = 0;
        for (int coin : coins) {
            for (int money = coin; money <= amount; money++) {
                dp[money] = Math.min(dp[money], dp[money - coin] + 1);
            }
        }
        return dp[amount] == impossible ? -1 : dp[amount];
    }

    // dp[j] 表示凑出金额 j 的组合数。外层 coin，内层金额，可以避免排列重复。
    public int change(int amount, int[] coins) {
        int[] dp = new int[amount + 1];
        dp[0] = 1;
        for (int coin : coins) {
            for (int j = coin; j <= amount; j++) {
                dp[j] += dp[j - coin];
            }
        }
        return dp[amount];
    }

    public int numSquares(int n) {
        int[] dp = new int[n + 1];
        Arrays.fill(dp, n + 1);
        dp[0] = 0;
        for (int square = 1; square * square <= n; square++) {
            int value = square * square;
            for (int j = value; j <= n; j++) {
                dp[j] = Math.min(dp[j], dp[j - value] + 1);
            }
        }
        return dp[n];
    }

    // dp[i][j] 表示 word1 前 i 个字符变成 word2 前 j 个字符的最少操作数。
    public int minDistance(String word1, String word2) {
        int m = word1.length();
        int n = word2.length();
        int[][] dp = new int[m + 1][n + 1];
        for (int i = 0; i <= m; i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= n; j++) {
            dp[0][j] = j;
        }
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = min3(dp[i - 1][j], dp[i][j - 1], dp[i - 1][j - 1]) + 1;
                }
            }
        }
        return dp[m][n];
    }

    public int longestCommonSubsequence(String text1, String text2) {
        int m = text1.length();
        int n = text2.length();
        int[][] dp = new int[m + 1][n + 1];
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }
        return dp[m][n];
    }

    // dp[i][j] 表示 s[i..j] 的最长回文子序列长度。
    public int longestPalindromeSubseq(String s) {
        int n = s.length();
        int[][] dp = new int[n][n];
        for (int i = n - 1; i >= 0; i--) {
            dp[i][i] = 1;
            for (int j = i + 1; j < n; j++) {
                if (s.charAt(i) == s.charAt(j)) {
                    dp[i][j] = dp[i + 1][j - 1] + 2;
                } else {
                    dp[i][j] = Math.max(dp[i + 1][j], dp[i][j - 1]);
                }
            }
        }
        return dp[0][n - 1];
    }

    public int minInsertions(String s) {
        return s.length() - longestPalindromeSubseq(s);
    }

    // 递归含义：k 个鸡蛋、n 层楼，最坏情况下至少扔几次。
    public int superEggDrop(int k, int n) {
        return eggDropMemo(k, n, new HashMap<>());
    }

    private int eggDropMemo(int eggs, int floors, Map<String, Integer> memo) {
        if (floors == 0 || floors == 1) {
            return floors;
        }
        if (eggs == 1) {
            return floors;
        }
        String key = eggs + "," + floors;
        if (memo.containsKey(key)) {
            return memo.get(key);
        }

        int ans = floors;
        int left = 1;
        int right = floors;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            int broken = eggDropMemo(eggs - 1, mid - 1, memo);
            int notBroken = eggDropMemo(eggs, floors - mid, memo);
            int worst = Math.max(broken, notBroken) + 1;
            ans = Math.min(ans, worst);
            if (broken > notBroken) {
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }

        memo.put(key, ans);
        return ans;
    }

    // dp[m][e] 表示扔 m 次、e 个鸡蛋，最多能确定多少层。
    public int superEggDropByMoves(int k, int n) {
        int[] dp = new int[k + 1];
        int moves = 0;
        while (dp[k] < n) {
            moves++;
            for (int eggs = k; eggs >= 1; eggs--) {
                dp[eggs] = dp[eggs] + dp[eggs - 1] + 1;
            }
        }
        return moves;
    }

    // dp[i][j] 存区间 [i,j] 内先手和后手的最大分差。
    public boolean predictTheWinner(int[] nums) {
        int n = nums.length;
        int[][] dp = new int[n][n];
        for (int i = 0; i < n; i++) {
            dp[i][i] = nums[i];
        }
        for (int len = 2; len <= n; len++) {
            for (int i = 0; i + len - 1 < n; i++) {
                int j = i + len - 1;
                dp[i][j] = Math.max(nums[i] - dp[i + 1][j], nums[j] - dp[i][j - 1]);
            }
        }
        return dp[0][n - 1] >= 0;
    }

    public boolean stoneGame(int[] piles) {
        return true;
    }

    public boolean isMatchRegex(String s, String p) {
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

    // dp[i] 表示 i 次按键最多能得到多少个 A。
    public int maxA(int n) {
        int[] dp = new int[n + 1];
        for (int i = 1; i <= n; i++) {
            dp[i] = dp[i - 1] + 1;
            for (int copyAt = 2; copyAt < i; copyAt++) {
                // copyAt-2 次前打出内容，然后 Ctrl-A、Ctrl-C，剩余次数全部 Ctrl-V。
                dp[i] = Math.max(dp[i], dp[copyAt - 2] * (i - copyAt + 1));
            }
        }
        return dp[n];
    }

    public int strStrKmp(String haystack, String needle) {
        if (needle.isEmpty()) {
            return 0;
        }
        int[] lps = buildLps(needle);
        int j = 0;
        for (int i = 0; i < haystack.length(); i++) {
            while (j > 0 && haystack.charAt(i) != needle.charAt(j)) {
                j = lps[j - 1];
            }
            if (haystack.charAt(i) == needle.charAt(j)) {
                j++;
            }
            if (j == needle.length()) {
                return i - needle.length() + 1;
            }
        }
        return -1;
    }

    private int[] buildLps(String pattern) {
        int[] lps = new int[pattern.length()];
        int j = 0;
        for (int i = 1; i < pattern.length(); i++) {
            while (j > 0 && pattern.charAt(i) != pattern.charAt(j)) {
                j = lps[j - 1];
            }
            if (pattern.charAt(i) == pattern.charAt(j)) {
                j++;
            }
            lps[i] = j;
        }
        return lps;
    }

    public int eraseOverlapIntervals(int[][] intervals) {
        if (intervals.length == 0) {
            return 0;
        }
        Arrays.sort(intervals, Comparator.comparingInt(a -> a[1]));
        int keep = 1;
        int end = intervals[0][1];
        for (int i = 1; i < intervals.length; i++) {
            if (intervals[i][0] >= end) {
                keep++;
                end = intervals[i][1];
            }
        }
        return intervals.length - keep;
    }

    public int findMinArrowShots(int[][] points) {
        if (points.length == 0) {
            return 0;
        }
        Arrays.sort(points, Comparator.comparingInt(a -> a[1]));
        int arrows = 1;
        int end = points[0][1];
        for (int i = 1; i < points.length; i++) {
            if (points[i][0] > end) {
                arrows++;
                end = points[i][1];
            }
        }
        return arrows;
    }

    public int maxProfitOneTransaction(int[] prices) {
        int minPrice = Integer.MAX_VALUE;
        int ans = 0;
        for (int price : prices) {
            minPrice = Math.min(minPrice, price);
            ans = Math.max(ans, price - minPrice);
        }
        return ans;
    }

    public int maxProfitUnlimitedTransactions(int[] prices) {
        int ans = 0;
        for (int i = 1; i < prices.length; i++) {
            if (prices[i] > prices[i - 1]) {
                ans += prices[i] - prices[i - 1];
            }
        }
        return ans;
    }

    // dp[t][0/1]：当前完成最多 t 次交易时，不持有/持有股票的最大利润。
    public int maxProfitKTransactions(int k, int[] prices) {
        if (prices.length == 0) {
            return 0;
        }
        if (k >= prices.length / 2) {
            return maxProfitUnlimitedTransactions(prices);
        }
        int[][] dp = new int[k + 1][2];
        for (int t = 0; t <= k; t++) {
            dp[t][1] = Integer.MIN_VALUE / 2;
        }
        for (int price : prices) {
            for (int t = k; t >= 1; t--) {
                dp[t][0] = Math.max(dp[t][0], dp[t][1] + price);
                dp[t][1] = Math.max(dp[t][1], dp[t - 1][0] - price);
            }
        }
        return dp[k][0];
    }

    public int maxProfitWithCooldown(int[] prices) {
        int hold = Integer.MIN_VALUE / 2;
        int sold = 0;
        int rest = 0;
        for (int price : prices) {
            int oldSold = sold;
            sold = hold + price;
            hold = Math.max(hold, rest - price);
            rest = Math.max(rest, oldSold);
        }
        return Math.max(sold, rest);
    }

    public int maxProfitWithFee(int[] prices, int fee) {
        int cash = 0;
        int hold = Integer.MIN_VALUE / 2;
        for (int price : prices) {
            cash = Math.max(cash, hold + price);
            hold = Math.max(hold, cash - price - fee);
        }
        return cash;
    }

    public int robLine(int[] nums) {
        int prev2 = 0;
        int prev1 = 0;
        for (int num : nums) {
            int cur = Math.max(prev1, prev2 + num);
            prev2 = prev1;
            prev1 = cur;
        }
        return prev1;
    }

    public int robCircle(int[] nums) {
        if (nums.length == 1) {
            return nums[0];
        }
        return Math.max(robRange(nums, 0, nums.length - 2), robRange(nums, 1, nums.length - 1));
    }

    private int robRange(int[] nums, int start, int end) {
        int prev2 = 0;
        int prev1 = 0;
        for (int i = start; i <= end; i++) {
            int cur = Math.max(prev1, prev2 + nums[i]);
            prev2 = prev1;
            prev1 = cur;
        }
        return prev1;
    }

    public int robTree(TreeNode root) {
        int[] ans = robTreeState(root);
        return Math.max(ans[0], ans[1]);
    }

    // 返回数组含义：[不抢当前节点的最大值, 抢当前节点的最大值]。
    private int[] robTreeState(TreeNode root) {
        if (root == null) {
            return new int[]{0, 0};
        }
        int[] left = robTreeState(root.left);
        int[] right = robTreeState(root.right);
        int notRob = Math.max(left[0], left[1]) + Math.max(right[0], right[1]);
        int rob = root.val + left[0] + right[0];
        return new int[]{notRob, rob};
    }

    private int min3(int a, int b, int c) {
        return Math.min(a, Math.min(b, c));
    }
}
