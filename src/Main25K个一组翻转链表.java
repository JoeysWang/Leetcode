public class Main25K个一组翻转链表 {


    public ListNode reverseKGroup(ListNode head, int k) {
        ListNode dummy = new ListNode(0);
        dummy.next = head;

        ListNode current = dummy;


        while (current != null) {
            current = reverse(current, k);

        }


        return dummy.next;
    }

    private ListNode reverse(ListNode pre, int k) {

        ListNode previous = pre;
        ListNode current = previous.next;


        for (int i = 0; i < k; i++) {
            if (current == null) return null;
            ListNode temple = current.next;
            current.next = previous;
            previous = current;
            current = temple;
        }

        return current;
    }
}
