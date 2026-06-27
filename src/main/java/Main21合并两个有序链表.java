import data.ListNode;

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

    public static void main(String[] args) {
        test();
    }

    public static void test() {
        TestUtil.reset();

        // 测试用例1: 示例用例 [1,2,4] + [1,3,4] → [1,1,2,3,4,4]
        ListNode l1 = buildList(new int[]{1, 2, 4});
        ListNode l2 = buildList(new int[]{1, 3, 4});
        ListNode merged = mergeTwoLists(l1, l2);
        TestUtil.assertArrayEquals(new int[]{1, 1, 2, 3, 4, 4}, listToArray(merged), "示例: [1,2,4]+[1,3,4]");

        // 测试用例2: 两个空链表
        ListNode merged2 = mergeTwoLists(null, null);
        TestUtil.assertArrayEquals(new int[]{}, listToArray(merged2), "边界: 两个空链表");

        // 测试用例3: 一个为空
        ListNode l3 = buildList(new int[]{1, 2, 3});
        ListNode merged3 = mergeTwoLists(null, l3);
        TestUtil.assertArrayEquals(new int[]{1, 2, 3}, listToArray(merged3), "一个为空");

        // 测试用例4: 单元素
        ListNode l4 = buildList(new int[]{1});
        ListNode l5 = buildList(new int[]{2});
        ListNode merged4 = mergeTwoLists(l4, l5);
        TestUtil.assertArrayEquals(new int[]{1, 2}, listToArray(merged4), "单元素: [1]+[2]");

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
