import java.util.PriorityQueue;

public class Main703数据流中的第K大元素 {

    /**
     * 设计一个找到数据流中第K大元素的类（class）。注意是排序后的第K大元素，不是第K个不同的元素。
     * <p>
     * 你的 KthLargest 类需要一个同时接收整数 k 和整数数组nums 的构造器，它包含数据流中的初始元素。每次调用 KthLargest.add，返回当前数据流中第K大的元素。
     * <p>
     * https://leetcode-cn.com/problems/kth-largest-element-in-a-stream
     */

    public static void main(String[] args) {
        PriorityQueue<Integer> queue = new PriorityQueue<>();


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
