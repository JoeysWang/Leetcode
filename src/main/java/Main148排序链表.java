import data.ListNode;

public class Main148排序链表 {

    /**
     * 在 O(n log n) 时间复杂度和常数级空间复杂度下，对链表进行排序。
     *
     * 示例 1:
     *
     * 输入: 4->2->1->3
     * 输出: 1->2->3->4
     * 示例 2:
     *
     * 输入: -1->5->3->4->0
     * 输出: -1->0->3->4->5
     */

    public static void main(String[] args) {
        test();
    }

    /**
     * 辅助方法：根据数组构建链表
     */
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

    /**
     * 辅助方法：将链表转为数组
     */
    private static int[] listToArray(ListNode head) {
        int count = 0;
        ListNode current = head;
        while (current != null) {
            count++;
            current = current.next;
        }
        int[] result = new int[count];
        current = head;
        for (int i = 0; i < count; i++) {
            result[i] = current.val;
            current = current.next;
        }
        return result;
    }

    public static void test() {
        TestUtil.reset();
        Solution solution = new Solution();

        // 测试用例1: 示例用例 - 4->2->1->3
        ListNode list1 = buildList(new int[]{4, 2, 1, 3});
        ListNode sorted1 = solution.sortList(list1);
        TestUtil.assertArrayEquals(new int[]{1, 2, 3, 4}, listToArray(sorted1), "示例用例: [4,2,1,3]排序");

        // 测试用例2: 示例用例 - -1->5->3->4->0
        ListNode list2 = buildList(new int[]{-1, 5, 3, 4, 0});
        ListNode sorted2 = solution.sortList(list2);
        TestUtil.assertArrayEquals(new int[]{-1, 0, 3, 4, 5}, listToArray(sorted2), "示例用例: [-1,5,3,4,0]排序");

        // 测试用例3: 边界用例 - 单节点
        ListNode list3 = buildList(new int[]{1});
        ListNode sorted3 = solution.sortList(list3);
        TestUtil.assertArrayEquals(new int[]{1}, listToArray(sorted3), "边界用例: 单节点");

        // 测试用例4: 边界用例 - 两个节点
        ListNode list4 = buildList(new int[]{2, 1});
        ListNode sorted4 = solution.sortList(list4);
        TestUtil.assertArrayEquals(new int[]{1, 2}, listToArray(sorted4), "边界用例: 两个节点");

        // 测试用例5: 已经有序
        ListNode list5 = buildList(new int[]{1, 2, 3, 4, 5});
        ListNode sorted5 = solution.sortList(list5);
        TestUtil.assertArrayEquals(new int[]{1, 2, 3, 4, 5}, listToArray(sorted5), "普通用例: 已有序链表");

        // 测试用例6: 逆序
        ListNode list6 = buildList(new int[]{5, 4, 3, 2, 1});
        ListNode sorted6 = solution.sortList(list6);
        TestUtil.assertArrayEquals(new int[]{1, 2, 3, 4, 5}, listToArray(sorted6), "普通用例: 逆序链表");

        TestUtil.printSummary();
    }

    /**
     * 自底向上 并归排序
     * 每一轮合并merge操作针对的单元都有固定长度intv，例如：
     * 第一轮合并时intv = 1，即将整个链表切分为多个长度为1的单元，并按顺序两两排序合并，合并完成的已排序单元长度为2。
     * 第二轮合并时intv = 2，即将整个链表切分为多个长度为2的单元，并按顺序两两排序合并，合并完成已排序单元长度为4。
     * 以此类推，直到单元长度intv >= 链表长度，代表已经排序完成。
     *
     * 使用快慢指针，切割链表
     * 快指针每次都跳两个、 慢指针跳一个 ，把慢指针的next切断，这样慢指针的前面就只有一个节点了
     * 然后继续从切断的地方继续跑快慢指针  
     */
    public static class Solution {
        public ListNode sortList(ListNode head) {
            return mergeSort(head);
        }

        private ListNode mergeSort(ListNode head) {
            //回归条件
            if (head.next == null) {
                return head;
            }
            //快指针,考虑到链表为2时的情况，fast比slow早一格
            ListNode fast = head.next;
            //慢指针
            ListNode slow = head;
            //快慢指针开跑
            while (fast != null && fast.next != null) {
                fast = fast.next.next;
                slow = slow.next;
            }
            //找到右子链表头元素，复用fast引用
            fast = slow.next;
            //将中点后续置空，切割为两个子链表
            slow.next = null;
            //递归分解左子链表,得到新链表起点
            head = mergeSort(head);
            //递归分解右子链表,得到新链表起点
            fast = mergeSort(fast);
            //并归两个子链表
            ListNode newHead = merge(head, fast);
            return newHead;

        }

        public final ListNode merge(ListNode left, ListNode right) {
            //维护临时序列的头元素
            ListNode head;
            if (left.val <= right.val) {
                head = left;
                left = left.next;
            } else {
                head = right;
                right = right.next;
            }
            //两个子链表均存在剩余元素
            ListNode temp = head;
            while (left != null && right != null) {
                //将较小的元素加入临时序列
                if (left.val <= right.val) {
                    temp.next = left;
                    left = left.next;
                    temp = temp.next;
                } else {
                    temp.next = right;
                    right = right.next;
                    temp = temp.next;
                }
            }
            //左子序列用完将右子序列余下元素加入临时序列
            if (left == null) {
                temp.next = right;
            }
            //右子序列用完将左子序列余下元素加入临时序列
            if (right == null) {
                temp.next = left;
            }
            return head;
        }


    }
}
