import java.util.List;

public class Main206反转链表 {

    public ListNode reverseList(ListNode head) {

        ListNode current = head;
        ListNode pervious = null;


        while (current != null) {
            ListNode temple = current.next;
            current.next=pervious;
            pervious=current;
            current=temple;

        }

        return pervious;

    }
}
