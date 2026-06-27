package review;

import data.ListNode;
import data.TreeNode;

import java.util.List;

/**
 * 第二章、数据结构系列。
 *
 * 默写目标：
 * 1. 先写清楚数据结构不变量，再写 API。
 * 2. 能从数组、链表两种底层结构解释上层结构。
 * 3. 重点默写：堆、LRU、BST、单调栈、单调队列、链表反转。
 *
 * 对应题目：
 * 1, 20, 206, 102, 200, 215, 347, 295, 23, 146, 460, 98, 700,
 * 701, 450, 230, 538, 222, 496, 503, 739, 84, 42, 239, 862,
 * 355, 92, 25, 232, 225, 155.
 */
public class Chapter02DataStructuresReview {

    public static void main(String[] args) {
        System.out.println("第二章：在本文件中默写数据结构实现。");
    }

    // 二叉堆 / 优先级队列
    public int findKthLargest(int[] nums, int k) {
        throw todo("215 Kth Largest Element");
    }

    public int[] topKFrequent(int[] nums, int k) {
        throw todo("347 Top K Frequent Elements");
    }

    static class MedianFinder {
        public MedianFinder() {
            throw todo("295 MedianFinder 初始化");
        }

        public void addNum(int num) {
            throw todo("295 addNum");
        }

        public double findMedian() {
            throw todo("295 findMedian");
        }
    }

    // LRU / LFU
    static class LRUCache {
        public LRUCache(int capacity) {
            throw todo("146 LRU 初始化");
        }

        public int get(int key) {
            throw todo("146 LRU get");
        }

        public void put(int key, int value) {
            throw todo("146 LRU put");
        }
    }

    // 二叉搜索树
    public boolean isValidBST(TreeNode root) {
        throw todo("98 Validate Binary Search Tree");
    }

    public TreeNode searchBST(TreeNode root, int val) {
        throw todo("700 Search in a BST");
    }

    public TreeNode insertIntoBST(TreeNode root, int val) {
        throw todo("701 Insert into a BST");
    }

    public TreeNode deleteNode(TreeNode root, int key) {
        throw todo("450 Delete Node in a BST");
    }

    public int kthSmallest(TreeNode root, int k) {
        throw todo("230 Kth Smallest Element in a BST");
    }

    public int countNodes(TreeNode root) {
        throw todo("222 Count Complete Tree Nodes");
    }

    // 单调栈
    public int[] nextGreaterElement(int[] nums1, int[] nums2) {
        throw todo("496 Next Greater Element I");
    }

    public int[] nextGreaterElementsCircular(int[] nums) {
        throw todo("503 Next Greater Element II");
    }

    public int[] dailyTemperatures(int[] temperatures) {
        throw todo("739 Daily Temperatures");
    }

    public int largestRectangleArea(int[] heights) {
        throw todo("84 Largest Rectangle in Histogram");
    }

    // 单调队列
    public int[] maxSlidingWindow(int[] nums, int k) {
        throw todo("239 Sliding Window Maximum");
    }

    // 设计 Twitter
    static class Twitter {
        public Twitter() {
            throw todo("355 Twitter 初始化");
        }

        public void postTweet(int userId, int tweetId) {
            throw todo("355 postTweet");
        }

        public List<Integer> getNewsFeed(int userId) {
            throw todo("355 getNewsFeed");
        }

        public void follow(int followerId, int followeeId) {
            throw todo("355 follow");
        }

        public void unfollow(int followerId, int followeeId) {
            throw todo("355 unfollow");
        }
    }

    // 递归反转链表
    public ListNode reverseList(ListNode head) {
        throw todo("206 Reverse Linked List");
    }

    public ListNode reverseBetween(ListNode head, int left, int right) {
        throw todo("92 Reverse Linked List II");
    }

    public ListNode reverseKGroup(ListNode head, int k) {
        throw todo("25 Reverse Nodes in k-Group");
    }

    // 队列实现栈 / 栈实现队列
    static class MyQueue {
        public MyQueue() {
            throw todo("232 MyQueue 初始化");
        }

        public void push(int x) {
            throw todo("232 push");
        }

        public int pop() {
            throw todo("232 pop");
        }

        public int peek() {
            throw todo("232 peek");
        }

        public boolean empty() {
            throw todo("232 empty");
        }
    }

    static class MyStack {
        public MyStack() {
            throw todo("225 MyStack 初始化");
        }

        public void push(int x) {
            throw todo("225 push");
        }

        public int pop() {
            throw todo("225 pop");
        }

        public int top() {
            throw todo("225 top");
        }

        public boolean empty() {
            throw todo("225 empty");
        }
    }

    private static UnsupportedOperationException todo(String topic) {
        return new UnsupportedOperationException("TODO 默写: " + topic);
    }
}
