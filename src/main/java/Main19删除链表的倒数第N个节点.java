import data.ListNode;

public class Main19删除链表的倒数第N个节点 {

    /**
     * 给定一个链表: null- 1->2->3->4->5->null, 和 n = 2.
     * <p>
     * 当删除了倒数第二个节点后，链表变为 1->2->3->5.
     */
    public static void main(String[] args) {
        test();
    }

    public static void test() {
        TestUtil.reset();
        Main19删除链表的倒数第N个节点 outer = new Main19删除链表的倒数第N个节点();
        Solution solution = outer.new Solution();

        // 测试用例1: 示例用例 [1,2,3,4,5] n=2 → [1,2,3,5]
        ListNode head1 = buildList(new int[]{1, 2, 3, 4, 5});
        ListNode result1 = solution.removeNthFromEnd(head1, 2);
        TestUtil.assertArrayEquals(new int[]{1, 2, 3, 5}, listToArray(result1), "示例: [1,2,3,4,5] n=2");

        // 测试用例2: 边界用例 [1] n=1 → []
        ListNode head2 = buildList(new int[]{1});
        ListNode result2 = solution.removeNthFromEnd(head2, 1);
        TestUtil.assertArrayEquals(new int[]{}, listToArray(result2), "边界: [1] n=1");

        // 测试用例3: 删除第一个节点 n=5
        ListNode head3 = buildList(new int[]{1, 2, 3, 4, 5});
        ListNode result3 = solution.removeNthFromEnd(head3, 5);
        TestUtil.assertArrayEquals(new int[]{2, 3, 4, 5}, listToArray(result3), "删除头节点: n=5");

        TestUtil.printSummary();
    }

    private static ListNode buildList(int[] arr) {
        ListNode dummy = new ListNode(0);
        ListNode current = dummy;
        for (int v : arr) {
            current.next = new ListNode(v);
            current = current.next;
        }
        return dummy.next;
    }

    private static int[] listToArray(ListNode head) {
        int count = 0;
        ListNode curr = head;
        while (curr != null) { count++; curr = curr.next; }
        int[] arr = new int[count];
        curr = head;
        for (int i = 0; i < count; i++) { arr[i] = curr.val; curr = curr.next; }
        return arr;
    }

    /**
     * 一次遍历法，用双指针， 一个fast，一个slow
     * fast先走n，然后再和slow一起走到fast.next为null，
     * 这时候slow指的位置就是倒数第n个节点！
     * 然后slow的下一个指向下下个，就跳过了第N节点
     * 
     */
    class Solution {

        public ListNode removeNthFromEnd(ListNode head, int n) {
            ListNode dummy = new ListNode();
            // null- 1->2->3->4->5->null

            dummy.next = head;
            ListNode slow = dummy;
            ListNode fast = dummy;

            for (int i = 0; i < n; i++) {
                fast = fast.next;
            }
            while (fast.next != null) {
                fast = fast.next;
                slow = slow.next;
            }
            slow.next = slow.next.next;

            // 注意不要返回head，因为head也有可能被remove掉,比如n=5
            return dummy.next;
        }

        private ListNode reverse(ListNode head) {
            ListNode pre = null;
            ListNode current = head;

            while (current != null) {
                ListNode temple = current.next;
                current.next = pre;
                pre = current;
                current = temple;
            }

            return pre;
        }
    }
}
