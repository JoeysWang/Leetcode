import data.ListNode;

public class Main24两两交换链表中的节点 {


    // node1 -> node2 -> node3

    //给定 1->2->3->4, 你应该返回 2->1->4->3.

    public ListNode swapPairs(ListNode head) {

        ListNode current = head;
        ListNode result = new ListNode(0);
        result.next = head;

        ListNode pre = result;

        while (current != null && current.next != null) {
            ListNode next = current.next;//2

            current.next = next.next;

            next.next = current;

            pre.next = next;

            pre = current;
            current = current.next;
        }

        return result.next;

    }
}
