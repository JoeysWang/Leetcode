package wirte;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 两个线程交替打印
 * <p>
 * 方式一：synchronized + wait/notify（一个共享锁 + 一个 boolean 标志位）
 * 方式二：ReentrantLock + 两个 Condition（更灵活，推荐）
 * <p>
 * 目标：线程 A 打印 1,3,5,7,9；线程 B 打印 2,4,6,8,10，交替输出 1..10。
 */
public class 两个线程交替打印 {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== 方式一：wait/notify ===");
        runByWaitNotify();

        System.out.println("\n=== 方式二：Lock + Condition ===");
        runByCondition();
    }

    // =================== 方式一：synchronized + wait/notify ===================
    // 用一个锁对象 + 一个 boolean 标志位 turn，true 表示轮到线程 A。
    // 谁拿到锁但发现还没轮到自己，就 wait() 让出；打印完翻转标志并 notify 唤醒对方。
    static void runByWaitNotify() throws InterruptedException {
        final Object lock = new Object();
        // turn=true 轮到 A(奇数)，turn=false 轮到 B(偶数)
        final boolean[] turn = {true};

        Thread A = new Thread(() -> {
            for (int i = 1; i <= 10; i += 2) {
                synchronized (lock) {
                    while (!turn[0]) {            // 没轮到我，等
                        try { lock.wait(); } catch (InterruptedException e) { return; }
                    }
                    System.out.println("A: " + i);
                    turn[0] = false;              // 翻转，轮到 B
                    lock.notify();                // 唤醒 B
                }
            }
        }, "Thread-A");

        Thread B = new Thread(() -> {
            for (int i = 2; i <= 10; i += 2) {
                synchronized (lock) {
                    while (turn[0]) {             // 没轮到我，等
                        try { lock.wait(); } catch (InterruptedException e) { return; }
                    }
                    System.out.println("B: " + i);
                    turn[0] = true;               // 翻转，轮到 A
                    lock.notify();                // 唤醒 A
                }
            }
        }, "Thread-B");

        A.start();
        B.start();
        A.join();
        B.join();
    }

    // =================== 方式二：ReentrantLock + 两个 Condition ===================
    // 两个 Condition 各管一个线程，谁干完活就 signal 对方的 condition，自己再 await。
    // 比 wait/notify 更精准：notify 只能随机唤醒一个等待者，Condition 能定向唤醒。
    static void runByCondition() {
        Lock lock = new ReentrantLock();
        Condition condA = lock.newCondition();   // A 在这等
        Condition condB = lock.newCondition();   // B 在这等
        // true 轮到 A
        boolean[] turn = {true};

        Thread A = new Thread(() -> {
            for (int i = 1; i <= 10; i += 2) {
                lock.lock();
                try {
                    while (!turn[0]) {
                        condA.await();
                    }
                    System.out.println("A: " + i);
                    turn[0] = false;
                    condB.signal();              // 叫醒 B
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    lock.unlock();
                }
            }
        }, "Thread-A");

        Thread B = new Thread(() -> {
            for (int i = 2; i <= 10; i += 2) {
                lock.lock();
                try {
                    while (turn[0]) {
                        condB.await();
                    }
                    System.out.println("B: " + i);
                    turn[0] = true;
                    condA.signal();              // 叫醒 A
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    lock.unlock();
                }
            }
        }, "Thread-B");

        A.start();
        B.start();
    }
}
