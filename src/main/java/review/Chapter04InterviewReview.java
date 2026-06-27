package review;

import data.ListNode;

import java.util.List;

/**
 * 第四章、高频面试系列。
 *
 * 默写目标：
 * 1. 高频题先识别套路，再写代码。
 * 2. 重点练边界：二分区间、链表奇偶长度、数组原地修改、括号计数。
 * 3. 并查集题先建模，再实现 find/union。
 *
 * 对应题目：
 * 204, 50, 372, 704, 34, 875, 1011, 410, 42, 11, 26, 80, 27,
 * 283, 5, 647, 516, 55, 45, 25, 92, 206, 20, 921, 1541, 32,
 * 268, 448, 41, 645, 287, 234, 382, 398, 855, 547, 684, 990,
 * 200, 130, 721, 1584, 292, 319, 877, 392, 792.
 */
public class Chapter04InterviewReview {

    public static void main(String[] args) {
        System.out.println("第四章：在本文件中默写高频面试题。");
    }

    // 数学基础
    public int countPrimes(int n) {
        throw todo("204 Count Primes");
    }

    public double myPow(double x, int n) {
        throw todo("50 Pow(x, n)");
    }

    public int superPow(int a, int[] b) {
        throw todo("372 Super Pow");
    }

    // 二分查找和搜索答案
    public int binarySearch(int[] nums, int target) {
        throw todo("704 Binary Search");
    }

    public int[] searchRange(int[] nums, int target) {
        throw todo("34 Find First and Last Position");
    }

    public int minEatingSpeed(int[] piles, int h) {
        throw todo("875 Koko Eating Bananas");
    }

    public int shipWithinDays(int[] weights, int days) {
        throw todo("1011 Capacity To Ship Packages");
    }

    public int splitArray(int[] nums, int k) {
        throw todo("410 Split Array Largest Sum");
    }

    // 双指针 / 单调栈
    public int trap(int[] height) {
        throw todo("42 Trapping Rain Water");
    }

    public int maxArea(int[] height) {
        throw todo("11 Container With Most Water");
    }

    // 数组原地修改
    public int removeDuplicates(int[] nums) {
        throw todo("26 Remove Duplicates from Sorted Array");
    }

    public int removeDuplicatesKeepTwo(int[] nums) {
        throw todo("80 Remove Duplicates from Sorted Array II");
    }

    public int removeElement(int[] nums, int val) {
        throw todo("27 Remove Element");
    }

    public void moveZeroes(int[] nums) {
        throw todo("283 Move Zeroes");
    }

    // 回文
    public String longestPalindrome(String s) {
        throw todo("5 Longest Palindromic Substring");
    }

    public int countSubstrings(String s) {
        throw todo("647 Palindromic Substrings");
    }

    public boolean isPalindromeList(ListNode head) {
        throw todo("234 Palindrome Linked List");
    }

    // 贪心
    public boolean canJump(int[] nums) {
        throw todo("55 Jump Game");
    }

    public int jump(int[] nums) {
        throw todo("45 Jump Game II");
    }

    // 链表
    public ListNode reverseKGroup(ListNode head, int k) {
        throw todo("25 Reverse Nodes in k-Group");
    }

    public ListNode reverseBetween(ListNode head, int left, int right) {
        throw todo("92 Reverse Linked List II");
    }

    // 括号
    public boolean isValidParentheses(String s) {
        throw todo("20 Valid Parentheses");
    }

    public int minAddToMakeValid(String s) {
        throw todo("921 Minimum Add to Make Parentheses Valid");
    }

    public int minInsertions(String s) {
        throw todo("1541 Minimum Insertions to Balance Parentheses");
    }

    public int longestValidParentheses(String s) {
        throw todo("32 Longest Valid Parentheses");
    }

    // 缺失和重复元素
    public int missingNumber(int[] nums) {
        throw todo("268 Missing Number");
    }

    public List<Integer> findDisappearedNumbers(int[] nums) {
        throw todo("448 Find All Numbers Disappeared");
    }

    public int firstMissingPositive(int[] nums) {
        throw todo("41 First Missing Positive");
    }

    public int[] findErrorNums(int[] nums) {
        throw todo("645 Set Mismatch");
    }

    public int findDuplicate(int[] nums) {
        throw todo("287 Find the Duplicate Number");
    }

    // 蓄水池抽样
    static class RandomNodePicker {
        public RandomNodePicker(ListNode head) {
            throw todo("382 RandomNodePicker 初始化");
        }

        public int getRandom() {
            throw todo("382 getRandom");
        }
    }

    // 考场座位
    static class ExamRoom {
        public ExamRoom(int n) {
            throw todo("855 ExamRoom 初始化");
        }

        public int seat() {
            throw todo("855 seat");
        }

        public void leave(int p) {
            throw todo("855 leave");
        }
    }

    // Union-Find
    static class UnionFind {
        public UnionFind(int n) {
            throw todo("UnionFind 初始化");
        }

        public int find(int x) {
            throw todo("UnionFind find");
        }

        public void union(int p, int q) {
            throw todo("UnionFind union");
        }

        public boolean connected(int p, int q) {
            throw todo("UnionFind connected");
        }
    }

    public int findCircleNum(int[][] isConnected) {
        throw todo("547 Number of Provinces");
    }

    public int[] findRedundantConnection(int[][] edges) {
        throw todo("684 Redundant Connection");
    }

    public boolean equationsPossible(String[] equations) {
        throw todo("990 Satisfiability of Equality Equations");
    }

    public List<List<String>> accountsMerge(List<List<String>> accounts) {
        throw todo("721 Accounts Merge");
    }

    // 数学规律
    public boolean canWinNim(int n) {
        throw todo("292 Nim Game");
    }

    public int bulbSwitch(int n) {
        throw todo("319 Bulb Switcher");
    }

    // 子序列二分
    public boolean isSubsequence(String s, String t) {
        throw todo("392 Is Subsequence");
    }

    public int numMatchingSubseq(String s, String[] words) {
        throw todo("792 Number of Matching Subsequences");
    }

    private static UnsupportedOperationException todo(String topic) {
        return new UnsupportedOperationException("TODO 默写: " + topic);
    }
}
