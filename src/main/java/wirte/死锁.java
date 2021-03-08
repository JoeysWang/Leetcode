package wirte;

public class 死锁 {

    public static void main(String[] args) {

        String resource1 = "1";
        String resource2 = "2";

        new Thread(new Test("thread1", resource1, resource2)).start();
        new Thread(new Test("thread2", resource2, resource1)).start();

    }


    static class Test implements Runnable {
        private String resource1;
        private String resource2;
        private String name;

        public Test(String name, String resource1, String resource2) {
            this.resource1 = resource1;
            this.resource2 = resource2;
            this.name = name;
        }

        @Override
        public void run() {
            synchronized (resource1) {
                System.out.println(name + " 获取到了 " + resource1);
                try {
                    Thread.sleep(1000);
                    synchronized (resource2) {
                        System.out.println(name + " 获取到了 " + resource2);

                    }
                } catch (Exception e) {

                }

            }
        }
    }

}
