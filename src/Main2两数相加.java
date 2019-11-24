public class Main2两数相加 {


//    给出两个 非空 的链表用来表示两个非负的整数。
//    其中，它们各自的位数是按照 逆序 的方式存储的，并且它们的每个节点只能存储 一位 数字。
//
//    如果，我们将这两个数相加起来，则会返回一个新的链表来表示它们的和。
//
//    您可以假设除了数字 0 之外，这两个数都不会以 0 开头。
//
//    示例：
//
//    输入：(2 -> 4 -> 3) + (5 -> 6 -> 4)
//    输出：7 -> 0 -> 8
//    原因：342 + 465 = 807

    /**
     * Definition for singly-linked list.
     * public class ListNode {
     * int val;
     * ListNode next;
     * ListNode(int x) { val = x; }
     * }
     */
    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        ListNode result = new ListNode(0);
        ListNode current = result;

        ListNode a = l1;
        ListNode b = l2;
        int carry = 0;
        while (a != null || b != null) {

            int va = a != null ? a.val : 0;
            int vb = b != null ? b.val : 0;

            int added = va + vb + carry;

            carry = added / 10;

            current.next = new ListNode(added % 10);
            current = current.next;


            if (a.next != null) {
                a = a.next;
            }
            if (b.next != null)
                b = b.next;
        }

        if (carry>0)
            current.next=new ListNode(1);

        return result.next;
    }

}
