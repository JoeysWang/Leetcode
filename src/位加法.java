public class 位加法 {

    /**
     * 不用加减乘除做加法
     *
     * p310
     *
     * 按位异或可以获得 不进位后的加法：
     * 101 ^ 001 = 100
     *
     * 按位与 可以获得需要进位的位置：
     * sum:    101 & 001 = 001
     * 然后把按位与的结果左移1位
     * carry:  001 << 1 = 010
     *
     * 再把 sum 和 carry 相加：
     */
    public static void main(String[] args) {

        Solution solution = new Solution();
        int res = solution.add(5, 2);

        System.out.println("res " + res);
    }

    public static class Solution {

        public int add(int a, int b) {
            int num1 = a;
            int num2 = b;
            do {
                int sum = num1 ^ num2;
                int carry = (num1 & num2) << 1;

                num1 = sum;
                num2 = carry;
            } while (num2 != 0);

            return num1;
        }

    }

}
