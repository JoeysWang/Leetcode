package review.answer;

import data.ListNode;
import data.TreeNode;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * 第零章答案：基础模板。
 *
 * 这章不追求“最短代码”，重点是把每类题的骨架默熟。
 */
public class Chapter00MustReadAnswer {

    public static void main(String[] args) {
        System.out.println("第零章答案：基础算法模板。");
    }

    // 数组遍历：线性结构最常见的访问方式。
    public void traverseArray(int[] nums) {
        for (int i = 0; i < nums.length; i++) {
            int value = nums[i];
            System.out.println("index=" + i + ", value=" + value);
        }
    }

    // 链表遍历：每次通过 next 指针走到下一个节点。
    public void traverseList(ListNode head) {
        for (ListNode cur = head; cur != null; cur = cur.next) {
            System.out.println(cur.val);
        }
    }

    // 二叉树遍历：前序位置适合自顶向下，后序位置适合自底向上。
    public void traverseTree(TreeNode root) {
        if (root == null) {
            return;
        }
        System.out.println("pre order: " + root.val);
        traverseTree(root.left);
        System.out.println("in order: " + root.val);
        traverseTree(root.right);
        System.out.println("post order: " + root.val);
    }

    // graph[i] 表示节点 i 能到达的所有邻居。
    public void traverseGraph(int[][] graph) {
        boolean[] visited = new boolean[graph.length];
        for (int i = 0; i < graph.length; i++) {
            dfsGraph(graph, i, visited);
        }
    }

    private void dfsGraph(int[][] graph, int node, boolean[] visited) {
        if (visited[node]) {
            return;
        }
        visited[node] = true;
        System.out.println("visit node: " + node);
        for (int next : graph[node]) {
            dfsGraph(graph, next, visited);
        }
    }

    // DFS 模板：深度优先，一条路走到底，再回退走别的路。
    public void dfsTemplate(int[][] graph, int start) {
        boolean[] visited = new boolean[graph.length];
        dfsTemplate(graph, start, visited);
    }

    private void dfsTemplate(int[][] graph, int node, boolean[] visited) {
        // 这个点来过了，直接返回，避免环导致无限递归。
        if (visited[node]) {
            return;
        }

        // 第一次来到这个点，立刻标记。
        visited[node] = true;
        System.out.println("DFS visit node: " + node);

        // graph[node] 是 node 的邻居列表，next 才是下一个节点编号。
        for (int next : graph[node]) {
            dfsTemplate(graph, next, visited);
        }
    }

    // BFS 模板：广度优先，先访问离 start 最近的一层，再访问下一层。
    public void bfsTemplate(int[][] graph, int start) {
        boolean[] visited = new boolean[graph.length];
        Queue<Integer> queue = new ArrayDeque<>();

        // BFS 推荐入队时就标记，避免同一个点被重复放进队列。
        queue.offer(start);
        visited[start] = true;

        while (!queue.isEmpty()) {
            int node = queue.poll();
            System.out.println("BFS visit node: " + node);

            for (int next : graph[node]) {
                if (visited[next]) {
                    continue;
                }
                visited[next] = true;
                queue.offer(next);
            }
        }
    }

    // 322. Coin Change
    public int coinChange(int[] coins, int amount) {
        int impossible = amount + 1;
        int[] dp = new int[amount + 1];
        Arrays.fill(dp, impossible);
        dp[0] = 0;

        for (int money = 1; money <= amount; money++) {
            for (int coin : coins) {
                if (money >= coin) {
                    dp[money] = Math.min(dp[money], dp[money - coin] + 1);
                }
            }
        }
        return dp[amount] == impossible ? -1 : dp[amount];
    }

    // 70. Climbing Stairs
    public int climbStairs(int n) {
        if (n <= 2) {
            return n;
        }
        int prev2 = 1;
        int prev1 = 2;
        for (int i = 3; i <= n; i++) {
            int cur = prev1 + prev2;
            prev2 = prev1;
            prev1 = cur;
        }
        return prev1;
    }

    // 46. Permutations
    public List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> ans = new ArrayList<>();
        boolean[] used = new boolean[nums.length];
        backtrackPermute(nums, used, new ArrayList<>(), ans);
        return ans;
    }

    private void backtrackPermute(int[] nums, boolean[] used, List<Integer> path, List<List<Integer>> ans) {
        if (path.size() == nums.length) {
            ans.add(new ArrayList<>(path));
            return;
        }
        for (int i = 0; i < nums.length; i++) {
            if (used[i]) {
                continue;
            }
            used[i] = true;
            path.add(nums[i]);
            backtrackPermute(nums, used, path, ans);
            path.remove(path.size() - 1);
            used[i] = false;
        }
    }

    // 77. Combinations
    public List<List<Integer>> combine(int n, int k) {
        List<List<Integer>> ans = new ArrayList<>();
        backtrackCombine(1, n, k, new ArrayList<>(), ans);
        return ans;
    }

    private void backtrackCombine(int start, int n, int k, List<Integer> path, List<List<Integer>> ans) {
        if (path.size() == k) {
            ans.add(new ArrayList<>(path));
            return;
        }
        for (int num = start; num <= n; num++) {
            path.add(num);
            backtrackCombine(num + 1, n, k, path, ans);
            path.remove(path.size() - 1);
        }
    }

    // 78. Subsets
    public List<List<Integer>> subsets(int[] nums) {
        List<List<Integer>> ans = new ArrayList<>();
        backtrackSubsets(nums, 0, new ArrayList<>(), ans);
        return ans;
    }

    private void backtrackSubsets(int[] nums, int start, List<Integer> path, List<List<Integer>> ans) {
        ans.add(new ArrayList<>(path));
        for (int i = start; i < nums.length; i++) {
            path.add(nums[i]);
            backtrackSubsets(nums, i + 1, path, ans);
            path.remove(path.size() - 1);
        }
    }

    // 704. Binary Search
    public int binarySearch(int[] nums, int target) {
        int left = 0;
        int right = nums.length - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] == target) {
                return mid;
            } else if (nums[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return -1;
    }

    // 34. 左边界
    public int leftBound(int[] nums, int target) {
        int left = 0;
        int right = nums.length;
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] >= target) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }
        return left < nums.length && nums[left] == target ? left : -1;
    }

    // 34. 右边界
    public int rightBound(int[] nums, int target) {
        int left = 0;
        int right = nums.length;
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] <= target) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }
        int index = left - 1;
        return index >= 0 && nums[index] == target ? index : -1;
    }

    // 滑动窗口 Java 框架
    public void slidingWindowTemplate(String s, String t) {
        // need：题目要求窗口里必须包含哪些字符，以及每个字符需要几个。
        // 例如 t = "AABC"，那么 need['A'] = 2, need['B'] = 1, need['C'] = 1。
        Map<Character, Integer> need = new HashMap<>();

        // window：当前窗口 [left, right) 里面已经有哪些字符，以及每个字符有几个。
        // 它会随着 right 右移而增加，随着 left 右移而减少。
        Map<Character, Integer> window = new HashMap<>();

        // 先把 t 里面的字符需求统计出来。
        for (char c : t.toCharArray()) {
            need.put(c, need.getOrDefault(c, 0) + 1);
        }

        // left 和 right 共同表示一个窗口。
        // 这里使用左闭右开区间 [left, right)，意思是：
        // 包含 left 位置，不包含 right 位置。
        int left = 0;
        int right = 0;

        // valid：有多少种字符已经满足 need 的要求。
        // 例如 need 里有 A、B、C 三种字符，valid == 3 就表示这三种字符数量都够了。
        int valid = 0;

        // 下面这些变量不是每道题都需要，只是用来提醒“答案应该在哪里更新”。
        // 最小覆盖子串这类题，常用 bestStart / bestLen 记录最短窗口。
        int bestStart = 0;
        int bestLen = Integer.MAX_VALUE;

        // 最长无重复子串这类题，常用 maxLen 记录最长窗口长度。
        int maxLen = 0;

        while (right < s.length()) {
            // c 是将移入窗口的字符。
            char c = s.charAt(right);

            // right 先右移一格，把 c 纳入窗口。
            // 因为 right++ 后，窗口仍然按 [left, right) 理解。
            right++;

            // 如果 c 是题目关心的字符，就更新 window。
            // 如果 c 不在 need 里，说明这个字符对当前题目没用，可以忽略。
            if (need.containsKey(c)) {
                window.put(c, window.getOrDefault(c, 0) + 1);

                // 当 window 中 c 的数量刚好等于 need 中 c 的数量，
                // 说明 c 这个字符的需求满足了，valid 加一。
                if (window.get(c).equals(need.get(c))) {
                    valid++;
                }
            }

            // debug 输出的位置：窗口是左闭右开区间 [left, right)。
            System.out.println("window: [" + left + ", " + right + ")");

            // 更新答案位置 1：扩大窗口之后。
            // 有些题在这里更新，例如“固定长度窗口”或某些计数类题。
            // 但不是所有题都在这里更新，所以先记住：答案更新位置要看题目。

            // 判断左侧窗口是否要收缩。
            // 注意：这里的条件每道题不同。
            // 最小覆盖子串：满足条件后收缩，尝试找更短答案。
            // 无重复最长子串：重复了就收缩，直到不重复。
            // 找异位词：窗口长度够了就收缩，保持固定长度。
            while (windowNeedsShrink(need, window, valid)) {
                // 更新答案位置 2：窗口满足条件，并且即将收缩之前。
                // 最小覆盖子串 minWindow 就是在这里更新答案：
                // 因为此时 [left, right) 已经覆盖了 t，先记录当前长度，再尝试缩短。
                if (right - left < bestLen) {
                    bestStart = left;
                    bestLen = right - left;
                }

                // d 是将移出窗口的字符。
                char d = s.charAt(left);

                // left 右移一格，把 d 移出窗口。
                left++;

                // 如果 d 是题目关心的字符，移出去以后也要更新 window。
                if (need.containsKey(d)) {
                    // 如果移出 d 之前，d 的数量正好满足 need，
                    // 那移出去之后就不满足了，所以 valid 减一。
                    if (window.get(d).equals(need.get(d))) {
                        valid--;
                    }
                    window.put(d, window.get(d) - 1);
                }
            }

            // 更新答案位置 3：收缩完成之后。
            // 最长无重复子串这类题常在这里更新：
            // while 结束后，窗口重新变合法，此时可以拿 right - left 更新最长长度。
            maxLen = Math.max(maxLen, right - left);
        }

        // 如果这是 minWindow，最后会返回：
        // bestLen == Integer.MAX_VALUE ? "" : s.substring(bestStart, bestStart + bestLen)
        //
        // 如果这是最长无重复子串，最后会返回：
        // maxLen
    }

    // 真实做题时，这个判断条件要按题目改：
    // 最小覆盖子串：valid == need.size()
    // 最长无重复子串：window.get(c) > 1
    // 固定长度异位词：right - left >= t.length()
    private boolean windowNeedsShrink(Map<Character, Integer> need, Map<Character, Integer> window, int valid) {
        return valid == need.size();
    }

    // 76. Minimum Window Substring
    public String minWindow(String s, String t) {
        Map<Character, Integer> need = new HashMap<>();
        Map<Character, Integer> window = new HashMap<>();
        for (char c : t.toCharArray()) {
            need.put(c, need.getOrDefault(c, 0) + 1);
        }

        int left = 0;
        int valid = 0;
        int start = 0;
        int minLen = Integer.MAX_VALUE;
        for (int right = 0; right < s.length(); right++) {
            char in = s.charAt(right);
            if (need.containsKey(in)) {
                window.put(in, window.getOrDefault(in, 0) + 1);
                if (window.get(in).equals(need.get(in))) {
                    valid++;
                }
            }

            while (valid == need.size()) {
                if (right - left + 1 < minLen) {
                    start = left;
                    minLen = right - left + 1;
                }
                char out = s.charAt(left);
                left++;
                if (need.containsKey(out)) {
                    if (window.get(out).equals(need.get(out))) {
                        valid--;
                    }
                    window.put(out, window.get(out) - 1);
                }
            }
        }
        return minLen == Integer.MAX_VALUE ? "" : s.substring(start, start + minLen);
    }

    // 3. Longest Substring Without Repeating Characters
    public int lengthOfLongestSubstring(String s) {
        Map<Character, Integer> count = new HashMap<>();
        int left = 0;
        int ans = 0;
        for (int right = 0; right < s.length(); right++) {
            char in = s.charAt(right);
            count.put(in, count.getOrDefault(in, 0) + 1);
            while (count.get(in) > 1) {
                char out = s.charAt(left++);
                count.put(out, count.get(out) - 1);
            }
            ans = Math.max(ans, right - left + 1);
        }
        return ans;
    }

    // 141. Linked List Cycle
    public boolean hasCycle(ListNode head) {
        ListNode slow = head;
        ListNode fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
            if (slow == fast) {
                return true;
            }
        }
        return false;
    }

    // 167. Two Sum II
    public int[] twoSumSorted(int[] numbers, int target) {
        int left = 0;
        int right = numbers.length - 1;
        while (left < right) {
            int sum = numbers[left] + numbers[right];
            if (sum == target) {
                return new int[]{left + 1, right + 1};
            } else if (sum < target) {
                left++;
            } else {
                right--;
            }
        }
        return new int[]{-1, -1};
    }

    // 111. Minimum Depth of Binary Tree
    public int minDepth(TreeNode root) {
        if (root == null) {
            return 0;
        }
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        int depth = 1;
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                if (node.left == null && node.right == null) {
                    return depth;
                }
                if (node.left != null) {
                    queue.offer(node.left);
                }
                if (node.right != null) {
                    queue.offer(node.right);
                }
            }
            depth++;
        }
        return depth;
    }

    // 752. Open the Lock
    public int openLock(String[] deadends, String target) {
        Set<String> dead = new HashSet<>(Arrays.asList(deadends));
        if (dead.contains("0000")) {
            return -1;
        }

        Queue<String> queue = new ArrayDeque<>();
        Set<String> visited = new HashSet<>();
        queue.offer("0000");
        visited.add("0000");

        int steps = 0;
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                String cur = queue.poll();
                if (cur.equals(target)) {
                    return steps;
                }
                for (String next : lockNeighbors(cur)) {
                    if (!dead.contains(next) && visited.add(next)) {
                        queue.offer(next);
                    }
                }
            }
            steps++;
        }
        return -1;
    }

    private List<String> lockNeighbors(String cur) {
        List<String> ans = new ArrayList<>();
        char[] chars = cur.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char old = chars[i];
            chars[i] = old == '9' ? '0' : (char) (old + 1);
            ans.add(new String(chars));
            chars[i] = old == '0' ? '9' : (char) (old - 1);
            ans.add(new String(chars));
            chars[i] = old;
        }
        return ans;
    }

    public String wordFrequencyCommand() {
        return "cat words.txt | tr -s ' ' '\\n' | sort | uniq -c | sort -nr | awk '{print $2\" \"$1}'";
    }

    public String duplicateEmailsSql() {
        return "select Email from Person group by Email having count(*) > 1;";
    }

    // 10. Regular Expression Matching
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
                        dp[i][j] = dp[i][j] || dp[i - 1][j];
                    }
                } else if (pc == '.' || pc == s.charAt(i - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                }
            }
        }
        return dp[m][n];
    }
}
