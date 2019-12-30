public class Main231_2的幂 {

    /**
     * 这个数字n转换成2进制，应该只有一个1
     * 比如 8 = 1000
     * 4 = 100
     * 2 = 10
     */
    public boolean isPowerOfTwo(int n) {
        if (n <= 0)
            return false;

        return (n & (n - 1)) == 0;

    }

    /**
     * 计算 value 有几个二进制1
     * @param args
     */
    public static void main(String[] args) {
        int value = 0b10101;
        int count=0;
        while (value != 0) {
            System.out.println(Integer.toBinaryString(value)+" & "+Integer.toBinaryString(value-1));
            count++;
            value = value & (value - 1);
            System.out.println("after:"+Integer.toBinaryString(value));
        }
        System.out.println("count is "+count);
    }
}
