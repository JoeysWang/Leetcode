public class Lock {

    static class Thread1 extends Thread implements  Runnable{
        Test test;

        public Thread1(Test test) {
            this.test = test;
        }

        @Override
        public void run() {
            super.run();
            int a = 2000;
            while (a > 0) {
                test.add();
                a--;
            }
            stop();
        }
    }

    public static void main(String[] args) {
        Test test = new Test();


//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                int a = 2000;
//                while (a > 0) {
//                    test.add();
//                    a--;
//                }
//            }
//        };
//
//        Runnable runnable2 = new Runnable() {
//            @Override
//            public void run() {
//                int a = 2000;
//                while (a > 0) {
//                    test.add();
//                    a--;
//                }
//            }
//        };
//        Runnable runnable3 = new Runnable() {
//            @Override
//            public void run() {
//                int a = 2000;
//                while (a > 0) {
//                    test.add();
//                    a--;
//                }
//            }
//        };

        Thread1 thread = new Thread1(test);
        Thread1 thread2 = new Thread1(test);
        Thread1 thread3 = new Thread1(test);
        thread.start();
        thread2.start();
        thread3.start();


        try {
            Thread.sleep(1000L);
            System.out.println("count = " + test.count);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static class Test {

          int count = 0;

        public  synchronized void add() {
            count += 1;
        }

    }
}
