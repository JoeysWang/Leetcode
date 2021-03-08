package wirte;

import java.util.ArrayDeque;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class 生产者消费者 {
    public static void main(String[] args) {

    }


    static class Manufacture {
        ReentrantLock lock;
        Condition notEmpty;
        Condition notFull;

        Queue<String> resources;
        private int max = 10;

        public Manufacture() {
            lock = new ReentrantLock();
            notEmpty = lock.newCondition();
            notFull = lock.newCondition();
            resources = new ArrayDeque<>();
            new Thread(new Producer()).start();
            new Thread(new Consumer()).start();
        }


        private class Producer implements Runnable {

            @Override
            public void run() {
                lock.lock();
                try {
                    while (resources.size() == max) {
                        notFull.await();
                    }
                    resources.offer("resource");

                    notEmpty.signalAll();
                } catch (Exception e) {

                } finally {
                    lock.unlock();
                }

            }
        }

        private class Consumer implements Runnable {
            @Override
            public void run() {
                lock.lock();
                try {
                    while (resources.size() == 0) {
                        notEmpty.await();
                    }
                    resources.poll();
                    notFull.signalAll();

                } catch (Exception e) {

                }finally {

                    lock.unlock();
                }
            }
        }


    }
}
