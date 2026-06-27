package review.answer;

import data.ListNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Stack;
import java.util.TreeSet;

/**
 * 第四章答案：高频面试题。
 */
public class Chapter04InterviewAnswer {

    public static void main(String[] args) {
        System.out.println("第四章答案：高频面试题。");
    }

    public int countPrimes(int n) {
        boolean[] isPrime = new boolean[n];
        Arrays.fill(isPrime, true);
        int ans = 0;
        for (int i = 2; i < n; i++) {
            if (isPrime[i]) {
                ans++;
                if ((long) i * i < n) {
                    for (int j = i * i; j < n; j += i) {
                        isPrime[j] = false;
                    }
                }
            }
        }
        return ans;
    }

    public double myPow(double x, int n) {
        long exp = n;
        if (exp < 0) {
            x = 1 / x;
            exp = -exp;
        }
        double ans = 1.0;
        while (exp > 0) {
            if ((exp & 1) == 1) {
                ans *= x;
            }
            x *= x;
            exp >>= 1;
        }
        return ans;
    }

    public int superPow(int a, int[] b) {
        int ans = 1;
        for (int digit : b) {
            ans = modPow(ans, 10) * modPow(a, digit) % 1337;
        }
        return ans;
    }

    private int modPow(int a, int n) {
        a %= 1337;
        int ans = 1;
        while (n > 0) {
            if ((n & 1) == 1) {
                ans = ans * a % 1337;
            }
            a = a * a % 1337;
            n >>= 1;
        }
        return ans;
    }

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

    public int[] searchRange(int[] nums, int target) {
        return new int[]{leftBound(nums, target), rightBound(nums, target)};
    }

    private int leftBound(int[] nums, int target) {
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

    private int rightBound(int[] nums, int target) {
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

    public int minEatingSpeed(int[] piles, int h) {
        int left = 1;
        int right = 0;
        for (int pile : piles) {
            right = Math.max(right, pile);
        }
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (canEatAll(piles, h, mid)) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }
        return left;
    }

    private boolean canEatAll(int[] piles, int h, int speed) {
        long hours = 0;
        for (int pile : piles) {
            hours += (pile + speed - 1) / speed;
        }
        return hours <= h;
    }

    public int shipWithinDays(int[] weights, int days) {
        int left = 0;
        int right = 0;
        for (int weight : weights) {
            left = Math.max(left, weight);
            right += weight;
        }
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (canShip(weights, days, mid)) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }
        return left;
    }

    private boolean canShip(int[] weights, int days, int capacity) {
        int usedDays = 1;
        int load = 0;
        for (int weight : weights) {
            if (load + weight > capacity) {
                usedDays++;
                load = 0;
            }
            load += weight;
        }
        return usedDays <= days;
    }

    public int splitArray(int[] nums, int k) {
        int left = 0;
        int right = 0;
        for (int num : nums) {
            left = Math.max(left, num);
            right += num;
        }
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (canSplit(nums, k, mid)) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }
        return left;
    }

    private boolean canSplit(int[] nums, int k, int maxSum) {
        int groups = 1;
        int sum = 0;
        for (int num : nums) {
            if (sum + num > maxSum) {
                groups++;
                sum = 0;
            }
            sum += num;
        }
        return groups <= k;
    }

    public int trap(int[] height) {
        int left = 0;
        int right = height.length - 1;
        int leftMax = 0;
        int rightMax = 0;
        int ans = 0;
        while (left < right) {
            leftMax = Math.max(leftMax, height[left]);
            rightMax = Math.max(rightMax, height[right]);
            if (leftMax < rightMax) {
                ans += leftMax - height[left++];
            } else {
                ans += rightMax - height[right--];
            }
        }
        return ans;
    }

    public int maxArea(int[] height) {
        int left = 0;
        int right = height.length - 1;
        int ans = 0;
        while (left < right) {
            ans = Math.max(ans, Math.min(height[left], height[right]) * (right - left));
            if (height[left] < height[right]) {
                left++;
            } else {
                right--;
            }
        }
        return ans;
    }

    public int removeDuplicates(int[] nums) {
        if (nums.length == 0) {
            return 0;
        }
        int slow = 1;
        for (int fast = 1; fast < nums.length; fast++) {
            if (nums[fast] != nums[fast - 1]) {
                nums[slow++] = nums[fast];
            }
        }
        return slow;
    }

    public int removeDuplicatesKeepTwo(int[] nums) {
        int slow = 0;
        for (int num : nums) {
            if (slow < 2 || num != nums[slow - 2]) {
                nums[slow++] = num;
            }
        }
        return slow;
    }

    public int removeElement(int[] nums, int val) {
        int slow = 0;
        for (int num : nums) {
            if (num != val) {
                nums[slow++] = num;
            }
        }
        return slow;
    }

    public void moveZeroes(int[] nums) {
        int slow = 0;
        for (int num : nums) {
            if (num != 0) {
                nums[slow++] = num;
            }
        }
        while (slow < nums.length) {
            nums[slow++] = 0;
        }
    }

    public String longestPalindrome(String s) {
        String ans = "";
        for (int i = 0; i < s.length(); i++) {
            String odd = expandPalindrome(s, i, i);
            String even = expandPalindrome(s, i, i + 1);
            if (odd.length() > ans.length()) {
                ans = odd;
            }
            if (even.length() > ans.length()) {
                ans = even;
            }
        }
        return ans;
    }

    private String expandPalindrome(String s, int left, int right) {
        while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
            left--;
            right++;
        }
        return s.substring(left + 1, right);
    }

    public int countSubstrings(String s) {
        int ans = 0;
        for (int i = 0; i < s.length(); i++) {
            ans += countPalindromeFromCenter(s, i, i);
            ans += countPalindromeFromCenter(s, i, i + 1);
        }
        return ans;
    }

    private int countPalindromeFromCenter(String s, int left, int right) {
        int count = 0;
        while (left >= 0 && right < s.length() && s.charAt(left--) == s.charAt(right++)) {
            count++;
        }
        return count;
    }

    public boolean isPalindromeList(ListNode head) {
        if (head == null || head.next == null) {
            return true;
        }
        ListNode slow = head;
        ListNode fast = head;
        while (fast.next != null && fast.next.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        ListNode second = reverse(slow.next);
        ListNode p1 = head;
        ListNode p2 = second;
        boolean ans = true;
        while (p2 != null) {
            if (!p1.val.equals(p2.val)) {
                ans = false;
                break;
            }
            p1 = p1.next;
            p2 = p2.next;
        }
        slow.next = reverse(second);
        return ans;
    }

    public boolean canJump(int[] nums) {
        int farthest = 0;
        for (int i = 0; i < nums.length; i++) {
            if (i > farthest) {
                return false;
            }
            farthest = Math.max(farthest, i + nums[i]);
        }
        return true;
    }

    public int jump(int[] nums) {
        int jumps = 0;
        int end = 0;
        int farthest = 0;
        for (int i = 0; i < nums.length - 1; i++) {
            farthest = Math.max(farthest, i + nums[i]);
            if (i == end) {
                jumps++;
                end = farthest;
            }
        }
        return jumps;
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

    private ListNode reverse(ListNode head) {
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

    public boolean isValidParentheses(String s) {
        Stack<Character> stack = new Stack<>();
        for (char c : s.toCharArray()) {
            if (c == '(') {
                stack.push(')');
            } else if (c == '[') {
                stack.push(']');
            } else if (c == '{') {
                stack.push('}');
            } else if (stack.isEmpty() || stack.pop() != c) {
                return false;
            }
        }
        return stack.isEmpty();
    }

    public int minAddToMakeValid(String s) {
        int needRight = 0;
        int insertions = 0;
        for (char c : s.toCharArray()) {
            if (c == '(') {
                needRight++;
            } else {
                needRight--;
                if (needRight == -1) {
                    insertions++;
                    needRight = 0;
                }
            }
        }
        return insertions + needRight;
    }

    // 每个 '(' 需要两个连续的 ')'。
    public int minInsertions(String s) {
        int needRight = 0;
        int insertions = 0;
        for (char c : s.toCharArray()) {
            if (c == '(') {
                needRight += 2;
                if (needRight % 2 == 1) {
                    insertions++;
                    needRight--;
                }
            } else {
                needRight--;
                if (needRight == -1) {
                    insertions++;
                    needRight = 1;
                }
            }
        }
        return insertions + needRight;
    }

    public int longestValidParentheses(String s) {
        Stack<Integer> stack = new Stack<>();
        stack.push(-1);
        int ans = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '(') {
                stack.push(i);
            } else {
                stack.pop();
                if (stack.isEmpty()) {
                    stack.push(i);
                } else {
                    ans = Math.max(ans, i - stack.peek());
                }
            }
        }
        return ans;
    }

    public int missingNumber(int[] nums) {
        int ans = nums.length;
        for (int i = 0; i < nums.length; i++) {
            ans ^= i;
            ans ^= nums[i];
        }
        return ans;
    }

    public List<Integer> findDisappearedNumbers(int[] nums) {
        for (int num : nums) {
            int index = Math.abs(num) - 1;
            nums[index] = -Math.abs(nums[index]);
        }
        List<Integer> ans = new ArrayList<>();
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] > 0) {
                ans.add(i + 1);
            }
        }
        return ans;
    }

    public int firstMissingPositive(int[] nums) {
        int n = nums.length;
        for (int i = 0; i < n; i++) {
            while (nums[i] >= 1 && nums[i] <= n && nums[nums[i] - 1] != nums[i]) {
                swap(nums, i, nums[i] - 1);
            }
        }
        for (int i = 0; i < n; i++) {
            if (nums[i] != i + 1) {
                return i + 1;
            }
        }
        return n + 1;
    }

    public int[] findErrorNums(int[] nums) {
        int duplicate = -1;
        int missing = -1;
        for (int num : nums) {
            int index = Math.abs(num) - 1;
            if (nums[index] < 0) {
                duplicate = Math.abs(num);
            } else {
                nums[index] = -nums[index];
            }
        }
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] > 0) {
                missing = i + 1;
            }
        }
        return new int[]{duplicate, missing};
    }

    public int findDuplicate(int[] nums) {
        int slow = nums[0];
        int fast = nums[0];
        do {
            slow = nums[slow];
            fast = nums[nums[fast]];
        } while (slow != fast);
        slow = nums[0];
        while (slow != fast) {
            slow = nums[slow];
            fast = nums[fast];
        }
        return slow;
    }

    private void swap(int[] nums, int i, int j) {
        int tmp = nums[i];
        nums[i] = nums[j];
        nums[j] = tmp;
    }

    static class RandomNodePicker {
        private final ListNode head;
        private final Random random = new Random();

        public RandomNodePicker(ListNode head) {
            this.head = head;
        }

        public int getRandom() {
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
    }

    static class ExamRoom {
        private final int n;
        private final TreeSet<Integer> seats = new TreeSet<>();

        public ExamRoom(int n) {
            this.n = n;
        }

        public int seat() {
            if (seats.isEmpty()) {
                seats.add(0);
                return 0;
            }
            int bestSeat = 0;
            int bestDistance = seats.first();
            Integer prev = null;
            for (int seat : seats) {
                if (prev != null) {
                    int distance = (seat - prev) / 2;
                    if (distance > bestDistance) {
                        bestDistance = distance;
                        bestSeat = prev + distance;
                    }
                }
                prev = seat;
            }
            int lastDistance = n - 1 - seats.last();
            if (lastDistance > bestDistance) {
                bestSeat = n - 1;
            }
            seats.add(bestSeat);
            return bestSeat;
        }

        public void leave(int p) {
            seats.remove(p);
        }
    }

    static class UnionFind {
        private final int[] parent;
        private final int[] size;

        public UnionFind(int n) {
            parent = new int[n];
            size = new int[n];
            for (int i = 0; i < n; i++) {
                parent[i] = i;
                size[i] = 1;
            }
        }

        public int find(int x) {
            while (parent[x] != x) {
                parent[x] = parent[parent[x]];
                x = parent[x];
            }
            return x;
        }

        public void union(int p, int q) {
            int rootP = find(p);
            int rootQ = find(q);
            if (rootP == rootQ) {
                return;
            }
            if (size[rootP] < size[rootQ]) {
                parent[rootP] = rootQ;
                size[rootQ] += size[rootP];
            } else {
                parent[rootQ] = rootP;
                size[rootP] += size[rootQ];
            }
        }

        public boolean connected(int p, int q) {
            return find(p) == find(q);
        }
    }

    public int findCircleNum(int[][] isConnected) {
        int n = isConnected.length;
        UnionFind uf = new UnionFind(n);
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (isConnected[i][j] == 1) {
                    uf.union(i, j);
                }
            }
        }
        int count = 0;
        for (int i = 0; i < n; i++) {
            if (uf.find(i) == i) {
                count++;
            }
        }
        return count;
    }

    public int[] findRedundantConnection(int[][] edges) {
        UnionFind uf = new UnionFind(edges.length + 1);
        for (int[] edge : edges) {
            if (uf.connected(edge[0], edge[1])) {
                return edge;
            }
            uf.union(edge[0], edge[1]);
        }
        return new int[0];
    }

    public boolean equationsPossible(String[] equations) {
        UnionFind uf = new UnionFind(26);
        for (String equation : equations) {
            if (equation.charAt(1) == '=') {
                uf.union(equation.charAt(0) - 'a', equation.charAt(3) - 'a');
            }
        }
        for (String equation : equations) {
            if (equation.charAt(1) == '!' && uf.connected(equation.charAt(0) - 'a', equation.charAt(3) - 'a')) {
                return false;
            }
        }
        return true;
    }

    public List<List<String>> accountsMerge(List<List<String>> accounts) {
        Map<String, Integer> emailToId = new HashMap<>();
        Map<String, String> emailToName = new HashMap<>();
        int id = 0;
        for (List<String> account : accounts) {
            String name = account.get(0);
            for (int i = 1; i < account.size(); i++) {
                String email = account.get(i);
                if (!emailToId.containsKey(email)) {
                    emailToId.put(email, id++);
                }
                emailToName.put(email, name);
            }
        }

        UnionFind uf = new UnionFind(id);
        for (List<String> account : accounts) {
            int first = emailToId.get(account.get(1));
            for (int i = 2; i < account.size(); i++) {
                uf.union(first, emailToId.get(account.get(i)));
            }
        }

        Map<Integer, List<String>> groups = new HashMap<>();
        for (String email : emailToId.keySet()) {
            int root = uf.find(emailToId.get(email));
            groups.computeIfAbsent(root, key -> new ArrayList<>()).add(email);
        }

        List<List<String>> ans = new ArrayList<>();
        for (List<String> emails : groups.values()) {
            Collections.sort(emails);
            List<String> account = new ArrayList<>();
            account.add(emailToName.get(emails.get(0)));
            account.addAll(emails);
            ans.add(account);
        }
        return ans;
    }

    public boolean canWinNim(int n) {
        return n % 4 != 0;
    }

    public int bulbSwitch(int n) {
        return (int) Math.sqrt(n);
    }

    public boolean isSubsequence(String s, String t) {
        int i = 0;
        for (int j = 0; j < t.length() && i < s.length(); j++) {
            if (s.charAt(i) == t.charAt(j)) {
                i++;
            }
        }
        return i == s.length();
    }

    @SuppressWarnings("unchecked")
    public int numMatchingSubseq(String s, String[] words) {
        List<Integer>[] positions = new ArrayList[256];
        for (int i = 0; i < positions.length; i++) {
            positions[i] = new ArrayList<>();
        }
        for (int i = 0; i < s.length(); i++) {
            positions[s.charAt(i)].add(i);
        }

        int ans = 0;
        for (String word : words) {
            int cur = -1;
            boolean ok = true;
            for (char c : word.toCharArray()) {
                List<Integer> list = positions[c];
                int nextIndex = upperBound(list, cur);
                if (nextIndex == list.size()) {
                    ok = false;
                    break;
                }
                cur = list.get(nextIndex);
            }
            if (ok) {
                ans++;
            }
        }
        return ans;
    }

    private int upperBound(List<Integer> nums, int target) {
        int left = 0;
        int right = nums.size();
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (nums.get(mid) > target) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }
        return left;
    }
}
