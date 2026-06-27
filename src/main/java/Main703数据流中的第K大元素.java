import java.util.PriorityQueue;

public class Main703数据流中的第K大元素 {

    /**
     * 设计一个找到数据流中第K大元素的类（class）。注意是排序后的第K大元素，不是第K个不同的元素。
     * <p>
     * 你的 KthLargest 类需要一个同时接收整数 k 和整数数组nums 的构造器，它包含数据流中的初始元素。
     * 每次调用 KthLargest.add，返回当前数据流中第K大的元素。
     * <p>
     * https://leetcode-cn.com/problems/kth-largest-element-in-a-stream
     */

    //用堆来做，堆里放k个元素，PriorityQueue实际上就是一个小顶堆，
    //add进去一个新数，判断堆顶和这个数，如果堆顶小，就把堆顶的最小数弹出去
    public static void main(String[] args) {
        PriorityQueue<Integer> queue = new PriorityQueue<>();
        test();

    }

    public static void test() {
        TestUtil.reset();

        // 测试用例1: 示例用例 k=3, nums=[4,5,8,2]
        KthLargest kthLargest1 = new KthLargest(3, new int[]{4, 5, 8, 2});
        TestUtil.assertEquals(4, kthLargest1.add(3), "示例用例: add(3) -> 4");
        TestUtil.assertEquals(5, kthLargest1.add(5), "示例用例: add(5) -> 5");
        TestUtil.assertEquals(5, kthLargest1.add(10), "示例用例: add(10) -> 5");
        TestUtil.assertEquals(8, kthLargest1.add(9), "示例用例: add(9) -> 8");
        TestUtil.assertEquals(8, kthLargest1.add(4), "示例用例: add(4) -> 8");

        // 测试用例2: 边界用例 k=1, nums=[0]
        // constructor: add(0) -> queue=[0], size=1
        KthLargest kthLargest2 = new KthLargest(1, new int[]{0});
        // add(-1): size=1, peek=0, 0 < -1? false, return 0
        TestUtil.assertEquals(0, kthLargest2.add(-1), "边界用例: k=1, 添加更小的数仍返回0");
        // add(1): size=1, peek=0, 0 < 1? true, poll, offer(1), queue=[1], return 1
        TestUtil.assertEquals(1, kthLargest2.add(1), "边界用例: k=1, add(1) -> 1");

        // 测试用例3: 普通用例 k=2, nums=[1,2]
        KthLargest kthLargest3 = new KthLargest(2, new int[]{1, 2});
        // constructor: add(1)->queue=[1], add(2)->size=1<2, offer(2), queue=[1,2]
        TestUtil.assertEquals(1, kthLargest3.add(0), "普通用例: k=2, add(0) -> 1");
        TestUtil.assertEquals(2, kthLargest3.add(3), "普通用例: k=2, add(3) -> 2");

        // 测试用例4: 边界用例 - 初始nums为空
        KthLargest kthLargest4 = new KthLargest(2, new int[]{});
        TestUtil.assertEquals(5, kthLargest4.add(5), "边界用例: 空初始数组, add(5) -> 5 (堆里[5])");
        TestUtil.assertEquals(3, kthLargest4.add(3), "边界用例: add(3) -> 3 (堆里[3,5])");
        TestUtil.assertEquals(5, kthLargest4.add(10), "边界用例: add(10) -> 5 (堆里[5,10])");

        TestUtil.printSummary();
    }

    //使用min heap
    public static class KthLargest {


        private int k;
        private PriorityQueue<Integer> mQueue;


        public KthLargest(int k, int[] nums) {
            this.k = k;
            //优先队列的容量是k个
            mQueue = new PriorityQueue<>(k);
            //将已有nums加入queue
            for (int num : nums) {
                add(num);
            }
        }

        //将最大的k个数放在queue里
        public int add(int val) {
            if (mQueue.size() < k) {
                //如果堆里元素数少于k个，就往堆里加入元素
                mQueue.offer(val);
            } else if (mQueue.peek() < val) {
                //因为是min heap，如果堆顶小于val，就把堆顶踢出，加入新的元素
                mQueue.poll();
                mQueue.offer(val);
            }

            return mQueue.peek();
        }
    }
}
