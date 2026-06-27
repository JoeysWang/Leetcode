public class Main204计数质数 {

    /**
     * 统计所有小于非负整数 n 的质数的数量。
     * 输入：n = 10
     * 输出：4
     * 解释：小于 10 的质数一共有 4 个, 它们是 2, 3, 5, 7 。
     */
    public static void main(String[] args) {
        Solution solution = new Solution();
        System.out.println(solution.countPrimes(10));
        test();
    }


    private static class Solution {

        /**
         * 暴力法
         * 对于每个数 x，我们可以从小到大枚举 [2,x-1]中的每个数 y，判断 y 是否为 x 的因数，
         * 即
         * for( int y =2; y < x-1; y++ )
         * {
         * if (x % y == 0 ){
         * return false;
         * }
         * }
         * 但是不必判断到x-1 ,只要判断到 y*y <= x 即可
         */
        public int countPrimes(int n) {
            int ans = 0;
            for (int i = 2; i < n; i++) {
                boolean isPrime = isPrime(i);
                if (isPrime)
                    ans++;
            }

            return ans;
        }

        private boolean isPrime(int i) {
            for (int y = 2; y * y <= i; y++) {
                if (i % y == 0)
                    return false;
            }
            return true;
        }




    }

    public static void test() {
        TestUtil.reset();
        Solution solution = new Solution();

        // 测试用例1: 示例用例 n=10 → 4 (质数: 2,3,5,7)
        TestUtil.assertEquals(4, solution.countPrimes(10), "示例用例 n=10");

        // 测试用例2: 边界用例 n=0 → 0
        TestUtil.assertEquals(0, solution.countPrimes(0), "边界用例 n=0");

        // 测试用例3: 边界用例 n=1 → 0
        TestUtil.assertEquals(0, solution.countPrimes(1), "边界用例 n=1");

        // 测试用例4: 边界用例 n=2 → 0 (小于2的质数没有)
        TestUtil.assertEquals(0, solution.countPrimes(2), "边界用例 n=2");

        // 测试用例5: n=3 → 1 (质数: 2)
        TestUtil.assertEquals(1, solution.countPrimes(3), "普通用例 n=3");

        // 测试用例6: n=20 → 8 (质数: 2,3,5,7,11,13,17,19)
        TestUtil.assertEquals(8, solution.countPrimes(20), "普通用例 n=20");

        TestUtil.printSummary();
    }

    private static class Solution2 {

        /**
         * 暴力法
         * 对于每个数 x，我们可以从小到大枚举 [2,x-1]中的每个数 y，判断 y 是否为 x 的因数，
         * 即
         * for( int y =2; y < x-1; y++ )
         * {
         * if (x % y == 0 ){
         * return false;
         * }
         * }
         * 但是不必判断到x-1 ,只要判断到 y*y <= x 即可
         */
        public int countPrimes(int n) {
            int ans = 0;
            for (int i = 2; i < n; i++) {
                boolean isPrime = isPrime(i);
                if (isPrime)
                    ans++;
            }

            return ans;
        }

        private boolean isPrime(int i) {
            for (int y = 2; y * y <= i; y++) {
                if (i % y == 0)
                    return false;
            }
            return true;
        }




    }

}
