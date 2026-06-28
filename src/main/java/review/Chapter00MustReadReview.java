package review;

import data.ListNode;
import data.Node;
import data.TreeNode;

import java.util.*;

/**
 * 第零章、必读系列。
 * <p>
 * 默写目标：
 * 1. 数据结构遍历框架：数组、链表、二叉树、N 叉树、图。
 * 2. 算法模板：动态规划、回溯、二分查找、滑动窗口、双指针、BFS。
 * 3. 基础扩展：Shell、SQL、正则表达式。
 * <p>
 * 对应题目：
 * 104, 226, 543, 124, 20, 206, 146, 200, 70, 322, 509, 300,
 * 198, 213, 72, 1143, 46, 77, 78, 39, 704, 34, 35, 875,
 * 76, 3, 438, 567, 141, 142, 167, 15, 111, 102, 752, 773,
 * 175, 182, 196, 10, 44.
 */
public class Chapter00MustReadReview {

    public static void main(String[] args) {
        System.out.println("第零章：在本文件中默写基础算法模板。");
    }

    // 学习算法和刷题的思路指南：遍历框架
    public void traverseArray(int[] nums) {
        for (int i = 0; i < nums.length; i++) {
            // 处理 nums[i]
            System.out.println("处理 nums[" + i + "] = " + nums[i]);
        }
    }

    public void traverseList(ListNode head) {
        for (ListNode p = head; p != null; p = p.next) {
            System.out.println(p);
        }
    }

    public void traverseTree(TreeNode root) {
        if (root == null) {
            return;
        }
        System.out.println(root.val);
        traverseTree(root.left);
        traverseTree(root.right);
    }

    public void traverseGraph(int[][] graph) {
        boolean[] visited = new boolean[graph.length];
        for (int i = 0; i < graph.length; i++) {
            dfsGraph(graph, visited, i);
        }
    }

    private void dfsGraph(int[][] graph, boolean[] visited, int node) {
        if (visited[node]) {
            return;
        }
        visited[node] = true;
        System.out.println("访问节点: " + node);
        for (int next : graph[node]) {
            dfsGraph(graph, visited, next);
        }
    }

    // DFS/BFS 代码框架默写题：
    // graph[node] 表示 node 能直接去到的所有邻居。
    // 目标：从 start 出发，按 DFS 深度优先遍历所有能到达的点。
    public void dfsTemplate(Node node) {
        if (node.childern != null) {
            for (Node child : node.childern) {
                dfsTemplate(child);
            }
        }
    }

    // DFS/BFS 代码框架默写题：
    // 目标：从 start 出发，按 BFS 广度优先一层一层遍历所有能到达的点。
    public void bfsTemplate(Node node, int start) {
        if (node == null) {
            return;
        }
        Map<Node, Boolean> visitted = new HashMap<>();
        Queue<Node> queue = new ArrayDeque<>();
        queue.offer(node);
        visitted.put(node, true);
        while (!queue.isEmpty()) {
            int size = queue.size();

            for (int i = 0; i < size; i++) {
                Node now = queue.poll();
                System.out.println("访问节点: " + now);
                for (Node child : node.childern) {
                    if (visitted.containsKey(node)) {
                        continue;
                    }
                    queue.offer(child);
                    visitted.put(node, true);

                }
            }
        }
    }

    // 动态规划解题套路框架：状态、选择、base case、状态转移
    public int coinChange(int[] coins, int amount) {
        throw todo("322 Coin Change");
    }

    public int climbStairs(int n) {
        throw todo("70 Climbing Stairs");
    }

    // 回溯算法解题套路框架：路径、选择列表、结束条件
    public List<List<Integer>> permute(int[] nums) {
        throw todo("46 Permutations");
    }

    public List<List<Integer>> combine(int n, int k) {
        throw todo("77 Combinations");
    }

    public List<List<Integer>> subsets(int[] nums) {
        throw todo("78 Subsets");
    }

    // 二分查找解题套路框架：普通查找、左边界、右边界
    public int binarySearch(int[] nums, int target) {
        if (nums.length < 1)
            return -1;
        int left = 0;
        int right = nums.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] == target) {
                return mid;
            } else if (nums[mid] < target) {
                left = mid + 1;
            } else if (nums[mid] > target) {
                right = mid - 1;
            }
        }
        return -1;
    }

    public int leftBound(int[] nums, int target) {
        if (nums.length < 1) {
            return -1;
        }

        int left = 0;
        int right = nums.length - 1;
        while (left <= right) {

            int mid = left + (right - left) / 2;
            if (nums[mid] == target) {
                right = mid - 1;
            } else if (nums[mid] < target) {
                left = right + 1;
            } else if (nums[mid] > target) {
                right = left - 1;
            }
        }
        if (left < 0 || nums[left] != target)
            return -1;
        return left;
    }

    public int rightBound(int[] nums, int target) {
        if (nums.length < 1) {
            return -1;
        }
        int left = 0;
        int right = nums.length - 1;
        while (left <= right) {

            int mid = left + (right - left) / 2;
            if (nums[mid] == target) {
                left = mid + 1;
            } else if (nums[mid] < target) {
                left = right + 1;
            } else if (nums[mid] > target) {
                right = left - 1;
            }
        }
        if (right == nums.length || nums[right] != target)
            return -1;
        return right;
    }

    // 滑动窗口算法框架默写题：
    // 把 labuladong 的 C++ 框架翻译成 Java，重点默写：
    // 1. need/window 两个计数器
    // 2. right 右移扩大窗口
    // 3. while 判断是否收缩窗口
    // 4. left 左移缩小窗口
    // 5. 在正确位置更新答案：最长类多在收缩后，最短类多在收缩前
    public void slidingWindowTemplate(String s, String t) {
        throw todo("滑动窗口 Java 框架：HashMap + left/right + valid");
    }

    // 滑动窗口解题套路框架：扩大窗口、收缩窗口、更新答案
    public String minWindow(String s, String t) {
        // 1, 先建立关键变量，需要的字符的数量、窗口里已经有的字符的数量、左指针、右指针；答案起始点、答案长度；
        // 2, 统计好滑动窗口需要哪些东西，这样在收缩或者扩大时才知道是否需要更新答案。


        HashMap<Character, Integer> need = new HashMap<>();
        HashMap<Character, Integer> window = new HashMap<>();
        int bestStart = 0;
        int bestLen = Integer.MAX_VALUE;
        int valid = 0;

        for (int i = 0; i < t.length(); i++) {
            need.put(t.charAt(i), need.getOrDefault(t.charAt(i), 0) + 1);
        }


        int left = 0;
        int right = 0;

        while (right < s.length()) {
            Character nowChar = s.charAt(right);
            right++;

            window.put(nowChar, window.getOrDefault(nowChar, 0) + 1);

            if (need.containsKey(nowChar)) {
                if (window.get(nowChar).equals(need.get(nowChar))) {
                    valid++;
                }
            }

            while (valid == need.size()) {

                int currentLen = right - left;
                if (currentLen < bestLen) {
                    bestLen = currentLen;
                    bestStart = left;
                }

                Character removeChar = s.charAt(left);
                left++;


                if (need.containsKey(removeChar) && need.get(removeChar).equals(window.get(removeChar))) {

                    valid--;
                }
                window.put(removeChar, window.get(removeChar) - 1);

            }

        }

        if (bestLen < Integer.MAX_VALUE) {
            return s.substring(bestStart, bestStart + bestLen);
        }
        return "";
    }
    public int lengthOfLongestSubstring(String s) {
        throw todo("3 Longest Substring Without Repeating Characters");
    }

    // 双指针技巧总结：快慢指针、左右指针、同向指针
    public boolean hasCycle(ListNode head) {
        throw todo("141 Linked List Cycle");
    }

    public int[] twoSumSorted(int[] numbers, int target) {
        int[] res = new int[]{-1, -1};
        if (numbers.length < 2)
            return res;
        int left = 0;
        int right = numbers.length - 1;


        while (left < right) {
            int sum = numbers[left] + numbers[right];
            if (sum == target) {
                res[0] = left;
                res[1] = right;
                return res;
            } else if (sum < target) {
                left++;
            } else if (sum > target) {
                right--;
            }
        }
        return res;
    }

    public int[] threeSumSorted(int[] numbers, int target) {
        int[] res = new int[]{-1, -1, -1};

        if (numbers.length < 3)
            return res;

        for (int i = 0; i < numbers.length - 2; i++) {

            int left = i + 1;
            int right = numbers.length - 1;
            while (left < right) {
                int sum = numbers[i] + numbers[left] + numbers[right];
                if (sum == target) {
                    return new int[]{i, left, right};
                } else if (sum < target) {
                    left++;
                } else if (sum > target) {
                    right--;
                }
            }
        }
        return new int[]{-1, -1, -1};
    }


    // BFS 算法套路框架：队列、层数、visited
    public int minDepth(TreeNode root) {
        throw todo("111 Minimum Depth of Binary Tree");
    }

    public int openLock(String[] deadends, String target) {
        throw todo("752 Open the Lock");
    }

    // Linux / SQL / 正则：写出命令或 SQL 字符串
    public String wordFrequencyCommand() {
        throw todo("192 Word Frequency shell command");
    }

    public String duplicateEmailsSql() {
        throw todo("182 Duplicate Emails SQL");
    }

    public boolean regexMatch(String s, String p) {
        throw todo("10 Regular Expression Matching");
    }

    private static UnsupportedOperationException todo(String topic) {
        return new UnsupportedOperationException("TODO 默写: " + topic);
    }
}
