import data.ListNode;

public class Main25K个一组翻转链表 {

    public static void main(String[] args) {


    }

    private ListNode reverse(ListNode start, ListNode end) {

        ListNode cur = start;
        ListNode next = null;
        ListNode pre = null;

        while (cur != end) {
            next = cur.next;
            cur.next = pre;
            pre = cur;
            cur = next;
        }
        return cur;
    }


    public ListNode reverseKGroup(ListNode head, int k) {
        if (head == null) return null;

        ListNode a = head;
        ListNode b = head;

        for (int i = 0; i < k; i++) {

            if (b == null) return a;
            b = b.next;
        }


        ListNode reverseHead = reverse(a, b);

        a.next = reverseKGroup(b, k);
        return reverseHead;

    }

    public ListNode reverseKGroup2(ListNode head, int k) {
        if (head == null) return null;

        ListNode start = head;
        ListNode end = head;


        for (int i = 0; i < k; i++) {
            if (end == null) return head;
            end = end.next;
        }

        start.next = reverseKGroup2(end,k);
        ListNode newHead = reverse(start, end);

        return newHead;
    }

}
