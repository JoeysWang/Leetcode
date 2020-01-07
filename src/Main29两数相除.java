public class Main29两数相除 {

    public static void main(String[] args) {
        System.out.println(divide(-2147483648, -1));
    }

    public static int divide(int dividend, int divisor) {

        // 6/2=3

        boolean sign = (dividend > 0) ^ (divisor > 0);

        int newDividend = dividend > 0 ? -dividend : dividend;
        int newDivisor = divisor > 0 ? -divisor : divisor;


        int result = 0;
        int current = 0;
        while (current > newDividend) {
            result += 1;
            current -= newDivisor;
        }
        if (current < newDividend)
            result -= 1;

        return sign ? result :- result;
    }


}
