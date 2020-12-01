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
