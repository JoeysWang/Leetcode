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
     * 注意 最后一个进位  还有两个node不一样长的判空！
     */
    public static class Solution {
        public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
            ListNode result = new ListNode(0);
            ListNode resultRecord = result;
            int overflow = 0;
            while (l1 != null || l2 != null) {

                int now = (l1 != null ? l1.val : 0) + (l2 != null ? l2.val : 0) + overflow;
                int nodeVal = (now) % 10;
                resultRecord.next = new ListNode(nodeVal);
                overflow = now / 10;
                if (l1 != null)
                    l1 = l1.next;
                if (l2 != null)
                    l2 = l2.next;
                resultRecord = resultRecord.next;
            }
            if (overflow != 0) {
                resultRecord.next = new ListNode(overflow);
            }

            return result.next;
        }
    }
}
