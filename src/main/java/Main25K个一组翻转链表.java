import data.ListNode;

public class Main25K个一组翻转链表 {

    public static void main(String[] args) {
        test();
    }

    public static void test() {
        TestUtil.reset();
        Main25K个一组翻转链表 outer = new Main25K个一组翻转链表();
        Solution solution = outer.new Solution();

        // 测试用例1: k=2 [1,2,3,4,5] → [2,1,4,3,5]
        ListNode head1 = buildList(new int[]{1, 2, 3, 4, 5});
        ListNode result1 = solution.reverseKGroup(head1, 2);
        TestUtil.assertArrayEquals(new int[]{2, 1, 4, 3, 5}, listToArray(result1), "示例: k=2 [1,2,3,4,5]");

        // 测试用例2: k=3 [1,2,3,4,5] → [3,2,1,4,5]
        ListNode head2 = buildList(new int[]{1, 2, 3, 4, 5});
        ListNode result2 = solution.reverseKGroup(head2, 3);
        TestUtil.assertArrayEquals(new int[]{3, 2, 1, 4, 5}, listToArray(result2), "k=3 [1,2,3,4,5]");

        // 测试用例3: k=1 不变
        ListNode head3 = buildList(new int[]{1, 2, 3});
        ListNode result3 = solution.reverseKGroup(head3, 1);
        TestUtil.assertArrayEquals(new int[]{1, 2, 3}, listToArray(result3), "k=1 不变");

        // 测试用例4: k等于链表长度
        ListNode head4 = buildList(new int[]{1, 2, 3});
        ListNode result4 = solution.reverseKGroup(head4, 3);
        TestUtil.assertArrayEquals(new int[]{3, 2, 1}, listToArray(result4), "k等于长度: [1,2,3]");

        TestUtil.printSummary();
    }

    private static ListNode buildList(int[] arr) {
        ListNode dummy = new ListNode(0);
        ListNode current = dummy;
        for (int v : arr) {
            current.next = new ListNode(v);
            current = current.next;
        }
        return dummy.next;
    }

    private static int[] listToArray(ListNode head) {
        int count = 0;
        ListNode curr = head;
        while (curr != null) { count++; curr = curr.next; }
        int[] arr = new int[count];
        curr = head;
        for (int i = 0; i < count; i++) { arr[i] = curr.val; curr = curr.next; }
        return arr;
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
