public class Main21合并两个有序链表 {

    public static ListNode mergeTwoLists(ListNode l1, ListNode l2) {

        ListNode result = new ListNode(0);
        ListNode current = result;

        while (l1 != null || l2 != null) {

            if (l1 != null && l2 != null) {
                if (l1.val < l2.val) {
                    current.next = l1;
                    l1 = l1.next;
                } else {
                    current.next = l2;
                    l2 = l2.next;
                }

            } else if (l1 == null) {
                current.next = l2;
                l2 = l2.next;
            } else {
                current.next = l1;
                l1 = l1.next;
            }
            current = current.next;
        }
        return result.next;
    }


    class Solution {

        public ListNode mergeTwoLists(ListNode l1, ListNode l2) {

            ListNode res = new ListNode(0);
            ListNode current = res;
            while (l1 != null || l2 != null) {
                if (l1 != null && l2 != null) {
                    if (l1.val <= l2.val) {
                        current.next = new ListNode(l1.val);
                        l1 = l1.next;
                    } else {
                        current.next = new ListNode(l2.val);
                        l2 = l2.next;
                    }

                } else if (l1 == null) {
                    current.next = new ListNode(l2.val);
                    l2 = l2.next;
                } else {
                    current.next = new ListNode(l1.val);
                    l1 = l1.next;
                }

                current = current.next;
            }


            return res.next;
        }


    }
}
