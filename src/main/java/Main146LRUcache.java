import java.util.HashMap;
import java.util.LinkedHashMap;

public class Main146LRUcache {

    public static void main(String[] args) {

        LRUCache2 lruCache2 = new LRUCache2(2);

        lruCache2.put(1, 1);
        lruCache2.put(2, 2);
        lruCache2.put(3, 3);
        lruCache2.put(4, 4);
        lruCache2.put(5, 5);
        lruCache2.put(6, 6);
        test();
    }

    public static void test() {
        TestUtil.reset();

        // 测试用例1: 基本put/get操作
        LRUCache2 cache1 = new LRUCache2(2);
        cache1.put(1, 1);
        cache1.put(2, 2);
        TestUtil.assertEquals(1, cache1.get(1), "示例用例: put(1,1)+put(2,2),get(1)=1");

        // 测试用例2: 容量淘汰 - 加入第三个元素应淘汰key=2
        cache1.put(3, 3);
        TestUtil.assertEquals(-1, cache1.get(2), "示例用例: put(3,3)后get(2)应返回-1(被淘汰)");

        // 测试用例3: get后LRU顺序改变
        TestUtil.assertEquals(3, cache1.get(3), "示例用例: get(3)=3");
        cache1.put(4, 4);
        TestUtil.assertEquals(-1, cache1.get(1), "示例用例: put(4,4)后get(1)应返回-1(被淘汰)");
        TestUtil.assertEquals(3, cache1.get(3), "示例用例: get(3)=3(未淘汰)");
        TestUtil.assertEquals(4, cache1.get(4), "示例用例: get(4)=4");

        // 测试用例4: 边界用例 - 获取不存在的key
        LRUCache2 cache2 = new LRUCache2(1);
        TestUtil.assertEquals(-1, cache2.get(99), "边界用例: 获取不存在的key返回-1");

        // 测试用例5: 更新已有key的值
        LRUCache2 cache3 = new LRUCache2(2);
        cache3.put(1, 1);
        cache3.put(1, 10);
        TestUtil.assertEquals(10, cache3.get(1), "普通用例: 更新已有key的值");

        // 测试用例6: 容量为1的缓存
        LRUCache2 cache4 = new LRUCache2(1);
        cache4.put(1, 1);
        cache4.put(2, 2);
        TestUtil.assertEquals(-1, cache4.get(1), "普通用例: 容量1时put(2,2)淘汰key=1");
        TestUtil.assertEquals(2, cache4.get(2), "普通用例: 容量1时get(2)=2");

        TestUtil.printSummary();
    }

    /**
     * hashmap + 双向链表
     */
    public static class LRUCache2 {

        LRUNode head = new LRUNode();
        LRUNode tail = new LRUNode();
        HashMap<Integer, LRUNode> map;
        private int capacity;

        public LRUCache2(int capacity) {
            map = new HashMap<>();
            this.capacity = capacity;

            head.next = tail;
            tail.pre = head;
        }

        public int get(int key) {
            LRUNode node = map.get(key);
            if (node == null) {
                return -1;
            } else {
                removeNode(node);
                addNode(node);
                return node.val;
            }

        }

        public void put(int key, int value) {
            LRUNode node = map.get(key);
            if (node == null) {
                node = new LRUNode(key, value);
                addNode(node);
                if (map.size() > capacity) {
                    removeTailNode();
                }

            } else {
                removeNode(node);
                node.val = value;
                addNode(node);
            }
        }

        private void removeTailNode() {
            LRUNode node = tail.pre;
            tail.pre = node.pre;
            node.pre.next = tail;
            map.remove(node.key);
        }

        private void removeNode(LRUNode node) {
            node.next.pre = node.pre;
            node.pre.next = node.next;
            map.remove(node.key);
        }

        private void addNode(LRUNode node) {

            LRUNode first = head.next;


            first.pre = node;
            node.next = first;

            head.next = node;
            node.pre = head;

            map.put(node.key, node);
        }


    }

    public static class LRUNode {
        int val;
        int key;
        LRUNode pre;
        LRUNode next;

        public LRUNode() {
        }

        public LRUNode(int key, int val) {
            this.val = val;
            this.key = key;
        }
    }


    /**
     * 直接继承linkedhashmap ，
     * 构造方法第三个参数传true
     */
    public static class LRUCache1 extends LinkedHashMap<Integer, Integer> {

        public LRUCache1(int capacity) {
            super(capacity, 0.75f, true);
        }

        public int get(int key) {
            return super.getOrDefault(key, -1);
        }

        @Override
        public Integer get(Object key) {
            return super.getOrDefault(key, -1);
        }

        public void put(int key, int value) {
            super.put(key, value);
        }
    }


}
