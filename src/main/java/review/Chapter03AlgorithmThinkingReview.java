package review;

import data.ListNode;
import data.TreeNode;

import java.util.List;

/**
 * 第三章、算法思维系列。
 *
 * 默写目标：
 * 1. 把问题先拆成决策树、状态图、区间关系或前缀关系。
 * 2. 练习回溯、滑动窗口、双指针、位运算、前缀和、Flood Fill、随机算法。
 * 3. 每题写代码前先说明剪枝条件或不变量。
 *
 * 对应题目：
 * 78, 90, 77, 46, 47, 39, 40, 37, 36, 22, 20, 32, 3, 76, 438,
 * 567, 424, 1, 167, 15, 18, 136, 137, 260, 191, 231, 338,
 * 224, 227, 150, 969, 303, 304, 560, 1248, 43, 415, 733, 200,
 * 695, 130, 56, 57, 435, 986, 354, 382, 398, 384, 528, 206,
 * 104, 226, 124.
 */
public class Chapter03AlgorithmThinkingReview {

    public static void main(String[] args) {
        System.out.println("第三章：在本文件中默写算法思维题。");
    }

    // 回溯：子集、组合、排列
    public List<List<Integer>> subsets(int[] nums) {
        throw todo("78 Subsets");
    }

    public List<List<Integer>> subsetsWithDup(int[] nums) {
        throw todo("90 Subsets II");
    }

    public List<List<Integer>> combine(int n, int k) {
        throw todo("77 Combinations");
    }

    public List<List<Integer>> permute(int[] nums) {
        throw todo("46 Permutations");
    }

    public List<List<Integer>> combinationSum(int[] candidates, int target) {
        throw todo("39 Combination Sum");
    }

    public void solveSudoku(char[][] board) {
        throw todo("37 Sudoku Solver");
    }

    public List<String> generateParenthesis(int n) {
        throw todo("22 Generate Parentheses");
    }

    // 滑动窗口
    public int lengthOfLongestSubstring(String s) {
        throw todo("3 Longest Substring Without Repeating Characters");
    }

    public String minWindow(String s, String t) {
        throw todo("76 Minimum Window Substring");
    }

    public List<Integer> findAnagrams(String s, String p) {
        throw todo("438 Find All Anagrams in a String");
    }

    public boolean checkInclusion(String s1, String s2) {
        throw todo("567 Permutation in String");
    }

    // Two Sum / N Sum
    public int[] twoSum(int[] nums, int target) {
        throw todo("1 Two Sum");
    }

    public List<List<Integer>> threeSum(int[] nums) {
        throw todo("15 3Sum");
    }

    public List<List<Integer>> fourSum(int[] nums, int target) {
        throw todo("18 4Sum");
    }

    // 位操作
    public int singleNumber(int[] nums) {
        throw todo("136 Single Number");
    }

    public int hammingWeight(int n) {
        throw todo("191 Number of 1 Bits");
    }

    public int[] countBits(int n) {
        throw todo("338 Counting Bits");
    }

    // 实现计算器
    public int calculate(String s) {
        throw todo("224 Basic Calculator");
    }

    public int evalRPN(String[] tokens) {
        throw todo("150 Evaluate Reverse Polish Notation");
    }

    // 烧饼排序
    public List<Integer> pancakeSort(int[] arr) {
        throw todo("969 Pancake Sorting");
    }

    // 前缀和
    static class NumArray {
        public NumArray(int[] nums) {
            throw todo("303 NumArray 初始化");
        }

        public int sumRange(int left, int right) {
            throw todo("303 sumRange");
        }
    }

    static class NumMatrix {
        public NumMatrix(int[][] matrix) {
            throw todo("304 NumMatrix 初始化");
        }

        public int sumRegion(int row1, int col1, int row2, int col2) {
            throw todo("304 sumRegion");
        }
    }

    public int subarraySum(int[] nums, int k) {
        throw todo("560 Subarray Sum Equals K");
    }

    // 字符串乘法
    public String multiply(String num1, String num2) {
        throw todo("43 Multiply Strings");
    }

    public String addStrings(String num1, String num2) {
        throw todo("415 Add Strings");
    }

    // Flood Fill / 岛屿
    public int[][] floodFill(int[][] image, int sr, int sc, int color) {
        throw todo("733 Flood Fill");
    }

    public int numIslands(char[][] grid) {
        throw todo("200 Number of Islands");
    }

    public int maxAreaOfIsland(int[][] grid) {
        throw todo("695 Max Area of Island");
    }

    public void solveSurroundedRegions(char[][] board) {
        throw todo("130 Surrounded Regions");
    }

    // 区间问题
    public int[][] mergeIntervals(int[][] intervals) {
        throw todo("56 Merge Intervals");
    }

    public int[][] insertInterval(int[][] intervals, int[] newInterval) {
        throw todo("57 Insert Interval");
    }

    public int[][] intervalIntersection(int[][] firstList, int[][] secondList) {
        throw todo("986 Interval List Intersections");
    }

    // 随机算法
    static class RandomizedArray {
        public RandomizedArray(int[] nums) {
            throw todo("384 Shuffle an Array 初始化");
        }

        public int[] reset() {
            throw todo("384 reset");
        }

        public int[] shuffle() {
            throw todo("384 shuffle");
        }
    }

    public int reservoirPick(ListNode head) {
        throw todo("382 Linked List Random Node");
    }

    // 递归
    public ListNode reverseList(ListNode head) {
        throw todo("206 Reverse Linked List");
    }

    public int maxDepth(TreeNode root) {
        throw todo("104 Maximum Depth of Binary Tree");
    }

    public TreeNode invertTree(TreeNode root) {
        throw todo("226 Invert Binary Tree");
    }

    public int maxPathSum(TreeNode root) {
        throw todo("124 Binary Tree Maximum Path Sum");
    }

    private static UnsupportedOperationException todo(String topic) {
        return new UnsupportedOperationException("TODO 默写: " + topic);
    }
}
