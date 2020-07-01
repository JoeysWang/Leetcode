package test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ThreadTest {

    public static void main(String[] args) {

//        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
//        scheduledExecutorService.scheduleWithFixedDelay(
//                new Task(),
//                0,
//                1,
//                TimeUnit.SECONDS);

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        Task command = new Task();
        for (int i = 0; i < 10000; i++) {
            executorService.execute(command);
        }
    }


    static class Task implements Runnable {
        volatile int count = 0;

        @Override
        public void run() {
            synchronized (this){
                count++;
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(String.format("%d current thread is %s", count, Thread.currentThread().getName()));
        }
    }
}
