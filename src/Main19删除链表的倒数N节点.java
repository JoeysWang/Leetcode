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
}
