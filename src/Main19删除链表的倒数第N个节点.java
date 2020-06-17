import data.ListNode;

public class Main19删除链表的倒数第N个节点 {

    /**
     * 给定一个链表: null- 1->2->3->4->5->null, 和 n = 2.
     * <p>
     * 当删除了倒数第二个节点后，链表变为 1->2->3->5.
     */
    public static void main(String[] args) {

    }


    /**
     * 一次遍历法，用双指针， 一个fast，一个slow
     * fast先走n+1，然后再和slow一起走到fast为null，
     * 这时候slow指的位置就是倒数第n个节点的前一个！
     * 然后slow的下一个指向下下个，就跳过了第N节点
     */
    class Solution {

        public ListNode removeNthFromEnd(ListNode head, int n) {
            ListNode dummy = new ListNode(0);
            dummy.next = head;


            ListNode fast = dummy;
            ListNode slow = dummy;

            for (int i = 0; i < n + 1; i++) {
                fast = fast.next;
            }

            while (fast != null) {
                fast = fast.next;
                slow = slow.next;
            }
            slow.next = slow.next.next;

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
