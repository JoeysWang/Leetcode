import java.util.List;

public class Main206反转链表 {


    class Solution {
        public ListNode reverseList(ListNode head) {
            ListNode current = head;
            ListNode pre = null;

            while (current != null) {

                ListNode next = current.next;
                current.next = pre;
                pre = current;

                current = next;
            }

            return pre;
        }
    }

    public ListNode reverseList(ListNode head) {

        ListNode current = head;
        ListNode pervious = null;


        while (current != null) {
            ListNode temple = current.next;
            current.next = pervious;
            pervious = current;
            current = temple;

        }

        return pervious;

    }
}
