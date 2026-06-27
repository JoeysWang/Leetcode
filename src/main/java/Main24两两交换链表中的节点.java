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

    public static void main(String[] args) {
        test();
    }

    public static void test() {
        TestUtil.reset();
        Main24两两交换链表中的节点 obj = new Main24两两交换链表中的节点();

        // 测试用例1: 示例用例 [1,2,3,4] → [2,1,4,3]
        ListNode head1 = buildList(new int[]{1, 2, 3, 4});
        ListNode result1 = obj.swapPairs(head1);
        TestUtil.assertArrayEquals(new int[]{2, 1, 4, 3}, listToArray(result1), "示例: [1,2,3,4]");

        // 测试用例2: 奇数个节点 [1,2,3] → [2,1,3]
        ListNode head2 = buildList(new int[]{1, 2, 3});
        ListNode result2 = obj.swapPairs(head2);
        TestUtil.assertArrayEquals(new int[]{2, 1, 3}, listToArray(result2), "奇数个: [1,2,3]");

        // 测试用例3: 空链表
        ListNode result3 = obj.swapPairs(null);
        TestUtil.assertArrayEquals(new int[]{}, listToArray(result3), "边界: 空链表");

        // 测试用例4: 单个节点
        ListNode head4 = buildList(new int[]{1});
        ListNode result4 = obj.swapPairs(head4);
        TestUtil.assertArrayEquals(new int[]{1}, listToArray(result4), "边界: 单节点");

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
}
