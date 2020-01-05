public class Main24两两交换链表中的节点 {


    // node1 -> node2 -> node3

    //给定 1->2->3->4, 你应该返回 2->1->4->3.

    public ListNode swapPairs(ListNode head) {
        if (head == null || head.next == null) return null;

        ListNode current = head;
        ListNode result = new ListNode(0);


        ListNode pre = null;

//
        while (current != null) {
            ListNode next = current.next;//2

            current.next = next.next;//1->2->3->4
//                                      \ 3/

            next.next = current; //2->1->3->4

            if (pre != null)
                pre.next = next;

            pre = current;
            current = current.next;
        }

        return result.next;

    }
}
