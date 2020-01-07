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
