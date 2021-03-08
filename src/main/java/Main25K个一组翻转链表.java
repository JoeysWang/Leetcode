import data.ListNode;

public class Main25K个一组翻转链表 {

    public static void main(String[] args) {


    }


    class Solution {


        public ListNode reverseKGroup(ListNode head, int k) {
            ListNode start = head;
            ListNode end = head;

            for (int i = 0; i < k; i++) {
                if (end == null) return start;
                end = end.next;
            }
            ListNode newHead = reverse(start, end);
            //翻转过后，start变成最后一个，并且next变成null了,所以start作为最后一个，需要连接之后翻转的newHead
            start.next = reverseKGroup(end, k);

            return newHead;
        }

        private ListNode reverse(ListNode start, ListNode end) {
            ListNode current = start;
            ListNode pre = null;
            ListNode next = null;

            while (current != end) {
                next = current.next;
                current.next = pre;
                pre = current;
                current = next;
            }
            //翻转过后记得返回pre ，这个pre就是新链表的头，也是老链表最后的节点
            return pre;
        }
    }

//    private ListNode reverse(ListNode start, ListNode end) {
//
//        ListNode cur = start;
//        ListNode next = null;
//        ListNode pre = null;
//
//        while (cur != end) {
//            next = cur.next;
//            cur.next = pre;
//            pre = cur;
//            cur = next;
//        }
//        return cur;
//    }
//
//
//    public ListNode reverseKGroup(ListNode head, int k) {
//        if (head == null) return null;
//
//        ListNode a = head;
//        ListNode b = head;
//
//        for (int i = 0; i < k; i++) {
//
//            if (b == null) return a;
//            b = b.next;
//        }
//
//
//        ListNode reverseHead = reverse(a, b);
//
//        a.next = reverseKGroup(b, k);
//        return reverseHead;
//
//    }
//
//    public ListNode reverseKGroup2(ListNode head, int k) {
//        if (head == null) return null;
//
//        ListNode start = head;
//        ListNode end = head;
//
//
//        for (int i = 0; i < k; i++) {
//            if (end == null) return head;
//            end = end.next;
//        }
//
//        start.next = reverseKGroup2(end,k);
//        ListNode newHead = reverse(start, end);
//
//        return newHead;
//    }

}
