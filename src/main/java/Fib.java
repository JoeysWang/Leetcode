

public class Fib {

    public static void main(String[] args) {

        Solution solution = new Solution();

        long start = System.currentTimeMillis();

        int request = 64;
        long result = solution.fib(request);
        long now = System.currentTimeMillis();
        System.out.println("fib:" + request + " = " + result +
                " , cost:" + (now - start));

    }

    public static class Solution {


        public long fib(int x) {
            if (x < 2) return 1;
            long[] a = new long[x];
            a[0] = 1;
            a[1] = 1;
            for (int i = 2; i < x; i++) {
                a[i] = a[i - 1] + a[i - 2];
            }
            return a[x - 1];
        }

    }
}
