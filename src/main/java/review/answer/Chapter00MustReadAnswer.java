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
