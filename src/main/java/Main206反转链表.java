import data.ListNode;

public class Main206反转链表 {


    class Solution {
        public ListNode reverseList(ListNode head) {
            ListNode current = head;
            ListNode pre = null;

            while (current != null) {

                ListNode next = current.next;
                current.next = pre;
                pre = current;

                current = next;
            }

            return pre;
        }
    }

    // 切断current 和 next的联系
    public ListNode reverseList(ListNode head) {

        ListNode current = head;
        ListNode pervious = null;


        while (current != null) {
            ListNode temple = current.next;
            current.next = pervious;
            pervious = current;
            current = temple;

        }

        return pervious;

    }

    // 辅助方法：构建链表
    private static ListNode buildList(int[] values) {
        if (values == null || values.length == 0) return null;
        ListNode head = new ListNode(values[0]);
        ListNode current = head;
        for (int i = 1; i < values.length; i++) {
            current.next = new ListNode(values[i]);
            current = current.next;
        }
        return head;
    }

    // 辅助方法：链表转数组
    private static int[] listToArray(ListNode head) {
        if (head == null) return new int[0];
        int size = 0;
        ListNode current = head;
        while (current != null) {
            size++;
            current = current.next;
        }
        int[] result = new int[size];
        current = head;
        for (int i = 0; i < size; i++) {
            result[i] = current.val;
            current = current.next;
        }
        return result;
    }

    public static void test() {
        TestUtil.reset();
        Main206反转链表 solution = new Main206反转链表();

        // 测试用例1: 示例用例
        ListNode list1 = buildList(new int[]{1, 2, 3, 4, 5});
        ListNode reversed1 = solution.reverseList(list1);
        TestUtil.assertArrayEquals(new int[]{5, 4, 3, 2, 1}, listToArray(reversed1), "示例用例: 反转链表");

        // 测试用例2: 边界用例 - 空链表
        ListNode list2 = buildList(new int[]{});
        ListNode reversed2 = solution.reverseList(list2);
        TestUtil.assertArrayEquals(new int[]{}, listToArray(reversed2), "边界用例: 空链表");

        // 测试用例3: 边界用例 - 单个节点
        ListNode list3 = buildList(new int[]{1});
        ListNode reversed3 = solution.reverseList(list3);
        TestUtil.assertArrayEquals(new int[]{1}, listToArray(reversed3), "边界用例: 单个节点");

        // 测试用例4: 普通用例
        ListNode list4 = buildList(new int[]{1, 2});
        ListNode reversed4 = solution.reverseList(list4);
        TestUtil.assertArrayEquals(new int[]{2, 1}, listToArray(reversed4), "普通用例: 两个节点");

        TestUtil.printSummary();
    }

    public static void main(String[] args) {
        test();
    }
}
