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

}
