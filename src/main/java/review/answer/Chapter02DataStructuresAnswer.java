package review.answer;

import data.ListNode;
import data.TreeNode;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

/**
 * 第二章答案：数据结构。
 *
 * 数据结构题要先记住“不变量”：例如 LRU 的链表头部永远是最近使用。
 */
public class Chapter02DataStructuresAnswer {

    public static void main(String[] args) {
        System.out.println("第二章答案：数据结构。");
    }

    public int findKthLargest(int[] nums, int k) {
        PriorityQueue<Integer> heap = new PriorityQueue<>();
        for (int num : nums) {
            heap.offer(num);
            if (heap.size() > k) {
                heap.poll();
            }
        }
        return heap.peek();
    }

    public int[] topKFrequent(int[] nums, int k) {
        Map<Integer, Integer> count = new HashMap<>();
        for (int num : nums) {
            count.put(num, count.getOrDefault(num, 0) + 1);
        }
        PriorityQueue<int[]> heap = new PriorityQueue<>((a, b) -> a[1] - b[1]);
        for (Map.Entry<Integer, Integer> entry : count.entrySet()) {
            heap.offer(new int[]{entry.getKey(), entry.getValue()});
            if (heap.size() > k) {
                heap.poll();
            }
        }
        int[] ans = new int[k];
        for (int i = k - 1; i >= 0; i--) {
            ans[i] = heap.poll()[0];
        }
        return ans;
    }

    static class MedianFinder {
        private final PriorityQueue<Integer> small;
        private final PriorityQueue<Integer> large;

        public MedianFinder() {
            small = new PriorityQueue<>((a, b) -> b - a);
            large = new PriorityQueue<>();
        }

        public void addNum(int num) {
            if (small.isEmpty() || num <= small.peek()) {
                small.offer(num);
            } else {
                large.offer(num);
            }
            if (small.size() > large.size() + 1) {
                large.offer(small.poll());
            }
            if (large.size() > small.size()) {
                small.offer(large.poll());
            }
        }

        public double findMedian() {
            if (small.size() == large.size()) {
                return (small.peek() + large.peek()) / 2.0;
            }
            return small.peek();
        }
    }

    static class LRUCache {
        private final int capacity;
        private final Map<Integer, Node> map = new HashMap<>();
        private final Node head = new Node(0, 0);
        private final Node tail = new Node(0, 0);

        public LRUCache(int capacity) {
            this.capacity = capacity;
            head.next = tail;
            tail.prev = head;
        }

        public int get(int key) {
            Node node = map.get(key);
            if (node == null) {
                return -1;
            }
            moveToHead(node);
            return node.value;
        }

        public void put(int key, int value) {
            Node node = map.get(key);
            if (node != null) {
                node.value = value;
                moveToHead(node);
                return;
            }

            Node added = new Node(key, value);
            map.put(key, added);
            addAfterHead(added);
            if (map.size() > capacity) {
                Node removed = removeTail();
                map.remove(removed.key);
            }
        }

        private void moveToHead(Node node) {
            remove(node);
            addAfterHead(node);
        }

        private void addAfterHead(Node node) {
            node.prev = head;
            node.next = head.next;
            head.next.prev = node;
            head.next = node;
        }

        private void remove(Node node) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }

        private Node removeTail() {
            Node node = tail.prev;
            remove(node);
            return node;
        }

        static class Node {
            int key;
            int value;
            Node prev;
            Node next;

            Node(int key, int value) {
                this.key = key;
                this.value = value;
            }
        }
    }

    public boolean isValidBST(TreeNode root) {
        return isValidBST(root, null, null);
    }

    private boolean isValidBST(TreeNode root, Integer min, Integer max) {
        if (root == null) {
            return true;
        }
        if (min != null && root.val <= min) {
            return false;
        }
        if (max != null && root.val >= max) {
            return false;
        }
        return isValidBST(root.left, min, root.val) && isValidBST(root.right, root.val, max);
    }

    public TreeNode searchBST(TreeNode root, int val) {
        while (root != null) {
            if (root.val == val) {
                return root;
            } else if (root.val > val) {
                root = root.left;
            } else {
                root = root.right;
            }
        }
        return null;
    }

    public TreeNode insertIntoBST(TreeNode root, int val) {
        if (root == null) {
            return new TreeNode(val);
        }
        if (val < root.val) {
            root.left = insertIntoBST(root.left, val);
        } else if (val > root.val) {
            root.right = insertIntoBST(root.right, val);
        }
        return root;
    }

    public TreeNode deleteNode(TreeNode root, int key) {
        if (root == null) {
            return null;
        }
        if (key < root.val) {
            root.left = deleteNode(root.left, key);
        } else if (key > root.val) {
            root.right = deleteNode(root.right, key);
        } else {
            if (root.left == null) {
                return root.right;
            }
            if (root.right == null) {
                return root.left;
            }
            TreeNode successor = minNode(root.right);
            root.val = successor.val;
            root.right = deleteNode(root.right, successor.val);
        }
        return root;
    }

    private TreeNode minNode(TreeNode root) {
        while (root.left != null) {
            root = root.left;
        }
        return root;
    }

    public int kthSmallest(TreeNode root, int k) {
        Deque<TreeNode> stack = new ArrayDeque<>();
        while (root != null || !stack.isEmpty()) {
            while (root != null) {
                stack.push(root);
                root = root.left;
            }
            root = stack.pop();
            k--;
            if (k == 0) {
                return root.val;
            }
            root = root.right;
        }
        return -1;
    }

    // 完全二叉树：如果左右高度相同，说明左子树是满的；否则右子树是满的。
    public int countNodes(TreeNode root) {
        if (root == null) {
            return 0;
        }
        int leftHeight = height(root.left);
        int rightHeight = height(root.right);
        if (leftHeight == rightHeight) {
            return (1 << leftHeight) + countNodes(root.right);
        }
        return (1 << rightHeight) + countNodes(root.left);
    }

    private int height(TreeNode root) {
        int h = 0;
        while (root != null) {
            h++;
            root = root.left;
        }
        return h;
    }

    public int[] nextGreaterElement(int[] nums1, int[] nums2) {
        Map<Integer, Integer> next = new HashMap<>();
        Deque<Integer> stack = new ArrayDeque<>();
        for (int i = nums2.length - 1; i >= 0; i--) {
            while (!stack.isEmpty() && stack.peek() <= nums2[i]) {
                stack.pop();
            }
            next.put(nums2[i], stack.isEmpty() ? -1 : stack.peek());
            stack.push(nums2[i]);
        }
        int[] ans = new int[nums1.length];
        for (int i = 0; i < nums1.length; i++) {
            ans[i] = next.get(nums1[i]);
        }
        return ans;
    }

    public int[] nextGreaterElementsCircular(int[] nums) {
        int n = nums.length;
        int[] ans = new int[n];
        ArraysFill(ans, -1);
        Deque<Integer> stack = new ArrayDeque<>();
        for (int i = 2 * n - 1; i >= 0; i--) {
            int index = i % n;
            while (!stack.isEmpty() && stack.peek() <= nums[index]) {
                stack.pop();
            }
            if (i < n && !stack.isEmpty()) {
                ans[index] = stack.peek();
            }
            stack.push(nums[index]);
        }
        return ans;
    }

    private void ArraysFill(int[] nums, int value) {
        for (int i = 0; i < nums.length; i++) {
            nums[i] = value;
        }
    }

    public int[] dailyTemperatures(int[] temperatures) {
        int[] ans = new int[temperatures.length];
        Deque<Integer> stack = new ArrayDeque<>();
        for (int i = 0; i < temperatures.length; i++) {
            while (!stack.isEmpty() && temperatures[i] > temperatures[stack.peek()]) {
                int prev = stack.pop();
                ans[prev] = i - prev;
            }
            stack.push(i);
        }
        return ans;
    }

    public int largestRectangleArea(int[] heights) {
        int n = heights.length;
        Deque<Integer> stack = new ArrayDeque<>();
        int ans = 0;
        for (int i = 0; i <= n; i++) {
            int curHeight = i == n ? 0 : heights[i];
            while (!stack.isEmpty() && curHeight < heights[stack.peek()]) {
                int h = heights[stack.pop()];
                int left = stack.isEmpty() ? -1 : stack.peek();
                ans = Math.max(ans, h * (i - left - 1));
            }
            stack.push(i);
        }
        return ans;
    }

    public int[] maxSlidingWindow(int[] nums, int k) {
        int[] ans = new int[nums.length - k + 1];
        Deque<Integer> deque = new ArrayDeque<>();
        for (int i = 0; i < nums.length; i++) {
            while (!deque.isEmpty() && deque.peekFirst() <= i - k) {
                deque.pollFirst();
            }
            while (!deque.isEmpty() && nums[deque.peekLast()] <= nums[i]) {
                deque.pollLast();
            }
            deque.offerLast(i);
            if (i >= k - 1) {
                ans[i - k + 1] = nums[deque.peekFirst()];
            }
        }
        return ans;
    }

    static class Twitter {
        private static int timestamp = 0;
        private final Map<Integer, Set<Integer>> follows = new HashMap<>();
        private final Map<Integer, Tweet> tweets = new HashMap<>();

        public Twitter() {
        }

        public void postTweet(int userId, int tweetId) {
            Tweet tweet = new Tweet(tweetId, timestamp++);
            tweet.next = tweets.get(userId);
            tweets.put(userId, tweet);
        }

        public List<Integer> getNewsFeed(int userId) {
            PriorityQueue<Tweet> heap = new PriorityQueue<>((a, b) -> b.time - a.time);
            if (tweets.containsKey(userId)) {
                heap.offer(tweets.get(userId));
            }
            for (int followee : follows.getOrDefault(userId, new HashSet<>())) {
                if (tweets.containsKey(followee)) {
                    heap.offer(tweets.get(followee));
                }
            }

            List<Integer> ans = new ArrayList<>();
            while (!heap.isEmpty() && ans.size() < 10) {
                Tweet cur = heap.poll();
                ans.add(cur.id);
                if (cur.next != null) {
                    heap.offer(cur.next);
                }
            }
            return ans;
        }

        public void follow(int followerId, int followeeId) {
            if (followerId == followeeId) {
                return;
            }
            follows.computeIfAbsent(followerId, key -> new HashSet<>()).add(followeeId);
        }

        public void unfollow(int followerId, int followeeId) {
            if (follows.containsKey(followerId)) {
                follows.get(followerId).remove(followeeId);
            }
        }

        static class Tweet {
            int id;
            int time;
            Tweet next;

            Tweet(int id, int time) {
                this.id = id;
                this.time = time;
            }
        }
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

    public ListNode reverseBetween(ListNode head, int left, int right) {
        ListNode dummy = new ListNode(0);
        dummy.next = head;
        ListNode pre = dummy;
        for (int i = 1; i < left; i++) {
            pre = pre.next;
        }

        ListNode cur = pre.next;
        for (int i = 0; i < right - left; i++) {
            ListNode move = cur.next;
            cur.next = move.next;
            move.next = pre.next;
            pre.next = move;
        }
        return dummy.next;
    }

    public ListNode reverseKGroup(ListNode head, int k) {
        ListNode a = head;
        ListNode b = head;
        for (int i = 0; i < k; i++) {
            if (b == null) {
                return head;
            }
            b = b.next;
        }
        ListNode newHead = reverseRange(a, b);
        a.next = reverseKGroup(b, k);
        return newHead;
    }

    private ListNode reverseRange(ListNode a, ListNode b) {
        ListNode prev = null;
        ListNode cur = a;
        while (cur != b) {
            ListNode next = cur.next;
            cur.next = prev;
            prev = cur;
            cur = next;
        }
        return prev;
    }

    static class MyQueue {
        private final Deque<Integer> in = new ArrayDeque<>();
        private final Deque<Integer> out = new ArrayDeque<>();

        public MyQueue() {
        }

        public void push(int x) {
            in.push(x);
        }

        public int pop() {
            moveIfNeeded();
            return out.pop();
        }

        public int peek() {
            moveIfNeeded();
            return out.peek();
        }

        public boolean empty() {
            return in.isEmpty() && out.isEmpty();
        }

        private void moveIfNeeded() {
            if (!out.isEmpty()) {
                return;
            }
            while (!in.isEmpty()) {
                out.push(in.pop());
            }
        }
    }

    static class MyStack {
        private final Queue<Integer> queue = new LinkedList<>();

        public MyStack() {
        }

        public void push(int x) {
            queue.offer(x);
            for (int i = 0; i < queue.size() - 1; i++) {
                queue.offer(queue.poll());
            }
        }

        public int pop() {
            return queue.poll();
        }

        public int top() {
            return queue.peek();
        }

        public boolean empty() {
            return queue.isEmpty();
        }
    }
}
