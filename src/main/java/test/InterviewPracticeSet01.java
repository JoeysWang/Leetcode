package test;

import data.ListNode;
import data.TreeNode;

import java.util.*;

public class InterviewPracticeSet01 {

    public static void main(String[] args) {
        runSelfCheck();
        System.out.println("All checks passed. Now explain your ideas like an interview.");
    }

    /*
     * 1. 最长无重复子串
     *
     * 给定字符串 s，返回不含重复字符的最长子串长度。
     *
     * 示例：
     * s = "abcabcbb" -> 3
     * s = "bbbbb" -> 1
     * s = "" -> 0
     */
    public static int lengthOfLongestSubstring(String s) {
        // 用滑动窗口
        int left = 0;
        Map<Character, Integer> window = new HashMap<>();
        int right = 0;

        int longest = 0;

        String ans = "";
        int repeatCount = 0;
        for (int i = 0; i < s.length(); i++) {
            char added = s.charAt(i);
            right++;

            window.put(added, window.getOrDefault(added, 0) + 1);
            if (window.containsKey(added) && window.get(added) > 1) {
                repeatCount++;
            }
            if (repeatCount == 0) {
                longest = Math.max(longest, right - left);
            }
            while (repeatCount > 0) {

                char removed = s.charAt(left);
                if (window.get(removed) > 1) {
                    repeatCount--;
                }
                window.put(removed, window.get(removed) - 1);
                left++;

                if (repeatCount == 0) {
                    longest = Math.max(longest, right - left);
                }
            }
        }
        return longest;
    }

    /*
     * 2. 和为 K 的子数组
     *
     * 给定整数数组 nums 和整数 k，返回数组中和为 k 的连续子数组个数。
     *
     * 示例：
     * nums = [1, 1, 1], k = 2 -> 2
     * nums = [1, 2, 3], k = 3 -> 2
     */
    public static int subarraySum(int[] nums, int k) {
        // 里面有负数，所以滑动窗口不是单调递增的，要用前缀和

        Map<Integer, Integer> map = new HashMap<>();
        map.put(0, 1);

        int sum = 0;
        int ans = 0;


        for (int num : nums) {

            sum += sum + num;

            int need = sum - k;

            if (map.containsKey(need)) {
                ans++;
            }

            map.put(sum, map.getOrDefault(sum, 0) + 1);
        }
        return ans;
    }

    /*
     * 3. 搜索旋转排序数组
     *
     * 给定升序数组在某个点旋转后的数组 nums，以及目标值 target。
     * 返回 target 的下标；如果不存在，返回 -1。要求时间复杂度 O(log n)。
     *
     * 示例：
     * nums = [4,5,6,7,0,1,2], target = 0 -> 4
     * nums = [4,5,6,7,0,1,2], target = 3 -> -1
     */
    public static int searchRotatedArray(int[] nums, int target) {
        // TODO: 写你的解法
        throw new UnsupportedOperationException("TODO");
    }

    /*
     * 4. 岛屿数量
     *
     * 给定由 '1' 和 '0' 组成的二维网格，'1' 表示陆地，'0' 表示水。
     * 上下左右相连算同一个岛，返回岛屿数量。
     *
     * 示例：
     * [
     *   ['1','1','0'],
     *   ['1','0','0'],
     *   ['0','0','1']
     * ] -> 2
     */
    public static int numIslands(char[][] grid) {
        if (grid.length == 0 || grid[0].length == 0) {
            return 0;
        }

        int ans = 0;


        for (int i = 0; i < grid.length; i++) {

            for (int j = 0; j < grid[i].length; j++) {

                if (grid[i][j] == '1') {
                    ans++;
                    floodfill(grid, i, j);
                }

            }

        }
        return ans;
    }

    private static void floodfill(char[][] grid, int x, int y) {
        if (x < 0 || y < 0 || x > grid.length - 1 || y > grid[0].length - 1) {
            return;
        }

        if (grid[x][y] != '1') {
            return;
        }

        grid[x][y] = '0';

        floodfill(grid, x - 1, y);
        floodfill(grid, x, y + 1);
        floodfill(grid, x + 1, y);
        floodfill(grid, x, y - 1);
    }

    /*
     * 5. 每日温度
     *
     * 给定数组 temperatures，返回一个数组，表示对于每一天，
     * 需要等几天才会出现更高温度；如果之后没有更高温度，则为 0。
     *
     * 示例：
     * [73,74,75,71,69,72,76,73] -> [1,1,4,2,1,1,0,0]
     */
    public static int[] dailyTemperatures(int[] temperatures) {
        // TODO: 写你的解法
        throw new UnsupportedOperationException("TODO");
    }

    /*
     * 6. 反转链表
     *
     * 给定单链表的头节点 head，反转链表并返回新的头节点。
     *
     * 示例：
     * 1 -> 2 -> 3 -> 4 -> 5 -> null
     * 返回：
     * 5 -> 4 -> 3 -> 2 -> 1 -> null
     */
    public static ListNode reverseList(ListNode head) {
        if (head == null) {
            return null;
        }

        ListNode prev = null;
        ListNode current = head;

        while (current != null) {
            ListNode temp = current.next;
            current.next = prev;
            prev = current;
            current = temp;
        }
        return prev;
    }

    /*
     * 7. 二叉树层序遍历
     *
     * 给定二叉树 root，按从上到下、从左到右的层序返回节点值。
     *
     * 示例：
     *     3
     *    / \
     *   9  20
     *      / \
     *     15  7
     * 返回：
     * [[3], [9,20], [15,7]]
     */
    public static List<List<Integer>> levelOrder(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();


        Queue<TreeNode> nodes = new ArrayDeque<>();
        nodes.offer(root);


        while (!nodes.isEmpty()) {

            int size = nodes.size();

            ArrayList<Integer> temple = new ArrayList<>();
            for (int i = 0; i < size; i++) {


                TreeNode node = nodes.poll();
                if (node != null) {
                    temple.add(node.val);
                    nodes.offer(node.left);
                    nodes.offer(node.right);

                }
            }
            result.add(temple);
        }
        return result;
    }

    /*
     * 8. 前 K 个高频元素
     *
     * 给定整数数组 nums 和整数 k，返回出现频率最高的 k 个元素。
     * 答案顺序不限。
     *
     * 示例：
     * nums = [1,1,1,2,2,3], k = 2 -> [1,2]
     */
    public static int[] topKFrequent(int[] nums, int k) {
        Map<Integer, Integer> result = new HashMap<>();

        for (int i = 0; i < nums.length; i++) {
            int num = nums[i];
            result.put(num, result.getOrDefault(num, 0) + 1);
        }
        PriorityQueue<Integer> queue = new PriorityQueue<>(k, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return result.get(o1) - result.get(o2);
            }
        });

        for (Integer num : result.keySet()) {
            queue.offer(num);
            if (queue.size() > k) {
                queue.poll();
            }

        }
        int[] res = new int[k];
        for (int i = 0; i < k; i++) {
            res[i] = queue.poll();
        }
        return res;
    }

    /*
     * 9. 括号生成
     *
     * 给定 n，生成所有由 n 对括号组成的合法括号组合。
     * 答案顺序不限。
     *
     * 示例：
     * n = 3 -> ["((()))", "(()())", "(())()", "()(())", "()()()"]
     */
    public static List<String> generateParenthesis(int n) {
        List<String> res = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        backtrace(res, sb, 0, 0, n);
        return res;
    }


    private static void backtrace(List<String> res, StringBuilder sb, int leftused, int rightused, int n) {

        if (sb.length() == 2 * n) {
            res.add(sb.toString());
            return;
        }

        if (leftused < n) {

            sb.append("(");

            backtrace(res, sb, leftused + 1, rightused, n);

            sb.deleteCharAt(sb.length() - 1);
        }


        if (rightused < leftused) {
            sb.append(")");
            backtrace(res, sb, leftused, rightused + 1, n);
            sb.deleteCharAt(sb.length() - 1);
        }
    }

    /*
     * 10. 螺旋矩阵
     *
     * 给定 m x n 矩阵 matrix，按照顺时针螺旋顺序返回所有元素。
     *
     * 示例：
     * [
     *   [1,2,3],
     *   [4,5,6],
     *   [7,8,9]
     * ] -> [1,2,3,6,9,8,7,4,5]
     */
    public static List<Integer> spiralOrder(int[][] matrix) {
        // TODO: 写你的解法
        throw new UnsupportedOperationException("TODO");
    }

    /*
     * 11. 冒泡排序
     *
     * 给定整数数组 nums，请原地升序排序。
     *
     * 示例：
     * nums = [5,1,1,2,0,-3]
     * 排序后：
     * nums = [-3,0,1,1,2,5]
     */
    public static void bubbleSort(int[] nums) {
        for (int i = 0; i < nums.length - 1; i++) {

            boolean swaped = false;
            for (int j = 0; j < nums.length - i - 1; j++) {

                if (nums[j] > nums[j + 1]) {

                    swap(nums, j, j + 1);
                    swaped = true;
                }

            }
            if (!swaped) {
                return;
            }
        }

    }

    private static void swap(int[] nums, int i, int j) {
        int temp = nums[i];
        nums[i] = nums[j];
        nums[j] = temp;
    }

    /*
     * 12. 快速排序
     *
     * 给定整数数组 nums，请原地升序排序。
     * 你可以自己补 private 的 partition、swap 等辅助方法。
     *
     * 示例：
     * nums = [5,1,1,2,0,-3]
     * 排序后：
     * nums = [-3,0,1,1,2,5]
     */
    public static void quickSort(int[] nums) {

        quickSortInter(nums, 0, nums.length - 1);
    }

    public static void quickSortInter(int[] nums, int start, int end) {
        if (start >= end) {
            return;
        }
        int p = pviot(nums, start, end);

        quickSortInter(nums, start, p - 1);
        quickSortInter(nums, p + 1, end);
    }

    private static int pviot(int[] nums, int left, int right) {
        int pivot = left;
        int lessEnd = left;
        int index = left;

        while (index <= right) {
            if (nums[pivot] > nums[index]) {
                lessEnd++;
                swap(nums, lessEnd, index);
            }
            index++;
        }

        swap(nums, pivot, lessEnd);
        return lessEnd;
    }

    /*
     * 13. N 皇后
     *
     * 给定整数 n，返回所有不同的 n 皇后问题解决方案。
     * 每一种解法包含一个不同的 n 皇后棋盘摆放方案，'Q' 表示皇后，'.' 表示空位。
     *
     * 示例：
     * n = 4 -> [
     *   [".Q..", "...Q", "Q...", "..Q."],
     *   ["..Q.", "Q...", "...Q", ".Q.."]
     * ]
     */
    public static List<List<String>> solveNQueens(int n) {
        // TODO: 写你的解法
        throw new UnsupportedOperationException("TODO");
    }


    private static void runSelfCheck() {
        expectEquals(3, lengthOfLongestSubstring("abcabcbb"), "lengthOfLongestSubstring example 1");
        expectEquals(1, lengthOfLongestSubstring("bbbbb"), "lengthOfLongestSubstring repeated chars");
        expectEquals(3, lengthOfLongestSubstring("pwwkew"), "lengthOfLongestSubstring example 2");
        expectEquals(0, lengthOfLongestSubstring(""), "lengthOfLongestSubstring empty string");

        expectEquals(2, subarraySum(new int[]{1, 1, 1}, 2), "subarraySum example 1");
        expectEquals(2, subarraySum(new int[]{1, 2, 3}, 3), "subarraySum example 2");
        expectEquals(4, subarraySum(new int[]{1, -1, 0, 2, -2}, 0), "subarraySum with negative numbers");

        expectEquals(4, searchRotatedArray(new int[]{4, 5, 6, 7, 0, 1, 2}, 0), "searchRotatedArray found");
        expectEquals(-1, searchRotatedArray(new int[]{4, 5, 6, 7, 0, 1, 2}, 3), "searchRotatedArray not found");
        expectEquals(0, searchRotatedArray(new int[]{1}, 1), "searchRotatedArray single item");

        expectEquals(2, numIslands(new char[][]{
                {'1', '1', '0'},
                {'1', '0', '0'},
                {'0', '0', '1'}
        }), "numIslands example");
        expectEquals(1, numIslands(new char[][]{
                {'1', '1', '1'},
                {'0', '1', '0'},
                {'1', '1', '1'}
        }), "numIslands connected around center");
        expectEquals(0, numIslands(new char[][]{
                {'0', '0'},
                {'0', '0'}
        }), "numIslands all water");

        expectArrayEquals(new int[]{1, 1, 4, 2, 1, 1, 0, 0},
                dailyTemperatures(new int[]{73, 74, 75, 71, 69, 72, 76, 73}),
                "dailyTemperatures example");
        expectArrayEquals(new int[]{1, 1, 0},
                dailyTemperatures(new int[]{30, 40, 50}),
                "dailyTemperatures increasing");
        expectArrayEquals(new int[]{0, 0, 0},
                dailyTemperatures(new int[]{50, 40, 30}),
                "dailyTemperatures decreasing");

        expectListValues(new int[]{5, 4, 3, 2, 1},
                reverseList(buildList(1, 2, 3, 4, 5)),
                "reverseList example");
        expectListValues(new int[]{1},
                reverseList(buildList(1)),
                "reverseList single item");
        expectListValues(new int[]{},
                reverseList(null),
                "reverseList empty list");

        TreeNode levelOrderRoot = new TreeNode(3);
        levelOrderRoot.left = new TreeNode(9);
        levelOrderRoot.right = new TreeNode(20);
        levelOrderRoot.right.left = new TreeNode(15);
        levelOrderRoot.right.right = new TreeNode(7);
        expectListEquals(Arrays.asList(
                        Arrays.asList(3),
                        Arrays.asList(9, 20),
                        Arrays.asList(15, 7)),
                levelOrder(levelOrderRoot),
                "levelOrder example");
        expectListEquals(Collections.emptyList(),
                levelOrder(null),
                "levelOrder empty tree");

        expectArrayEqualsIgnoreOrder(new int[]{1, 2},
                topKFrequent(new int[]{1, 1, 1, 2, 2, 3}, 2),
                "topKFrequent example");
        expectArrayEqualsIgnoreOrder(new int[]{-1, 2},
                topKFrequent(new int[]{4, 1, -1, 2, -1, 2, 3}, 2),
                "topKFrequent with negative numbers");
        expectArrayEqualsIgnoreOrder(new int[]{1},
                topKFrequent(new int[]{1}, 1),
                "topKFrequent single item");

        expectListEqualsIgnoreOrder(Arrays.asList("((()))", "(()())", "(())()", "()(())", "()()()"),
                generateParenthesis(3),
                "generateParenthesis n = 3");
        expectListEqualsIgnoreOrder(Collections.singletonList("()"),
                generateParenthesis(1),
                "generateParenthesis n = 1");

        expectListEquals(Arrays.asList(1, 2, 3, 6, 9, 8, 7, 4, 5),
                spiralOrder(new int[][]{
                        {1, 2, 3},
                        {4, 5, 6},
                        {7, 8, 9}
                }),
                "spiralOrder square matrix");
        expectListEquals(Arrays.asList(1, 2, 3, 4, 8, 12, 11, 10, 9, 5, 6, 7),
                spiralOrder(new int[][]{
                        {1, 2, 3, 4},
                        {5, 6, 7, 8},
                        {9, 10, 11, 12}
                }),
                "spiralOrder rectangle matrix");
        expectListEquals(Arrays.asList(1, 2, 3, 4),
                spiralOrder(new int[][]{{1, 2, 3, 4}}),
                "spiralOrder single row");

        int[] bubbleExample = {5, 1, 1, 2, 0, -3};
        bubbleSort(bubbleExample);
        expectArrayEquals(new int[]{-3, 0, 1, 1, 2, 5}, bubbleExample, "bubbleSort example");
        int[] bubbleAlreadySorted = {-2, 0, 3, 9};
        bubbleSort(bubbleAlreadySorted);
        expectArrayEquals(new int[]{-2, 0, 3, 9}, bubbleAlreadySorted, "bubbleSort already sorted");
        int[] bubbleEmpty = {};
        bubbleSort(bubbleEmpty);
        expectArrayEquals(new int[]{}, bubbleEmpty, "bubbleSort empty array");

        int[] quickExample = {5, 1, 1, 2, 0, -3};
        quickSort(quickExample);
        expectArrayEquals(new int[]{-3, 0, 1, 1, 2, 5}, quickExample, "quickSort example");
        int[] quickReverse = {9, 7, 5, 3, 1};
        quickSort(quickReverse);
        expectArrayEquals(new int[]{1, 3, 5, 7, 9}, quickReverse, "quickSort reverse sorted");
        int[] quickSingle = {42};
        quickSort(quickSingle);
        expectArrayEquals(new int[]{42}, quickSingle, "quickSort single item");

        expectListEqualsIgnoreOrder(Arrays.asList(
                        Arrays.asList(".Q..", "...Q", "Q...", "..Q."),
                        Arrays.asList("..Q.", "Q...", "...Q", ".Q..")),
                solveNQueens(4),
                "solveNQueens n = 4");
        expectListEqualsIgnoreOrder(Collections.singletonList(Collections.singletonList("Q")),
                solveNQueens(1),
                "solveNQueens n = 1");
    }

    private static void expectEquals(int expected, int actual, String name) {
        if (expected != actual) {
            throw new AssertionError(name + " expected " + expected + ", but got " + actual);
        }
    }

    private static void expectArrayEquals(int[] expected, int[] actual, String name) {
        if (!Arrays.equals(expected, actual)) {
            throw new AssertionError(name + " expected " + Arrays.toString(expected)
                    + ", but got " + Arrays.toString(actual));
        }
    }

    private static void expectArrayEqualsIgnoreOrder(int[] expected, int[] actual, String name) {
        int[] expectedCopy = Arrays.copyOf(expected, expected.length);
        int[] actualCopy = Arrays.copyOf(actual, actual.length);
        Arrays.sort(expectedCopy);
        Arrays.sort(actualCopy);
        expectArrayEquals(expectedCopy, actualCopy, name);
    }

    private static <T> void expectListEquals(List<T> expected, List<T> actual, String name) {
        if (!expected.equals(actual)) {
            throw new AssertionError(name + " expected " + expected + ", but got " + actual);
        }
    }

    private static <T> void expectListEqualsIgnoreOrder(List<T> expected, List<T> actual, String name) {
        if (!new HashSet<>(expected).equals(new HashSet<>(actual)) || expected.size() != actual.size()) {
            throw new AssertionError(name + " expected " + expected + ", but got " + actual);
        }
    }

    private static ListNode buildList(int... values) {
        ListNode dummy = new ListNode(0);
        ListNode current = dummy;
        for (int value : values) {
            current.next = new ListNode(value);
            current = current.next;
        }
        return dummy.next;
    }

    private static void expectListValues(int[] expected, ListNode actual, String name) {
        int index = 0;
        ListNode current = actual;
        while (index < expected.length && current != null) {
            if (expected[index] != current.val) {
                throw new AssertionError(name + " expected " + Arrays.toString(expected)
                        + ", but node " + index + " was " + current.val);
            }
            index++;
            current = current.next;
        }
        if (index != expected.length || current != null) {
            throw new AssertionError(name + " expected " + Arrays.toString(expected)
                    + ", but list length did not match");
        }
    }
}
