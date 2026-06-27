import data.ListNode;

public class Main23合并K个排序列表 {

//
//    输入:
//            [
//            1->4->5,
//            1->3->4,
//            2->6
//            ]
//    输出: 1->1->2->3->4->4->5->6


    public static ListNode mergeKLists(ListNode[] lists) {
//        分治
        if (lists == null || lists.length == 0) return null;
        return sort(lists, 0, lists.length - 1);
    }

    private static ListNode sort(ListNode[] lists, int low, int high) {

        if (low >= high) return lists[low];

        int mid = (high - low) / 2 + low;


        ListNode l1 = sort(lists, low, mid);
        ListNode l2 = sort(lists, mid + 1, high);

        return Main21合并两个有序链表.mergeTwoLists(l1, l2);
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

        // 测试用例1: 示例用例
        ListNode[] lists1 = new ListNode[]{
            buildList(new int[]{1, 4, 5}),
            buildList(new int[]{1, 3, 4}),
            buildList(new int[]{2, 6})
        };
        ListNode result1 = mergeKLists(lists1);
        TestUtil.assertArrayEquals(new int[]{1, 1, 2, 3, 4, 4, 5, 6}, listToArray(result1), "示例用例: 合并K个链表");

        // 测试用例2: 边界用例 - 空数组
        ListNode[] lists2 = new ListNode[]{};
        ListNode result2 = mergeKLists(lists2);
        TestUtil.assertArrayEquals(new int[]{}, listToArray(result2), "边界用例: 空数组");

        // 测试用例3: 边界用例 - 包含空链表
        ListNode[] lists3 = new ListNode[]{null, buildList(new int[]{1}), null};
        ListNode result3 = mergeKLists(lists3);
        TestUtil.assertArrayEquals(new int[]{1}, listToArray(result3), "边界用例: 包含空链表");

        // 测试用例4: 普通用例 - 单个链表
        ListNode[] lists4 = new ListNode[]{buildList(new int[]{1, 2, 3})};
        ListNode result4 = mergeKLists(lists4);
        TestUtil.assertArrayEquals(new int[]{1, 2, 3}, listToArray(result4), "普通用例: 单个链表");

        TestUtil.printSummary();
    }

    public static void main(String[] args) {
        test();
    }
}
