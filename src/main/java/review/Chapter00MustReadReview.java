package review;

import data.ListNode;
import data.TreeNode;

import java.util.List;

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
        for (ListNode p = head; p != null; p = head.next) {
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
        for (int i = 0; i < graph.length; i++) {
            int[] now = graph[i];
            for (int j = 0; j < now.length; j++) {
                System.out.println("int[" + i + "][" + j + "]=" + now[j]);
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
        throw todo("704 Binary Search");
    }

    public int leftBound(int[] nums, int target) {
        throw todo("34 左边界");
    }

    public int rightBound(int[] nums, int target) {
        throw todo("34 右边界");
    }

    // 滑动窗口解题套路框架：扩大窗口、收缩窗口、更新答案
    public String minWindow(String s, String t) {
        throw todo("76 Minimum Window Substring");
    }

    public int lengthOfLongestSubstring(String s) {
        throw todo("3 Longest Substring Without Repeating Characters");
    }

    // 双指针技巧总结：快慢指针、左右指针、同向指针
    public boolean hasCycle(ListNode head) {
        throw todo("141 Linked List Cycle");
    }

    public int[] twoSumSorted(int[] numbers, int target) {
        throw todo("167 Two Sum II");
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
