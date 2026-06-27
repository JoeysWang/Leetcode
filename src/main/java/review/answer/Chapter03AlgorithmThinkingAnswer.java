package review.answer;

import data.ListNode;
import data.TreeNode;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;

/**
 * 第三章答案：算法思维。
 *
 * 本章重点是先识别题型，再套模板。
 */
public class Chapter03AlgorithmThinkingAnswer {

    private int calcIndex;
    private int maxPathAns;

    public static void main(String[] args) {
        System.out.println("第三章答案：算法思维。");
    }

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

    public List<List<Integer>> subsetsWithDup(int[] nums) {
        Arrays.sort(nums);
        List<List<Integer>> ans = new ArrayList<>();
        backtrackSubsetsWithDup(nums, 0, new ArrayList<>(), ans);
        return ans;
    }

    private void backtrackSubsetsWithDup(int[] nums, int start, List<Integer> path, List<List<Integer>> ans) {
        ans.add(new ArrayList<>(path));
        for (int i = start; i < nums.length; i++) {
            if (i > start && nums[i] == nums[i - 1]) {
                continue;
            }
            path.add(nums[i]);
            backtrackSubsetsWithDup(nums, i + 1, path, ans);
            path.remove(path.size() - 1);
        }
    }

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
        for (int i = start; i <= n; i++) {
            path.add(i);
            backtrackCombine(i + 1, n, k, path, ans);
            path.remove(path.size() - 1);
        }
    }

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

    public List<List<Integer>> combinationSum(int[] candidates, int target) {
        List<List<Integer>> ans = new ArrayList<>();
        Arrays.sort(candidates);
        backtrackCombinationSum(candidates, target, 0, new ArrayList<>(), ans);
        return ans;
    }

    private void backtrackCombinationSum(int[] candidates, int remain, int start, List<Integer> path, List<List<Integer>> ans) {
        if (remain == 0) {
            ans.add(new ArrayList<>(path));
            return;
        }
        for (int i = start; i < candidates.length; i++) {
            if (candidates[i] > remain) {
                break;
            }
            path.add(candidates[i]);
            backtrackCombinationSum(candidates, remain - candidates[i], i, path, ans);
            path.remove(path.size() - 1);
        }
    }

    public void solveSudoku(char[][] board) {
        solveSudokuBacktrack(board, 0, 0);
    }

    private boolean solveSudokuBacktrack(char[][] board, int row, int col) {
        if (col == 9) {
            return solveSudokuBacktrack(board, row + 1, 0);
        }
        if (row == 9) {
            return true;
        }
        if (board[row][col] != '.') {
            return solveSudokuBacktrack(board, row, col + 1);
        }

        for (char ch = '1'; ch <= '9'; ch++) {
            if (!isValidSudokuChoice(board, row, col, ch)) {
                continue;
            }
            board[row][col] = ch;
            if (solveSudokuBacktrack(board, row, col + 1)) {
                return true;
            }
            board[row][col] = '.';
        }
        return false;
    }

    private boolean isValidSudokuChoice(char[][] board, int row, int col, char ch) {
        for (int i = 0; i < 9; i++) {
            if (board[row][i] == ch || board[i][col] == ch) {
                return false;
            }
            int boxRow = (row / 3) * 3 + i / 3;
            int boxCol = (col / 3) * 3 + i % 3;
            if (board[boxRow][boxCol] == ch) {
                return false;
            }
        }
        return true;
    }

    public List<String> generateParenthesis(int n) {
        List<String> ans = new ArrayList<>();
        backtrackParenthesis(n, n, new StringBuilder(), ans);
        return ans;
    }

    private void backtrackParenthesis(int left, int right, StringBuilder path, List<String> ans) {
        if (left == 0 && right == 0) {
            ans.add(path.toString());
            return;
        }
        if (left > 0) {
            path.append('(');
            backtrackParenthesis(left - 1, right, path, ans);
            path.deleteCharAt(path.length() - 1);
        }
        if (right > left) {
            path.append(')');
            backtrackParenthesis(left, right - 1, path, ans);
            path.deleteCharAt(path.length() - 1);
        }
    }

    public int lengthOfLongestSubstring(String s) {
        Map<Character, Integer> window = new HashMap<>();
        int left = 0;
        int ans = 0;
        for (int right = 0; right < s.length(); right++) {
            char in = s.charAt(right);
            window.put(in, window.getOrDefault(in, 0) + 1);
            while (window.get(in) > 1) {
                char out = s.charAt(left++);
                window.put(out, window.get(out) - 1);
            }
            ans = Math.max(ans, right - left + 1);
        }
        return ans;
    }

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
                char out = s.charAt(left++);
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

    public List<Integer> findAnagrams(String s, String p) {
        List<Integer> ans = new ArrayList<>();
        if (s.length() < p.length()) {
            return ans;
        }
        int[] need = new int[26];
        int[] window = new int[26];
        for (char c : p.toCharArray()) {
            need[c - 'a']++;
        }
        for (int right = 0; right < s.length(); right++) {
            window[s.charAt(right) - 'a']++;
            if (right >= p.length()) {
                window[s.charAt(right - p.length()) - 'a']--;
            }
            if (Arrays.equals(need, window)) {
                ans.add(right - p.length() + 1);
            }
        }
        return ans;
    }

    public boolean checkInclusion(String s1, String s2) {
        return !findAnagrams(s2, s1).isEmpty();
    }

    public int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            int need = target - nums[i];
            if (map.containsKey(need)) {
                return new int[]{map.get(need), i};
            }
            map.put(nums[i], i);
        }
        return new int[]{-1, -1};
    }

    public List<List<Integer>> threeSum(int[] nums) {
        Arrays.sort(nums);
        List<List<Integer>> ans = new ArrayList<>();
        for (int i = 0; i < nums.length; i++) {
            if (i > 0 && nums[i] == nums[i - 1]) {
                continue;
            }
            int left = i + 1;
            int right = nums.length - 1;
            while (left < right) {
                int sum = nums[i] + nums[left] + nums[right];
                if (sum == 0) {
                    ans.add(Arrays.asList(nums[i], nums[left], nums[right]));
                    left++;
                    right--;
                    while (left < right && nums[left] == nums[left - 1]) {
                        left++;
                    }
                    while (left < right && nums[right] == nums[right + 1]) {
                        right--;
                    }
                } else if (sum < 0) {
                    left++;
                } else {
                    right--;
                }
            }
        }
        return ans;
    }

    public List<List<Integer>> fourSum(int[] nums, int target) {
        Arrays.sort(nums);
        List<List<Integer>> ans = new ArrayList<>();
        int n = nums.length;
        for (int i = 0; i < n; i++) {
            if (i > 0 && nums[i] == nums[i - 1]) {
                continue;
            }
            for (int j = i + 1; j < n; j++) {
                if (j > i + 1 && nums[j] == nums[j - 1]) {
                    continue;
                }
                int left = j + 1;
                int right = n - 1;
                while (left < right) {
                    long sum = (long) nums[i] + nums[j] + nums[left] + nums[right];
                    if (sum == target) {
                        ans.add(Arrays.asList(nums[i], nums[j], nums[left], nums[right]));
                        left++;
                        right--;
                        while (left < right && nums[left] == nums[left - 1]) {
                            left++;
                        }
                        while (left < right && nums[right] == nums[right + 1]) {
                            right--;
                        }
                    } else if (sum < target) {
                        left++;
                    } else {
                        right--;
                    }
                }
            }
        }
        return ans;
    }

    public int singleNumber(int[] nums) {
        int ans = 0;
        for (int num : nums) {
            ans ^= num;
        }
        return ans;
    }

    public int hammingWeight(int n) {
        int count = 0;
        while (n != 0) {
            n &= n - 1;
            count++;
        }
        return count;
    }

    public int[] countBits(int n) {
        int[] ans = new int[n + 1];
        for (int i = 1; i <= n; i++) {
            ans[i] = ans[i >> 1] + (i & 1);
        }
        return ans;
    }

    public int calculate(String s) {
        calcIndex = 0;
        return parseExpression(s);
    }

    private int parseExpression(String s) {
        Stack<Integer> stack = new Stack<>();
        int number = 0;
        char sign = '+';
        while (calcIndex < s.length()) {
            char c = s.charAt(calcIndex++);
            if (Character.isDigit(c)) {
                number = number * 10 + c - '0';
            }
            if (c == '(') {
                number = parseExpression(s);
            }
            if ((!Character.isDigit(c) && c != ' ') || calcIndex == s.length()) {
                if (sign == '+') {
                    stack.push(number);
                } else if (sign == '-') {
                    stack.push(-number);
                }
                sign = c;
                number = 0;
            }
            if (c == ')') {
                break;
            }
        }
        int ans = 0;
        while (!stack.isEmpty()) {
            ans += stack.pop();
        }
        return ans;
    }

    public int evalRPN(String[] tokens) {
        Deque<Integer> stack = new ArrayDeque<>();
        for (String token : tokens) {
            if ("+".equals(token) || "-".equals(token) || "*".equals(token) || "/".equals(token)) {
                int b = stack.pop();
                int a = stack.pop();
                if ("+".equals(token)) {
                    stack.push(a + b);
                } else if ("-".equals(token)) {
                    stack.push(a - b);
                } else if ("*".equals(token)) {
                    stack.push(a * b);
                } else {
                    stack.push(a / b);
                }
            } else {
                stack.push(Integer.parseInt(token));
            }
        }
        return stack.pop();
    }

    public List<Integer> pancakeSort(int[] arr) {
        List<Integer> ans = new ArrayList<>();
        for (int size = arr.length; size > 1; size--) {
            int maxIndex = 0;
            for (int i = 1; i < size; i++) {
                if (arr[i] > arr[maxIndex]) {
                    maxIndex = i;
                }
            }
            if (maxIndex == size - 1) {
                continue;
            }
            reversePrefix(arr, maxIndex + 1);
            ans.add(maxIndex + 1);
            reversePrefix(arr, size);
            ans.add(size);
        }
        return ans;
    }

    private void reversePrefix(int[] arr, int k) {
        int left = 0;
        int right = k - 1;
        while (left < right) {
            int tmp = arr[left];
            arr[left++] = arr[right];
            arr[right--] = tmp;
        }
    }

    static class NumArray {
        private final int[] preSum;

        public NumArray(int[] nums) {
            preSum = new int[nums.length + 1];
            for (int i = 0; i < nums.length; i++) {
                preSum[i + 1] = preSum[i] + nums[i];
            }
        }

        public int sumRange(int left, int right) {
            return preSum[right + 1] - preSum[left];
        }
    }

    static class NumMatrix {
        private final int[][] preSum;

        public NumMatrix(int[][] matrix) {
            int m = matrix.length;
            int n = m == 0 ? 0 : matrix[0].length;
            preSum = new int[m + 1][n + 1];
            for (int i = 1; i <= m; i++) {
                for (int j = 1; j <= n; j++) {
                    preSum[i][j] = matrix[i - 1][j - 1] + preSum[i - 1][j] + preSum[i][j - 1] - preSum[i - 1][j - 1];
                }
            }
        }

        public int sumRegion(int row1, int col1, int row2, int col2) {
            return preSum[row2 + 1][col2 + 1] - preSum[row1][col2 + 1] - preSum[row2 + 1][col1] + preSum[row1][col1];
        }
    }

    public int subarraySum(int[] nums, int k) {
        Map<Integer, Integer> count = new HashMap<>();
        count.put(0, 1);
        int preSum = 0;
        int ans = 0;
        for (int num : nums) {
            preSum += num;
            ans += count.getOrDefault(preSum - k, 0);
            count.put(preSum, count.getOrDefault(preSum, 0) + 1);
        }
        return ans;
    }

    public String multiply(String num1, String num2) {
        if ("0".equals(num1) || "0".equals(num2)) {
            return "0";
        }
        int[] res = new int[num1.length() + num2.length()];
        for (int i = num1.length() - 1; i >= 0; i--) {
            for (int j = num2.length() - 1; j >= 0; j--) {
                int mul = (num1.charAt(i) - '0') * (num2.charAt(j) - '0');
                int p1 = i + j;
                int p2 = i + j + 1;
                int sum = mul + res[p2];
                res[p2] = sum % 10;
                res[p1] += sum / 10;
            }
        }
        StringBuilder sb = new StringBuilder();
        for (int digit : res) {
            if (sb.length() == 0 && digit == 0) {
                continue;
            }
            sb.append(digit);
        }
        return sb.toString();
    }

    public String addStrings(String num1, String num2) {
        StringBuilder sb = new StringBuilder();
        int i = num1.length() - 1;
        int j = num2.length() - 1;
        int carry = 0;
        while (i >= 0 || j >= 0 || carry != 0) {
            int a = i >= 0 ? num1.charAt(i--) - '0' : 0;
            int b = j >= 0 ? num2.charAt(j--) - '0' : 0;
            int sum = a + b + carry;
            sb.append(sum % 10);
            carry = sum / 10;
        }
        return sb.reverse().toString();
    }

    public int[][] floodFill(int[][] image, int sr, int sc, int color) {
        int old = image[sr][sc];
        if (old == color) {
            return image;
        }
        floodFillDfs(image, sr, sc, old, color);
        return image;
    }

    private void floodFillDfs(int[][] image, int row, int col, int old, int color) {
        if (row < 0 || row >= image.length || col < 0 || col >= image[0].length) {
            return;
        }
        if (image[row][col] != old) {
            return;
        }
        image[row][col] = color;
        floodFillDfs(image, row + 1, col, old, color);
        floodFillDfs(image, row - 1, col, old, color);
        floodFillDfs(image, row, col + 1, old, color);
        floodFillDfs(image, row, col - 1, old, color);
    }

    public int numIslands(char[][] grid) {
        int ans = 0;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == '1') {
                    ans++;
                    sinkIsland(grid, i, j);
                }
            }
        }
        return ans;
    }

    private void sinkIsland(char[][] grid, int row, int col) {
        if (row < 0 || row >= grid.length || col < 0 || col >= grid[0].length || grid[row][col] != '1') {
            return;
        }
        grid[row][col] = '0';
        sinkIsland(grid, row + 1, col);
        sinkIsland(grid, row - 1, col);
        sinkIsland(grid, row, col + 1);
        sinkIsland(grid, row, col - 1);
    }

    public int maxAreaOfIsland(int[][] grid) {
        int ans = 0;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == 1) {
                    ans = Math.max(ans, islandArea(grid, i, j));
                }
            }
        }
        return ans;
    }

    private int islandArea(int[][] grid, int row, int col) {
        if (row < 0 || row >= grid.length || col < 0 || col >= grid[0].length || grid[row][col] != 1) {
            return 0;
        }
        grid[row][col] = 0;
        return 1 + islandArea(grid, row + 1, col) + islandArea(grid, row - 1, col)
                + islandArea(grid, row, col + 1) + islandArea(grid, row, col - 1);
    }

    public void solveSurroundedRegions(char[][] board) {
        int m = board.length;
        int n = board[0].length;
        for (int i = 0; i < m; i++) {
            markSafe(board, i, 0);
            markSafe(board, i, n - 1);
        }
        for (int j = 0; j < n; j++) {
            markSafe(board, 0, j);
            markSafe(board, m - 1, j);
        }
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (board[i][j] == 'O') {
                    board[i][j] = 'X';
                } else if (board[i][j] == '#') {
                    board[i][j] = 'O';
                }
            }
        }
    }

    private void markSafe(char[][] board, int row, int col) {
        if (row < 0 || row >= board.length || col < 0 || col >= board[0].length || board[row][col] != 'O') {
            return;
        }
        board[row][col] = '#';
        markSafe(board, row + 1, col);
        markSafe(board, row - 1, col);
        markSafe(board, row, col + 1);
        markSafe(board, row, col - 1);
    }

    public int[][] mergeIntervals(int[][] intervals) {
        Arrays.sort(intervals, (a, b) -> a[0] - b[0]);
        List<int[]> ans = new ArrayList<>();
        for (int[] interval : intervals) {
            if (ans.isEmpty() || interval[0] > ans.get(ans.size() - 1)[1]) {
                ans.add(interval);
            } else {
                ans.get(ans.size() - 1)[1] = Math.max(ans.get(ans.size() - 1)[1], interval[1]);
            }
        }
        return ans.toArray(new int[0][]);
    }

    public int[][] insertInterval(int[][] intervals, int[] newInterval) {
        List<int[]> ans = new ArrayList<>();
        int i = 0;
        while (i < intervals.length && intervals[i][1] < newInterval[0]) {
            ans.add(intervals[i++]);
        }
        while (i < intervals.length && intervals[i][0] <= newInterval[1]) {
            newInterval[0] = Math.min(newInterval[0], intervals[i][0]);
            newInterval[1] = Math.max(newInterval[1], intervals[i][1]);
            i++;
        }
        ans.add(newInterval);
        while (i < intervals.length) {
            ans.add(intervals[i++]);
        }
        return ans.toArray(new int[0][]);
    }

    public int[][] intervalIntersection(int[][] firstList, int[][] secondList) {
        List<int[]> ans = new ArrayList<>();
        int i = 0;
        int j = 0;
        while (i < firstList.length && j < secondList.length) {
            int start = Math.max(firstList[i][0], secondList[j][0]);
            int end = Math.min(firstList[i][1], secondList[j][1]);
            if (start <= end) {
                ans.add(new int[]{start, end});
            }
            if (firstList[i][1] < secondList[j][1]) {
                i++;
            } else {
                j++;
            }
        }
        return ans.toArray(new int[0][]);
    }

    static class RandomizedArray {
        private final int[] original;
        private final Random random = new Random();

        public RandomizedArray(int[] nums) {
            original = nums.clone();
        }

        public int[] reset() {
            return original.clone();
        }

        public int[] shuffle() {
            int[] ans = original.clone();
            for (int i = 0; i < ans.length; i++) {
                int j = i + random.nextInt(ans.length - i);
                int tmp = ans[i];
                ans[i] = ans[j];
                ans[j] = tmp;
            }
            return ans;
        }
    }

    public int reservoirPick(ListNode head) {
        Random random = new Random();
        int ans = head.val;
        int count = 0;
        for (ListNode cur = head; cur != null; cur = cur.next) {
            count++;
            if (random.nextInt(count) == 0) {
                ans = cur.val;
            }
        }
        return ans;
    }

    public ListNode reverseList(ListNode head) {
        ListNode prev = null;
        ListNode cur = head;
        while (cur != null) {
            ListNode next = cur.next;
            cur.next = prev;
            prev = cur;
            cur = next;
        }
        return prev;
    }

    public int maxDepth(TreeNode root) {
        if (root == null) {
            return 0;
        }
        return Math.max(maxDepth(root.left), maxDepth(root.right)) + 1;
    }

    public TreeNode invertTree(TreeNode root) {
        if (root == null) {
            return null;
        }
        TreeNode left = invertTree(root.left);
        TreeNode right = invertTree(root.right);
        root.left = right;
        root.right = left;
        return root;
    }

    public int maxPathSum(TreeNode root) {
        maxPathAns = Integer.MIN_VALUE;
        maxGain(root);
        return maxPathAns;
    }

    private int maxGain(TreeNode root) {
        if (root == null) {
            return 0;
        }
        int left = Math.max(0, maxGain(root.left));
        int right = Math.max(0, maxGain(root.right));
        maxPathAns = Math.max(maxPathAns, left + right + root.val);
        return Math.max(left, right) + root.val;
    }
}
