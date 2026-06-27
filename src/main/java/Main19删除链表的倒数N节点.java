import data.ListNode;

public class Main19删除链表的倒数N节点 {

    /**
     * 给定一个链表: 1->2->3->4->5, 和 n = 2.
     * <p>
     * 当删除了倒数第二个节点后，链表变为 1->2->3->5.
     */




    public ListNode removeNthFromEnd(ListNode head, int n) {


        ListNode dummy = new ListNode(0);
        ListNode slow = dummy;
        ListNode fast = dummy;

        dummy.next = head;

        for (int i = 0; i < n; i++) {
            fast = fast.next;
        }

        while (fast.next != null) {
            fast = fast.next;
            slow = slow.next;
        }
        slow.next = slow.next.next;

        return dummy.next;

    }

    public static void main(String[] args) {
        test();
    }

    public static void test() {
        TestUtil.reset();
        Main19删除链表的倒数N节点 obj = new Main19删除链表的倒数N节点();

        // 测试用例1: 示例用例 [1,2,3,4,5] n=2 → [1,2,3,5]
        ListNode head1 = buildList(new int[]{1, 2, 3, 4, 5});
        ListNode result1 = obj.removeNthFromEnd(head1, 2);
        TestUtil.assertArrayEquals(new int[]{1, 2, 3, 5}, listToArray(result1), "示例: [1,2,3,4,5] n=2");

        // 测试用例2: 边界用例 [1] n=1 → []
        ListNode head2 = buildList(new int[]{1});
        ListNode result2 = obj.removeNthFromEnd(head2, 1);
        TestUtil.assertArrayEquals(new int[]{}, listToArray(result2), "边界: [1] n=1");

        // 测试用例3: 删除第一个节点 [1,2] n=2 → [2]
        ListNode head3 = buildList(new int[]{1, 2});
        ListNode result3 = obj.removeNthFromEnd(head3, 2);
        TestUtil.assertArrayEquals(new int[]{2}, listToArray(result3), "删除第一个: [1,2] n=2");

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
}
